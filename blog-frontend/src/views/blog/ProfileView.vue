<template>
  <div class="profile-page">
    <el-row :gutter="24" justify="center">
      <el-col :span="14">
        <el-tabs v-model="activeTab">
          <!-- 基本信息 -->
          <el-tab-pane label="个人资料" name="info">
            <el-card>
              <el-form :model="profileForm" label-width="80px">
                <el-form-item label="用户名">
                  <el-input :value="profile?.username" disabled />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input :value="profile?.email" disabled />
                </el-form-item>
                <el-form-item label="昵称">
                  <el-input v-model="profileForm.nickname" />
                </el-form-item>
                <el-form-item label="简介">
                  <el-input v-model="profileForm.bio" type="textarea" :rows="3" />
                </el-form-item>
                <el-form-item label="头像">
                  <el-upload
                    action="#"
                    :auto-upload="false"
                    :show-file-list="false"
                    accept="image/*"
                    :on-change="handleAvatarChange"
                  >
                    <el-avatar :size="64" :src="avatarPreview || profile?.avatarBase64">{{ profile?.nickname?.charAt(0) }}</el-avatar>
                    <div style="margin-top:8px;font-size:12px;color:#999">点击更换头像</div>
                  </el-upload>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="saving" @click="saveProfile">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-tab-pane>

          <!-- 修改密码 -->
          <el-tab-pane label="修改密码" name="password">
            <el-card>
              <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="changingPwd" @click="changePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import type { FormInstance, UploadFile } from 'element-plus'
import { userApi } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const auth = useAuthStore()
const activeTab = ref('info')
const profile = ref<any>(null)
const saving = ref(false)
const changingPwd = ref(false)
const avatarPreview = ref('')
const avatarBase64 = ref('')
const pwdFormRef = ref<FormInstance>()

const profileForm = reactive({ nickname: '', bio: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码' }],
  newPassword: [{ required: true, min: 6, message: '新密码至少6位' }],
}

async function handleAvatarChange(file: UploadFile) {
  const raw = file.raw!
  if (!raw.type.startsWith('image/')) return ElMessage.error('请上传图片文件')
  if (raw.size > 2 * 1024 * 1024) return ElMessage.error('头像图片不能超过 2MB')
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarBase64.value = e.target?.result as string
    avatarPreview.value = avatarBase64.value
  }
  reader.readAsDataURL(raw)
}

async function saveProfile() {
  saving.value = true
  try {
    await userApi.updateProfile({
      nickname: profileForm.nickname,
      bio: profileForm.bio,
      avatarBase64: avatarBase64.value || undefined,
    })
    auth.nickname = profileForm.nickname
    localStorage.setItem('nickname', profileForm.nickname)
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}

async function changePassword() {
  await pwdFormRef.value?.validate()
  changingPwd.value = true
  try {
    await userApi.updatePassword(pwdForm)
    ElMessage.success('密码修改成功，请重新登录')
    auth.clearUser()
  } finally {
    changingPwd.value = false
  }
}

onMounted(async () => {
  const res = await userApi.getProfile() as any
  profile.value = res.data
  profileForm.nickname = res.data.nickname || ''
  profileForm.bio = res.data.bio || ''
})
</script>

<style scoped>
.profile-page {
  padding: 24px 0;
}
.profile-page :deep(.el-card) {
  border-radius: 16px;
}
</style>
