import styles from './Button.module.css'

export default function Button({
  children,
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  type = 'button',
  onClick,
  fullWidth = false,
  icon,
}) {
  return (
    <button
      type={type}
      className={[
        styles.btn,
        styles[variant],
        styles[size],
        fullWidth ? styles.fullWidth : '',
        loading ? styles.loading : '',
      ].join(' ')}
      disabled={disabled || loading}
      onClick={onClick}
    >
      {loading ? (
        <>
          <span className={styles.spinner} />
          <span>Loading...</span>
        </>
      ) : (
        <>
          {icon && <span className={styles.icon}>{icon}</span>}
          {children}
        </>
      )}
    </button>
  )
}
