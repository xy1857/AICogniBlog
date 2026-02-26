<template>
  <div>
    <div class="page-header">
      <h2>用户管理</h2>
      <el-input v-model="keyword" placeholder="搜索用户名/昵称/邮箱" clearable
        style="width:240px" @keyup.enter="fetchUsers" @clear="fetchUsers">
        <template #suffix><el-icon style="cursor:pointer" @click="fetchUsers"><Search /></el-icon></template>
      </el-input>
    </div>

    <el-card>
      <el-table :data="users" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="角色" width="90">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'danger' : 'primary'" size="small">
              {{ row.role === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后登录" width="160">
          <template #default="{ row }">{{ formatDate(row.lastLoginAt) }}</template>
        </el-table-column>
        <el-table-column label="注册时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <!-- 启用/禁用 -->
            <el-button text size="small" :type="row.status === 1 ? 'warning' : 'success'"
              @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <!-- 角色切换 -->
            <el-button text size="small" type="primary" @click="toggleRole(row)">
              {{ row.role === 1 ? '降为普通' : '升为管理员' }}
            </el-button>
            <!-- 重置密码 -->
            <el-popconfirm title="确认重置该用户密码？" @confirm="resetPassword(row.id)">
              <template #reference>
                <el-button text size="small" type="danger">重置密码</el-button>
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
        @current-change="fetchUsers"
        style="margin-top:16px;justify-content:flex-end;display:flex"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { userApi } from '@/api/user'
import { ElMessage } from 'element-plus'

const users = ref<any[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)
const keyword = ref('')

async function fetchUsers() {
  loading.value = true
  try {
    const res = await userApi.adminList({
      page: currentPage.value,
      size: pageSize,
      keyword: keyword.value || undefined,
    }) as any
    users.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function toggleStatus(row: any) {
  const newStatus = row.status === 1 ? 0 : 1
  await userApi.adminUpdateStatus(row.id, newStatus)
  row.status = newStatus
  ElMessage.success(newStatus === 1 ? '账号已启用' : '账号已禁用')
}

async function toggleRole(row: any) {
  const newRole = row.role === 1 ? 0 : 1
  await userApi.adminUpdateRole(row.id, newRole)
  row.role = newRole
  ElMessage.success('角色修改成功')
}

async function resetPassword(id: number) {
  const res = await userApi.adminResetPassword(id) as any
  ElMessage.success(`密码已重置为：${res.data.newPassword}`)
}

function formatDate(date: string) {
  return date ? new Date(date).toLocaleString('zh-CN') : '-'
}

onMounted(fetchUsers)
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
</style>
