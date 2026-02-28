<template>
  <div class="page-builder" v-if="pageStore.currentPage">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button :icon="HomeFilled" @click="handleBackHome" text>
          回到主页
        </el-button>
        <el-divider direction="vertical" />
        <h2>页面装修</h2>
        <el-input
          v-model="pageStore.currentPage.name"
          placeholder="页面名称"
          style="width: 200px; margin-left: 1rem"
        />
      </div>

      <div class="toolbar-right">
        <el-button :icon="RefreshLeft" :disabled="!pageStore.canUndo" @click="pageStore.undo()">
          撤销
        </el-button>
        <el-button :icon="RefreshRight" :disabled="!pageStore.canRedo" @click="pageStore.redo()">
          重做
        </el-button>
        <el-button :icon="View" @click="handlePreview">预览</el-button>
        <el-button :icon="House" @click="handleViewMyHome">查看我的主页</el-button>
        <el-dropdown trigger="click" @command="handleCommand">
          <el-button type="primary" :icon="Check">
            保存
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="save">
                <el-icon><Check /></el-icon>
                保存配置
              </el-dropdown-item>
              <el-dropdown-item command="setDefault">
                <el-icon><Star /></el-icon>
                保存并设为默认主页
              </el-dropdown-item>
              <el-dropdown-item command="cancelDefault" divided>
                <el-icon><Close /></el-icon>
                取消默认主页
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="builder-content">
      <!-- 左侧组件库 -->
      <div class="sidebar-left">
        <ComponentLibrary @drag-start="handleDragStart" />
      </div>

      <!-- 中间画布区 -->
      <div class="canvas-area">
        <div class="canvas-wrapper">
          <div 
            class="canvas"
            :style="canvasStyle"
            @dragover.prevent
            @drop="handleDrop"
          >
            <draggable
              v-model="components"
              item-key="id"
              handle=".drag-handle"
              @start="isDragging = true"
              @end="handleDragEnd"
            >
              <template #item="{ element }">
                <div
                  class="component-wrapper"
                  :class="{ selected: pageStore.selectedComponentId === element.id }"
                  @click.stop="selectComponent(element.id)"
                >
                  <div class="component-actions">
                    <el-icon class="drag-handle"><Rank /></el-icon>
                    <el-icon class="action-btn" @click.stop="duplicateComponent(element)">
                      <CopyDocument />
                    </el-icon>
                    <el-icon class="action-btn delete" @click.stop="deleteComponent(element.id)">
                      <Delete />
                    </el-icon>
                  </div>

                  <div :style="element.style">
                    <component
                      :is="getComponentByType(element.type)"
                      v-bind="element.props"
                    />
                  </div>
                </div>
              </template>
            </draggable>

            <!-- 空状态 -->
            <div v-if="components.length === 0" class="empty-canvas">
              <el-icon class="empty-icon"><Plus /></el-icon>
              <p>从左侧拖拽组件到这里开始装修</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧配置面板 -->
      <div class="sidebar-right">
        <el-tabs v-model="activeTab" class="config-tabs">
          <el-tab-pane label="组件配置" name="component">
            <div v-if="pageStore.selectedComponent" class="config-content">
              <ConfigPanel
                :title="getComponentMeta(pageStore.selectedComponent.type).name"
                :schema="getComponentMeta(pageStore.selectedComponent.type).schema"
                :model-value="pageStore.selectedComponent.props"
                :style-value="pageStore.selectedComponent.style"
                @update:model-value="updateComponentProps"
                @update:style-value="updateComponentStyle"
              />
            </div>
            <div v-else class="empty-config">
              <el-icon class="empty-icon"><Setting /></el-icon>
              <p>请选择一个组件进行配置</p>
            </div>
          </el-tab-pane>

          <el-tab-pane label="主题设置" name="theme">
            <div class="theme-config" v-if="pageStore.currentPage">
              <el-form label-position="top">
                <el-form-item label="主色调">
                  <el-color-picker
                    v-model="pageStore.currentPage.theme.primaryColor"
                    @change="handleThemeChange"
                  />
                </el-form-item>

                <el-form-item label="背景色">
                  <el-color-picker
                    v-model="pageStore.currentPage.theme.backgroundColor"
                    @change="handleThemeChange"
                  />
                </el-form-item>

                <el-form-item label="文字颜色">
                  <el-color-picker
                    v-model="pageStore.currentPage.theme.textColor"
                    @change="handleThemeChange"
                  />
                </el-form-item>

                <el-form-item label="字体">
                  <el-select
                    v-model="pageStore.currentPage.theme.fontFamily"
                    @change="handleThemeChange"
                  >
                    <el-option label="Inter" value="Inter, system-ui, sans-serif" />
                    <el-option label="思源黑体" value="'Source Han Sans', sans-serif" />
                    <el-option label="霞鹜文楷" value="'LXGW WenKai', serif" />
                    <el-option label="JetBrains Mono" value="'JetBrains Mono', monospace" />
                  </el-select>
                </el-form-item>

                <el-form-item label="圆角">
                  <el-input
                    v-model="pageStore.currentPage.theme.borderRadius"
                    placeholder="例如: 8px"
                    @change="handleThemeChange"
                  />
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" title="页面预览" width="90%" fullscreen>
      <div class="preview-content" :style="canvasStyle">
        <component
          v-for="comp in components"
          :key="comp.id"
          :is="getComponentByType(comp.type)"
          v-bind="comp.props"
          :style="comp.style"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import {
  RefreshLeft,
  RefreshRight,
  View,
  Check,
  Rank,
  CopyDocument,
  Delete,
  Plus,
  Setting,
  House,
  HomeFilled,
  ArrowDown,
  Star,
  Close,
} from '@element-plus/icons-vue';
import draggable from 'vuedraggable';
import { useRouter } from 'vue-router';
import { usePageBuilderStore } from '@/stores/pageBuilder';
import { useAuthStore } from '@/stores/auth';
import { getComponentMeta, createComponent } from '@/components/page-builder/componentRegistry';
import ComponentLibrary from '@/components/page-builder/ComponentLibrary.vue';
import ConfigPanel from '@/components/page-builder/ConfigPanel.vue';
import HeroBanner from '@/components/page-builder/widgets/HeroBanner.vue';
import ArticleGrid from '@/components/page-builder/widgets/ArticleGrid.vue';
import CategoryNav from '@/components/page-builder/widgets/CategoryNav.vue';
import FriendLinks from '@/components/page-builder/widgets/FriendLinks.vue';
import InteractiveWidget from '@/components/page-builder/widgets/InteractiveWidget.vue';
import type { ComponentType, ComponentConfig } from '@/types/page-builder';

