<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { cartApi, catalogApi, favoriteApi, orderApi, chatApi } from '../api/modules'
import { useAuthStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const goods = ref(null)
const quantity = ref(1)
const submitting = ref(false)
const favoriteLoading = ref(false)
const messageLoading = ref(false)

async function loadDetail() {
  const response = await catalogApi.detail(route.params.id)
  goods.value = response.data
}

async function addToCart() {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  submitting.value = true
  try {
    await cartApi.add({ gid: goods.value.gid, quantity: quantity.value })
    router.push('/cart')
  } finally {
    submitting.value = false
  }
}

async function buyNow() {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  submitting.value = true
  try {
    await orderApi.create({ gid: goods.value.gid, quantity: quantity.value, fromCart: false })
    router.push('/orders/purchases')
  } finally {
    submitting.value = false
  }
}

async function favorite() {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  favoriteLoading.value = true
  try {
    await favoriteApi.add(goods.value.gid)
  } finally {
    favoriteLoading.value = false
  }
}

async function contactSeller() {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  messageLoading.value = true
  try {
    await chatApi.send({
      receiverUid: goods.value.sellerUid,
      goodsId: goods.value.gid,
      content: `你好，我对“${goods.value.name}”感兴趣，想进一步了解一下。`,
    })
    router.push('/messages')
  } finally {
    messageLoading.value = false
  }
}

onMounted(loadDetail)
</script>

<template>
  <section v-if="goods" class="detail-layout">
    <div class="detail-image-card">
      <img :src="goods.image || 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=1200&q=80'" class="detail-image" />
    </div>
    <div class="detail-panel">
      <p class="eyebrow">{{ goods.category }}</p>
      <h1>{{ goods.name }}</h1>
      <div class="price-line">￥{{ goods.price }}</div>
      <p class="detail-copy">{{ goods.comment || '卖家暂未填写更多描述。' }}</p>
      <div class="detail-stats">
        <div><span>库存</span><strong>{{ goods.stock }}</strong></div>
        <div><span>卖家</span><strong>{{ goods.sellerName || goods.sellerUid }}</strong></div>
        <div><span>交付方式</span><strong>{{ $enumLabel('deliveryMode', goods.deliveryMode) || '自提' }}</strong></div>
        <div><span>自提/送货说明</span><strong>{{ goods.pickupLocation || '待协商' }}</strong></div>
      </div>
      <div class="purchase-row">
        <input v-model.number="quantity" type="number" min="1" :max="goods.stock" class="number-input" />
        <button class="ghost-btn" :disabled="submitting" @click="addToCart">加入购物车</button>
        <button class="ghost-btn" :disabled="favoriteLoading" @click="favorite">收藏</button>
        <button class="ghost-btn" :disabled="messageLoading" @click="contactSeller">私信卖家</button>
        <button class="primary-btn" :disabled="submitting" @click="buyNow">立即下单</button>
      </div>
    </div>
  </section>
</template>
