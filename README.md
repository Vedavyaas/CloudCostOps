# CloudCostOps

A cloud cost auditing platform built on Spring Cloud microservices, a Python ML pipeline, and a React frontend.

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+ / npm
- Python 3.10+
- Apache Kafka on `localhost:9092`
- AWS credentials configured (`~/.aws/credentials` or environment) with access to Aurora DynamoDB, Aurora Postgres and RDS IAM auth

---

## Services & Ports

| Service | Port | Notes                                  |
|---|---|----------------------------------------|
| Discovery (Eureka) | `8761` | Start first                            |
| API Gateway | `6000` | Start last                             |
| AuthenticationSystem | `6001` | Requires AWS Aurora RDS (PostgreSQL)   |
| OrchestrationEngine | `6002` | Requires AWS Aurora DynamoDB           |
| Interceptor | — | Library JAR — embedded, not standalone |
| Audit ML Service | — | Kafka bridge process                   |
| Frontend | `3000` | Proxies to API Gateway                 |

---

## Running the Stack

### 1. Start Kafka

```bash
# Using Docker
docker run -d -p 9092:9092 apache/kafka:latest
```

### 2. Start Java Services (in order)

```bash
# Terminal 1 — Discovery
cd Discovery && ./mvnw spring-boot:run

# Terminal 2 — AuthenticationSystem
cd AuthenticationSystem && ./mvnw spring-boot:run

# Terminal 3 — OrchestrationEngine
cd OrchestrationEngine && ./mvnw spring-boot:run

# Terminal 4 — Interceptor (build & install to local Maven repo)
cd Interceptor && mvn clean install -DskipTests

# Terminal 5 — API Gateway
cd APIGateway && ./mvnw spring-boot:run
```

### 3. Start the ML Service

```bash
cd audit-ml-service
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

# Train the model (required on first run)
python main.py train

# Start the Kafka bridge
python main.py serve
```

### 4. Start the Frontend

```bash
cd frontend
npm install
npm run dev
# App available at http://localhost:3000
```

---

## Configuration

### AuthenticationSystem

Backed by **AWS Aurora PostgreSQL**. Uses the AWS Advanced JDBC Wrapper with IAM authentication — no static password needed.

**AWS setup required:**
- An Aurora PostgreSQL cluster in `ap-south-1`
- A database user (`postgres`) enabled for IAM authentication
- The IAM role on your runtime must have `rds-db:connect` permission

Update the cluster hostname in `AuthenticationSystem/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:aws-wrapper:postgresql://<your-aurora-cluster-endpoint>:5432/postgres?sslmode=require
spring.datasource.username=postgres
spring.datasource.hikari.data-source-properties.wrapperPlugins=iam
spring.datasource.hikari.data-source-properties.region=ap-south-1
```

> On first run, `spring.jpa.hibernate.ddl-auto=create` will auto-create the schema.

### OrchestrationEngine

Backed by **AWS Aurora DynamoDB**. No schema migration needed — tables must be created in AWS before starting the service.

**AWS setup required:**
- DynamoDB table for cloud metrics (partition key: `eventId`, type: String)
- DynamoDB table for audit metrics (partition key: `eventId`, type: String)
- The IAM role on your runtime must have `dynamodb:PutItem`, `dynamodb:GetItem`, `dynamodb:Query` on both tables

Configured in `OrchestrationEngine/src/main/resources/application.properties`:

```properties
aws.dynamodb.region=ap-south-1
aws.dynamodb.endpoint=https://dynamodb.ap-south-1.amazonaws.com
```

### Audit ML Service

All config is via environment variables (defaults shown):

```bash
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_INPUT_TOPIC=cloud_metrics
KAFKA_OUTPUT_TOPIC=cloud_audit_metric
PUBLISH_DELAY_SECONDS=1.5
TRAINING_SAMPLES=30000
```

### Frontend

The Vite dev server proxies API calls to the gateway. Set `BACKEND_URL` to override the default:

```bash
BACKEND_URL=http://localhost:6000 npm run dev
```

---

## Interceptor — Embedding in Another Service

The Interceptor is a drop-in library that automatically publishes telemetry to Kafka from any Spring Boot app.

**1. Build and install:**

```bash
cd Interceptor && mvn clean install -DskipTests
```

**2. Add to your app's `pom.xml`:**

```xml
<dependency>
    <groupId>com.pheonix</groupId>
    <artifactId>Interceptor</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

> Use the plain JAR. Do **not** add `<classifier>exec</classifier>`.

**3. Add to your app's `application.properties`:**

```properties
app.prop.company-name=Your Company Name
app.prop.resource-type=POSTGRESQL
app.prop.resource-id=db-prod-01
app.prop.environment=PRODUCTION
app.prop.region=ap-south-1
app.prop.availability-zone=ap-south-1a
```

Kafka is fully managed by the library — no `spring.kafka.*` config required. Every `@RestController` method will automatically emit a `cloud_metrics` event.

---