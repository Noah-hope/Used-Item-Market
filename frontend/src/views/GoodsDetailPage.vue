<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { cartApi, catalogApi, favoriteApi, orderApi, chatApi } from '../api/modules'
import { useAuthStore } from '../stores/auth'
import { resolveAssetUrl } from '../utils/assets'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const goods = ref(null)
const quantity = ref(1)
const submitting = ref(false)
const favoriteLoading = ref(false)
const messageLoading = ref(false)
const cartLoaded = ref(false)
const favoriteLoaded = ref(false)
const inCart = ref(false)
const isFavorited = ref(false)

const isLocked = computed(() => goods.value && goods.value.status !== 'ACTIVE')
const isAdmin = computed(() => Boolean(authStore.user?.admin))

async function loadDetail() {
  const tasks = [catalogApi.detail(route.params.id)]
  if (authStore.isAuthenticated && !isAdmin.value) {
    tasks.push(cartApi.list(), favoriteApi.list())
  }
  const [detailResponse, cartResponse, favoriteResponse] = await Promise.all(tasks)
  goods.value = detailResponse.data
  if (goods.value?.stock > 0) {
    quantity.value = 1
  }
  if (authStore.isAuthenticated && !isAdmin.value) {
    const cartItems = cartResponse?.data || []
    const favorites = favoriteResponse?.data || []
    inCart.value = cartItems.some((item) => item.gid === goods.value.gid)
    isFavorited.value = favorites.some((item) => item.gid === goods.value.gid)
    cartLoaded.value = true
    favoriteLoaded.value = true
  }
}

async function addToCart() {
  if (isLocked.value) return
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  if (inCart.value) {
    router.push('/cart')
    return
  }
  submitting.value = true
  try {
    await cartApi.add({ gid: goods.value.gid, quantity: quantity.value })
    inCart.value = true
  } finally {
    submitting.value = false
  }
}

async function buyNow() {
  if (isLocked.value) return
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
  if (isLocked.value) return
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  if (isFavorited.value) {
    router.push('/favorites')
    return
  }
  favoriteLoading.value = true
  try {
    await favoriteApi.add(goods.value.gid)
    isFavorited.value = true
  } finally {
    favoriteLoading.value = false
  }
}

async function contactSeller() {
  if (isLocked.value) return
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
      <img :src="resolveAssetUrl(goods.image) || 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=1200&q=80'" class="detail-image" />
    </div>
    <div class="detail-panel">
      <div class="detail-meta-tags">
        <span class="detail-meta-tag">{{ goods.category }}</span>
      </div>
      <h1>{{ goods.name }}</h1>
      <div v-if="isLocked" class="detail-offline-banner">商品已下架，当前仅保留信息展示。</div>
      <div class="detail-meta-tags">
        <span class="detail-meta-tag detail-meta-tag--price">价格：¥{{ goods.price }}</span>
      </div>
      <div class="detail-meta-tags">
        <span class="detail-meta-tag">说明：{{ goods.comment || '卖家暂未填写更多描述。' }}</span>
      </div>
      <div class="detail-stats">
        <div><span>库存</span><strong>{{ goods.stock }}</strong></div>
        <div><span>卖家</span><strong>{{ goods.sellerName || goods.sellerUid }}</strong></div>
        <div><span>交付方式</span><strong>{{ $enumLabel('deliveryMode', goods.deliveryMode) || '自提' }}</strong></div>
        <div><span>自提/送货说明</span><strong>{{ goods.pickupLocation || '待协商' }}</strong></div>
      </div>
      <div v-if="!isLocked && !isAdmin" class="purchase-row">
        <label class="detail-quantity-field">
          <span class="detail-quantity-label">购买数量</span>
          <input v-model.number="quantity" type="number" min="1" :max="goods.stock" class="number-input detail-quantity-input" />
        </label>
        <button
          class="ghost-btn"
          :class="{ 'detail-action-active': cartLoaded && inCart }"
          :disabled="submitting"
          @click="addToCart"
        >
          {{ inCart ? '已加入购物车' : '加入购物车' }}
        </button>
        <button
          class="ghost-btn"
          :class="{ 'detail-action-active': favoriteLoaded && isFavorited }"
          :disabled="favoriteLoading"
          @click="favorite"
        >
          {{ isFavorited ? '已收藏' : '收藏' }}
        </button>
        <button class="ghost-btn" :disabled="messageLoading" @click="contactSeller">私信卖家</button>
        <button class="primary-btn" :disabled="submitting" @click="buyNow">立即下单</button>
      </div>
      <div v-else-if="isAdmin" class="detail-admin-tip">管理员账号仅可查看商品信息与审核情况，不能进行交易操作。</div>
    </div>
  </section>
</template>

<style scoped>
.detail-meta-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 10px 0 0;
}

.detail-meta-tag {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #6b7280;
  font-size: 12px;
  line-height: 1.3;
}

.detail-meta-tag--price {
  color: #ff5a1f;
  background: #fff3ec;
  font-weight: 700;
}

.detail-quantity-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 96px;
}

.detail-quantity-label {
  color: #6b7280;
  font-size: 12px;
  font-weight: 600;
}

.detail-quantity-input {
  width: 100%;
  min-width: 0;
}

.detail-admin-tip {
  margin-top: 18px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  color: #6b7280;
  font-size: 14px;
}

.detail-action-active {
  border-color: #ff5a1f !important;
  background: #fff3ec !important;
  color: #ff5a1f !important;
  font-weight: 700;
}
</style>
