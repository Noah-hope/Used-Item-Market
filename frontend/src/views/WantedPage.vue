<script setup>
import { onMounted, reactive, ref } from 'vue'
import { catalogApi, wantedApi } from '../api/modules'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const categories = ref([])
const marketWanted = ref([])
const myWanted = ref([])
const form = reactive({
  title: '',
  category: '',
  keyword: '',
})

async function loadCategories() {
  const response = await catalogApi.categories()
  categories.value = response.data
  if (!form.category && categories.value.length) {
    form.category = categories.value[0].label
  }
}

async function loadWanted() {
  const response = await wantedApi.list()
  marketWanted.value = response.data
  if (authStore.isAuthenticated) {
    const mine = await wantedApi.mine()
    myWanted.value = mine.data
  }
}

async function submitWanted() {
  await wantedApi.create({
    title: form.title,
    category: form.category,
    keyword: form.keyword,
  })
  form.title = ''
  form.keyword = ''
  await loadWanted()
}

async function deleteWanted(id) {
  await wantedApi.remove(id)
  await loadWanted()
}

onMounted(async () => {
  await loadCategories()
  await loadWanted()
})
</script>

<template>
  <section class="wanted-layout">
    <div class="panel-card">
      <h2>求购广场</h2>
      <p class="hint-text market-tip">
        求购市场仅检索并展示名称匹配的商品，是否合适请自行搜索并查看商品详情。
      </p>
      <div v-if="!marketWanted.length" class="empty-state">暂时还没有求购信息</div>
      <div v-else class="wanted-grid">
        <article v-for="item in marketWanted" :key="item.id" class="wanted-card">
          <div class="wanted-card-head">
            <strong class="wanted-name">{{ item.title }}</strong>
            <span class="tag">{{ item.category }}</span>
          </div>
          <p class="wanted-keyword">关键词：{{ item.keyword || '未填写' }}</p>
          <div class="match-compact">
            <span class="match-label">匹配商品</span>
            <div v-if="item.matches?.length" class="match-name-list">
              <span v-for="match in item.matches" :key="match.gid" class="match-chip">{{ match.name }}</span>
            </div>
            <span v-else class="hint-text">暂无匹配商品</span>
          </div>
        </article>
      </div>
    </div>

    <div class="panel-card">
      <h2>告诉大家你想找什么</h2>
      <div class="form-grid">
        <input v-model="form.title" class="text-input" placeholder="求购物品名字" />
        <select v-model="form.category" class="select-input">
          <option v-for="item in categories" :key="item.code" :value="item.label">{{ item.label }}</option>
        </select>
        <input v-model="form.keyword" class="text-input" placeholder="关键词，例如高数教材 / 羽毛球拍" />
      </div>
      <button v-if="authStore.isAuthenticated" class="primary-btn" @click="submitWanted">发布求购</button>
      <p v-else class="hint-text">登录后才能发布求购信息。</p>

      <div v-if="authStore.isAuthenticated" class="my-section">
        <div class="section-header compact">
          <div>
            <h3>我发过的需求</h3>
          </div>
        </div>
        <div v-if="!myWanted.length" class="empty-state">你还没有发布过求购</div>
        <div v-else class="my-wanted-list">
          <div v-for="item in myWanted" :key="item.id" class="my-wanted-row">
            <div class="my-wanted-main">
              <strong>{{ item.title }}</strong>
              <p>{{ item.category }} · {{ item.keyword || '未填写关键词' }}</p>
            </div>
            <button class="ghost-btn" @click="deleteWanted(item.id)">删除</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.wanted-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.15fr) minmax(360px, 0.85fr);
  gap: 20px;
  align-items: start;
}

.market-tip {
  margin-bottom: 16px;
}

.panel-card > .primary-btn {
  margin-top: 14px;
}

.wanted-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 14px;
}

.wanted-card {
  border: 1px solid #dbe4f0;
  border-radius: 8px;
  padding: 18px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}

.wanted-card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.wanted-name {
  color: #1f2937;
  font-size: 16px;
  line-height: 1.4;
}

.wanted-keyword {
  margin: 8px 0 10px;
  color: #667085;
  font-size: 13px;
}

.match-compact {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.match-label {
  color: #98a2b3;
  font-size: 12px;
  font-weight: 700;
}

.match-name-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.match-chip {
  display: inline-flex;
  align-items: center;
  height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  background: #f8fafc;
  color: #374151;
  font-size: 13px;
  max-width: 100%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.my-wanted-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.my-wanted-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 18px;
  border: 1px solid #dbe4f0;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}

.my-wanted-main {
  min-width: 0;
}

.my-wanted-main strong,
.my-wanted-main p {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.my-wanted-main p {
  margin-top: 4px;
  color: #667085;
}

@media (max-width: 1100px) {
  .wanted-layout {
    grid-template-columns: 1fr;
  }
}
</style>
