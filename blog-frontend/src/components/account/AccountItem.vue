<template>
  <div class="account-item" :class="{ 'account-item--current': isCurrent }">
    <div class="account-avatar">
      <img v-if="account.avatarBase64" :src="account.avatarBase64" alt="头像" />
      <span v-else class="avatar-placeholder">{{ account.nickname?.charAt(0) || account.username.charAt(0) }}</span>
    </div>
    
    <div class="account-info">
      <div class="account-name">
        {{ account.nickname || account.username }}
        <span class="account-badge" v-if="account.role === 1">管理员</span>
      </div>
      <div class="account-meta">
        <span class="account-username">@{{ account.username }}</span>
        <span class="account-divider">·</span>
        <span class="account-time">{{ lastUsedText }}</span>
      </div>
    </div>

    <div class="account-actions">
      <el-tag v-if="isCurrent" type="primary" size="small">当前</el-tag>
      <template v-else>
        <el-button 
          type="primary" 
          size="small" 
          @click="$emit('switch')"
          :loading="switching"
        >
          切换
        </el-button>
        <el-button 
          type="danger" 
          size="small" 
          text
          @click="handleDelete"
        >
          删除
        </el-button>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { AccountInfo } from '@/stores/accountManager'

interface Props {
  account: AccountInfo
  isCurrent: boolean
  switching?: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  switch: []
  delete: []
}>()

// 计算最后使用时间文本
const lastUsedText = computed(() => {
  const now = Date.now()
  const diff = now - props.account.lastUsedAt
  
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  if (seconds < 60) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return new Date(props.account.lastUsedAt).toLocaleDateString()
})

// 删除账号
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除账号 "${props.account.nickname || props.account.username}" 吗？`,
      '删除账号',
      {
        type: 'warning',
        confirmButtonText: '删除',
        cancelButtonText: '取消',
      }
    )
    emit('delete')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.account-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  transition: all 0.2s;
}

.account-item:hover {
  border-color: var(--primary);
  box-shadow: 0 2px 8px rgba(14, 165, 233, 0.1);
}

.account-item--current {
  background: var(--primary-light);
  border-color: var(--primary);
}

.account-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.account-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-primary);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.account-info {
  flex: 1;
  min-width: 0;
}

.account-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.account-badge {
  font-size: 11px;
  padding: 2px 6px;
  background: var(--gradient-primary);
  color: #fff;
  border-radius: 4px;
}

.account-meta {
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.account-username {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.account-divider {
  opacity: 0.5;
}

.account-time {
  white-space: nowrap;
}

.account-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}
</style>

