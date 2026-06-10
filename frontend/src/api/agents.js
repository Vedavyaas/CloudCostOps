const BASE_URL = '';

const getToken = () => localStorage.getItem('cco_token');

const authHeaders = () => ({
  'Content-Type': 'application/json',
  Authorization: `Bearer ${getToken()}`,
});

export async function getAgentComparisonAnalytics(weights = {}) {
  const params = new URLSearchParams()
  if (weights.cpuCost  != null) params.set('cpuCost',  weights.cpuCost)
  if (weights.memCost  != null) params.set('memCost',  weights.memCost)
  if (weights.diskCost != null) params.set('diskCost', weights.diskCost)
  if (weights.netCost  != null) params.set('netCost',  weights.netCost)

  const qs  = params.toString() ? `?${params.toString()}` : ''
  const res = await fetch(`${BASE_URL}/ORCHESTRATIONENGINE/get/analytics/agents/compare${qs}`, {
    method: 'GET',
    headers: authHeaders(),
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || 'Failed to fetch agent comparisons');
  }

  return res.json();
}
