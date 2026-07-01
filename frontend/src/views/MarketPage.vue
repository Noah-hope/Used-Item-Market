<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { catalogApi } from '../api/modules'
import GoodsCard from '../components/GoodsCard.vue'

const query = reactive({
  keyword: '',
  category: '',
  sort: 'LATEST',
  page: 1,
  pageSize: 12,
})

const pageData = ref({ list: [], total: 0, page: 1, pageSize: 12 })
const loading = ref(false)
const categories = ref([])

async function loadCategories() {
  const response = await catalogApi.categories()
  categories.value = response.data
}

async function loadGoods() {
  loading.value = true
  try {
    const response = await catalogApi.list(query)
    pageData.value = response.data
  } finally {
    loading.value = false
  }
}

function submitSearch() {
  query.page = 1
  loadGoods()
}

function changePage(step) {
  const next = query.page + step
  const maxPage = Math.max(1, Math.ceil(pageData.value.total / query.pageSize))
  if (next < 1 || next > maxPage) return
  query.page = next
  loadGoods()
}

watch(() => [query.category, query.sort], () => {
  query.page = 1
  loadGoods()
})

onMounted(loadGoods)
onMounted(loadCategories)
</script>

<template>
  <section class="filters-panel">
    <div class="search-row">
      <input v-model="query.keyword" class="text-input grow" placeholder="搜索商品名称、分类、描述" @keyup.enter="submitSearch" />
      <button class="primary-btn" @click="submitSearch">搜索</button>
    </div>
    <div class="toolbar-row">
      <select v-model="query.category" class="select-input">
        <option value="">全部分类</option>
        <option v-for="item in categories" :key="item.code" :value="item.label">{{ item.label }}</option>
      </select>
      <select v-model="query.sort" class="select-input">
        <option value="LATEST">最新上架</option>
        <option value="PRICE_ASC">价格升序</option>
        <option value="PRICE_DESC">价格降序</option>
        <option value="STOCK_ASC">库存升序</option>
        <option value="STOCK_DESC">库存降序</option>
      </select>
      <span class="hint-text">共 {{ pageData.total }} 件商品</span>
    </div>
  </section>

  <section v-if="loading" class="empty-state">正在加载商品...</section>
  <section v-else-if="!pageData.list.length" class="empty-state">没有找到匹配的商品</section>
  <section v-else class="goods-grid">
    <GoodsCard v-for="item in pageData.list" :key="item.gid" :item="item" />
  </section>

  <section class="pagination-bar">
    <button class="ghost-btn" @click="changePage(-1)">上一页</button>
    <span>第 {{ query.page }} 页</span>
    <button class="ghost-btn" @click="changePage(1)">下一页</button>
  </section>
</template>
