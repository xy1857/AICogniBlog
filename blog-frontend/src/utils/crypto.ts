import JSEncrypt from 'jsencrypt'

let publicKey = ''

/**
 * 获取公钥
 */
export async function fetchPublicKey(): Promise<string> {
  if (publicKey) return publicKey
  
  try {
    const response = await fetch('/api/auth/public-key')
    const result = await response.json()
    publicKey = result.data.publicKey
    return publicKey
  } catch (error) {
    console.error('获取公钥失败:', error)
    throw new Error('获取公钥失败')
  }
}

/**
 * RSA 加密
 */
export function encryptPassword(password: string, key?: string): string {
  const encrypt = new JSEncrypt()
  encrypt.setPublicKey(key || publicKey)
  const encrypted = encrypt.encrypt(password)
  if (!encrypted) {
    throw new Error('密码加密失败')
  }
  return encrypted
}

/**
 * 加密密码（自动获取公钥）
 */
export async function encryptPasswordAuto(password: string): Promise<string> {
  const key = await fetchPublicKey()
  return encryptPassword(password, key)
}

