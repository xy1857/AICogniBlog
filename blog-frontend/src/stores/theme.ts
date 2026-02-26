import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export type ThemeId = 'light' | 'dark' | 'blue'

const STORAGE_KEY = 'aicogniblog-theme'

export const useThemeStore = defineStore('theme', () => {
  const theme = ref<ThemeId>((localStorage.getItem(STORAGE_KEY) as ThemeId) || 'light')

  function setTheme(id: ThemeId) {
    theme.value = id
    localStorage.setItem(STORAGE_KEY, id)
    applyTheme(id)
  }

  function applyTheme(id: ThemeId) {
    const root = document.documentElement
    root.removeAttribute('data-theme')
    root.setAttribute('data-theme', id)
  }

  watch(theme, applyTheme, { immediate: true })

  return { theme, setTheme }
})
