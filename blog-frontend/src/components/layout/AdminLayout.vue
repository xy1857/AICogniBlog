<template>
  <el-container class="admin-layout" style="height: 100vh">
    <!-- 侧边栏 -->
    <el-aside width="240px" class="sidebar">
      <div class="sidebar-glow" />
      <div class="sidebar-logo">
        <span class="logo-text">AICogniBlog</span>
        <span class="logo-badge">管理</span>
      </div>
      <el-menu router :default-active="$route.path" class="admin-menu">
        <el-menu-item index="/admin/articles">
          <el-icon><Document /></el-icon>
          <span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/comments">
          <el-icon><ChatDotRound /></el-icon>
          <span>评论管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <div class="menu-divider" />
        <el-menu-item index="/" @click.prevent="$router.push('/')">
          <el-icon><HomeFilled /></el-icon>
          <span>回到前台</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container direction="vertical">
      <!-- 顶栏 -->
      <el-header class="admin-header">
        <span style="flex:1"></span>
        <el-dropdown trigger="click">
          <span class="user-name">
            <span class="user-avatar">{{ auth.nickname?.charAt(0) }}</span>
            {{ auth.nickname }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/')">回到前台</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <!-- 内容区 -->
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
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
.admin-layout {
  background: var(--bg-page);
}

.sidebar {
  background: var(--bg-card);
  border-right: 1px solid var(--border);
  position: relative;
  box-shadow: var(--shadow-sm);
}

.sidebar-glow {
  position: absolute;
  top: 0;
  right: 0;
  width: 3px;
  height: 100%;
  background: var(--gradient-primary);
  opacity: 0.5;
}

.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-bottom: 1px solid var(--border);
}

.logo-text {
  font-family: var(--font-display);
  font-weight: 700;
  font-size: 16px;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-badge {
  font-size: 11px;
  padding: 3px 8px;
  background: var(--gradient-primary);
  color: #fff;
  border-radius: 6px;
  -webkit-text-fill-color: #fff;
}

.admin-menu {
  padding: 16px 12px;
  border: none !important;
  background: transparent !important;
}

.admin-menu .el-menu-item {
  border-radius: 10px;
  margin-bottom: 4px;
}

.admin-menu .el-menu-item span {
  margin-left: 10px;
}

.menu-divider {
  height: 1px;
  background: var(--border);
  margin: 16px 12px;
}

.admin-header {
  background: var(--bg-card);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  padding: 0 24px;
  box-shadow: var(--shadow-sm);
}

.user-name {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: var(--text-primary);
  padding: 8px 16px;
  border-radius: 10px;
  transition: all 0.2s;
}

.user-name:hover {
  background: var(--primary-light);
  color: var(--primary);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.admin-main {
  background: var(--bg-page);
  padding: 24px;
  overflow-y: auto;
}
</style>