const pageStore = usePageBuilderStore();
const auth = useAuthStore();
const router = useRouter();
const activeTab = ref('component');
const isDragging = ref(false);
const previewVisible = ref(false);

const components = computed({
  get: () => pageStore.currentPage?.components || [],
  set: (value) => pageStore.updateComponentsOrder(value),
});

const canvasStyle = computed(() => {
  if (!pageStore.currentPage) return {};
  const theme = pageStore.currentPage.theme;
  return {
    backgroundColor: theme.backgroundColor,
    color: theme.textColor,
    fontFamily: theme.fontFamily,
    '--primary-color': theme.primaryColor,
    '--border-radius': theme.borderRadius,
  };
});

const getComponentByType = (type: ComponentType) => {
  const componentMap = {
    HeroBanner,
    ArticleGrid,
    CategoryNav,
    FriendLinks,
    InteractiveWidget,
  };
  return componentMap[type];
};

const handleDragStart = () => {
  isDragging.value = true;
};

const handleDrop = (event: DragEvent) => {
  event.preventDefault();
  const componentType = event.dataTransfer?.getData('componentType') as ComponentType;
  if (componentType) {
    const newComponent = createComponent(componentType);
    pageStore.addComponent(newComponent);
    ElMessage.success('组件已添加');
  }
  isDragging.value = false;
};

const handleDragEnd = () => {
  isDragging.value = false;
};

const selectComponent = (id: string) => {
  pageStore.selectComponent(id);
  activeTab.value = 'component';
};

const deleteComponent = (id: string) => {
  pageStore.removeComponent(id);
  ElMessage.success('组件已删除');
};

const duplicateComponent = (component: ComponentConfig) => {
  const newComponent = {
    ...JSON.parse(JSON.stringify(component)),
    id: `${component.type}-${Date.now()}`,
  };
  pageStore.addComponent(newComponent);
  ElMessage.success('组件已复制');
};

