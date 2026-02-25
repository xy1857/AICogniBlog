<template>
  <el-container class="min-h-screen" direction="vertical">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-inner">
        <router-link to="/" class="logo">AICogniBlog</router-link>
        <nav class="nav-links">
          <router-link to="/">首页</router-link>
          <router-link v-if="auth.isLoggedIn" to="/articles/create">写文章</router-link>
        </nav>
        <div class="nav-actions">
          <template v-if="auth.isLoggedIn">
            <el-dropdown trigger="click">
              <span class="user-name">{{ auth.nickname }} <el-icon><ArrowDown /></el-icon></span>
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
            <el-button text @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 主内容 -->
    <el-main class="main-content">
      <router-view />
    </el-main>

    <!-- 页脚 -->
    <el-footer class="footer">
      <span>© 2025 AICogniBlog · 由 AI 驱动的现代博客</span>
    </el-footer>
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
.header {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  max-width: 1100px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 24px;
}
.logo {
  font-size: 20px;
  font-weight: 700;
  color: #409eff;
  text-decoration: none;
}
.nav-links {
  display: flex;
  gap: 20px;
  flex: 1;
}
.nav-links a {
  color: #333;
  text-decoration: none;
  font-size: 14px;
}
.nav-links a:hover { color: #409eff; }
.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.user-name {
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.main-content {
  max-width: 1100px;
  width: 100%;
  margin: 0 auto;
  padding: 24px 20px;
}
.footer {
  text-align: center;
  color: #999;
  font-size: 13px;
  border-top: 1px solid #f0f0f0;
  line-height: 60px;
}
</style>
