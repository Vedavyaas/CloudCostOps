import { useState, useRef, useCallback, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { compareBudgetWithAudit } from '../api/analytics'
import Navbar from '../components/Navbar'
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend,
  ResponsiveContainer, ReferenceLine, Cell,
} from 'recharts'
import styles from './BudgetComparisonPage.module.css'

/* ─── tiny helpers (display only, no business logic) ─────────────────── */
const fmtD = (n) => `$${Math.abs(n ?? 0).toFixed(2)}`
const fmtP = (n) => `${(n ?? 0).toFixed(1)}%`
const sign = (n) => (n >= 0 ? '+' : '')
const isPos = (n) => n >= 0

const MONTHS = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
]

/* ─── Custom tooltip ──────────────────────────────────────────────────── */
function ChartTip({ active, payload, label }) {
  if (!active || !payload?.length) return null
  return (
    <div className={styles.tip}>
      <div className={styles.tipTitle}>{label}</div>
      {payload.map((p, i) => (
        <div key={i} className={styles.tipRow}>
          <span className={styles.tipDot} style={{ background: p.color }} />
          <span className={styles.tipLabel}>{p.name}</span>
          <span className={styles.tipVal}>
            {String(p.name).includes('%') ? fmtP(p.value) : fmtD(p.value)}
          </span>
        </div>
      ))}
    </div>
  )
}

/* ─── Variance badge ──────────────────────────────────────────────────── */
function VBadge({ val, suffix = '' }) {
  const pos = isPos(val)
  return (
    <span className={[styles.badge, pos ? styles.badgeUp : styles.badgeDown].join(' ')}>
      {pos ? '▲' : '▼'} {Math.abs(val ?? 0).toFixed(2)}{suffix}
    </span>
  )
}

