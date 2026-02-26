import { defineConfig, devices } from '@playwright/test'

/**
 * Playwright E2E 配置 - 用于 AICogniBlog 前端
 *
 * 使用方式：
 * 1. 安装依赖与浏览器：npm install && npx playwright install
 * 2. 运行测试：npm run test:e2e（会自动启动 dev 服务器，若 5173 已被占用则复用）
 * 3. 调试界面：npm run test:e2e:ui
 *
 * 注意：若本机 5173 端口被其他项目占用，请先关闭或改用其他端口，否则测试会命中错误页面。
 * 若需测登录等完整流程，请同时启动后端: cd blog-backend && mvn spring-boot:run
 *
 * @see https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  use: {
    baseURL: 'http://localhost:5173',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
    // 如需多浏览器可取消注释（Windows 下 Firefox/WebKit 可能需额外环境）
    // { name: 'firefox', use: { ...devices['Desktop Firefox'] } },
    // { name: 'webkit', use: { ...devices['Desktop Safari'] } },
  ],
  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:5173',
    reuseExistingServer: !process.env.CI,
  },
})
