<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { catalogApi } from '../api/modules'
import GoodsCard from '../components/GoodsCard.vue'

const route = useRoute()
const router = useRouter()
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

const categoryTabs = computed(() => [
  { label: '全部', value: '' },
  ...categories.value.map((item) => ({ label: item.label, value: item.label })),
])

function syncQueryFromRoute() {
  query.keyword = typeof route.query.keyword === 'string' ? route.query.keyword : ''
  query.category = typeof route.query.category === 'string' ? route.query.category : ''
  query.page = Number(route.query.page || 1)
}

function pushRouteQuery() {
  const nextQuery = {}
  if (query.keyword) nextQuery.keyword = query.keyword
  if (query.category) nextQuery.category = query.category
  if (query.page > 1) nextQuery.page = String(query.page)
  router.replace({ path: route.path, query: nextQuery })
}

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

function changePage(step) {
  const next = query.page + step
  const maxPage = Math.max(1, Math.ceil(pageData.value.total / query.pageSize))
  if (next < 1 || next > maxPage) return
  query.page = next
  pushRouteQuery()
}

function changeCategory(value) {
  if (query.category === value) return
  query.category = value
  query.page = 1
  pushRouteQuery()
}

watch(
  () => route.fullPath,
  async () => {
    syncQueryFromRoute()
    await loadGoods()
  },
  { immediate: true },
)

onMounted(loadCategories)
</script>

<template>
  <section class="market-shell">
    <div class="category-bar">
      <button
        v-for="item in categoryTabs"
        :key="item.value || 'all'"
        class="category-tab"
        :class="{ active: query.category === item.value }"
        @click="changeCategory(item.value)"
      >
        {{ item.label }}
      </button>
    </div>

    <section v-if="loading" class="market-empty">正在加载商品...</section>
    <section v-else-if="!pageData.list.length" class="market-empty">没有找到匹配的商品</section>
    <section v-else class="market-grid">
      <GoodsCard v-for="item in pageData.list" :key="item.gid" :item="item" />
    </section>

    <section class="market-pagination">
      <button class="page-btn" @click="changePage(-1)">上一页</button>
      <span>第 {{ query.page }} 页</span>
      <button class="page-btn" @click="changePage(1)">下一页</button>
    </section>
  </section>
</template>

<style scoped>
.market-shell {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.category-bar {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 2px 0 6px;
}

.category-tab {
  flex: 0 0 auto;
  height: 38px;
  padding: 0 16px;
  border-radius: 999px;
  background: #fff7f1;
  color: #9a5b33;
  border: 1px solid #ffd7c2;
  font-weight: 600;
}

.category-tab:hover {
  background: #ffe8da;
  color: #ff5a1f;
  border-color: #ffcfbf;
}

.category-tab.active {
  background: linear-gradient(135deg, #ff7a1f, #ff5a1f);
  color: #fff;
  border-color: #ff5a1f;
  box-shadow: 0 8px 20px rgba(255, 90, 31, 0.14);
}

.market-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.market-empty {
  min-height: 220px;
  display: grid;
  place-items: center;
  background: #fff;
  border-radius: 12px;
  color: #6b7280;
}

.market-pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  padding: 10px 0 4px;
  color: #6b7280;
}

.page-btn {
  height: 40px;
  padding: 0 20px;
  border-radius: 999px;
  background: #fff7f1;
  border: 1px solid #ffd7c2;
  color: #8a4b22;
  font-weight: 600;
}

.page-btn:hover {
  background: #ffe8da;
  border-color: #ffcfbf;
  color: #ff5a1f;
}

@media (max-width: 1280px) {
  .market-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 860px) {
  .market-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .market-grid {
    grid-template-columns: 1fr;
  }
}
</style>
