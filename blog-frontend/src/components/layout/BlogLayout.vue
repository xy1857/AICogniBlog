<template>
  <el-container class="blog-layout" direction="vertical">
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
                  <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/articles/create')">写文章</el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/drafts')">我的草稿</el-dropdown-item>
                  <el-dropdown-item v-if="auth.isAdmin" @click="$router.push('/admin')">管理后台</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
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
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/auth'

const auth = useAuthStore()
const router = useRouter()

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
  flex-direction: column;
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
</style>
