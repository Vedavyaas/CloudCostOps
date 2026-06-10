import { createContext, useContext, useState, useCallback, useEffect } from 'react'
import { login as apiLogin, getSelfInfo } from '../api/auth'

const AuthContext = createContext(null)

// Decode a JWT payload without verifying (client-side use only)
function decodeJwt(token) {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    const json = atob(base64)
    return JSON.parse(json)
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('cco_token'))
  const [user, setUser] = useState(null) // { username, companyName, email, role }
  const [loading, setLoading] = useState(!!localStorage.getItem('cco_token'))

  // On mount, if there's a stored token, fetch user info
  useEffect(() => {
    if (token) {
      getSelfInfo()
        .then((info) => setUser(info))
        .catch(() => {
          // Token expired or invalid
          localStorage.removeItem('cco_token')
          setToken(null)
        })
        .finally(() => setLoading(false))
    } else {
      setLoading(false)
    }
  }, []) // eslint-disable-line

  const login = useCallback(async (credentials) => {
    const data = await apiLogin(credentials)
    const jwt = data.token
    localStorage.setItem('cco_token', jwt)
    setToken(jwt)

    const info = await getSelfInfo()
    setUser(info)
    return info
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('cco_token')
    setToken(null)
    setUser(null)
  }, [])

  const isAdmin = user?.role === 'ADMIN'
  const isAnalyst = user?.role === 'ANALYST'
  const isAuthenticated = !!token && !!user

  return (
    <AuthContext.Provider value={{ token, user, loading, login, logout, isAdmin, isAnalyst, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
