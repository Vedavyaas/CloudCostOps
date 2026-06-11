import { Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import LoginPage from './pages/LoginPage'
import CreateCompanyPage from './pages/CreateCompanyPage'
import DashboardPage from './pages/DashboardPage'
import AnalyticsPage from './pages/AnalyticsPage'
import AgentComparisonPage from './pages/AgentComparisonPage'
import BudgetComparisonPage from './pages/BudgetComparisonPage'

/* ─── Protected Route ─────────────────────────────────────────────── */
// Redirects to /login if not authenticated; shows spinner while loading
function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth()

  if (loading) {
    return (
    <div style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: '#0a0e1a',
        flexDirection: 'column',
        gap: '16px',
      }}>
        <div style={{
          width: '40px',
          height: '40px',
          borderRadius: '50%',
          border: '2px solid rgba(99,102,241,0.15)',
          borderTopColor: '#6366f1',
          animation: 'spin 0.65s linear infinite',
        }} />
        <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
        <span style={{ color: 'rgba(180,192,220,0.7)', fontSize: '13px', fontFamily: '-apple-system, Inter, sans-serif', letterSpacing: '-0.01em' }}>
          Restoring session…
        </span>
      </div>
    )
  }

  return isAuthenticated ? children : <Navigate to="/login" replace />
}

/* ─── Public Route ────────────────────────────────────────────────── */
// Redirects to /dashboard if already logged in
function PublicRoute({ children }) {
  const { isAuthenticated, loading } = useAuth()
  if (loading) return null
  return isAuthenticated ? <Navigate to="/dashboard" replace /> : children
}

/* ─── App Routes ──────────────────────────────────────────────────── */
function AppRoutes() {
  return (
    <Routes>
      {/* Default: redirect to login */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* Public pages */}
      <Route
        path="/login"
        element={
          <PublicRoute>
            <LoginPage />
          </PublicRoute>
        }
      />
      <Route
        path="/create-company"
        element={
          <PublicRoute>
            <CreateCompanyPage />
          </PublicRoute>
        }
      />

      {/* Protected pages */}
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/analytics"
        element={
          <ProtectedRoute>
            <AnalyticsPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/agents-compare"
        element={
          <ProtectedRoute>
            <AgentComparisonPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/budget-compare"
        element={
          <ProtectedRoute>
            <BudgetComparisonPage />
          </ProtectedRoute>
        }
      />

      {/* Catch-all */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}

/* ─── Root App ────────────────────────────────────────────────────── */
export default function App() {
  return (
    <AuthProvider>
      <AppRoutes />
    </AuthProvider>
  )
}
