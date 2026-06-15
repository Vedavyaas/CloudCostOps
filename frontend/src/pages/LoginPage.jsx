import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Button from '../components/Button'
import Input from '../components/Input'
import { ToastContainer, useToast } from '../components/Toast'
import styles from './LoginPage.module.css'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const { toasts, addToast, removeToast } = useToast()

  const [form, setForm] = useState({ username: 'amazonAnalyst', password: '123456' })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

  const handleChange = (e) => {
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }))
    setErrors((err) => ({ ...err, [e.target.name]: '' }))
  }

  const validate = () => {
    const errs = {}
    if (!form.username.trim()) errs.username = 'Username is required'
    if (!form.password) errs.password = 'Password is required'
    return errs
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const errs = validate()
    if (Object.keys(errs).length) { setErrors(errs); return }

    setLoading(true)
    try {
      const user = await login(form)
      addToast(`Welcome back, ${user.username}!`, 'success')
      setTimeout(() => navigate('/dashboard'), 600)
    } catch (err) {
      addToast(err.message || 'Invalid credentials', 'error')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={styles.page}>
      {/* Animated background */}
      <div className="bg-orb bg-orb-1" />
      <div className="bg-orb bg-orb-2" />
      <div className="bg-orb bg-orb-3" />

      {/* Grid pattern overlay */}
      <div className={styles.grid} />

      <div className={styles.container}>
        {/* Header */}
        <div className={styles.header}>
          <div className={styles.logoMark}>
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
              <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="url(#lg1)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
              <defs>
                <linearGradient id="lg1" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stopColor="#6c63ff" />
                  <stop offset="100%" stopColor="#00d4ff" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h1 className={styles.title}>
            Welcome back to{' '}
            <span className="gradient-text">CloudCostOps</span>
          </h1>
          <p className={styles.subtitle}>
            Sign in to your account to access your cloud cost intelligence dashboard
          </p>
        </div>

        {/* Card */}
        <div className={[styles.card, 'glass-card'].join(' ')}>

          {/* ── Test credentials banner ── */}
          <div style={{
            background: 'rgba(79, 70, 229, 0.07)',
            border: '1px solid rgba(79, 70, 229, 0.18)',
            borderRadius: '10px',
            padding: '10px 14px',
            marginBottom: '20px',
          }}>
            <div style={{ fontSize: '10px', fontWeight: 700, color: 'var(--accent-primary)', textTransform: 'uppercase', letterSpacing: '0.07em', marginBottom: '8px' }}>🔑 Test Credentials</div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              {/* Row 1: amazonAnalyst */}
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' }}>
                <span style={{ fontSize: '12px', color: 'var(--text-secondary)', minWidth: '120px' }}>
                  <span style={{ fontWeight: 600, color: 'var(--text-muted)' }}>Username: </span>
                  <code style={{ fontFamily: 'var(--font-mono)', fontWeight: 700, color: 'var(--text-primary)' }}>amazonAnalyst</code>
                </span>
                <span style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
                  <span style={{ fontWeight: 600, color: 'var(--text-muted)' }}>Password: </span>
                  <code style={{ fontFamily: 'var(--font-mono)', fontWeight: 700, color: 'var(--text-primary)' }}>123456</code>
                </span>
                <span style={{
                  fontSize: '9px', fontWeight: 700,
                  background: 'rgba(5,150,105,0.10)', color: 'var(--tahoe-mint)',
                  border: '1px solid rgba(5,150,105,0.22)',
                  borderRadius: '99px', padding: '2px 8px',
                  textTransform: 'uppercase', letterSpacing: '0.06em', flexShrink: 0,
                }}>Pre-filled</span>
              </div>
              {/* Row 2: amazonAdmin */}
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' }}>
                <span style={{ fontSize: '12px', color: 'var(--text-secondary)', minWidth: '120px' }}>
                  <span style={{ fontWeight: 600, color: 'var(--text-muted)' }}>Username: </span>
                  <code style={{ fontFamily: 'var(--font-mono)', fontWeight: 700, color: 'var(--text-primary)' }}>amazonAdmin</code>
                </span>
                <span style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
                  <span style={{ fontWeight: 600, color: 'var(--text-muted)' }}>Password: </span>
                  <code style={{ fontFamily: 'var(--font-mono)', fontWeight: 700, color: 'var(--text-primary)' }}>123456</code>
                </span>
                <span style={{
                  fontSize: '9px', fontWeight: 700,
                  background: 'rgba(79, 70, 229, 0.12)', color: 'var(--accent-primary)',
                  border: '1px solid rgba(79, 70, 229, 0.25)',
                  borderRadius: '99px', padding: '2px 8px',
                  textTransform: 'uppercase', letterSpacing: '0.06em', flexShrink: 0,
                }}>Admin</span>
              </div>
            </div>
          </div>

          <form onSubmit={handleSubmit} className={styles.form} id="login-form">
            <Input
              id="login-username"
              name="username"
              label="Username"
              placeholder="Enter your username"
              value={form.username}
              onChange={handleChange}
              error={errors.username}
              autoComplete="username"
              icon={
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
              }
            />

            <div className={styles.passwordField}>
              <Input
                id="login-password"
                name="password"
                label="Password"
                type={showPassword ? 'text' : 'password'}
                placeholder="Enter your password"
                value={form.password}
                onChange={handleChange}
                error={errors.password}
                autoComplete="current-password"
                icon={
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                    <path d="M7 11V7a5 5 0 0110 0v4" />
                  </svg>
                }
              />
              <button
                type="button"
                className={styles.togglePass}
                onClick={() => setShowPassword((s) => !s)}
                id="toggle-password-btn"
                aria-label="Toggle password visibility"
              >
                {showPassword ? (
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94" />
                    <path d="M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19" />
                    <line x1="1" y1="1" x2="23" y2="23" />
                  </svg>
                ) : (
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                )}
              </button>
            </div>

            <Button
              type="submit"
              variant="primary"
              size="lg"
              fullWidth
              loading={loading}
              id="login-submit-btn"
            >
              Sign In
            </Button>
          </form>

          <div className={styles.footer}>
            <span className={styles.footerText}>New to CloudCostOps?</span>
            <Link to="/create-company" className={styles.footerLink} id="go-to-register-link">
              Create your enterprise →
            </Link>
          </div>
        </div>
      </div>

      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </div>
  )
}
