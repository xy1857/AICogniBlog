<template>
  <el-dialog
    v-model="visible"
    title="切换账号"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="account-switcher">
      <!-- 账号列表 -->
      <div v-if="sortedAccounts.length > 0" class="account-list">
        <AccountItem
          v-for="account in sortedAccounts"
          :key="account.userId"
          :account="account"
          :is-current="account.userId === accountManager.currentUserId"
          :switching="switchingUserId === account.userId"
          @switch="handleSwitch(account)"
          @delete="handleDelete(account.userId)"
        />
      </div>

      <!-- 空状态 -->
      <el-empty v-else description="暂无保存的账号" :image-size="100" />

      <!-- 添加账号按钮 -->
      <div class="add-account-section">
        <el-button 
          type="primary" 
          :icon="Plus" 
          @click="handleAddAccount"
          :disabled="!accountManager.canAddAccount"
          class="add-account-btn"
        >
          添加新账号
        </el-button>
        <div v-if="!accountManager.canAddAccount" class="account-limit-tip">
          已达到账号数量上限（最多 5 个）
        </div>
      </div>
    </div>
  </el-dialog>

  <!-- 登录弹窗 -->
  <LoginDialog v-model="showLoginDialog" @success="handleLoginSuccess" />
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import AccountItem from './AccountItem.vue'
import LoginDialog from './LoginDialog.vue'
import { useAccountManagerStore, type AccountInfo } from '@/stores/accountManager'
import { useAuthStore } from '@/stores/auth'

interface Props {
  modelValue: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>()

const router = useRouter()
const accountManager = useAccountManagerStore()
const authStore = useAuthStore()

const switchingUserId = ref<number>(0)
const showLoginDialog = ref(false)

// 双向绑定
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 获取排序后的账号列表
const sortedAccounts = computed(() => accountManager.getSortedAccounts())

/**
 * 切换账号
 */
const handleSwitch = async (account: AccountInfo) => {
  // 检查 Token 是否过期
  if (accountManager.isTokenExpired(account.token)) {
    ElMessage.warning('该账号登录已过期，请重新登录')
    // 跳转到登录页，并传递用户名
    visible.value = false
    router.push({
      path: '/login',
      query: { username: account.username }
    })
    return
  }

  switchingUserId.value = account.userId

  try {
    // 切换账号
    accountManager.switchAccount(account.userId)
    
    // 恢复登录状态
    authStore.restoreFromAccount(account)
    
    ElMessage.success(`已切换到账号：${account.nickname || account.username}`)
    
    // 关闭弹窗
    visible.value = false
    
    // 刷新页面以更新数据
    setTimeout(() => {
      window.location.reload()
    }, 300)
  } catch (error) {
    console.error('切换账号失败:', error)
    ElMessage.error('切换账号失败，请重试')
  } finally {
    switchingUserId.value = 0
  }
}

/**
 * 删除账号
 */
const handleDelete = (userId: number) => {
  accountManager.removeAccount(userId)
  ElMessage.success('账号已删除')
}

/**
 * 添加新账号
 */
const handleAddAccount = () => {
  // 打开登录弹窗
  showLoginDialog.value = true
}

/**
 * 登录成功回调
 */
const handleLoginSuccess = () => {
  // 账号已添加，无需其他操作
  // 账号列表会自动更新
}

/**
 * 关闭弹窗
 */
const handleClose = () => {
  switchingUserId.value = 0
}
</script>

<style scoped>
.account-switcher {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.account-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
  padding: 4px;
}

.account-list::-webkit-scrollbar {
  width: 6px;
}

.account-list::-webkit-scrollbar-thumb {
  background: var(--border);
  border-radius: 3px;
}

.account-list::-webkit-scrollbar-thumb:hover {
  background: var(--text-secondary);
}

.add-account-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding-top: 8px;
  border-top: 1px solid var(--border);
}

.add-account-btn {
  width: 100%;
}

.account-limit-tip {
  font-size: 12px;
  color: var(--text-muted);
}
</style>

