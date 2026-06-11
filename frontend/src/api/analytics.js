const BASE_URL = '';

const getToken = () => localStorage.getItem('cco_token');

const authHeaders = () => ({
  'Content-Type': 'application/json',
  Authorization: `Bearer ${getToken()}`,
});

export async function getAnalyticsLogs() {
  const res = await fetch(`${BASE_URL}/ORCHESTRATIONENGINE/get/logs`, {
    method: 'GET',
    headers: authHeaders(),
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || 'Failed to fetch analytics');
  }

  return res.json();
}

export async function getCompanyAnalyticsSummary(weights = {}) {
  const params = new URLSearchParams()
  if (weights.cpuCost  != null) params.set('cpuCost',  weights.cpuCost)
  if (weights.memCost  != null) params.set('memCost',  weights.memCost)
  if (weights.diskCost != null) params.set('diskCost', weights.diskCost)
  if (weights.netCost  != null) params.set('netCost',  weights.netCost)

  const qs  = params.toString() ? `?${params.toString()}` : ''
  const res = await fetch(`${BASE_URL}/ORCHESTRATIONENGINE/get/analytics/summary${qs}`, {
    method: 'GET',
    headers: authHeaders(),
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || 'Failed to fetch analytics summary');
  }

  return res.json();
}

/**
 * Upload a CSV budget report + chosen month to the backend.
 * The backend parses the CSV, fetches live audit data, computes all
 * variances, and returns a ready-to-render BudgetComparisonResultDto.
 *
 * @param {File}   csvFile     - the .csv File object from the file input
 * @param {string} reportMonth - e.g. "2026-06"
 * @returns {Promise<object>}  - BudgetComparisonResultDto JSON
 */
export async function compareBudgetWithAudit(csvFile, reportMonth) {
  const form = new FormData();
  form.append('file', csvFile);
  form.append('month', reportMonth);

  const res = await fetch(`${BASE_URL}/ORCHESTRATIONENGINE/get/analytics/budget-comparison`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${getToken()}` }, // no Content-Type — browser sets multipart boundary
    body: form,
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || 'Budget comparison request failed');
  }

  return res.json();
}
