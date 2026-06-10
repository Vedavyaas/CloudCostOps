import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getAgentComparisonAnalytics } from '../api/agents'
import Navbar from '../components/Navbar'
import styles from './AnalyticsPage.module.css'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'

const fmt  = (n, d = 2) => (n ?? 0).toFixed(d)
const fmtD = (n, d = 2) => `$${fmt(n, d)}`
const DEFAULTS = { cpuCost: 0.05, memCost: 0.01, diskCost: 0.008, netCost: 0.002 }

const getStringColor = (str) => {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  return `hsl(${Math.abs(hash % 360)}, 70%, 60%)`;
}

export default function AgentComparisonPage() {
  const { user } = useAuth()
  const [data, setData]       = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError]     = useState(null)
  const [weights, setWeights] = useState(DEFAULTS)
  const [configOpen, setConfigOpen] = useState(false)
  const [localWeights, setLocalWeights] = useState(DEFAULTS)

  const fetchData = useCallback((w = DEFAULTS) => {
    setLoading(true)
    setError(null)
    getAgentComparisonAnalytics(w)
      .then(setData)
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => { fetchData(DEFAULTS) }, [])   // eslint-disable-line

  const handleApply = () => {
    setWeights(localWeights)
    fetchData(localWeights)
  }
  const handleReset = () => {
    setLocalWeights(DEFAULTS)
    setWeights(DEFAULTS)
    fetchData(DEFAULTS)
  }

  const graphDataMap = {}
  if (data) {
    data.forEach(agent => {
      (agent.monthlySnapshots || []).forEach(snap => {
        if (!graphDataMap[snap.month]) {
          graphDataMap[snap.month] = { month: snap.month }
        }
        graphDataMap[snap.month][agent.companyName] = snap.cost
      })
    })
  }
  const graphData = Object.values(graphDataMap).sort((a, b) => a.month.localeCompare(b.month))
  const agentNames = data ? data.map(a => a.companyName) : []

  return (
    <div className={styles.page}>
      <div className="bg-orb bg-orb-1" />
      <div className="bg-orb bg-orb-2" />
      <div className="bg-orb bg-orb-3" />

      <Navbar />

      <main className={styles.main}>
        {/* Header */}
        <div className={styles.pageHeading}>
          <div className={styles.headingLeft}>
            <div className={styles.breadcrumb}>
              <Link to="/dashboard">Dashboard</Link>
              <span className={styles.breadcrumbSep}>/</span>
              <span>Agent Comparison</span>
            </div>
            <h1 className={styles.pageTitle}>Agent Comparison</h1>
            <p className={styles.pageDesc}>Compare cost and performance across different agents.</p>
          </div>
          <Link to="/dashboard" className={styles.backButton}>← Dashboard</Link>
        </div>

        {/* Cost Config Banner */}
        <div className={styles.configBanner}>
          <div className={styles.configBannerHeader} onClick={() => setConfigOpen(o => !o)}>
            <div className={styles.configBannerLeft}>
              <div className={styles.configBannerIcon}>⚙️</div>
              <div>
                <div className={styles.configBannerTitle}>Cost Weight Configuration</div>
                <div className={styles.configBannerSub}>
                  CPU: ${localWeights.cpuCost}/% · Mem: ${localWeights.memCost}/GB · Disk: ${localWeights.diskCost}/GB · Net: ${localWeights.netCost}/MB
                </div>
              </div>
            </div>
            <svg className={[styles.configBannerChevron, configOpen ? styles.open : ''].join(' ')}
              width="18" height="18" viewBox="0 0 24 24" fill="none"
              stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <polyline points="6 9 12 15 18 9" />
            </svg>
          </div>
          {configOpen && (
            <div className={styles.configBody}>
              <div className={styles.configGrid}>
                {[
                  { key: 'cpuCost',  label: 'CPU ($ / 1% usage)',      hint: 'default $0.05' },
                  { key: 'memCost',  label: 'Memory ($ / GB used)',     hint: 'default $0.01' },
                  { key: 'diskCost', label: 'Storage ($ / GB)',         hint: 'default $0.008' },
                  { key: 'netCost',  label: 'Network ($ / MB in+out)',  hint: 'default $0.002' },
                ].map(({ key, label, hint }) => (
                  <div key={key} className={styles.configField}>
                    <label className={styles.configLabel}>{label}</label>
                    <input type="number" step="0.001" min="0"
                      className={styles.configInput}
                      value={localWeights[key] ?? ''}
                      onChange={e => setLocalWeights(p => ({ ...p, [key]: parseFloat(e.target.value) || 0 }))}
                    />
                    <span className={styles.configInputHint}>{hint}</span>
                  </div>
                ))}
              </div>
              <div className={styles.configActions}>
                <button className={styles.configApplyBtn} onClick={handleApply} disabled={loading}>
                  {loading ? 'Recalculating…' : '↺ Apply & Recalculate'}
                </button>
                <button className={styles.configResetBtn} onClick={handleReset}>Reset</button>
              </div>
            </div>
          )}
        </div>

        {loading && (
          <div className={styles.loadingState}>
            <div className={styles.spinner} />
            Processing comparisons…
          </div>
        )}

        {error && <div className={styles.errorState}>Error: {error}</div>}

        {data && !loading && !error && (
          <>
            {/* ── Summary Table ── */}
            <div className={styles.sectionLabel}>
              <span className={styles.sectionDot} style={{ background: 'var(--accent-primary)' }} />
              Agent Summary
            </div>
            <div className={styles.panel}>
              <table className={styles.statTable}>
                <thead>
                  <tr className={styles.monthTableHead}>
                    <td>Agent ID</td>
                    <td>Total Cost</td>
                    <td>Current Month Cost</td>
                    <td>Avg CPU</td>
                    <td>Avg Mem</td>
                  </tr>
                </thead>
                <tbody>
                  {data.map(agent => (
                    <tr key={agent.companyName}>
                      <td style={{ color: 'var(--text-primary)', fontWeight: 600 }}>{agent.companyName}</td>
                      <td>{fmtD(agent.totalAllTimeCost)}</td>
                      <td>{fmtD(agent.currentMonthCost)}</td>
                      <td>{fmt(agent.avgCpuPercent)}%</td>
                      <td>{fmt(agent.avgMemPercent)}%</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Monthly Trend Comparison Graph */}
            <div className={styles.sectionLabel} style={{ marginTop: '2rem' }}>
              <span className={styles.sectionDot} style={{ background: 'var(--tahoe-mint)' }} />
              Month-over-Month Cost Comparison
            </div>
            <div className={styles.panel} style={{ height: '400px', padding: '1rem', marginBottom: '1.5rem', overflowX: 'auto', overflowY: 'hidden' }}>
              {graphData.length > 0 ? (
                <div style={{ minWidth: `${Math.max(800, graphData.length * 100)}px`, height: '100%' }}>
                  <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={graphData} margin={{ top: 20, right: 30, left: 60, bottom: 20 }}>
                      <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.06)" />
                      <XAxis dataKey="month" stroke="#6b7280" dy={10} tick={{ fill: '#6b7280', fontSize: 12 }} />
                      <YAxis stroke="#6b7280" tickFormatter={val => `$${val}`} dx={-10} tick={{ fill: '#6b7280', fontSize: 12 }} />
                      <Tooltip 
                        contentStyle={{ backgroundColor: '#ffffff', border: '1px solid rgba(0,0,0,0.1)', borderRadius: '8px', boxShadow: '0 4px 12px rgba(0,0,0,0.08)' }}
                        itemStyle={{ color: '#1e293b', fontWeight: 600 }}
                        formatter={(value, name) => [`$${value.toFixed(2)}`, name]}
                      />
                      <Legend wrapperStyle={{ paddingTop: '20px' }} />
                      {agentNames.map((name) => (
                        <Line 
                          key={name}
                          type="monotone"
                          dataKey={name} 
                          name={name}
                          stroke={getStringColor(name)} 
                          strokeWidth={3}
                          activeDot={{ r: 8, stroke: '#fff', strokeWidth: 2 }}
                        />
                      ))}
                    </LineChart>
                  </ResponsiveContainer>
                </div>
              ) : (
                <div style={{ color: 'rgba(255,255,255,0.5)', textAlign: 'center', marginTop: '100px' }}>
                  No monthly data available for comparison.
                </div>
              )}
            </div>
          </>
        )}
      </main>
    </div>
  )
}
