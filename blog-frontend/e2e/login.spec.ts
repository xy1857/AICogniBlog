import { test, expect } from '@playwright/test'

test.describe('登录页', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.evaluate(() => localStorage.clear())
    await page.goto('/login')
  })

  test('应展示登录表单与标题', async ({ page }) => {
    await page.waitForLoadState('domcontentloaded')
    await expect(page.getByPlaceholder('请输入用户名')).toBeVisible({ timeout: 10000 })
    await expect(page.getByPlaceholder('请输入密码')).toBeVisible()
    await expect(page.getByRole('button', { name: '登 录' })).toBeVisible()
  })

  test('应有跳转注册的链接', async ({ page }) => {
    await page.waitForLoadState('domcontentloaded')
    await expect(page.locator('a[href="/register"]').or(page.getByText('立即注册'))).toBeVisible({ timeout: 10000 })
  })

  test('使用正确账号密码应登录成功并跳转', async ({ page }) => {
    await page.waitForLoadState('domcontentloaded')
    await page.getByPlaceholder('请输入用户名').fill('xieyan')
    await page.getByPlaceholder('请输入密码').fill('xieyan21')
    await page.getByRole('button', { name: '登 录' }).click()
    await expect(page).toHaveURL(/\/(admin)?$/, { timeout: 10000 })
  })
})