<template>
  <div>
    <div class="page-header">
      <h2>{{ isEdit ? '编辑文章' : '写文章' }}</h2>
      <div style="display:flex;gap:8px">
        <el-button @click="$router.back()">取消</el-button>
        <el-button @click="save(0)" :loading="saving">保存草稿</el-button>
        <el-button type="primary" @click="save(1)" :loading="saving">发布文章</el-button>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :span="17">
        <el-card>
          <el-input v-model="form.title" placeholder="文章标题" size="large"
            style="font-size:18px;margin-bottom:16px" />

          <!-- Markdown 编辑器 -->
          <div class="editor-wrap">
            <el-input
              v-model="form.contentMd"
              type="textarea"
              :rows="25"
              placeholder="使用 Markdown 格式编写文章内容..."
              style="font-family: monospace"
            />
          </div>
        </el-card>
      </el-col>

      <el-col :span="7">
        <el-card>
          <template #header>文章设置</template>
          <el-form label-width="70px" size="small">
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" placeholder="选择分类" clearable style="width:100%">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="标签">
              <el-select v-model="form.tagIds" multiple placeholder="选择标签" style="width:100%">
                <el-option v-for="tag in tags" :key="tag.id" :label="tag.name" :value="tag.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="摘要">
              <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="文章摘要（留空则自动截取）" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { articleApi } from '@/api/article'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const saving = ref(false)
const categories = ref<any[]>([])
const tags = ref<any[]>([])
const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '',
  summary: '',
  contentMd: '',
  categoryId: undefined as number | undefined,
  tagIds: [] as number[],
  status: 0,
})

async function save(status: number) {
  if (!form.title.trim()) return ElMessage.warning('请输入文章标题')
  if (!form.contentMd.trim()) return ElMessage.warning('请输入文章内容')
  saving.value = true
  try {
    const payload = { ...form, status }
    if (isEdit.value) {
      await articleApi.update(Number(route.params.id), payload)
      ElMessage.success('更新成功')
    } else {
      await articleApi.create(payload)
      ElMessage.success(status === 1 ? '发布成功' : '草稿已保存')
      const auth = (await import('@/stores/auth')).useAuthStore()
      router.push(auth.isAdmin ? '/admin/articles' : '/')
    }
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const [catRes, tagRes] = await Promise.all([articleApi.categories(), articleApi.tags()]) as any[]
  categories.value = catRes.data
  tags.value = tagRes.data

  if (isEdit.value) {
    const res = await articleApi.getForEdit(Number(route.params.id)) as any
    const a = res.data
    form.title = a.title
    form.summary = a.summary || ''
    form.contentMd = a.contentMd || ''
    form.categoryId = a.categoryId
    form.tagIds = a.tagIds || []
    form.status = a.status
  }
})
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; }
.editor-wrap { border: 1px solid #ddd; border-radius: 4px; }
</style>
