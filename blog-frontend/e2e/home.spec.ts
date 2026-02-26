import { test, expect } from '@playwright/test'

test.describe('首页', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/')
    await page.evaluate(() => localStorage.clear())
  })

  test('应展示首页并包含搜索与文章区域', async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('domcontentloaded')
    await expect(page.getByPlaceholder('搜索文章...')).toBeVisible({ timeout: 10000 })
  })

  test('未登录时导航应包含登录与注册按钮', async ({ page }) => {
    await page.goto('/')
    await page.waitForLoadState('domcontentloaded')
    await expect(page.getByRole('button', { name: '登录' })).toBeVisible({ timeout: 10000 })
    await expect(page.getByRole('button', { name: '注册' })).toBeVisible()
  })
})
