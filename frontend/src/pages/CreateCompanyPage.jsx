import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { createCompany } from '../api/auth'
import Button from '../components/Button'
import Input from '../components/Input'
import { ToastContainer, useToast } from '../components/Toast'
import styles from './CreateCompanyPage.module.css'

export default function CreateCompanyPage() {
  const navigate = useNavigate()
  const { toasts, addToast, removeToast } = useToast()

  const [form, setForm] = useState({
    companyName: '',
    adminUserName: '',
    email: '',
    password: '',
    confirmPassword: '',
  })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [step, setStep] = useState(1) // 1: company info, 2: admin info

  const handleChange = (e) => {
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }))
    setErrors((err) => ({ ...err, [e.target.name]: '' }))
  }

  const validateStep1 = () => {
    const errs = {}
    if (!form.companyName.trim()) errs.companyName = 'Company name is required'
    else if (form.companyName.length < 3) errs.companyName = 'Minimum 3 characters'
    return errs
  }

  const validateStep2 = () => {
    const errs = {}
    if (!form.adminUserName.trim()) errs.adminUserName = 'Admin username is required'
    if (!form.email.trim()) errs.email = 'Email is required'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) errs.email = 'Invalid email address'
    if (!form.password) errs.password = 'Password is required'
    else if (form.password.length < 6) errs.password = 'Minimum 6 characters'
    if (form.confirmPassword !== form.password) errs.confirmPassword = 'Passwords do not match'
    return errs
  }

  const handleNext = () => {
    const errs = validateStep1()
    if (Object.keys(errs).length) { setErrors(errs); return }
    setStep(2)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const errs = validateStep2()
    if (Object.keys(errs).length) { setErrors(errs); return }

    setLoading(true)
    try {
      await createCompany({
        companyName: form.companyName,
        adminUserName: form.adminUserName,
        email: form.email,
        password: form.password,
      })
      addToast('Enterprise created successfully! Please log in.', 'success')
      setTimeout(() => navigate('/login'), 1500)
    } catch (err) {
      addToast(err.message || 'Failed to create company', 'error')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={styles.page}>
      <div className="bg-orb bg-orb-1" />
      <div className="bg-orb bg-orb-2" />
      <div className={styles.grid} />

      <div className={styles.container}>
        {/* Left panel */}
        <div className={styles.leftPanel}>
          <div className={styles.logoMark}>
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
              <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="url(#lg2)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              <defs>
                <linearGradient id="lg2" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stopColor="#6c63ff"/>
                  <stop offset="100%" stopColor="#00d4ff"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h1 className={styles.title}>
            Launch Your<br />
            <span className="gradient-text">Cloud Enterprise</span>
          </h1>
          <p className={styles.subtitle}>
            Set up your organization in minutes. Your admin account will have full control over analysts and cost data.
          </p>

          <div className={styles.features}>
            {[
              { icon: '🏢', label: 'Enterprise Workspace', desc: 'Dedicated company environment' },
              { icon: '👑', label: 'Admin Control', desc: 'Full user management access' },
              { icon: '📊', label: 'Cost Analytics', desc: 'Real-time cloud cost intelligence' },
              { icon: '🔐', label: 'JWT Security', desc: 'RSA-encrypted authentication' },
            ].map((f) => (
              <div key={f.label} className={styles.featureItem}>
                <span className={styles.featureIcon}>{f.icon}</span>
                <div>
                  <div className={styles.featureLabel}>{f.label}</div>
                  <div className={styles.featureDesc}>{f.desc}</div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Right panel — form */}
        <div className={[styles.card, 'glass-card'].join(' ')}>
          {/* Step indicator */}
          <div className={styles.stepIndicator}>
            <div className={[styles.stepItem, step >= 1 ? styles.stepActive : ''].join(' ')}>
              <div className={styles.stepDot}>
                {step > 1 ? (
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                    <polyline points="20 6 9 17 4 12"/>
                  </svg>
                ) : '1'}
              </div>
              <span>Company Info</span>
            </div>
            <div className={[styles.stepLine, step >= 2 ? styles.stepLineActive : ''].join(' ')} />
            <div className={[styles.stepItem, step >= 2 ? styles.stepActive : ''].join(' ')}>
              <div className={styles.stepDot}>{step > 2 ? '✓' : '2'}</div>
              <span>Admin Account</span>
            </div>
          </div>

          <form onSubmit={handleSubmit} className={styles.form} id="create-company-form">
            {step === 1 && (
              <div className={styles.stepContent} key="step1">
                <div className={styles.stepHeader}>
                  <h2 className={styles.stepTitle}>Company Details</h2>
                  <p className={styles.stepDesc}>Name your enterprise on CloudCostOps</p>
                </div>

                <Input
                  id="company-name-input"
                  name="companyName"
                  label="Company Name"
                  placeholder="e.g. Acme Corp, Phoenix Technologies"
                  value={form.companyName}
                  onChange={handleChange}
                  error={errors.companyName}
                  autoFocus
                  icon={
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
                      <polyline points="9 22 9 12 15 12 15 22"/>
                    </svg>
                  }
                />

                <Button
                  type="button"
                  variant="primary"
                  size="lg"
                  fullWidth
                  onClick={handleNext}
                  id="company-next-btn"
                >
                  Continue →
                </Button>
              </div>
            )}

            {step === 2 && (
              <div className={styles.stepContent} key="step2">
                <div className={styles.stepHeader}>
                  <h2 className={styles.stepTitle}>Admin Account</h2>
                  <p className={styles.stepDesc}>Create your administrator credentials</p>
                </div>

                <Input
                  id="admin-username-input"
                  name="adminUserName"
                  label="Admin Username"
                  placeholder="Choose a username"
                  value={form.adminUserName}
                  onChange={handleChange}
                  error={errors.adminUserName}
                  autoFocus
                  icon={
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/>
                      <circle cx="12" cy="7" r="4"/>
                    </svg>
                  }
                />

                <Input
                  id="admin-email-input"
                  name="email"
                  type="email"
                  label="Email Address"
                  placeholder="admin@company.com"
                  value={form.email}
                  onChange={handleChange}
                  error={errors.email}
                  icon={
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                      <polyline points="22,6 12,13 2,6"/>
                    </svg>
                  }
                />

                <div className={styles.passwordField}>
                  <Input
                    id="admin-password-input"
                    name="password"
                    type={showPassword ? 'text' : 'password'}
                    label="Password"
                    placeholder="Min. 6 characters"
                    value={form.password}
                    onChange={handleChange}
                    error={errors.password}
                    icon={
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                        <path d="M7 11V7a5 5 0 0110 0v4"/>
                      </svg>
                    }
                  />
                  <button type="button" className={styles.togglePass} onClick={() => setShowPassword((s) => !s)} id="toggle-pass-create-btn">
                    {showPassword ? (
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/>
                      </svg>
                    ) : (
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>
                      </svg>
                    )}
                  </button>
                </div>

                <Input
                  id="admin-confirm-password-input"
                  name="confirmPassword"
                  type="password"
                  label="Confirm Password"
                  placeholder="Repeat your password"
                  value={form.confirmPassword}
                  onChange={handleChange}
                  error={errors.confirmPassword}
                  icon={
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                    </svg>
                  }
                />

                <div className={styles.actions}>
                  <Button
                    type="button"
                    variant="secondary"
                    size="lg"
                    onClick={() => setStep(1)}
                    id="company-back-btn"
                  >
                    ← Back
                  </Button>
                  <Button
                    type="submit"
                    variant="primary"
                    size="lg"
                    loading={loading}
                    id="create-company-submit-btn"
                    style={{ flex: 1 }}
                  >
                    Create Enterprise
                  </Button>
                </div>
              </div>
            )}
          </form>

          <div className={styles.footer}>
            <span className={styles.footerText}>Already have an account?</span>
            <Link to="/login" className={styles.footerLink} id="go-to-login-link">Sign in →</Link>
          </div>
        </div>
      </div>

      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </div>
  )
}
