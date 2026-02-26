<template>
  <div class="latest-comments-page">
    <h2 class="page-title">最新评论</h2>
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
          @click="$router.push(`/article/${c.article?.id}`)"
        >
          <div class="comment-content">{{ c.content }}</div>
          <div class="comment-meta">
            <span class="comment-user">{{ c.user?.nickname ?? '匿名' }}</span>
            <span class="comment-sep">·</span>
            <span class="comment-article">{{ c.article?.title ?? '未知文章' }}</span>
            <span class="comment-date">{{ formatDate(c.createdAt) }}</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { commentApi } from '@/api/comment'

const comments = ref<any[]>([])
const loading = ref(false)

async function fetchLatest() {
  loading.value = true
  try {
    const res = await commentApi.latest({ limit: 50 }) as any
    comments.value = res.data ?? []
  } finally {
    loading.value = false
  }
}

function formatDate(date: string) {
  return date ? new Date(date).toLocaleString('zh-CN') : ''
}

onMounted(fetchLatest)
</script>

<style scoped>
.latest-comments-page {
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
  font-size: 12px;
  color: var(--text-muted);
}
.comment-user { font-weight: 500; color: var(--primary); }
.comment-sep { margin: 0 6px; }
.comment-article { color: var(--text-secondary); }
.comment-date { margin-left: auto; float: right; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
