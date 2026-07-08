<script setup>
import { onMounted, reactive, ref } from 'vue'
import { adminApi } from '../api/modules'

const categories = ref([])
const form = reactive({
  code: '',
  label: '',
  sortOrder: null,
  enabled: true,
})

async function loadCategories() {
  const response = await adminApi.categories()
  categories.value = response.data
}

async function submit() {
  await adminApi.saveCategory(form)
  form.code = ''
  form.label = ''
  form.sortOrder = null
  form.enabled = true
  await loadCategories()
}

async function toggle(item) {
  await adminApi.saveCategory({
    code: item.code,
    label: item.label,
    sortOrder: item.sortOrder,
    enabled: !item.enabled,
  })
  await loadCategories()
}

onMounted(loadCategories)
</script>

<template>
  <section class="admin-panel">
    <div class="section-header compact">
      <div>
        <p class="eyebrow">类目管理</p>
        <h2>维护发布分类</h2>
      </div>
    </div>
    <div class="admin-stack">
      <div class="panel-card admin-form-panel">
        <div class="admin-section-tag">新增或更新分类</div>
        <div class="form-grid admin-category-form">
          <div class="form-grid two">
            <input v-model="form.code" class="text-input" placeholder="分类编码，例如 DIGITAL" />
            <input v-model="form.label" class="text-input" placeholder="分类名称，例如 数码" />
          </div>
          <div class="form-grid two admin-category-form-row">
            <input v-model.number="form.sortOrder" class="text-input" type="number" placeholder="排序值" />
            <label class="checkbox-row admin-checkbox-row">
              <input v-model="form.enabled" type="checkbox" />
              <span>启用该分类</span>
            </label>
          </div>
        </div>
        <button class="primary-btn" @click="submit">保存分类</button>
      </div>
      <div class="panel-card">
        <div class="admin-section-tag">当前分类</div>
        <div v-if="!categories.length" class="empty-state">暂无分类数据</div>
        <div v-else class="admin-simple-list">
          <div v-for="item in categories" :key="item.code" class="admin-simple-row">
            <div>
              <strong>{{ item.label }}</strong>
              <p>{{ item.code }} · 排序 {{ item.sortOrder }}</p>
            </div>
            <button class="ghost-btn" @click="toggle(item)">{{ item.enabled ? '停用' : '启用' }}</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
