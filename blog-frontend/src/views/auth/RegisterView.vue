<template>
  <div class="auth-page">
    <div class="auth-bg">
      <div class="auth-bg-grid" />
      <div class="auth-bg-glow auth-bg-glow--1" />
      <div class="auth-bg-glow auth-bg-glow--2" />
    </div>
    <el-card class="auth-card">
      <div class="auth-card-glow" />
      <h2 class="auth-title">注册 AICogniBlog</h2>
      <p class="auth-subtitle">加入我们，开启 AI 驱动的创作体验</p>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="3-20位字母/数字/下划线" size="large" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="至少6位" show-password size="large" />
        </el-form-item>
        <el-button type="primary" :loading="loading" class="auth-submit" @click="handleRegister">注 册</el-button>
      </el-form>
      <div class="auth-footer">
        已有账号？
        <router-link to="/login" class="auth-link">去登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance } from 'element-plus'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { encryptPasswordAuto } from '@/utils/crypto'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ username: '', email: '', password: '' })
const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, max: 20, message: '用户名长度为3-20位' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '只能包含字母、数字和下划线' },
  ],
  email: [
    { required: true, message: '请输入邮箱' },
    { type: 'email', message: '邮箱格式不正确' },
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码至少6位' },
  ],
}

async function handleRegister() {
  await formRef.value?.validate()
  loading.value = true
  try {
    // 加密密码
    const encryptedPassword = await encryptPasswordAuto(form.password)
    await authApi.register({
      username: form.username,
      email: form.email,
      password: encryptedPassword
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error: any) {
    ElMessage.error(error.message || '注册失败')
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
