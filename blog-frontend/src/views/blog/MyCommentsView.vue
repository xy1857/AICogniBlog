<template>
  <div class="my-comments-page">
    <h2 class="page-title">我的评论</h2>
    <div v-if="loading" class="loading-wrap">
      <el-skeleton :rows="5" animated />
    </div>
    <template v-else>
      <el-empty v-if="!comments.length" description="暂无评论" />
      <div v-else class="comment-list">
        <div
          v-for="c in comments"
          :key="c.id"
          class="comment-item"
          @click="$router.push(`/article/${c.articleId}`)"
        >
          <div class="comment-content">{{ c.content }}</div>
          <div class="comment-meta">
            <span class="comment-article">{{ c.articleTitle ?? '未知文章' }}</span>
            <el-tag v-if="c.status === 0" size="small" type="warning">待审核</el-tag>
            <el-tag v-else-if="c.status === 1" size="small" type="success">已发布</el-tag>
            <el-tag v-else-if="c.status === 2" size="small" type="danger">已拒绝</el-tag>
            <span class="comment-date">{{ formatDate(c.createdAt) }}</span>
          </div>
        </div>
      </div>
      <el-pagination
        v-if="total > pageSize"
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="fetchComments"
        class="pagination"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { userApi } from '@/api/user'

const comments = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)

async function fetchComments() {
  loading.value = true
  try {
    const res = await userApi.myComments({
      page: currentPage.value,
      size: pageSize,
    }) as any
    comments.value = res.data.records ?? []
    total.value = res.data.total ?? 0
  } finally {
    loading.value = false
  }
}

function formatDate(date: string) {
  return date ? new Date(date).toLocaleString('zh-CN') : ''
}

onMounted(fetchComments)
</script>

<style scoped>
.my-comments-page {
  animation: fadeIn 0.4s ease;
}
.page-title {
  margin: 0 0 24px;
  font-size: 20px;
  color: var(--text-primary);
}
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.comment-item {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
}
.comment-item:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-sm);
}
.comment-content {
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 8px;
}
.comment-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--text-muted);
}
.comment-article {
  flex: 1;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.comment-date {
  flex-shrink: 0;
}
.pagination {
  margin-top: 24px;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