/* ═══════════════════════════════ PAGE ═══════════════════════════════════ */
export default function BudgetComparisonPage() {
  const now = new Date()
  const [month, setMonth] = useState(now.getMonth())
  const [year, setYear] = useState(now.getFullYear())
  const [csvFile, setCsvFile] = useState(null)
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [dragging, setDragging] = useState(false)
  const [tab, setTab] = useState('overview')
  const fileRef = useRef()

  /* ── Pre-load sample CSV on mount ─────────────────────────────────── */
  useEffect(() => {
    fetch('/sample_budget_report.csv')
      .then(r => r.ok ? r.blob() : null)
      .then(blob => {
        if (blob) setCsvFile(new File([blob], 'sample_budget_report.csv', { type: 'text/csv' }))
      })
      .catch(() => { }) // silently ignore if file not served
  }, [])

  /* ── file selection ────────────────────────────────────────────────── */
  const pickFile = useCallback((file) => {
    if (!file) return
    if (!file.name.toLowerCase().endsWith('.csv')) {
      setError('Please upload a .csv file')
      return
    }
    setError(null)
    setResult(null)
    setCsvFile(file)
  }, [])

  const onDrop = useCallback((e) => {
    e.preventDefault()
    setDragging(false)
    pickFile(e.dataTransfer.files[0])
  }, [pickFile])

  /* ── submit to backend ─────────────────────────────────────────────── */
  const handleCompare = useCallback(async () => {
    if (!csvFile) return
    setLoading(true)
    setError(null)
    setResult(null)
    const reportMonth = `${year}-${String(month + 1).padStart(2, '0')}`
    try {
      const data = await compareBudgetWithAudit(csvFile, reportMonth)
      setResult(data)
      setTab('overview')
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [csvFile, month, year])

  /* ── chart data (built straight from API result) ───────────────────── */
  const barData = result?.rows?.map(r => ({
    name: r.serviceCategory,
    'Budget': r.budgetAllocated,
    'CSV Actual': r.actualSpend,
    'Audit Actual': r.auditActualSpend,
    'Prior Month': r.priorMonthSpend,
  })) ?? []

  const varianceData = result?.rows?.map(r => ({
    name: r.serviceCategory,
    'Budget Variance': r.budgetVarianceDollar,
    'Audit vs CSV': r.auditVsCsvDollar,
  })) ?? []

  const momData = result?.rows?.map(r => ({
    name: r.serviceCategory,
    'CSV MoM ($)': r.momVarianceDollar,
    'Audit MoM ($)': r.auditMomVarianceDollar,
  })) ?? []

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
              <span className={styles.sep}>/</span>
              <span>Budget Comparison</span>
            </div>
            <h1 className={styles.pageTitle}>
              Budget vs <span className="gradient-text">Audit Report</span>
            </h1>
            <p className={styles.pageDesc}>
              Upload your CSV budget spreadsheet — the system compares every line against live audit metrics
            </p>
          </div>
          <Link to="/dashboard" className={styles.backBtn}>← Dashboard</Link>
        </div>

        {/* ── Controls ── */}
        <div className={styles.controlsCard}>
          {/* Month picker */}
          <div className={styles.pickerGroup}>
            <label className={styles.pickerLabel}>Report Month</label>
            <div className={styles.pickerRow}>
              <select id="month-select" className={styles.select}
                value={month} onChange={e => setMonth(+e.target.value)}>
                {MONTHS.map((m, i) => <option key={m} value={i}>{m}</option>)}
              </select>
              <select id="year-select" className={styles.select}
                value={year} onChange={e => setYear(+e.target.value)}>
                {[now.getFullYear() - 1, now.getFullYear()].map(y =>
                  <option key={y} value={y}>{y}</option>)}
              </select>
            </div>
          </div>

          <div className={styles.divider} />

          {/* Drop zone */}
          <div
            id="csv-drop-zone"
            className={[styles.dropZone, dragging ? styles.dropActive : ''].join(' ')}
            onDragOver={e => { e.preventDefault(); setDragging(true) }}
            onDragLeave={() => setDragging(false)}
            onDrop={onDrop}
            onClick={() => fileRef.current?.click()}
          >
            <input ref={fileRef} type="file" accept=".csv" id="csv-file-input"
              style={{ display: 'none' }} onChange={e => pickFile(e.target.files[0])} />

            <svg className={styles.dropIcon} width="26" height="26" viewBox="0 0 24 24"
              fill="none" stroke="currentColor" strokeWidth="1.8"
              strokeLinecap="round" strokeLinejoin="round">
              <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4" />
              <polyline points="17 8 12 3 7 8" />
              <line x1="12" y1="3" x2="12" y2="15" />
            </svg>

            {csvFile
              ? <div className={styles.dropInfo}>
                <span className={styles.dropName}>{csvFile.name}</span>
                <span className={styles.dropSub}>Click or drop to replace</span>
              </div>
              : <div className={styles.dropInfo}>
                <span className={styles.dropName}>Drop your CSV budget report here</span>
                <span className={styles.dropSub}>or click to browse · Service Category, Budget, Actual Spend, Prior Month Spend, MoM Variance</span>
              </div>
            }
            {csvFile && <span className={styles.loadedBadge}>✓ Ready</span>}
          </div>

          {/* Run button */}
          <button
            id="compare-btn"
            className={styles.compareBtn}
            onClick={handleCompare}
            disabled={!csvFile || loading}
          >
            {loading
              ? <><div className={styles.btnSpinner} /> Analysing…</>
              : <><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
              </svg> Compare with Audit</>
            }
          </button>
        </div>

        {/* ── Error ── */}
        {error && <div className={styles.errorBox}>⚠ {error}</div>}

        {/* ════════════════════ RESULTS ════════════════════════════════ */}
        {result && (
          <>
            {/* ── No data: stop here, show warning only ── */}
            {result.noDataForMonth ? (
              <div style={{
                display: 'flex', alignItems: 'flex-start', gap: '16px',
                background: 'rgba(217,119,6,0.07)',
                border: '1px solid rgba(217,119,6,0.28)',
                borderRadius: '14px', padding: '22px 24px',
              }}>
                <span style={{ fontSize: '28px', lineHeight: 1 }}>⚠️</span>
                <div>
                  <div style={{ fontSize: '15px', fontWeight: 700, color: 'var(--tahoe-amber)', marginBottom: '6px' }}>
                    No audit records found for <strong>{result.reportMonth}</strong>
                  </div>
                  <div style={{ fontSize: '13px', color: 'var(--text-muted)', lineHeight: 1.6 }}>
                    The database has no metrics ingested for this month. Please choose a month that has audit data,
                    or wait until records are collected for this period.
                  </div>
                  <div style={{ marginTop: '14px', fontSize: '12px', color: 'var(--text-muted)' }}>
                    Your CSV budget data was parsed successfully ({result.rowCount} rows) — but without audit records
                    there is nothing to compare against.
                  </div>
                </div>
              </div>
            ) : (
              <>
                {/* ── Data found confirmation ── */}
                <div style={{
                  display: 'flex', alignItems: 'center', gap: '8px',
                  background: 'rgba(5,150,105,0.06)',
                  border: '1px solid rgba(5,150,105,0.18)',
                  borderRadius: '10px', padding: '10px 16px',
                }}>
                  <span style={{ fontSize: '15px' }}>✅</span>
                  <span style={{ fontSize: '12px', fontWeight: 600, color: 'var(--tahoe-mint)' }}>
                    Comparing against <strong>{result.auditSampleCount} audit record{result.auditSampleCount !== 1 ? 's' : ''}</strong> recorded in <strong>{result.reportMonth}</strong>
                  </span>
                </div>

                {/* ── KPI summary strip ── */}
                <div className={styles.kpiStrip}>
                  {[
                    { icon: '📊', label: 'Total Budget', val: fmtD(result.totalBudgetAllocated), sub: `${result.rowCount} categories`, color: 'blue' },
                    { icon: '📄', label: 'CSV Actual Spend', val: fmtD(result.totalCsvActualSpend), sub: <VBadge val={result.totalBudgetVarianceDollar} suffix=" vs budget" />, color: result.totalBudgetVarianceDollar > 0 ? 'rose' : 'green' },
                    { icon: '🔍', label: `Audit Actual (${result.reportMonth})`, val: fmtD(result.totalAuditActualSpend), sub: <VBadge val={result.totalAuditVsCsvDollar} suffix=" vs CSV" />, color: 'purple' },
                    { icon: '📈', label: 'Audit MoM Change', val: `${sign(result.auditMonthOverMonthChangePercent)}${fmtP(result.auditMonthOverMonthChangePercent)}`, sub: `Prior: ${fmtD(result.totalAuditPriorMonthSpend)}`, color: result.auditMonthOverMonthChangePercent > 0 ? 'orange' : 'green' },
                    { icon: '🖥', label: 'Avg CPU (Audit)', val: fmtP(result.auditAvgCpuPercent), sub: `Mem: ${fmtP(result.auditAvgMemPercent)}`, color: 'cyan' },
                    { icon: '⚠', label: 'Error Rate (Audit)', val: fmtP(result.auditAvgErrorRatePercent), sub: result.auditAvgErrorRatePercent > 2 ? 'Elevated' : 'Healthy', color: result.auditAvgErrorRatePercent > 2 ? 'rose' : 'green' },
                  ].map((k, i) => (
                    <div key={i} className={[styles.kpiCard, styles[k.color]].join(' ')}>
                      <span className={styles.kpiIcon}>{k.icon}</span>
                      <span className={styles.kpiLabel}>{k.label}</span>
                      <span className={styles.kpiVal}>{k.val}</span>
                      <span className={styles.kpiSub}>{k.sub}</span>
                    </div>
                  ))}
                </div>

                {/* ── Tabs ── */}
                <div className={styles.tabs}>
                  {[
                    { id: 'overview', label: '📊 Spend Overview' },
                    { id: 'variance', label: '⚡ Variance Analysis' },
                    { id: 'table', label: '📋 Full Detail Table' },
                  ].map(t => (
                    <button key={t.id} id={`tab-${t.id}`}
                      className={[styles.tab, tab === t.id ? styles.tabActive : ''].join(' ')}
                      onClick={() => setTab(t.id)}>
                      {t.label}
                    </button>
                  ))}
                </div>


                {/* ══ OVERVIEW ══ */}
                {tab === 'overview' && (
                  <div className={styles.tabContent}>
                    <SectionLabel color="var(--accent-primary)">Budget · CSV Actual · Audit Actual by Category</SectionLabel>
                    <div className={styles.chartCard}>
                      <p className={styles.chartNote}>Side-by-side per service category — budget vs what the CSV reported vs what the audit system recorded</p>
                      <ResponsiveContainer width="100%" height={320}>
                        <BarChart data={barData} margin={{ top: 10, right: 20, left: 10, bottom: 20 }} barCategoryGap="30%">
                          <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.05)" />
                          <XAxis dataKey="name" tick={{ fill: '#64748b', fontSize: 11 }} dy={8} />
                          <YAxis tickFormatter={v => `$${v}`} tick={{ fill: '#64748b', fontSize: 11 }} />
                          <Tooltip content={<ChartTip />} />
                          <Legend wrapperStyle={{ paddingTop: '10px', fontSize: '12px' }} />
                          <Bar dataKey="Budget" fill="#6366f1" radius={[4, 4, 0, 0]} />
                          <Bar dataKey="CSV Actual" fill="#0891b2" radius={[4, 4, 0, 0]} />
                          <Bar dataKey="Audit Actual" fill="#059669" radius={[4, 4, 0, 0]} />
                          <Bar dataKey="Prior Month" fill="#d97706" radius={[4, 4, 0, 0]} opacity={0.6} />
                        </BarChart>
                      </ResponsiveContainer>
                    </div>

                    <SectionLabel color="var(--tahoe-mint)">Month-over-Month — CSV Report vs Audit System</SectionLabel>
                    <div className={styles.chartCard}>
                      <p className={styles.chartNote}>Compares the MoM variance as reported in the CSV spreadsheet against what the audit system computed</p>
                      <ResponsiveContainer width="100%" height={280}>
                        <BarChart data={momData} margin={{ top: 10, right: 20, left: 10, bottom: 20 }} barCategoryGap="35%">
                          <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.05)" />
                          <XAxis dataKey="name" tick={{ fill: '#64748b', fontSize: 11 }} dy={8} />
                          <YAxis tickFormatter={v => `$${v}`} tick={{ fill: '#64748b', fontSize: 11 }} />
                          <Tooltip content={<ChartTip />} />
                          <Legend wrapperStyle={{ paddingTop: '10px', fontSize: '12px' }} />
                          <ReferenceLine y={0} stroke="rgba(0,0,0,0.15)" strokeWidth={1.5} />
                          <Bar dataKey="CSV MoM ($)" fill="#0891b2" radius={[4, 4, 0, 0]}>
                            {momData.map((e, i) => <Cell key={i} fill={e['CSV MoM ($)'] >= 0 ? '#e11d48' : '#059669'} />)}
                          </Bar>
                          <Bar dataKey="Audit MoM ($)" fill="#6366f1" radius={[4, 4, 0, 0]}>
                            {momData.map((e, i) => <Cell key={i} fill={e['Audit MoM ($)'] >= 0 ? '#d97706' : '#6366f1'} />)}
                          </Bar>
                        </BarChart>
                      </ResponsiveContainer>
                    </div>
                  </div>
                )}

                {/* ══ VARIANCE ══ */}
                {tab === 'variance' && (
                  <div className={styles.tabContent}>
                    <SectionLabel color="var(--tahoe-rose)">Variance Waterfall — Budget Gap &amp; Audit vs CSV Gap</SectionLabel>
                    <div className={styles.chartCard}>
                      <p className={styles.chartNote}>
                        <strong style={{ color: '#e11d48' }}>Red/Green</strong> = actual spend vs budget ·&nbsp;
                        <strong style={{ color: '#d97706' }}>Amber/Indigo</strong> = audit system vs CSV actual
                      </p>
                      <ResponsiveContainer width="100%" height={320}>
                        <BarChart data={varianceData} margin={{ top: 10, right: 20, left: 10, bottom: 20 }} barCategoryGap="35%">
                          <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.05)" />
                          <XAxis dataKey="name" tick={{ fill: '#64748b', fontSize: 11 }} dy={8} />
                          <YAxis tickFormatter={v => `$${v}`} tick={{ fill: '#64748b', fontSize: 11 }} />
                          <Tooltip content={<ChartTip />} />
                          <Legend wrapperStyle={{ paddingTop: '10px', fontSize: '12px' }} />
                          <ReferenceLine y={0} stroke="rgba(0,0,0,0.18)" strokeWidth={2} />
                          <Bar dataKey="Budget Variance" radius={[4, 4, 0, 0]}>
                            {varianceData.map((e, i) => <Cell key={i} fill={e['Budget Variance'] >= 0 ? '#e11d48' : '#059669'} />)}
                          </Bar>
                          <Bar dataKey="Audit vs CSV" radius={[4, 4, 0, 0]}>
                            {varianceData.map((e, i) => <Cell key={i} fill={e['Audit vs CSV'] >= 0 ? '#d97706' : '#6366f1'} />)}
                          </Bar>
                        </BarChart>
                      </ResponsiveContainer>
                    </div>

                    {/* Per-category variance cards */}
                    <SectionLabel color="var(--tahoe-amber)">Per-Category Variance Detail</SectionLabel>
                    <div className={styles.varGrid}>
                      {result.rows.map((row, i) => (
                        <div key={i} className={styles.varCard}>
                          <div className={styles.varCat}>{row.serviceCategory}</div>
                          {row.matchedAuditCategory && (
                            <span className={styles.varAuditTag}>Audit: {row.matchedAuditCategory}</span>
                          )}
                          <div className={styles.varRow}>
                            <span className={styles.varLbl}>vs Budget</span>
                            <VBadge val={row.budgetVarianceDollar} />
                            <span className={styles.varPct}>({fmtP(row.budgetVariancePercent)})</span>
                          </div>
                          <div className={styles.varRow}>
                            <span className={styles.varLbl}>Audit vs CSV</span>
                            <VBadge val={row.auditVsCsvDollar} />
                            <span className={styles.varPct}>({fmtP(row.auditVsCsvPercent)})</span>
                          </div>
                          <div className={styles.varRow}>
                            <span className={styles.varLbl}>Audit util.</span>
                            <span className={styles.varUtil}>{fmtP(row.auditAvgUtilizationPercent)}</span>
                            <span className={styles.varPct}>({fmtP(row.auditCostSharePercent)} of cost)</span>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* ══ TABLE ══ */}
                {tab === 'table' && (
                  <div className={styles.tabContent}>
                    <SectionLabel color="var(--accent-secondary)">Full Row-by-Row Comparison</SectionLabel>
                    <div className={styles.tableWrap}>
                      <table className={styles.table} id="comparison-table">
                        <thead>
                          <tr>
                            {['Service / Cost Center', 'Budget', 'CSV Actual', 'Budget Variance',
                              'Prior Month (CSV)', 'MoM (CSV)', 'Audit Actual',
                              'Audit vs CSV', 'Audit MoM', 'Status'].map(h => (
                                <th key={h} className={styles.th}>{h}</th>
                              ))}
                          </tr>
                        </thead>
                        <tbody>
                          {result.rows.map((row, i) => {
                            const over = row.budgetVarianceDollar > 0
                            const gap = Math.abs(row.auditVsCsvPercent) > 15
                            const healthy = !over && !gap
                            return (
                              <tr key={i} className={styles.tr}>
                                <td className={styles.tdService}>
                                  <div>{row.serviceCategory}</div>
                                  {row.matchedAuditCategory && (
                                    <span className={styles.tdTag}>{row.matchedAuditCategory}</span>
                                  )}
                                </td>
                                <td className={styles.td}>{fmtD(row.budgetAllocated)}</td>
                                <td className={styles.td}>{fmtD(row.actualSpend)}</td>
                                <td className={styles.td}>
                                  <VBadge val={row.budgetVarianceDollar} />
                                  <div className={styles.tinyPct}>{sign(row.budgetVariancePercent)}{fmtP(row.budgetVariancePercent)}</div>
                                </td>
                                <td className={styles.td}>{fmtD(row.priorMonthSpend)}</td>
                                <td className={styles.td}>
                                  <VBadge val={row.momVarianceDollar} />
                                  <div className={styles.tinyPct}>{sign(row.momVariancePercent)}{fmtP(row.momVariancePercent)}</div>
                                </td>
                                <td className={[styles.td, styles.auditCol].join(' ')}>{fmtD(row.auditActualSpend)}</td>
                                <td className={styles.td}>
                                  <VBadge val={row.auditVsCsvDollar} />
                                  <div className={styles.tinyPct}>{sign(row.auditVsCsvPercent)}{fmtP(row.auditVsCsvPercent)}</div>
                                </td>
                                <td className={styles.td}>
                                  <VBadge val={row.auditMomVarianceDollar} />
                                  <div className={styles.tinyPct}>{sign(row.auditMomVariancePercent)}{fmtP(row.auditMomVariancePercent)}</div>
                                </td>
                                <td className={styles.td}>
                                  <span className={[styles.statusPill,
                                  healthy ? styles.statusOk :
                                    over ? styles.statusWarn :
                                      styles.statusInfo].join(' ')}>
                                    {healthy ? '✓ On Track' : over ? '⚠ Over Budget' : '↔ Audit Gap'}
                                  </span>
                                </td>
                              </tr>
                            )
                          })}
                        </tbody>
                        <tfoot>
                          <tr className={styles.tfootRow}>
                            <td className={styles.tfootLabel}>TOTAL</td>
                            <td className={styles.td}>{fmtD(result.totalBudgetAllocated)}</td>
                            <td className={styles.td}>{fmtD(result.totalCsvActualSpend)}</td>
                            <td className={styles.td}><VBadge val={result.totalBudgetVarianceDollar} /></td>
                            <td className={styles.td}>{fmtD(result.totalCsvPriorMonthSpend)}</td>
                            <td className={styles.td}><VBadge val={result.totalCsvMomVarianceDollar} /></td>
                            <td className={[styles.td, styles.auditCol].join(' ')}>{fmtD(result.totalAuditActualSpend)}</td>
                            <td className={styles.td}><VBadge val={result.totalAuditVsCsvDollar} /></td>
                            <td className={styles.td}><VBadge val={result.totalAuditMomVarianceDollar} /></td>
                            <td />
                          </tr>
                        </tfoot>
                      </table>
                    </div>
                  </div>
                )}
              </>
            )}
          </>
        )}
      </main>
    </div>
  )
}

/* ─── tiny section label helper ──────────────────────────────────────── */
function SectionLabel({ color, children }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: '7px',
      fontSize: '10px', fontWeight: 700, color: 'var(--text-muted)',
      textTransform: 'uppercase', letterSpacing: '0.09em', marginTop: '6px'
    }}>
      <span style={{ width: 5, height: 5, borderRadius: '50%', background: color, flexShrink: 0 }} />
      {children}
    </div>
  )
}
