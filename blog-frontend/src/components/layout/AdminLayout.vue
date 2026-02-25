<template>
  <el-container style="height: 100vh">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="sidebar-logo">AICogniBlog 管理</div>
      <el-menu router :default-active="$route.path" background-color="#001529" text-color="#ccc" active-text-color="#fff">
        <el-menu-item index="/admin/articles">
          <el-icon><Document /></el-icon>文章管理
        </el-menu-item>
        <el-menu-item index="/admin/comments">
          <el-icon><ChatDotRound /></el-icon>评论管理
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>用户管理
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container direction="vertical">
      <!-- 顶栏 -->
      <el-header class="admin-header">
        <span style="flex:1"></span>
        <el-dropdown trigger="click">
          <span class="user-name">{{ auth.nickname }} <el-icon><ArrowDown /></el-icon></span>
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
.sidebar { background: #001529; }
.sidebar-logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  border-bottom: 1px solid #002140;
}
.admin-header {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  padding: 0 24px;
}
.user-name {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}
.admin-main { background: #f5f7fa; padding: 20px; }
</style>
