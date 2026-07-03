<script setup>
import { onMounted, ref } from 'vue'
import { sellerApi } from '../api/modules'

const goods = ref([])

async function loadGoods() {
  const response = await sellerApi.list()
  goods.value = response.data
}

async function removeItem(item) {
  await sellerApi.remove(item.gid)
  await loadGoods()
}

onMounted(loadGoods)
</script>

<template>
  <section class="section-header">
    <div>
      <p class="eyebrow">我的商品</p>
      <h2>在售管理</h2>
    </div>
    <button class="primary-btn" @click="$router.push('/publish')">发布新商品</button>
  </section>

  <div v-if="!goods.length" class="empty-state">你还没有发布商品</div>
  <div v-else class="list-panel">
    <div v-for="item in goods" :key="item.gid" class="list-row">
      <div>
        <strong>{{ item.name }}</strong>
        <p>{{ item.category }} · ￥{{ item.price }} · 库存 {{ item.stock }} · 状态 {{ $enumLabel('goodsStatus', item.status) }}</p>
        <p>{{ $enumLabel('deliveryMode', item.deliveryMode) || item.deliveryMode }} · {{ item.pickupLocation || '待补充' }}</p>
        <p v-if="item.reviewNote" class="error-text">审核备注：{{ item.reviewNote }}</p>
      </div>
      <div class="inline-actions">
        <button class="ghost-btn" @click="$router.push(`/seller/goods/${item.gid}/edit`)">编辑</button>
        <button class="primary-btn danger" @click="removeItem(item)">下架</button>
      </div>
    </div>
  </div>
</template>
