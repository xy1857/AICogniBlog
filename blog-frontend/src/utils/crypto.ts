import CryptoJS from 'crypto-js'
import JSEncrypt from 'jsencrypt'

/**
 * 加密工具类
 * 
 * 用于敏感数据的加密和解密，主要用于账号信息的本地存储
 */

// 生成设备指纹作为加密密钥的一部分
function generateDeviceFingerprint(): string {
  const navigator = window.navigator
  const screen = window.screen
  
  const fingerprint = [
    navigator.userAgent,
    navigator.language,
    screen.colorDepth,
    screen.width + 'x' + screen.height,
    new Date().getTimezoneOffset(),
  ].join('|')
  
  return CryptoJS.MD5(fingerprint).toString()
}

// 加密密钥（设备指纹 + 固定盐值）
const SECRET_KEY = generateDeviceFingerprint() + 'aicogniblog_account_salt_2026'

/**
 * 加密字符串
 * 
 * @param plainText 明文
 * @returns 密文
 */
export function encrypt(plainText: string): string {
  try {
    return CryptoJS.AES.encrypt(plainText, SECRET_KEY).toString()
  } catch (error) {
    console.error('加密失败:', error)
    return plainText
  }
}

/**
 * 解密字符串
 * 
 * @param cipherText 密文
 * @returns 明文
 */
export function decrypt(cipherText: string): string {
  try {
    const bytes = CryptoJS.AES.decrypt(cipherText, SECRET_KEY)
    return bytes.toString(CryptoJS.enc.Utf8)
  } catch (error) {
    console.error('解密失败:', error)
    return cipherText
  }
}

/**
 * 加密对象（转为 JSON 后加密）
 * 
 * @param obj 对象
 * @returns 密文
 */
export function encryptObject<T>(obj: T): string {
  try {
    const jsonStr = JSON.stringify(obj)
    return encrypt(jsonStr)
  } catch (error) {
    console.error('对象加密失败:', error)
    return ''
  }
}

/**
 * 解密对象（解密后解析 JSON）
 * 
 * @param cipherText 密文
 * @returns 对象
 */
export function decryptObject<T>(cipherText: string): T | null {
  try {
    const jsonStr = decrypt(cipherText)
    if (!jsonStr) return null
    return JSON.parse(jsonStr) as T
  } catch (error) {
    console.error('对象解密失败:', error)
    return null
  }
}

/**
 * 自动加密密码（用于登录/注册）
 * 
 * 如果后端配置了 RSA 公钥加密，则使用 RSA 加密
 * 否则直接返回明文密码
 * 
 * @param password 明文密码
 * @returns 加密后的密码或明文密码
 */
export async function encryptPasswordAuto(password: string): Promise<string> {
  try {
    // 尝试获取公钥
    const { authApi } = await import('@/api/auth')
    const res = await authApi.getPublicKey()
    
    if (res.data && res.data.publicKey) {
      // 使用 RSA 加密
      const encrypt = new JSEncrypt()
      encrypt.setPublicKey(res.data.publicKey)
      const encrypted = encrypt.encrypt(password)
      
      if (!encrypted) {
        console.error('RSA 加密失败')
        throw new Error('密码加密失败')
      }
      
      return encrypted
    }
  } catch (error) {
    console.error('密码加密失败:', error)
    throw error
  }
  
  // 如果没有公钥，返回明文密码
  return password
}