const updateComponentProps = (props: Record<string, any>) => {
  if (pageStore.selectedComponentId) {
    pageStore.updateComponentProps(pageStore.selectedComponentId, props);
  }
};

const updateComponentStyle = (style: Record<string, any>) => {
  if (pageStore.selectedComponentId) {
    pageStore.updateComponentStyle(pageStore.selectedComponentId, style);
  }
};

const handleThemeChange = () => {
  // 主题变化会自动通过响应式更新
};

const handlePreview = () => {
  previewVisible.value = true;
};

const handleSave = () => {
  pageStore.saveToLocal();
  
  // 同时保存到用户专属的 key（用于个人主页访问）
  if (auth.username) {
    localStorage.setItem(`pageConfig_${auth.username}`, JSON.stringify(pageStore.currentPage));
  }
  
  ElMessage.success('保存成功');
  // TODO: 调用后端API保存
};

const handleSetAsDefault = () => {
  if (!auth.username) {
    ElMessage.warning('请先登录');
    return;
  }
  
  // 保存配置
  handleSave();
  
  // 设置为默认主页
  localStorage.setItem(`autoShowCustomPage_${auth.username}`, 'true');
  
  ElMessage.success('已设为默认主页，下次访问首页将自动显示装修页面');
};

const handleCancelDefault = () => {
  if (!auth.username) {
    return;
  }
  
  localStorage.removeItem(`autoShowCustomPage_${auth.username}`);
  ElMessage.success('已取消默认主页设置');
};

const handleViewMyHome = () => {
  // 先保存
  handleSave();
  
  // 打开个人主页
  if (auth.username) {
    const url = router.resolve({ name: 'UserHome', params: { username: auth.username } });
    window.open(url.href, '_blank');
  } else {
    ElMessage.warning('请先登录');
  }
};

const handleBackHome = () => {
  router.push('/');
};

const handleCommand = (command: string) => {
  switch (command) {
    case 'save':
      handleSave();
      break;
    case 'setDefault':
      handleSetAsDefault();
      break;
    case 'cancelDefault':
      handleCancelDefault();
      break;
  }
};

onMounted(() => {
  // 尝试从本地存储加载
  pageStore.loadFromLocal();
  
  // 如果没有页面配置，初始化一个新页面
  if (!pageStore.currentPage) {
    pageStore.initPage();
  }
});
</script>

<style scoped>
.page-builder {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.toolbar-left h2 {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
}

.toolbar-right {
  display: flex;
  gap: 0.5rem;
}

.builder-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.sidebar-left {
  width: 280px;
  flex-shrink: 0;
}

.canvas-area {
  flex: 1;
  overflow: auto;
  padding: 2rem;
}

.canvas-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

.canvas {
  min-height: 600px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: relative;
}

.component-wrapper {
  position: relative;
  transition: all 0.3s ease;
}

.component-wrapper:hover {
  outline: 2px dashed #409EFF;
  outline-offset: 4px;
}

.component-wrapper.selected {
  outline: 2px solid #409EFF;
  outline-offset: 4px;
}

.component-actions {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  display: flex;
  gap: 0.5rem;
  background: white;
  padding: 0.5rem;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 10;
}

.component-wrapper:hover .component-actions {
  opacity: 1;
}

.drag-handle {
  cursor: move;
  color: #909399;
  font-size: 1.25rem;
}

.action-btn {
  cursor: pointer;
  color: #606266;
  font-size: 1.25rem;
  transition: color 0.3s ease;
}

.action-btn:hover {
  color: #409EFF;
}

.action-btn.delete:hover {
  color: #F56C6C;
}

.empty-canvas {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #909399;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.sidebar-right {
  width: 350px;
  flex-shrink: 0;
  background: white;
  border-left: 1px solid #e4e7ed;
}

.config-tabs {
  height: 100%;
}

:deep(.el-tabs__content) {
  height: calc(100% - 55px);
  overflow: auto;
}

.config-content {
  height: 100%;
}

.empty-config {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #909399;
}

.theme-config {
  padding: 1.5rem;
}

.preview-content {
  min-height: 100%;
}

@media (max-width: 1440px) {
  .sidebar-left {
    width: 240px;
  }
  
  .sidebar-right {
    width: 300px;
  }
}
</style>

