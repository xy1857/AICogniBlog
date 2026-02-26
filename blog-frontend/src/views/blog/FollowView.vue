<template>
  <div class="follow-page">
    <h2 class="page-title">我的关注</h2>
    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="5" animated />
    </div>
    <template v-else>
      <el-empty v-if="!list.length" description="暂无关注，去文章页关注作者吧" />
      <div v-else class="follow-list">
        <div v-for="user in list" :key="user.id" class="follow-item">
          <div class="user-avatar">{{ user.nickname?.charAt(0) }}</div>
          <div class="user-info">
            <span class="user-name">{{ user.nickname }}</span>
          </div>
          <el-button type="danger" text size="small" @click="unfollow(user.id)">取消关注</el-button>
        </div>
      </div>
      <el-pagination
        v-if="total > pageSize"
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="fetchList"
        class="pagination"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { followApi } from '@/api/follow'
import { ElMessage } from 'element-plus'

const list = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)

async function fetchList() {
  loading.value = true
  try {
    const res = await followApi.listFollowing({
      page: currentPage.value,
      size: pageSize,
    }) as any
    list.value = res.data.records ?? []
    total.value = res.data.total ?? 0
  } finally {
    loading.value = false
  }
}

async function unfollow(userId: number) {
  try {
    await followApi.unfollow(userId)
    ElMessage.success('已取消关注')
    fetchList()
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message ?? '操作失败')
  }
}

onMounted(fetchList)
</script>

<style scoped>
.follow-page {
  animation: fadeIn 0.4s ease;
}
.page-title {
  margin: 0 0 24px;
  font-size: 20px;
  color: var(--text-primary);
}
.follow-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.follow-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
}
.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #fff;
  margin-right: 16px;
}
.user-info {
  flex: 1;
}
.user-name {
  font-weight: 600;
  color: var(--text-primary);
}
.pagination {
  margin-top: 24px;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
