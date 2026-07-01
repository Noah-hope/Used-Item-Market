<script setup>
import { onMounted, ref } from 'vue'
import { adminApi } from '../api/modules'

const dashboard = ref(null)

onMounted(async () => {
  const response = await adminApi.dashboard()
  dashboard.value = response.data
})
</script>

<template>
  <section class="admin-panel">
    <div class="section-header compact">
      <div>
        <p class="eyebrow">数据看板</p>
        <h2>平台运营概览</h2>
      </div>
    </div>
    <div v-if="dashboard" class="metric-grid">
      <div class="metric-card">
        <span>累计发布</span>
        <strong>{{ dashboard.goodsPublished }}</strong>
      </div>
      <div class="metric-card">
        <span>当前上架</span>
        <strong>{{ dashboard.goodsActive }}</strong>
      </div>
      <div class="metric-card">
        <span>待审核商品</span>
        <strong>{{ dashboard.goodsPending }}</strong>
      </div>
      <div class="metric-card">
        <span>总订单数</span>
        <strong>{{ dashboard.totalOrders }}</strong>
      </div>
      <div class="metric-card">
        <span>已完成订单</span>
        <strong>{{ dashboard.completedOrders }}</strong>
      </div>
      <div class="metric-card">
        <span>黑名单用户</span>
        <strong>{{ dashboard.disabledUsers }}</strong>
      </div>
      <div class="metric-card">
        <span>开放求购数</span>
        <strong>{{ dashboard.wantedOpen }}</strong>
      </div>
    </div>
  </section>
</template>
