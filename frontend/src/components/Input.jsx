import { forwardRef } from 'react'
import styles from './Input.module.css'

const Input = forwardRef(function Input(
  { label, id, type = 'text', placeholder, error, icon, ...rest },
  ref
) {
  return (
    <div className={styles.wrapper}>
      {label && (
        <label htmlFor={id} className={styles.label}>
          {label}
        </label>
      )}
      <div className={[styles.inputWrapper, error ? styles.hasError : ''].join(' ')}>
        {icon && <span className={styles.icon}>{icon}</span>}
        <input
          ref={ref}
          id={id}
          type={type}
          placeholder={placeholder}
          className={[styles.input, icon ? styles.withIcon : ''].join(' ')}
          {...rest}
        />
      </div>
      {error && <span className={styles.errorMsg}>{error}</span>}
    </div>
  )
})

export default Input
