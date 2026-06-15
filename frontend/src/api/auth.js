// API layer — all requests routed via the API Gateway at port 6000
// Vite dev proxy maps /api, /admin, /get → http://3.6.254.66:6000

const BASE_URL = ''

// Helper to get auth token from localStorage
const getToken = () => localStorage.getItem('cco_token')

// Helper to build headers
const authHeaders = () => ({
  'Content-Type': 'application/json',
  Authorization: `Bearer ${getToken()}`,
})

const jsonHeaders = () => ({
  'Content-Type': 'application/json',
})

// ─────────────────────────────────────────────────────────────────────────────
// 1. Login  →  PUT /api/user/login
// ─────────────────────────────────────────────────────────────────────────────
export async function login({ username, password }) {
  const res = await fetch(`${BASE_URL}/api/user/login`, {
    method: 'PUT',
    headers: jsonHeaders(),
    body: JSON.stringify({ username, password }),
  })

  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || 'Login failed')
  }

  return res.json() // { token: "..." }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2. Create Company  →  PUT /api/user/create/company
// ─────────────────────────────────────────────────────────────────────────────
export async function createCompany({ companyName, adminUserName, email, password }) {
  const res = await fetch(`${BASE_URL}/api/user/create/company`, {
    method: 'PUT',
    headers: jsonHeaders(),
    body: JSON.stringify({ companyName, adminUserName, email, password }),
  })

  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || 'Failed to create company')
  }

  return res.text()
}

// ─────────────────────────────────────────────────────────────────────────────
// 3. Get Self Info  →  GET /get/info
// ─────────────────────────────────────────────────────────────────────────────
export async function getSelfInfo() {
  const res = await fetch(`${BASE_URL}/get/info`, {
    method: 'GET',
    headers: authHeaders(),
  })

  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || 'Failed to fetch user info')
  }

  return res.json() // { username, companyName, email, role }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4. Create Analyst  →  PUT /admin/create/analyst  (ADMIN only)
// ─────────────────────────────────────────────────────────────────────────────
export async function createAnalyst({ name, email, password }) {
  const res = await fetch(`${BASE_URL}/admin/create/analyst`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify({ name, email, password }),
  })

  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || 'Failed to create analyst')
  }

  return res.text()
}

// ─────────────────────────────────────────────────────────────────────────────
// 5. Get Analyst Count  →  GET /admin/get/analyst/count  (ADMIN only)
// ─────────────────────────────────────────────────────────────────────────────
export async function getAnalystCount() {
  const res = await fetch(`${BASE_URL}/admin/get/analyst/count`, {
    method: 'GET',
    headers: authHeaders(),
  })

  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || 'Failed to fetch analyst count')
  }

  return res.text() // "No of analyst: N"
}

// ─────────────────────────────────────────────────────────────────────────────
// 6. Get All Analysts  →  GET /admin/get/analyst  (ADMIN only)
// ─────────────────────────────────────────────────────────────────────────────
export async function getAnalysts() {
  const res = await fetch(`${BASE_URL}/admin/get/analyst`, {
    method: 'GET',
    headers: authHeaders(),
  })

  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || 'Failed to fetch analysts')
  }

  return res.json() // [{ username, companyName, email, role }, ...]
}
