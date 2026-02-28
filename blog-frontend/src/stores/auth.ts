import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { LoginResponse } from '@/api/auth'
import { useAccountManagerStore, type AccountInfo } from './accountManager'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(Number(localStorage.getItem('userId')) || 0)
  const username = ref(localStorage.getItem('username') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const role = ref(Number(localStorage.getItem('role')) || 0)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 1)

  function setUser(data: LoginResponse, saveToAccountList = true) {
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    nickname.value = data.nickname
    role.value = data.role

    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', String(data.userId))
    localStorage.setItem('username', data.username)
    localStorage.setItem('nickname', data.nickname)
    localStorage.setItem('role', String(data.role))

    // 保存到账号管理器
    if (saveToAccountList) {
      const accountManager = useAccountManagerStore()
      accountManager.addOrUpdateAccount({
        userId: data.userId,
        username: data.username,
        nickname: data.nickname,
        token: data.token,
        role: data.role,
        lastUsedAt: Date.now()
      })
    }
  }

  function clearUser() {
    token.value = ''
    userId.value = 0
    username.value = ''
    nickname.value = ''
    role.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('nickname')
    localStorage.removeItem('role')
  }

  /**
   * 从账号信息恢复登录状态
   * 
   * @param account 账号信息
   */
  function restoreFromAccount(account: AccountInfo) {
    token.value = account.token
    userId.value = account.userId
    username.value = account.username
    nickname.value = account.nickname
    role.value = account.role

    localStorage.setItem('token', account.token)
    localStorage.setItem('userId', String(account.userId))
    localStorage.setItem('username', account.username)
    localStorage.setItem('nickname', account.nickname)
    localStorage.setItem('role', String(account.role))
  }

  return { 
    token, 
    userId, 
    username, 
    nickname, 
    role, 
    isLoggedIn, 
    isAdmin, 
    setUser, 
    clearUser,
    restoreFromAccount 
  }
})
