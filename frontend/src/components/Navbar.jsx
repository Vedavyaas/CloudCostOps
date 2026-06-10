import { useAuth } from '../context/AuthContext'
import styles from './Navbar.module.css'

export default function Navbar() {
  const { user, logout, isAdmin, isAnalyst } = useAuth()

  const roleLabel = isAdmin ? 'Admin' : 'Analyst'
  const roleBadgeClass = isAdmin ? styles.badgeAdmin : styles.badgeAnalyst

  return (
    <nav className={styles.nav}>
      <div className={styles.logo}>
        <div className={styles.logoIcon}>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z" stroke="url(#g1)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            <polyline points="9 22 9 12 15 12 15 22" stroke="url(#g1)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            <defs>
              <linearGradient id="g1" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stopColor="#6c63ff"/>
                <stop offset="100%" stopColor="#00d4ff"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <div>
          <span className={styles.logoText}>CloudCostOps</span>
          <span className={styles.logoSub}>Cost Intelligence Platform</span>
        </div>
      </div>

      <div className={styles.right}>
        <div className={styles.userInfo}>
          <div className={styles.avatar}>
            {user?.username?.[0]?.toUpperCase() || 'U'}
          </div>
          <div className={styles.userDetails}>
            <span className={styles.userName}>{user?.username}</span>
            <span className={styles.userCompany}>{user?.companyName}</span>
          </div>
          <span className={[styles.badge, roleBadgeClass].join(' ')}>{roleLabel}</span>
        </div>

        <button className={styles.logoutBtn} onClick={logout} id="logout-btn">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/>
            <polyline points="16 17 21 12 16 7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          Logout
        </button>
      </div>
    </nav>
  )
}
