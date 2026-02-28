<template>
  <el-container class="blog-layout" direction="horizontal">
    <!-- 左侧导航 -->
    <el-aside class="side-nav" width="72px">
      <nav class="side-nav-inner">
        <router-link v-if="auth.isLoggedIn" to="/subscribe" class="side-item" title="订阅">
          <el-icon><Collection /></el-icon>
          <span class="side-label">订阅</span>
        </router-link>
        <router-link v-else to="/login" class="side-item side-item--muted" title="订阅（需登录）">
          <el-icon><Collection /></el-icon>
          <span class="side-label">订阅</span>
        </router-link>
        <router-link v-if="auth.isLoggedIn" to="/follow" class="side-item" title="关注">
          <el-icon><Star /></el-icon>
          <span class="side-label">关注</span>
        </router-link>
        <router-link v-else to="/login" class="side-item side-item--muted" title="关注（需登录）">
          <el-icon><Star /></el-icon>
          <span class="side-label">关注</span>
        </router-link>
        <router-link v-if="auth.isLoggedIn" to="/my-comments" class="side-item" title="我评">
          <el-icon><ChatDotRound /></el-icon>
          <span class="side-label">我评</span>
        </router-link>
        <router-link v-else to="/login" class="side-item side-item--muted" title="我评（需登录）">
          <el-icon><ChatDotRound /></el-icon>
          <span class="side-label">我评</span>
        </router-link>
        <router-link v-if="auth.isLoggedIn" to="/my-likes" class="side-item" title="我赞">
          <el-icon><Like /></el-icon>
          <span class="side-label">我赞</span>
        </router-link>
        <router-link v-else to="/login" class="side-item side-item--muted" title="我赞（需登录）">
          <el-icon><Like /></el-icon>
          <span class="side-label">我赞</span>
        </router-link>
        <router-link v-if="auth.isLoggedIn" to="/footprints" class="side-item" title="足迹">
          <el-icon><Clock /></el-icon>
          <span class="side-label">足迹</span>
        </router-link>
        <router-link v-else to="/login" class="side-item side-item--muted" title="足迹（需登录）">
          <el-icon><Clock /></el-icon>
          <span class="side-label">足迹</span>
        </router-link>
        <el-dropdown trigger="click" class="side-item-wrap">
          <span class="side-item">
            <el-icon><ArrowDown /></el-icon>
            <span class="side-label">更多</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/essays')">
                <el-icon><Document /></el-icon>
                所有随笔
              </el-dropdown-item>
              <el-dropdown-item @click="$router.push('/')">
                <el-icon><List /></el-icon>
                所有文章
              </el-dropdown-item>
              <el-dropdown-item @click="$router.push('/comments/latest')">
                <el-icon><ChatDotRound /></el-icon>
                最新评论
              </el-dropdown-item>
              <el-dropdown-item @click="$router.push('/official')">
                <el-icon><Notification /></el-icon>
                官方博客
              </el-dropdown-item>
              <el-dropdown-item divided class="skin-dropdown-item">
                <div class="skin-wrapper">
                  <span class="skin-label">博客皮肤</span>
                  <el-radio-group :model-value="theme.theme" size="small" class="skin-radios" @update:model-value="theme.setTheme">
                    <el-radio-button label="light">亮色</el-radio-button>
                    <el-radio-button label="dark">暗色</el-radio-button>
                    <el-radio-button label="blue">蓝色</el-radio-button>
                  </el-radio-group>
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </nav>
    </el-aside>

    <el-container direction="vertical" class="main-wrap">
      <!-- 页眉 -->
      <el-header class="header">
        <div class="header-bg" />
        <div class="header-glow" />
        <div class="header-inner">
          <router-link to="/" class="logo">
            <span class="logo-text">AICogniBlog</span>
            <span class="logo-badge">AI</span>
          </router-link>
          <nav class="nav-links">
            <router-link to="/" class="nav-link">首页</router-link>
            <router-link v-if="auth.isLoggedIn" to="/articles/create" class="nav-link nav-link--highlight">
              <el-icon><EditPen /></el-icon> 写文章
            </router-link>
          </nav>
          <div class="nav-actions">
            <template v-if="auth.isLoggedIn">
              <el-dropdown trigger="click">
                <span class="user-name">
                  <span class="user-avatar">{{ auth.nickname?.charAt(0) }}</span>
                  {{ auth.nickname }}
                  <el-icon><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="$router.push('/profile')">
                      <el-icon><User /></el-icon>
                      个人中心
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/articles/create')">
                      <el-icon><EditPen /></el-icon>
                      写文章
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/drafts')">
                      <el-icon><Document /></el-icon>
                      我的草稿
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/page-builder')">
                      <el-icon><Grid /></el-icon>
                      页面装修
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push(`/home/${auth.username}`)">
                      <el-icon><House /></el-icon>
                      我的主页
                    </el-dropdown-item>
                    <el-dropdown-item v-if="auth.isAdmin" @click="$router.push('/admin')">
                      <el-icon><Setting /></el-icon>
                      管理后台
                    </el-dropdown-item>
                    <el-dropdown-item divided @click="showAccountSwitcher = true">
                      <el-icon><Switch /></el-icon>
                      切换账号
                      <el-badge v-if="accountManager.accountCount > 1" :value="accountManager.accountCount" class="account-badge" />
                    </el-dropdown-item>
                    <el-dropdown-item @click="handleLogout">
                      <el-icon><SwitchButton /></el-icon>
                      退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <el-button text class="btn-ghost" @click="$router.push('/login')">登录</el-button>
              <el-button type="primary" class="btn-neon" @click="$router.push('/register')">注册</el-button>
            </template>
          </div>
        </div>
      </el-header>

      <!-- 主内容 -->
      <el-main class="main-content">
        <router-view />
      </el-main>

      <!-- 页脚 -->
      <footer class="footer">
      <div class="footer-glow" />
      <div class="footer-bg" />
      <div class="footer-inner">
        <div class="footer-copy">
          <span>© 2025 AICogniBlog</span>
          <span class="footer-divider">·</span>
          <span>由 AI 驱动的现代博客</span>
        </div>
      </div>
    </footer>
    </el-container>

    <!-- 账号切换弹窗 -->
    <AccountSwitcher v-model="showAccountSwitcher" />
  </el-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Grid, House } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'
