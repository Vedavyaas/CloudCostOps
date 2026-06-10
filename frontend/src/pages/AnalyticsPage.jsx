import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getCompanyAnalyticsSummary } from '../api/analytics'
import Navbar from '../components/Navbar'
import styles from './AnalyticsPage.module.css'

/* ── Helpers ──────────────────────────────────────────────────────────────── */
const fmt  = (n, d = 2) => (n ?? 0).toFixed(d)
const fmtD = (n, d = 2) => `$${fmt(n, d)}`

const DEFAULTS = { cpuCost: 0.05, memCost: 0.01, diskCost: 0.008, netCost: 0.002 }

/* ═══════════════════════ MAIN PAGE ══════════════════════════════════════════ */
export default function AnalyticsPage() {
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
    getCompanyAnalyticsSummary(w)
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

  return (
    <div className={styles.page}>
      <div className="bg-orb bg-orb-1" />
      <div className="bg-orb bg-orb-2" />
      <div className="bg-orb bg-orb-3" />

      <Navbar />

      <main className={styles.main}>

        {/* ── Header ── */}
        <div className={styles.pageHeading}>
          <div className={styles.headingLeft}>
            <div className={styles.breadcrumb}>
              <Link to="/dashboard">Dashboard</Link>
              <span className={styles.breadcrumbSep}>/</span>
              <span>Analytics</span>
            </div>
            <h1 className={styles.pageTitle}>
              {data?.companyName
                ? `${data.companyName.charAt(0).toUpperCase() + data.companyName.slice(1)} Analytics`
                : 'Cloud Analytics'}
            </h1>
            <p className={styles.pageDesc}>
              {loading ? 'Loading…' : `${data?.totalSamples?.toLocaleString() ?? '—'} samples processed`}
            </p>
          </div>
          <Link to="/dashboard" className={styles.backButton}>← Dashboard</Link>
        </div>

        {/* ── Cost config (collapsible) ── */}
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
            Processing {user?.companyName} metrics…
          </div>
        )}

        {error && <div className={styles.errorState}>Error: {error}</div>}

        {data && !loading && !error && (
          <>
            {/* ── Cost ── */}
            <div className={styles.sectionLabel}>
              <span className={styles.sectionDot} style={{ background: 'var(--tahoe-mint)' }} />
              Cost
            </div>
            <div className={styles.kpiGrid}>
              <div className={[styles.kpiCard, styles.green].join(' ')}>
                <div className={styles.kpiLabel}><span className={styles.kpiDot} style={{ background: 'var(--tahoe-mint)' }} />Today</div>
                <div className={styles.kpiValue}>{fmtD(data.todayCost)}</div>
                <div className={styles.kpiSub}>UTC calendar day</div>
              </div>
              <div className={[styles.kpiCard, styles.blue].join(' ')}>
                <div className={styles.kpiLabel}><span className={styles.kpiDot} style={{ background: 'var(--tahoe-blue)' }} />Current Month</div>
                <div className={styles.kpiValue}>{fmtD(data.currentMonthCost)}</div>
                <div className={styles.kpiSub}>Month-to-date</div>
                {data.monthOverMonthChangePercent != null && (
                  <div className={[styles.kpiChange, data.monthOverMonthChangePercent >= 0 ? styles.up : styles.down].join(' ')}>
                    {data.monthOverMonthChangePercent >= 0 ? '↑' : '↓'} {Math.abs(data.monthOverMonthChangePercent).toFixed(1)}% vs prev month
                  </div>
                )}
              </div>
              <div className={[styles.kpiCard, styles.purple].join(' ')}>
                <div className={styles.kpiLabel}><span className={styles.kpiDot} style={{ background: 'var(--tahoe-violet)' }} />Previous Month</div>
                <div className={styles.kpiValue}>{fmtD(data.previousMonthCost)}</div>
                <div className={styles.kpiSub}>Last full month</div>
              </div>
              <div className={[styles.kpiCard, styles.cyan].join(' ')}>
                <div className={styles.kpiLabel}><span className={styles.kpiDot} style={{ background: 'var(--tahoe-cyan)' }} />All-Time Total</div>
                <div className={styles.kpiValue}>{fmtD(data.totalAllTimeCost, 0)}</div>
                <div className={styles.kpiSub}>{data.totalSamples?.toLocaleString()} samples</div>
              </div>
            </div>

            {/* ── Infrastructure ── */}
            <div className={styles.sectionLabel}>
              <span className={styles.sectionDot} style={{ background: 'var(--accent-primary)' }} />
              Infrastructure
            </div>
            <div className={styles.kpiGrid}>
              <div className={[styles.kpiCard, styles.blue].join(' ')}>
                <div className={styles.kpiLabel}>Avg CPU</div>
                <div className={styles.kpiValue}>{fmt(data.avgCpuPercent)}%</div>
                <div className={styles.kpiSub}>Peak {fmt(data.peakCpuPercent)}%</div>
              </div>
              <div className={[styles.kpiCard, styles.purple].join(' ')}>
                <div className={styles.kpiLabel}>Avg Memory</div>
                <div className={styles.kpiValue}>{fmt(data.avgMemPercent)}%</div>
                <div className={styles.kpiSub}>{fmt(data.avgMemUsedGB, 1)} GB used avg · Peak {fmt(data.peakMemPercent)}%</div>
              </div>
              <div className={[styles.kpiCard, styles.orange].join(' ')}>
                <div className={styles.kpiLabel}>Avg Disk Usage</div>
                <div className={styles.kpiValue}>{fmt(data.avgDiskUsagePercent)}%</div>
                <div className={styles.kpiSub}>{fmt(data.avgStorageUsedGB, 1)} GB storage · R {fmt(data.avgDiskReadMBps)} / W {fmt(data.avgDiskWriteMBps)} MB/s</div>
              </div>
              <div className={[styles.kpiCard, styles.cyan].join(' ')}>
                <div className={styles.kpiLabel}>Network</div>
                <div className={styles.kpiValue}>{fmt(data.avgNetworkInMBps, 1)} MB/s</div>
                <div className={styles.kpiSub}>In · Out {fmt(data.avgNetworkOutMBps, 1)} MB/s · {Math.round(data.avgActiveConnections)} conn</div>
              </div>
            </div>

            {/* ── Application ── */}
            <div className={styles.sectionLabel}>
              <span className={styles.sectionDot} style={{ background: 'var(--tahoe-mint)' }} />
              Application
            </div>
            <div className={styles.kpiGrid}>
              <div className={[styles.kpiCard, styles.green].join(' ')}>
                <div className={styles.kpiLabel}>Requests / min</div>
                <div className={styles.kpiValue}>{Math.round(data.avgRequestsPerMin).toLocaleString()}</div>
                <div className={styles.kpiSub}>avg RPM</div>
              </div>
              <div className={[styles.kpiCard, data.avgErrorRatePercent > 2 ? styles.rose : styles.green].join(' ')}>
                <div className={styles.kpiLabel}>Error Rate</div>
                <div className={styles.kpiValue}>{fmt(data.avgErrorRatePercent)}%</div>
                <div className={styles.kpiSub}>{data.avgErrorRatePercent > 2 ? '⚠ Elevated' : '✓ Healthy'}</div>
              </div>
              <div className={[styles.kpiCard, styles.orange].join(' ')}>
                <div className={styles.kpiLabel}>Response Time</div>
                <div className={styles.kpiValue}>{fmt(data.avgResponseTimeMs, 0)} ms</div>
                <div className={styles.kpiSub}>avg p50 latency</div>
              </div>
              <div className={[styles.kpiCard, styles.blue].join(' ')}>
                <div className={styles.kpiLabel}>DB Query Time</div>
                <div className={styles.kpiValue}>{fmt(data.avgDbQueryTimeMs, 0)} ms</div>
                <div className={styles.kpiSub}>Cache hit {fmt(data.avgCacheHitRatio * 100)}% · {fmt(data.avgDbQueriesPerSec, 1)} QPS</div>
              </div>
            </div>

            {/* ── Cost breakdown table ── */}
            {data.costBreakdownPercent && (
              <>
                <div className={styles.sectionLabel}>
                  <span className={styles.sectionDot} style={{ background: 'var(--text-muted)' }} />
                  Cost Breakdown
                </div>
                <div className={styles.panel}>
                  <table className={styles.statTable}>
                    <thead>
                      <tr className={styles.monthTableHead}>
                        <td>Resource</td><td>Share</td>
                      </tr>
                    </thead>
                    <tbody>
                      {Object.entries(data.costBreakdownPercent).map(([k, v]) => (
                        <tr key={k}>
                          <td style={{ color: 'var(--text-primary)', fontWeight: 600 }}>{k}</td>
                          <td>{v}%</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </>
            )}

            {/* ── Monthly summary table ── */}
            <div className={styles.sectionLabel}>
              <span className={styles.sectionDot} style={{ background: 'var(--text-muted)' }} />
              Monthly Summary
            </div>
            <div className={styles.panel}>
              <table className={styles.statTable}>
                <thead>
                  <tr className={styles.monthTableHead}>
                    <td>Month</td><td>Cost ($)</td><td>CPU Avg</td><td>Mem Avg</td><td>Samples</td>
                  </tr>
                </thead>
                <tbody>
                  {[...(data.monthlySnapshots ?? [])].reverse().map(m => (
                    <tr key={m.month}>
                      <td style={{ color: 'var(--text-primary)', fontWeight: 600 }}>{m.month}</td>
                      <td>${fmt(m.cost)}</td>
                      <td>{fmt(m.cpuAvg)}%</td>
                      <td>{fmt(m.memAvg)}%</td>
                      <td>{m.samples}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </>
        )}
      </main>
    </div>
  )
}
