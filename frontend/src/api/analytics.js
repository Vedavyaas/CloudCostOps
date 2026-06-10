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