import { useAccountManagerStore } from '@/stores/accountManager'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/auth'
import AccountSwitcher from '@/components/account/AccountSwitcher.vue'

const auth = useAuthStore()
const theme = useThemeStore()
const accountManager = useAccountManagerStore()
const router = useRouter()

const showAccountSwitcher = ref(false)

async function handleLogout() {
  await authApi.logout().catch(() => {})
  auth.clearUser()
  router.push('/login')
}
</script>

<style scoped>
.blog-layout {
  min-height: 100vh;
  background: transparent;
  display: flex;
}

.main-wrap {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
}

.side-nav {
  background: var(--bg-card);
  border-right: 1px solid var(--border);
  flex-shrink: 0;
}

.side-nav-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 0;
  gap: 4px;
}

.side-item-wrap {
  width: 100%;
  display: flex;
  justify-content: center;
}

.side-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 10px 8px;
  min-width: 56px;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 12px;
  border-radius: 8px;
  transition: all 0.2s;
}

.side-item:hover {
  color: var(--primary);
  background: var(--primary-light);
}

.side-item.router-link-active {
  color: var(--primary);
  background: var(--primary-light);
}

.side-item--muted {
  opacity: 0.7;
}

.side-label {
  white-space: nowrap;
}

.skin-dropdown-item {
  padding: 0 !important;
}

.skin-wrapper {
  padding: 12px 16px;
  background: var(--bg-card);
  border-radius: 6px;
  position: relative;
  z-index: 1;
}

.skin-label {
  display: block;
  padding-bottom: 10px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

.skin-radios {
  display: flex;
  gap: 6px;
}

.skin-radios :deep(.el-radio-button) {
  flex: 1;
}

.skin-radios :deep(.el-radio-button__inner) {
  padding: 6px 12px;
  width: 100%;
  font-size: 12px;
  border-radius: 6px;
  transition: all 0.2s;
}

.skin-radios :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  box-shadow: 0 2px 8px rgba(14, 165, 233, 0.3);
}

.header {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 0;
  flex-shrink: 0;
}

.header-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(255, 255, 255, 0.95) 100%);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border);
  box-shadow: 0 1px 0 rgba(255, 255, 255, 0.8), 0 4px 20px rgba(0, 0, 0, 0.04);
}

.header-glow {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--gradient-primary);
  opacity: 0.85;
}

.header-inner {
  position: relative;
  z-index: 1;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  gap: 32px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  font-family: var(--font-display);
  font-weight: 700;
  font-size: 22px;
  letter-spacing: 1px;
}

.logo-text {
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-badge {
  font-size: 10px;
  padding: 2px 6px;
  background: var(--gradient-primary);
  color: #fff;
  border-radius: 4px;
  -webkit-text-fill-color: #fff;
}

.nav-links {
  display: flex;
  gap: 8px;
  flex: 1;
}

.nav-link {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.2s;
}

.nav-link:hover {
  color: var(--primary);
  background: var(--primary-light);
}

.nav-link--highlight {
  color: var(--primary);
  border: 1px solid var(--primary);
}

.nav-link--highlight:hover {
  background: var(--primary-light);
  box-shadow: 0 2px 12px rgba(14, 165, 233, 0.2);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-primary);
  padding: 6px 12px;
  border-radius: 8px;
  transition: all 0.2s;
}

.user-name:hover {
  background: var(--primary-light);
  color: var(--primary);
}

.user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.btn-ghost {
  color: var(--text-secondary) !important;
}
.btn-ghost:hover {
  color: var(--primary) !important;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 32px 24px;
  background: transparent;
}

.footer {
  position: relative;
  flex-shrink: 0;
  padding: 0;
  margin-top: auto;
}

.footer-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--gradient-primary);
  opacity: 0.85;
}

.footer-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, var(--bg-card) 0%, #f1f5f9 100%);
  border-top: 1px solid var(--border);
  box-shadow: 0 -1px 0 rgba(255, 255, 255, 0.8), 0 -4px 20px rgba(0, 0, 0, 0.04);
}

.footer-inner {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px 28px;
  text-align: center;
}

.footer-copy {
  color: var(--text-muted);
  font-size: 13px;
}

.footer-divider {
  margin: 0 10px;
  opacity: 0.6;
}

.account-badge {
  margin-left: 8px;
}

.account-badge :deep(.el-badge__content) {
  background: var(--primary);
}
</style>
