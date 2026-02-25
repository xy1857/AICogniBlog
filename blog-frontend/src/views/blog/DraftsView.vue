<template>
  <div class="drafts-page">
    <div class="page-header">
      <h2>我的草稿</h2>
      <el-button type="primary" @click="$router.push('/articles/create')">
        <el-icon><Plus /></el-icon> 写文章
      </el-button>
    </div>

    <el-card v-loading="loading">
      <template v-if="drafts.length === 0 && !loading">
        <el-empty description="暂无草稿，快去写点什么吧～" :image-size="120">
          <el-button type="primary" @click="$router.push('/articles/create')">开始创作</el-button>
        </el-empty>
      </template>

      <template v-else>
        <div class="draft-list">
          <div v-for="draft in drafts" :key="draft.id" class="draft-item">
            <div class="draft-info">
              <h3 class="draft-title">{{ draft.title || '（无标题）' }}</h3>
              <p class="draft-summary">{{ draft.summary || '暂无摘要' }}</p>
              <div class="draft-meta">
                <el-icon><Clock /></el-icon>
                <span>最后修改：{{ formatDate(draft.updatedAt || draft.createdAt) }}</span>
              </div>
            </div>
            <div class="draft-actions">
              <el-button type="primary" size="small" @click="$router.push(`/articles/edit/${draft.id}`)">
                继续编辑
              </el-button>
              <el-popconfirm title="确认删除该草稿？" @confirm="deleteDraft(draft.id)">
                <template #reference>
                  <el-button size="small" type="danger" plain>删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>

        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="currentPage"
          @current-change="fetchDrafts"
          style="margin-top: 16px; justify-content: flex-end; display: flex"
        />
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { articleApi } from '@/api/article'
import { ElMessage } from 'element-plus'
import { Plus, Clock } from '@element-plus/icons-vue'

const drafts = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)

async function fetchDrafts() {
  loading.value = true
  try {
    const res = await articleApi.myDrafts({ page: currentPage.value, size: pageSize }) as any
    drafts.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function deleteDraft(id: number) {
  await articleApi.delete(id)
  ElMessage.success('草稿已删除')
  fetchDrafts()
}

function formatDate(date: string) {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit',
  })
}

onMounted(fetchDrafts)
</script>

<style scoped>
.drafts-page {
  max-width: 860px;
  margin: 0 auto;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h2 {
  margin: 0;
  font-size: 20px;
}
.draft-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.draft-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  gap: 16px;
}
.draft-item:last-child {
  border-bottom: none;
}
.draft-info {
  flex: 1;
  min-width: 0;
}
.draft-title {
  margin: 0 0 4px;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.draft-summary {
  margin: 0 0 8px;
  font-size: 13px;
  color: #888;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.draft-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #aaa;
}
.draft-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}
</style>
