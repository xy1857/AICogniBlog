<template>
  <div>
    <div class="page-header">
      <h2>文章管理</h2>
      <el-button type="primary" @click="$router.push('/admin/articles/create')">
        <el-icon><Plus /></el-icon> 写文章
      </el-button>
    </div>

    <el-card>
      <el-table :data="articles" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <a :href="`/article/${row.id}`" target="_blank" class="article-link">{{ row.title }}</a>
          </template>
        </el-table-column>
        <el-table-column label="分类" width="100">
          <template #default="{ row }">{{ row.category?.name || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column label="发布时间" width="160">
          <template #default="{ row }">{{ formatDate(row.publishedAt || row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button text size="small" type="primary" @click="$router.push(`/admin/articles/edit/${row.id}`)">编辑</el-button>
            <el-popconfirm title="确认删除该文章？" @confirm="deleteArticle(row.id)">
              <template #reference>
                <el-button text size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        background
        layout="prev, pager, next, total"
        :total="total"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="fetchArticles"
        style="margin-top:16px;justify-content:flex-end;display:flex"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { articleApi } from '@/api/article'
import { ElMessage } from 'element-plus'

const articles = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)

async function fetchArticles() {
  loading.value = true
  try {
    const res = await articleApi.list({ page: currentPage.value, size: pageSize }) as any
    articles.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function deleteArticle(id: number) {
  await articleApi.delete(id)
  ElMessage.success('删除成功')
  fetchArticles()
}

function formatDate(date: string) {
  return date ? new Date(date).toLocaleString('zh-CN') : '-'
}

onMounted(fetchArticles)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-header h2 {
  margin: 0;
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  color: var(--text-primary);
}
.article-link {
  color: var(--primary);
  text-decoration: none;
}
.article-link:hover {
  text-decoration: underline;
  color: var(--primary-hover);
}
</style>
