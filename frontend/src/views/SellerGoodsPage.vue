<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { sellerApi } from '../api/modules'
import { resolveAssetUrl } from '../utils/assets'

const router = useRouter()
const goods = ref([])

function canPermanentDelete(status) {
  return ['OFF_SHELF', 'REJECTED', 'BANNED'].includes(status)
}

function canOffShelf(status) {
  return ['ACTIVE', 'PENDING_REVIEW'].includes(status)
}

function canRelist(status) {
  return ['OFF_SHELF', 'REJECTED', 'BANNED'].includes(status)
}

async function loadGoods() {
  const response = await sellerApi.list()
  goods.value = response.data
}

async function offShelfItem(item) {
  await sellerApi.remove(item.gid)
  await loadGoods()
}

function relistItem(item) {
  router.push({
    path: `/seller/goods/${item.gid}/edit`,
    query: { mode: 'relist' },
  })
}

async function permanentDeleteItem(item) {
  if (!window.confirm(`确认永久删除“${item.name}”吗？相关购物车、收藏、订单记录会一并清理。`)) {
    return
  }
  await sellerApi.permanentDelete(item.gid)
  await loadGoods()
}

onMounted(loadGoods)
</script>

<template>
  <section class="section-header">
    <div>
      <h2>在售管理</h2>
    </div>
    <button class="primary-btn" @click="$router.push('/publish')">发布新商品</button>
  </section>

  <div v-if="!goods.length" class="empty-state">你还没有发布商品</div>
  <div v-else class="seller-goods-list">
    <article v-for="item in goods" :key="item.gid" class="seller-goods-card">
      <div class="seller-goods-image-wrap">
        <img
          :src="resolveAssetUrl(item.image) || 'https://img.alicdn.com/imgextra/i1/O1CN01v3ZQ7I1Fvxck5BYnX_!!6000000000550-2-tps-800-800.png'"
          :alt="item.name"
          class="seller-goods-image"
        />
      </div>
      <div class="seller-goods-body">
        <div class="seller-goods-head">
          <p class="goods-category">{{ item.category }}</p>
          <h3>{{ item.name }}</h3>
        </div>
        <div class="goods-meta-tags seller-goods-tags">
          <span class="goods-meta-tag">价格：¥{{ item.price }}</span>
          <span class="goods-meta-tag">库存：{{ item.stock }}</span>
          <span class="goods-meta-tag">状态：{{ $enumLabel('goodsStatus', item.status) }}</span>
          <span class="goods-meta-tag">交付方式：{{ $enumLabel('deliveryMode', item.deliveryMode) || item.deliveryMode }}</span>
          <span class="goods-meta-tag">说明：{{ item.pickupLocation || '待补充' }}</span>
        </div>
        <div v-if="item.reviewNote" class="seller-goods-note">
          <span class="seller-goods-note-label">审核备注</span>
          <p class="error-text">{{ item.reviewNote }}</p>
        </div>
        <div class="seller-goods-actions">
          <button class="ghost-btn" @click="$router.push(`/seller/goods/${item.gid}/edit`)">编辑</button>
          <button
            v-if="canOffShelf(item.status)"
            class="ghost-btn"
            @click="offShelfItem(item)"
          >
            下架
          </button>
          <button
            v-if="canRelist(item.status)"
            class="primary-btn"
            @click="relistItem(item)"
          >
            重新上架
          </button>
          <button
            v-if="canPermanentDelete(item.status)"
            class="primary-btn danger"
            @click="permanentDeleteItem(item)"
          >
            删除
          </button>
        </div>
      </div>
    </article>
  </div>
</template>
