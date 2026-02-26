<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="auth-bg-grid" />
      <div class="auth-bg-glow auth-bg-glow--1" />
      <div class="auth-bg-glow auth-bg-glow--2" />
    </div>
    <el-card class="auth-card">
      <div class="auth-card-glow" />
      <h2 class="auth-title">登录 AICogniBlog</h2>
      <p class="auth-subtitle">欢迎回来，继续你的创作之旅</p>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" @keyup.enter="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password size="large" />
        </el-form-item>
        <el-button type="primary" :loading="loading" class="auth-submit" @click="handleLogin">登 录</el-button>
      </el-form>
      <div class="auth-footer">
        还没有账号？
        <router-link to="/register" class="auth-link">立即注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance } from 'element-plus'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
}

async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await authApi.login(form) as any
    auth.setUser(res.data)
    ElMessage.success('登录成功')
    router.push(auth.isAdmin ? '/admin' : '/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.auth-bg {
  position: fixed;
  inset: 0;
  z-index: 0;
}

.auth-bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(14, 165, 233, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(14, 165, 233, 0.06) 1px, transparent 1px);
  background-size: 60px 60px;
}

.auth-bg-glow {
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  filter: blur(120px);
  opacity: 0.4;
}

.auth-bg-glow--1 {
  top: -100px;
  left: -100px;
  background: var(--primary);
  opacity: 0.2;
}

.auth-bg-glow--2 {
  bottom: -100px;
  right: -100px;
  background: var(--accent);
  opacity: 0.2;
}

.auth-card {
  position: relative;
  width: 420px;
  padding: 40px;
  background: var(--bg-card) !important;
  border: 1px solid var(--border) !important;
  border-radius: 24px;
  overflow: hidden;
  z-index: 1;
  box-shadow: var(--shadow-hover);
}

.auth-card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--gradient-primary);
}

.auth-title {
  text-align: center;
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 700;
  font-family: var(--font-display);
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.auth-subtitle {
  text-align: center;
  margin: 0 0 32px;
  font-size: 14px;
  color: var(--text-muted);
}

.auth-submit {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-top: 8px;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-muted);
}

.auth-link {
  color: var(--primary);
  text-decoration: none;
  margin-left: 4px;
}

.auth-link:hover {
  text-decoration: underline;
}
</style>
