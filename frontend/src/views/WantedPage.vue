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
  budget: 0,
  keyword: '',
  description: '',
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
  await wantedApi.create(form)
  form.title = ''
  form.budget = 0
  form.keyword = ''
  form.description = ''
  await loadWanted()
}

async function closeWanted(id) {
  await wantedApi.close(id)
  await loadWanted()
}

onMounted(async () => {
  await loadCategories()
  await loadWanted()
})
</script>

<template>
  <section class="profile-grid">
    <div class="panel-card">
      <p class="eyebrow">闲置求购</p>
      <h2>求购广场</h2>
      <div v-if="!marketWanted.length" class="empty-state">暂时还没有求购信息</div>
      <div v-else class="list-panel">
        <div v-for="item in marketWanted" :key="item.id" class="list-row stacked">
          <div>
            <strong>{{ item.title }}</strong>
            <p>{{ item.category }} · 预算 ￥{{ item.budget || 0 }} · {{ item.publisherName || item.uid }}</p>
            <p>{{ item.description || '求购方还没有补充更多描述。' }}</p>
          </div>
          <div class="match-block">
            <span class="tag">{{ item.status }}</span>
            <p class="hint-text">关键词：{{ item.keyword || '无' }}</p>
            <div v-if="item.matches?.length" class="match-list">
              <div v-for="match in item.matches" :key="match.gid" class="mini-item">
                <span>{{ match.name }}</span>
                <strong>￥{{ match.price }}</strong>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="panel-card">
      <p class="eyebrow">发布求购</p>
      <h2>告诉大家你想找什么</h2>
      <div class="form-grid">
        <input v-model="form.title" class="text-input" placeholder="求购标题" />
        <select v-model="form.category" class="select-input">
          <option v-for="item in categories" :key="item.code" :value="item.label">{{ item.label }}</option>
        </select>
        <input v-model.number="form.budget" class="text-input" type="number" min="0" placeholder="预算" />
        <input v-model="form.keyword" class="text-input" placeholder="关键词，例如高数教材 / 羽毛球拍" />
        <textarea v-model="form.description" class="text-area" placeholder="补充你的需求说明"></textarea>
      </div>
      <button v-if="authStore.isAuthenticated" class="primary-btn" @click="submitWanted">发布求购</button>
      <p v-else class="hint-text">登录后才能发布求购信息。</p>

      <div v-if="authStore.isAuthenticated" class="my-section">
        <div class="section-header compact">
          <div>
            <p class="eyebrow">我的求购</p>
            <h3>我发过的需求</h3>
          </div>
        </div>
        <div v-if="!myWanted.length" class="empty-state">你还没有发布过求购</div>
        <div v-else class="list-panel">
          <div v-for="item in myWanted" :key="item.id" class="list-row">
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.category }} · {{ item.status }}</p>
            </div>
            <button v-if="item.status === 'OPEN'" class="ghost-btn" @click="closeWanted(item.id)">关闭求购</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
