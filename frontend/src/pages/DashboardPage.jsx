import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { createAnalyst, getAnalystCount, getAnalysts } from '../api/auth'
import Navbar from '../components/Navbar'
import Button from '../components/Button'
import Input from '../components/Input'
import { ToastContainer, useToast } from '../components/Toast'
import styles from './DashboardPage.module.css'

/* ─── Shared: Self Info Card ─────────────────────────────────────── */
function SelfInfoCard({ user }) {
  const roleIsAdmin = user?.role === 'ADMIN'

  const fields = [
    {
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" /><circle cx="12" cy="7" r="4" />
        </svg>
      ),
      label: 'Username',
      value: user?.username,
    },
    {
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" /><polyline points="22,6 12,13 2,6" />
        </svg>
      ),
      label: 'Email',
      value: user?.email,
    },
    {
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" /><polyline points="9 22 9 12 15 12 15 22" />
        </svg>
      ),
      label: 'Company',
      value: user?.companyName,
    },
    {
      icon: (
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
        </svg>
      ),
      label: 'Role',
      value: user?.role,
      isRole: true,
    },
  ]

  return (
    <div className={styles.selfInfoCard}>
      {/* Avatar header */}
      <div className={styles.avatarSection}>
        <div className={[styles.bigAvatar, roleIsAdmin ? styles.avatarAdmin : styles.avatarAnalyst].join(' ')}>
          {user?.username?.[0]?.toUpperCase()}
        </div>
        <div className={styles.avatarDetails}>
          <h2 className={styles.avatarName}>{user?.username}</h2>
          <p className={styles.avatarCompany}>{user?.companyName}</p>
        </div>
        <span className={[styles.rolePill, roleIsAdmin ? styles.pillAdmin : styles.pillAnalyst].join(' ')}>
          {roleIsAdmin ? '👑 Admin' : '📊 Analyst'}
        </span>
      </div>

      {/* Fields */}
      <div className={styles.infoGrid}>
        {fields.map((f) => (
          <div key={f.label} className={styles.infoField}>
            <div className={styles.infoFieldIcon}>{f.icon}</div>
            <div className={styles.infoFieldBody}>
              <span className={styles.infoFieldLabel}>{f.label}</span>
              {f.isRole ? (
                <span className={[styles.infoFieldValue, styles.roleValue, roleIsAdmin ? styles.roleAdmin : styles.roleAnalyst].join(' ')}>
                  {f.value}
                </span>
              ) : (
                <span className={styles.infoFieldValue}>{f.value}</span>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

/* ─── Admin: Analyst Profile Card ───────────────────────────────── */
function AnalystProfileCard({ analyst }) {
  return (
    <div className={styles.analystProfileCard}>
      <div className={styles.analystProfileAvatar}>
        {analyst.username?.[0]?.toUpperCase()}
      </div>
      <div className={styles.analystProfileInfo}>
        <span className={styles.analystProfileName}>{analyst.username}</span>
        <span className={styles.analystProfileEmail}>{analyst.email}</span>
        <span className={styles.analystProfileRole}>
          <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2z" />
            <path d="M15 5v14a2 2 0 002 2h2a2 2 0 002-2V5a2 2 0 00-2-2h-2a2 2 0 00-2 2z" />
          </svg>
          Analyst
        </span>
      </div>
    </div>
  )
}

/* ─── Admin: Analyst Team Panel ─────────────────────────────────── */
function AnalystTeamPanel({ refreshTrigger }) {
  const [analysts, setAnalysts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    setLoading(true)
    getAnalysts()
      .then(setAnalysts)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [refreshTrigger])

  return (
    <div className={styles.teamPanel}>
      <div className={styles.panelHeader}>
        <div className={styles.panelHeaderLeft}>
          <div className={styles.panelIcon}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="url(#tg)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
              <circle cx="9" cy="7" r="4" />
              <path d="M23 21v-2a4 4 0 00-3-3.87" />
              <path d="M16 3.13a4 4 0 010 7.75" />
              <defs>
                <linearGradient id="tg" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stopColor="#6c63ff" />
                  <stop offset="100%" stopColor="#00d4ff" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <div>
            <h3 className={styles.panelTitle}>Your Analyst Team</h3>
            <p className={styles.panelSubtitle}>All analysts registered under your enterprise</p>
          </div>
        </div>
        <span className={styles.teamCount}>{analysts.length} member{analysts.length !== 1 ? 's' : ''}</span>
      </div>

      {loading && (
        <div className={styles.teamLoading}>
          <div className={styles.loadingSpinner} />
          <span>Loading analysts…</span>
        </div>
      )}

      {!loading && error && (
        <div className={styles.teamError}>{error}</div>
      )}

      {!loading && !error && analysts.length === 0 && (
        <div className={styles.teamEmpty}>
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" style={{ opacity: 0.3 }}>
            <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" />
            <circle cx="9" cy="7" r="4" />
          </svg>
          <p>No analysts added yet. Create one to get started.</p>
        </div>
      )}

      {!loading && !error && analysts.length > 0 && (
        <div className={styles.analystProfileGrid}>
          {analysts.map((a) => (
            <AnalystProfileCard key={a.username} analyst={a} />
          ))}
        </div>
      )}
    </div>
  )
}

/* ─── Admin: Create Analyst Panel ────────────────────────────────── */
function CreateAnalystPanel({ onSuccess }) {
  const { toasts, addToast, removeToast } = useToast()
  const [form, setForm] = useState({ name: '', email: '', password: '' })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)
  const [showPass, setShowPass] = useState(false)

  const handleChange = (e) => {
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }))
    setErrors((err) => ({ ...err, [e.target.name]: '' }))
  }

  const validate = () => {
    const errs = {}
    if (!form.name.trim()) errs.name = 'Username is required'
    if (!form.email.trim()) errs.email = 'Email is required'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) errs.email = 'Invalid email'
    if (!form.password) errs.password = 'Password is required'
    else if (form.password.length < 6) errs.password = 'Minimum 6 characters'
    return errs
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const errs = validate()
    if (Object.keys(errs).length) { setErrors(errs); return }

    setLoading(true)
    try {
      await createAnalyst(form)
      addToast(`Analyst "${form.name}" created successfully!`, 'success')
      setForm({ name: '', email: '', password: '' })
      onSuccess?.()
    } catch (err) {
      addToast(err.message || 'Failed to create analyst', 'error')
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <div className={styles.analystPanel}>
        <div className={styles.panelHeader}>
          <div className={styles.panelHeaderLeft}>
            <div className={styles.panelIcon}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="url(#pg)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M16 21v-2a4 4 0 00-4-4H6a4 4 0 00-4 4v2" /><circle cx="9" cy="7" r="4" />
                <line x1="19" y1="8" x2="19" y2="14" /><line x1="22" y1="11" x2="16" y2="11" />
                <defs>
                  <linearGradient id="pg" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" stopColor="#6c63ff" />
                    <stop offset="100%" stopColor="#00d4ff" />
                  </linearGradient>
                </defs>
              </svg>
            </div>
            <div>
              <h3 className={styles.panelTitle}>Add New Analyst</h3>
              <p className={styles.panelSubtitle}>Create an analyst account for your enterprise</p>
            </div>
          </div>
          <span className={styles.adminOnlyBadge}>Admin Only</span>
        </div>

        <form onSubmit={handleSubmit} className={styles.analystForm} id="create-analyst-form">
          <div className={styles.formRow}>
            <Input
              id="analyst-name-input"
              name="name"
              label="Username"
              placeholder="analyst_username"
              value={form.name}
              onChange={handleChange}
              error={errors.name}
              icon={
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" /><circle cx="12" cy="7" r="4" />
                </svg>
              }
            />
            <Input
              id="analyst-email-input"
              name="email"
              type="email"
              label="Email Address"
              placeholder="analyst@company.com"
              value={form.email}
              onChange={handleChange}
              error={errors.email}
              icon={
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" /><polyline points="22,6 12,13 2,6" />
                </svg>
              }
            />
          </div>

          <div className={styles.passwordRow}>
            <div style={{ position: 'relative', flex: 1 }}>
              <Input
                id="analyst-password-input"
                name="password"
                type={showPass ? 'text' : 'password'}
                label="Password"
                placeholder="Min. 6 characters"
                value={form.password}
                onChange={handleChange}
                error={errors.password}
                icon={
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" /><path d="M7 11V7a5 5 0 0110 0v4" />
                  </svg>
                }
              />
              <button type="button" className={styles.togglePass} onClick={() => setShowPass((s) => !s)} id="toggle-analyst-pass-btn">
                {showPass ? (
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94" /><path d="M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19" /><line x1="1" y1="1" x2="23" y2="23" />
                  </svg>
                ) : (
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" /><circle cx="12" cy="12" r="3" />
                  </svg>
                )}
              </button>
            </div>

            <div className={styles.submitWrapper}>
              <Button
                type="submit"
                variant="primary"
                size="md"
                loading={loading}
                id="create-analyst-submit-btn"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <line x1="12" y1="5" x2="12" y2="19" /><line x1="5" y1="12" x2="19" y2="12" />
                </svg>
                Add Analyst
              </Button>
            </div>
          </div>
        </form>
      </div>
      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </>
  )
}

/* ─── Analyst: Analytics Link State ──────────────────────────────── */
function AnalystAnalyticsLink() {
  return (
    <div className={styles.emptyState}>
      <div className={styles.emptyIcon}>
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="url(#eg)" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
          <path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2z"/>
          <path d="M15 5v14a2 2 0 002 2h2a2 2 0 002-2V5a2 2 0 00-2-2h-2a2 2 0 00-2 2z"/>
          <path d="M9 10V7a2 2 0 012-2h2a2 2 0 012 2v3"/>
          <defs>
            <linearGradient id="eg" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#6c63ff"/>
              <stop offset="100%" stopColor="#00d4ff"/>
            </linearGradient>
          </defs>
        </svg>
      </div>
      <h3 className={styles.emptyTitle}>Analytics Dashboard</h3>
      <p className={styles.emptyDesc}>
        View aggregated cost and performance analytics for your company, or compare performance across agents.
      </p>
      <div style={{ display: 'flex', gap: '12px', justifyContent: 'center', flexWrap: 'wrap', marginTop: '16px' }}>
        <Link to="/analytics" style={{
          display: 'inline-block',
          background: 'rgba(79, 70, 229, 0.1)',
          color: '#818cf8',
          padding: '10px 20px',
          borderRadius: '8px',
          textDecoration: 'none',
          fontWeight: '600',
          border: '1px solid rgba(79, 70, 229, 0.2)',
          transition: 'all 0.2s'
        }}
        onMouseOver={(e) => {
          e.target.style.background = 'rgba(79, 70, 229, 0.2)'
          e.target.style.color = '#fff'
        }}
        onMouseOut={(e) => {
          e.target.style.background = 'rgba(79, 70, 229, 0.1)'
          e.target.style.color = '#818cf8'
        }}
        >
          Company Analytics &rarr;
        </Link>
        <Link to="/agents-compare" style={{
          display: 'inline-block',
          background: 'rgba(16, 185, 129, 0.1)',
          color: '#34d399',
          padding: '10px 20px',
          borderRadius: '8px',
          textDecoration: 'none',
          fontWeight: '600',
          border: '1px solid rgba(16, 185, 129, 0.2)',
          transition: 'all 0.2s'
        }}
        onMouseOver={(e) => {
          e.target.style.background = 'rgba(16, 185, 129, 0.2)'
          e.target.style.color = '#fff'
        }}
        onMouseOut={(e) => {
          e.target.style.background = 'rgba(16, 185, 129, 0.1)'
          e.target.style.color = '#34d399'
        }}
        >
          Agent Comparison &rarr;
        </Link>
      </div>
    </div>
  )
}

/* ─── Main Dashboard Page ────────────────────────────────────────── */
export default function DashboardPage() {
  const { user, isAdmin, isAnalyst } = useAuth()
  const { toasts, addToast, removeToast } = useToast()
  const [analystCount, setAnalystCount] = useState('—')
  const [teamRefresh, setTeamRefresh] = useState(0)

  // Fetch live analyst count on mount (admin only)
  useEffect(() => {
    if (!isAdmin) return
    getAnalystCount()
      .then((text) => {
        // Response is "No of analyst: N" — extract the number
        const match = text.match(/\d+/)
        setAnalystCount(match ? match[0] : text)
      })
      .catch(() => setAnalystCount('—'))
  }, [isAdmin, teamRefresh])

  const handleAnalystCreated = () => {
    setTeamRefresh((r) => r + 1)
  }

  return (
    <div className={styles.page}>
      <div className="bg-orb bg-orb-1" style={{ opacity: 0.07 }} />
      <div className="bg-orb bg-orb-2" style={{ opacity: 0.07 }} />

      <Navbar />

      <main className={styles.main}>
        {/* Page heading */}
        <div className={styles.pageHeading}>
          <div>
            <h1 className={styles.pageTitle}>
              {isAdmin ? (
                <>Admin <span className="gradient-text">Dashboard</span></>
              ) : (
                <>My <span className="gradient-text">Dashboard</span></>
              )}
            </h1>
            <p className={styles.pageDesc}>
              {isAdmin
                ? 'Manage your enterprise, create analysts, and monitor cloud cost intelligence.'
                : 'View your account details and access cloud analytics.'}
            </p>
          </div>

          {/* Live indicator */}
          <div className={styles.liveIndicator}>
            <span className={styles.liveDot} />
            <span className={styles.liveText}>System Online</span>
          </div>
        </div>

        {/* ── ADMIN layout ── */}
        {isAdmin && (
          <div className={styles.adminLayout}>
            {/* Stats bar */}
            <div className={styles.statsBar}>
              {[
                { label: 'Role', value: 'Administrator', accent: 'purple' },
                { label: 'Company', value: user?.companyName, accent: 'cyan' },
                { label: 'Analysts', value: analystCount, accent: 'green' },
                { label: 'Status', value: 'Active', accent: 'orange' },
              ].map((s) => (
                <div key={s.label} className={[styles.statCard, styles[`statCard_${s.accent}`]].join(' ')}>
                  <span className={styles.statCardValue}>{s.value}</span>
                  <span className={styles.statCardLabel}>{s.label}</span>
                </div>
              ))}
            </div>

            {/* Two-column: Self info + Create analyst */}
            <div className={styles.adminColumns}>
              <div className={styles.sectionWrapper}>
                <div className={styles.sectionLabel}>
                  <span className={styles.sectionDot} style={{ background: 'var(--accent-primary)' }} />
                  Account Information
                </div>
                <SelfInfoCard user={user} />
              </div>

              <div className={styles.sectionWrapper}>
                <div className={styles.sectionLabel}>
                  <span className={styles.sectionDot} style={{ background: 'var(--accent-secondary)' }} />
                  Team Management
                </div>
                <CreateAnalystPanel onSuccess={handleAnalystCreated} />
              </div>
            </div>

            {/* Full-width: Analyst team profiles */}
            <div className={styles.sectionWrapper} style={{ marginTop: '2rem' }}>
              <div className={styles.sectionLabel}>
                <span className={styles.sectionDot} style={{ background: 'var(--accent-green)' }} />
                Analyst Profiles
              </div>
              <AnalystTeamPanel refreshTrigger={teamRefresh} />
            </div>
          </div>
        )}

        {/* ── ANALYST layout ── */}
        {isAnalyst && (
          <div className={styles.analystLayout}>
            {/* Stats bar */}
            <div className={styles.statsBar}>
              {[
                { label: 'Role', value: 'Analyst', accent: 'cyan' },
                { label: 'Company', value: user?.companyName, accent: 'purple' },
                { label: 'Email', value: user?.email, accent: 'green' },
                { label: 'Status', value: 'Active', accent: 'orange' },
              ].map((s) => (
                <div key={s.label} className={[styles.statCard, styles[`statCard_${s.accent}`]].join(' ')}>
                  <span className={styles.statCardValue}>{s.value}</span>
                  <span className={styles.statCardLabel}>{s.label}</span>
                </div>
              ))}
            </div>

            {/* Two-column: Self info + empty analytics */}
            <div className={styles.analystColumns}>
              <div className={styles.sectionWrapper}>
                <div className={styles.sectionLabel}>
                  <span className={styles.sectionDot} style={{ background: 'var(--accent-secondary)' }} />
                  Account Information
                </div>
                <SelfInfoCard user={user} />
              </div>

              <div className={styles.sectionWrapper}>
                <div className={styles.sectionLabel}>
                  <span className={styles.sectionDot} style={{ background: 'var(--accent-green)' }} />
                  Analytics Overview
                </div>
                <AnalystAnalyticsLink />
              </div>
            </div>
          </div>
        )}
      </main>

      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </div>
  )
}
