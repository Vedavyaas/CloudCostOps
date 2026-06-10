#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <librdkafka/rdkafka.h>

static void dr_msg_cb(rd_kafka_t *rk, const rd_kafka_message_t *rkmessage, void *opaque) {
    if (rkmessage->err) {
        fprintf(stderr, "%% Message delivery failed: %s\n", rd_kafka_err2str(rkmessage->err));
    } else {
        fprintf(stdout, "%% Message delivered (%zu bytes, partition %"PRId32")\n",
                rkmessage->len, rkmessage->partition);
    }
}

int load_config_file(rd_kafka_conf_t *conf, const char *filename) {
    FILE *fp = fopen(filename, "r");
    if (!fp) {
        fprintf(stderr, "Failed to open config file: %s\n", filename);
        return -1;
    }

    char line[512];
    char errstr[512];
    while (fgets(line, sizeof(line), fp)) {
        if (line[0] == '#' || line[0] == '\n' || line[0] == '\r') continue;

        line[strcspn(line, "\r\n")] = 0;

        char *key = strtok(line, "=");
        char *val = strtok(NULL, "=");

        if (key && val) {
            if (rd_kafka_conf_set(conf, key, val, errstr, sizeof(errstr)) != RD_KAFKA_CONF_OK) {
                fprintf(stderr, "Configuration error: %s\n", errstr);
                fclose(fp);
                return -1;
            }
        }
    }
    fclose(fp);
    return 0;
}

int main(int argc, char **argv) {
    rd_kafka_t *rk;
    rd_kafka_conf_t *conf;
    char errstr[512];
    const char *topic_name = "cloud_metrics";

    printf("%% Initializing configuration...\n");
    conf = rd_kafka_conf_new();

    printf("%% Loading config file...\n");
    if (load_config_file(conf, "kafka.conf") != 0) {
        fprintf(stderr, "%% ERROR: Failed to load kafka.conf.\n");
        rd_kafka_conf_destroy(conf);
        return 1;
    }

    rd_kafka_conf_set_dr_msg_cb(conf, dr_msg_cb);

    printf("%% Creating producer instance...\n");
    rk = rd_kafka_new(RD_KAFKA_PRODUCER, conf, errstr, sizeof(errstr));
    if (!rk) {
        fprintf(stderr, "%% ERROR: Failed to create new producer: %s\n", errstr);
        return 1;
    }

    const char *payload = "Hello Kafka from C on Mac!";
    size_t len = strlen(payload);

    printf("%% Sending message: %s\n", payload);

    // FIX: Using rd_kafka_producev instead of rd_kafka_produce
    rd_kafka_resp_err_t err;
    err = rd_kafka_producev(
            rk,                                      // Producer handle
            RD_KAFKA_V_TOPIC(topic_name),            // Target topic name
            RD_KAFKA_V_PARTITION(RD_KAFKA_PARTITION_UA), // Auto-partition
            RD_KAFKA_V_MSGFLAGS(RD_KAFKA_MSG_F_COPY),// Copy payload buffer
            RD_KAFKA_V_VALUE((void *)payload, len),  // Message payload & len
            RD_KAFKA_V_END);                         // Sentinel to end arguments

    if (err) {
        fprintf(stderr, "%% ERROR: Failed to produce to topic %s: %s\n", topic_name, rd_kafka_err2str(err));
    } else {
        printf("%% Enqueued message (%zu bytes) for topic %s\n", len, topic_name);
    }

    printf("%% Flushing final messages...\n");
    rd_kafka_flush(rk, 10 * 1000);

    printf("%% Cleaning up...\n");
    rd_kafka_destroy(rk);

    return 0;
}