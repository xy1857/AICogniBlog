<template>
  <div class="config-panel">
    <div class="panel-header">
      <h3>{{ title }}</h3>
      <el-button text @click="$emit('close')">
        <el-icon><Close /></el-icon>
      </el-button>
    </div>

    <el-scrollbar class="panel-body">
      <el-form label-position="top">
        <template v-for="(field, key) in schema.properties" :key="key">
          <!-- 字符串输入 -->
          <el-form-item v-if="field.type === 'string'" :label="field.title">
            <el-input
              :model-value="modelValue[key]"
              @update:model-value="updateValue(key, $event)"
              :placeholder="field.description"
            />
          </el-form-item>

          <!-- 文本域 -->
          <el-form-item v-else-if="field.type === 'textarea'" :label="field.title">
            <el-input
              type="textarea"
              :rows="3"
              :model-value="modelValue[key]"
              @update:model-value="updateValue(key, $event)"
              :placeholder="field.description"
            />
          </el-form-item>

          <!-- 数字输入 -->
          <el-form-item v-else-if="field.type === 'number'" :label="field.title">
            <el-input-number
              :model-value="modelValue[key]"
              @update:model-value="updateValue(key, $event)"
              :min="field.min"
              :max="field.max"
              style="width: 100%"
            />
          </el-form-item>

          <!-- 布尔开关 -->
          <el-form-item v-else-if="field.type === 'boolean'" :label="field.title">
            <el-switch
              :model-value="modelValue[key]"
              @update:model-value="updateValue(key, $event)"
            />
          </el-form-item>

          <!-- 颜色选择器 -->
          <el-form-item v-else-if="field.type === 'color'" :label="field.title">
            <el-color-picker
              :model-value="modelValue[key]"
              @update:model-value="updateValue(key, $event)"
              show-alpha
            />
          </el-form-item>

          <!-- 图片上传 -->
          <el-form-item v-else-if="field.type === 'image'" :label="field.title">
            <el-input
              :model-value="modelValue[key]"
              @update:model-value="updateValue(key, $event)"
              placeholder="输入图片URL"
            >
              <template #append>
                <el-button :icon="Upload">上传</el-button>
              </template>
            </el-input>
          </el-form-item>

          <!-- 下拉选择 -->
          <el-form-item v-else-if="field.type === 'select'" :label="field.title">
            <el-select
              :model-value="modelValue[key]"
              @update:model-value="updateValue(key, $event)"
              style="width: 100%"
            >
              <el-option
                v-for="option in field.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </el-form-item>

          <!-- 数组 (颜色数组) -->
          <el-form-item v-else-if="field.type === 'array' && field.items?.type === 'color'" :label="field.title">
            <div class="color-array">
              <el-color-picker
                v-for="(color, index) in modelValue[key]"
                :key="index"
                :model-value="color"
                @update:model-value="updateArrayItem(key, index, $event)"
              />
              <el-button
                :icon="Plus"
                circle
                size="small"
                @click="addArrayItem(key, '#409EFF')"
              />
              <el-button
                v-if="modelValue[key]?.length > 1"
                :icon="Minus"
                circle
                size="small"
                @click="removeArrayItem(key)"
              />
            </div>
          </el-form-item>
        </template>

        <!-- 样式配置 -->
        <el-divider>样式设置</el-divider>

        <el-form-item label="背景色">
          <el-color-picker
            :model-value="styleValue.backgroundColor"
            @update:model-value="updateStyle('backgroundColor', $event)"
            show-alpha
          />
        </el-form-item>

        <el-form-item label="内边距">
          <el-input
            :model-value="styleValue.padding"
            @update:model-value="updateStyle('padding', $event)"
            placeholder="例如: 2rem"
          />
        </el-form-item>

        <el-form-item label="外边距">
          <el-input
            :model-value="styleValue.margin"
            @update:model-value="updateStyle('margin', $event)"
            placeholder="例如: 1rem 0"
          />
        </el-form-item>

        <el-form-item label="圆角">
          <el-input
            :model-value="styleValue.borderRadius"
            @update:model-value="updateStyle('borderRadius', $event)"
            placeholder="例如: 8px"
          />
        </el-form-item>
      </el-form>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { Close, Upload, Plus, Minus } from '@element-plus/icons-vue';
import type { FormSchema } from '@/types/page-builder';

interface Props {
  title: string;
  schema: FormSchema;
  modelValue: Record<string, any>;
  styleValue: Record<string, any>;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  'update:modelValue': [value: Record<string, any>];
  'update:styleValue': [value: Record<string, any>];
  close: [];
}>();

const updateValue = (key: string, value: any) => {
  emit('update:modelValue', { ...props.modelValue, [key]: value });
};

const updateStyle = (key: string, value: any) => {
  emit('update:styleValue', { ...props.styleValue, [key]: value });
};

const updateArrayItem = (key: string, index: number, value: any) => {
  const arr = [...(props.modelValue[key] || [])];
  arr[index] = value;
  updateValue(key, arr);
};

const addArrayItem = (key: string, defaultValue: any) => {
  const arr = [...(props.modelValue[key] || [])];
  arr.push(defaultValue);
  updateValue(key, arr);
};

const removeArrayItem = (key: string) => {
  const arr = [...(props.modelValue[key] || [])];
  arr.pop();
  updateValue(key, arr);
};
</script>

<style scoped>
.config-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e4e7ed;
}

.panel-header h3 {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
}

.panel-body {
  flex: 1;
  padding: 1.5rem;
}

.color-array {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  flex-wrap: wrap;
}

:deep(.el-form-item) {
  margin-bottom: 1.5rem;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #303133;
}
</style>

