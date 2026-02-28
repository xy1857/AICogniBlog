<template>
  <el-dialog
    v-model="visible"
    title="添加新账号"
    width="450px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="login-dialog">
      <p class="login-tip">请登录新账号，登录成功后将自动添加到账号列表</p>
      
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" @keyup.enter="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password size="large" />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleLogin">登录并添加</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch } from 'vue'
import type { FormInstance } from 'element-plus'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import { useAccountManagerStore } from '@/stores/accountManager'
import { ElMessage } from 'element-plus'
import { encryptPasswordAuto } from '@/utils/crypto'

interface Props {
  modelValue: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'success': []
}>()

const authStore = useAuthStore()
const accountManager = useAccountManagerStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码' }],
}

// 双向绑定
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 监听弹窗打开，重置表单
watch(visible, (newVal) => {
  if (newVal) {
    form.username = ''
    form.password = ''
    formRef.value?.clearValidate()
  }
})

/**
 * 登录并添加账号
 */
async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true
  
  try {
    // 加密密码
    const encryptedPassword = await encryptPasswordAuto(form.password)
    const res = await authApi.login({
      username: form.username,
      password: encryptedPassword
    }) as any
    
    // 检查是否已达到账号数量上限
    if (!accountManager.canAddAccount) {
      ElMessage.warning('账号数量已达上限（最多 5 个），请先删除其他账号')
      return
    }
    
    // 添加到账号列表（不切换当前账号）
    const added = accountManager.addOrUpdateAccount({
      userId: res.data.userId,
      username: res.data.username,
      nickname: res.data.nickname,
      token: res.data.token,
      role: res.data.role,
      lastUsedAt: Date.now()
    })
    
    if (added) {
      ElMessage.success(`账号 "${res.data.nickname || res.data.username}" 已添加到列表`)
      visible.value = false
      emit('success')
    }
  } catch (error: any) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

/**
 * 关闭弹窗
 */
function handleClose() {
  form.username = ''
  form.password = ''
  formRef.value?.clearValidate()
}
</script>

<style scoped>
.login-dialog {
  padding: 8px 0;
}

.login-tip {
  margin: 0 0 20px;
  padding: 12px;
  background: var(--primary-light);
  border-left: 3px solid var(--primary);
  border-radius: 4px;
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>

