<script setup>
import { onMounted, ref } from 'vue'
import { catalogApi } from '../api/modules'
import GoodsCard from '../components/GoodsCard.vue'

const goods = ref([])

onMounted(async () => {
  const response = await catalogApi.list({ page: 1, pageSize: 8, sort: 'LATEST' })
  goods.value = response.data.list
})
</script>

<template>
  <section class="hero-section">
    <div class="hero-copy">
      <p class="eyebrow">校内闲置更快流转</p>
      <h1>把校园二手交易做成一套清楚、顺手、可信的体验</h1>
      <p>教材、数码、宿舍用品、运动器材都可以在这里发布、查找、下单和管理。</p>
      <div class="hero-actions">
        <button class="primary-btn" @click="$router.push('/market')">进入市场</button>
        <button class="ghost-btn" @click="$router.push('/publish')">发布闲置</button>
      </div>
    </div>
    <div class="hero-panel">
      <div class="metric-card">
        <span>分类</span>
        <strong>书籍 / 日用品 / 学习用品 / 床上用品</strong>
      </div>
      <div class="metric-card">
        <span>订单流</span>
        <strong>下单 → 发货 → 确认收货</strong>
      </div>
      <div class="metric-card">
        <span>后台支持</span>
        <strong>用户管理 / 订单记录 / 风险处理</strong>
      </div>
    </div>
  </section>

  <section class="section-header">
    <div>
      <p class="eyebrow">最新上架</p>
      <h2>正在流转的校园闲置</h2>
    </div>
    <button class="ghost-btn" @click="$router.push('/market')">查看全部</button>
  </section>

  <section class="goods-grid">
    <GoodsCard v-for="item in goods" :key="item.gid" :item="item" />
  </section>
</template>
