import { useEffect, useState } from 'react'
import styles from './Toast.module.css'

export default function Toast({ message, type = 'success', onDismiss }) {
  const [visible, setVisible] = useState(true)

  useEffect(() => {
    const timer = setTimeout(() => {
      setVisible(false)
      setTimeout(onDismiss, 300)
    }, 4000)
    return () => clearTimeout(timer)
  }, [onDismiss])

  const icons = {
    success: '✓',
    error: '✕',
    info: 'ℹ',
    warning: '⚠',
  }

  return (
    <div className={[styles.toast, styles[type], visible ? styles.visible : styles.hidden].join(' ')}>
      <span className={styles.icon}>{icons[type]}</span>
      <span className={styles.message}>{message}</span>
      <button className={styles.close} onClick={() => { setVisible(false); setTimeout(onDismiss, 300) }}>×</button>
    </div>
  )
}

// Toast Container
export function ToastContainer({ toasts, onRemove }) {
  return (
    <div className={styles.container}>
      {toasts.map((t) => (
        <Toast key={t.id} message={t.message} type={t.type} onDismiss={() => onRemove(t.id)} />
      ))}
    </div>
  )
}

// Hook to manage toasts
let _toastId = 0
export function useToast() {
  const [toasts, setToasts] = useState([])

  const addToast = (message, type = 'success') => {
    const id = ++_toastId
    setToasts((prev) => [...prev, { id, message, type }])
  }

  const removeToast = (id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id))
  }

  return { toasts, addToast, removeToast }
}
