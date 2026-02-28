<template>
  <div class="component-library">
    <div class="library-header">
      <h3>组件库</h3>
    </div>

    <el-scrollbar class="library-body">
      <div class="component-list">
        <div
          v-for="component in components"
          :key="component.type"
          class="component-item"
          draggable="true"
          @dragstart="handleDragStart($event, component)"
        >
          <div class="component-icon">
            <el-icon>
              <component :is="component.icon" />
            </el-icon>
          </div>
          <div class="component-info">
            <h4>{{ component.name }}</h4>
            <p>{{ component.description }}</p>
          </div>
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { getAllComponents } from '@/components/page-builder/componentRegistry';
import type { ComponentMeta } from '@/types/page-builder';

const components = getAllComponents();

const emit = defineEmits<{
  dragStart: [component: ComponentMeta];
}>();

const handleDragStart = (event: DragEvent, component: ComponentMeta) => {
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'copy';
    event.dataTransfer.setData('componentType', component.type);
  }
  emit('dragStart', component);
};
</script>

<style scoped>
.component-library {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-right: 1px solid #e4e7ed;
}

.library-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e4e7ed;
}

.library-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
}

.library-body {
  flex: 1;
  padding: 1rem;
}

.component-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.component-item {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: move;
  transition: all 0.3s ease;
}

.component-item:hover {
  background: #ecf5ff;
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.component-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: white;
  border-radius: 8px;
  color: #409EFF;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.component-info {
  flex: 1;
}

.component-info h4 {
  margin: 0 0 0.25rem 0;
  font-size: 0.9375rem;
  font-weight: 600;
  color: #303133;
}

.component-info p {
  margin: 0;
  font-size: 0.8125rem;
  color: #909399;
  line-height: 1.4;
}
</style>

