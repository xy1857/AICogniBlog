import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { encryptObject, decryptObject } from '@/utils/crypto'
import { ElMessage } from 'element-plus'

/**
 * 账号信息接口
 */
export interface AccountInfo {
  userId: number          // 用户 ID
  username: string        // 用户名
  nickname: string        // 昵称
  token: string          // JWT Token
  role: number           // 角色（0=普通用户，1=管理员）
  avatarBase64?: string  // 头像（可选）
  lastUsedAt: number     // 最后使用时间戳
}

/**
 * 账号列表存储结构
 */
interface AccountStorage {
  currentUserId: number           // 当前激活的用户 ID
  accounts: AccountInfo[]         // 账号列表
  version: string                 // 数据版本（用于迁移）
}

// LocalStorage 存储键
const STORAGE_KEY = 'aicogniblog_accounts'
const MAX_ACCOUNTS = 5  // 最多保存 5 个账号

/**
 * 账号管理 Store
 * 
 * 管理多账号的增删改查、切换等功能
 */
export const useAccountManagerStore = defineStore('accountManager', () => {
  // 状态
  const accounts = ref<AccountInfo[]>([])
  const currentUserId = ref<number>(0)

  // 计算属性
  const currentAccount = computed(() => 
    accounts.value.find(acc => acc.userId === currentUserId.value)
  )

  const accountCount = computed(() => accounts.value.length)

  const canAddAccount = computed(() => accounts.value.length < MAX_ACCOUNTS)

  /**
   * 从 LocalStorage 加载账号列表
   */
  function loadAccounts() {
    try {
      const encrypted = localStorage.getItem(STORAGE_KEY)
      if (!encrypted) {
        accounts.value = []
        currentUserId.value = 0
        return
      }

      const storage = decryptObject<AccountStorage>(encrypted)
      if (storage && storage.accounts) {
        accounts.value = storage.accounts
        currentUserId.value = storage.currentUserId || 0
      }
    } catch (error) {
      console.error('加载账号列表失败:', error)
      accounts.value = []
      currentUserId.value = 0
    }
  }

  /**
   * 保存账号列表到 LocalStorage
   */
  function saveAccounts() {
    try {
      const storage: AccountStorage = {
        currentUserId: currentUserId.value,
        accounts: accounts.value,
        version: '1.0.0'
      }
      const encrypted = encryptObject(storage)
      localStorage.setItem(STORAGE_KEY, encrypted)
    } catch (error) {
      console.error('保存账号列表失败:', error)
    }
  }

  /**
   * 添加或更新账号
   * 
   * @param account 账号信息
   * @returns 是否成功
   */
  function addOrUpdateAccount(account: AccountInfo): boolean {
    const existingIndex = accounts.value.findIndex(acc => acc.userId === account.userId)
    
    if (existingIndex >= 0) {
      // 更新现有账号
      accounts.value[existingIndex] = {
        ...account,
        lastUsedAt: Date.now()
      }
    } else {
      // 添加新账号
      if (!canAddAccount.value) {
        ElMessage.warning(`最多只能保存 ${MAX_ACCOUNTS} 个账号`)
        return false
      }
      
      accounts.value.push({
        ...account,
        lastUsedAt: Date.now()
      })
    }

    currentUserId.value = account.userId
    saveAccounts()
    return true
  }

  /**
   * 删除账号
   * 
   * @param userId 用户 ID
   */
  function removeAccount(userId: number) {
    const index = accounts.value.findIndex(acc => acc.userId === userId)
    if (index >= 0) {
      accounts.value.splice(index, 1)
      
      // 如果删除的是当前账号，清空当前用户
      if (currentUserId.value === userId) {
        currentUserId.value = 0
      }
      
      saveAccounts()
    }
  }

  /**
   * 切换到指定账号
   * 
   * @param userId 用户 ID
   * @returns 账号信息，如果不存在返回 null
   */
  function switchAccount(userId: number): AccountInfo | null {
    const account = accounts.value.find(acc => acc.userId === userId)
    if (!account) {
      return null
    }

    // 更新最后使用时间
    account.lastUsedAt = Date.now()
    currentUserId.value = userId
    saveAccounts()

    return account
  }

  /**
   * 获取指定账号信息
   * 
   * @param userId 用户 ID
   * @returns 账号信息
   */
  function getAccount(userId: number): AccountInfo | undefined {
    return accounts.value.find(acc => acc.userId === userId)
  }

  /**
   * 获取按最后使用时间排序的账号列表
   * 
   * @returns 排序后的账号列表
   */
  function getSortedAccounts(): AccountInfo[] {
    return [...accounts.value].sort((a, b) => b.lastUsedAt - a.lastUsedAt)
  }

  /**
   * 清空所有账号
   */
  function clearAllAccounts() {
    accounts.value = []
    currentUserId.value = 0
    localStorage.removeItem(STORAGE_KEY)
  }

  /**
   * 检查 Token 是否过期
   * 
   * @param token JWT Token
   * @returns 是否过期
   */
  function isTokenExpired(token: string): boolean {
    try {
      // 解析 JWT Token（格式：header.payload.signature）
      const payload = token.split('.')[1]
      if (!payload) return true

      const decoded = JSON.parse(atob(payload))
      const exp = decoded.exp

      if (!exp) return false

      // 检查是否过期（提前 5 分钟判定为过期）
      const now = Math.floor(Date.now() / 1000)
      return exp < now + 300
    } catch (error) {
      console.error('Token 解析失败:', error)
      return true
    }
  }

  // 初始化时加载账号列表
  loadAccounts()

  return {
    // 状态
    accounts,
    currentUserId,
    
    // 计算属性
    currentAccount,
    accountCount,
    canAddAccount,
    
    // 方法
    loadAccounts,
    addOrUpdateAccount,
    removeAccount,
    switchAccount,
    getAccount,
    getSortedAccounts,
    clearAllAccounts,
    isTokenExpired,
  }
})

