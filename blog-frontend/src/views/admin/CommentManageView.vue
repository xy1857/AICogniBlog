<template>
  <div>
    <div class="page-header">
      <h2>评论管理</h2>
      <el-radio-group v-model="statusFilter" @change="fetchComments">
        <el-radio-button :value="undefined">全部</el-radio-button>
        <el-radio-button :value="0">待审核</el-radio-button>
        <el-radio-button :value="1">已发布</el-radio-button>
        <el-radio-button :value="2">已拒绝</el-radio-button>
      </el-radio-group>
    </div>

    <el-card>
      <el-table :data="comments" v-loading="loading" stripe>
        <el-table-column label="评论内容" min-width="200">
          <template #default="{ row }">
            <div>{{ row.content }}</div>
            <div v-if="row.aiReplySuggestion" style="margin-top:4px">
              <el-tag size="small" type="success">AI建议回复：</el-tag>
              <span style="font-size:12px;color:#67c23a;margin-left:4px">{{ row.aiReplySuggestion }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="评论者" width="100">
          <template #default="{ row }">{{ row.user?.nickname }}</template>
        </el-table-column>
        <el-table-column label="所属文章" width="160">
          <template #default="{ row }">
            <el-tooltip :content="row.article?.title" placement="top">
              <span class="ellipsis">{{ row.article?.title }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="AI审核" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.aiAuditResult === 1" type="success" size="small">安全</el-tag>
            <el-tag v-else-if="row.aiAuditResult === 0" type="danger" size="small">违规</el-tag>
            <el-tag v-else type="info" size="small">待审</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status !== 1" text size="small" type="success" @click="approve(row.id)">通过</el-button>
            <el-button v-if="row.status !== 2" text size="small" type="warning" @click="reject(row.id)">拒绝</el-button>
            <el-button text size="small" type="primary" @click="aiReview(row.id)">AI审核</el-button>
            <el-popconfirm title="确认删除？" @confirm="deleteComment(row.id)">
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
        @current-change="fetchComments"
        style="margin-top:16px;justify-content:flex-end;display:flex"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { commentApi } from '@/api/comment'
import { ElMessage } from 'element-plus'

const comments = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)
const statusFilter = ref<number | undefined>(undefined)

async function fetchComments() {
  loading.value = true
  try {
    const res = await commentApi.adminList({
      page: currentPage.value,
      size: pageSize,
      status: statusFilter.value,
    }) as any
    comments.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function approve(id: number) {
  await commentApi.approve(id)
  ElMessage.success('已通过')
  fetchComments()
}

async function reject(id: number) {
  await commentApi.reject(id)
  ElMessage.success('已拒绝')
  fetchComments()
}

async function deleteComment(id: number) {
  await commentApi.delete(id)
  ElMessage.success('删除成功')
  fetchComments()
}

async function aiReview(id: number) {
  const res = await commentApi.aiReview(id) as any
  ElMessage.success(`AI审核完成：${res.data.safe ? '安全' : '违规'}`)
  fetchComments()
}

function statusLabel(status: number) {
  return { 0: '待审核', 1: '已发布', 2: '已拒绝' }[status] ?? '-'
}

function statusType(status: number) {
  return { 0: 'warning', 1: 'success', 2: 'danger' }[status] ?? 'info'
}

function formatDate(date: string) {
  return date ? new Date(date).toLocaleString('zh-CN') : '-'
}

onMounted(fetchComments)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}
.page-header h2 {
  margin: 0;
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 600;
  color: var(--text-primary);
}
.ellipsis {
  max-width: 140px;
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
