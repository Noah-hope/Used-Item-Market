<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { cartApi, orderApi, chatApi, catalogApi } from '../api/modules'
import { resolveAssetUrl } from '../utils/assets'

const router = useRouter()
const items = ref([])

function isItemLocked(item) {
  return item.status !== 'ACTIVE'
}

function canCheckout(item) {
  return item.status === 'ACTIVE' && item.stock > 0 && item.quantity > 0 && item.quantity <= item.stock
}

const activeCount = computed(() => items.value.filter(canCheckout).length)

async function loadCart() {
  const response = await cartApi.list()
  const cartItems = response.data || []
  const detailList = await Promise.all(
    cartItems.map(async (item) => {
      try {
        const detail = await catalogApi.detail(item.gid)
        return { gid: item.gid, sellerName: detail.data?.sellerName || '' }
      } catch {
        return { gid: item.gid, sellerName: '' }
      }
    }),
  )
  const sellerNameMap = new Map(detailList.map((item) => [item.gid, item.sellerName]))
  items.value = cartItems.map((item) => ({
    ...item,
    sellerName: sellerNameMap.get(item.gid) || item.sellerName || '',
  }))
}

async function removeItem(item) {
  const response = await cartApi.remove(item.gid)
  items.value = response.data
}

async function checkout(item) {
  if (!canCheckout(item)) return
  await orderApi.create({ gid: item.gid, quantity: item.quantity, fromCart: true })
  await loadCart()
}

async function contactSeller(item) {
  if (!item.sellerUid) return
  await chatApi.send({
    receiverUid: item.sellerUid,
    goodsId: item.gid,
    content: `你好，我想和你确认一下购物车商品“${item.name}”的交易细节。`,
  })
  router.push('/messages')
}

onMounted(loadCart)
</script>

<template>
  <section class="section-header">
    <div>
      <h2>待结算商品</h2>
    </div>
    <div class="tag">可下单 {{ activeCount }}</div>
  </section>
  <div v-if="!items.length" class="empty-state">购物车还是空的</div>
  <section v-else class="goods-grid cart-grid">
    <article
      v-for="item in items"
      :key="item.gid"
      class="goods-card cart-card"
      :class="{ 'goods-card--locked': isItemLocked(item) }"
    >
      <div class="goods-image-wrap">
        <img
          :src="resolveAssetUrl(item.image) || 'https://img.alicdn.com/imgextra/i1/O1CN01v3ZQ7I1Fvxck5BYnX_!!6000000000550-2-tps-800-800.png'"
          :alt="item.name"
          class="goods-image"
        />
        <div v-if="isItemLocked(item)" class="goods-lock-overlay">商品已下架</div>
      </div>
      <div class="goods-card-body">
        <p class="goods-category">{{ item.category }}</p>
        <h3>{{ item.name }}</h3>
        <p class="goods-meta">库存 {{ item.stock }} · {{ item.sellerName || '校内卖家' }}</p>
        <div class="goods-card-footer">
          <strong>{{ item.price }}</strong>
          <span class="goods-sales">购物车数量 {{ item.quantity }}</span>
        </div>
        <p v-if="isItemLocked(item)" class="cart-item-status">商品已下架，仅可联系或移除</p>
        <div class="cart-card-actions">
          <button class="ghost-btn small" @click="contactSeller(item)">联系</button>
          <button class="ghost-btn small" @click="removeItem(item)">移除</button>
          <button class="primary-btn small" :disabled="!canCheckout(item)" @click="checkout(item)">下单</button>
        </div>
      </div>
    </article>
  </section>
</template>

<style scoped>
.cart-card {
  cursor: default;
}

.cart-card:hover {
  transform: none;
}

.cart-grid {
  align-items: start;
}

.cart-card-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.cart-card-actions .ghost-btn,
.cart-card-actions .primary-btn {
  width: auto;
  min-width: 72px;
  min-height: 34px;
  padding: 7px 12px;
  font-size: 13px;
}

.cart-card-actions .ghost-btn,
.cart-card-actions .primary-btn {
  background: #fff3ec;
  color: #ff5a1f;
  border: 1px solid #ffcfbf;
  font-weight: 700;
}

.cart-card-actions .ghost-btn:hover,
.cart-card-actions .primary-btn:hover {
  background: #fff3ec;
  border-color: #ff5a1f;
  color: #ff5a1f;
  box-shadow: none;
  transform: none;
}

.cart-card-actions .primary-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}
</style>
