<script setup>
import { resolveAssetUrl } from '../utils/assets'

defineProps({
  item: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <article class="goods-card" @click="$router.push(`/goods/${item.gid}`)">
    <div class="goods-image-wrap">
      <img
        :src="resolveAssetUrl(item.image) || 'https://img.alicdn.com/imgextra/i1/O1CN01v3ZQ7I1Fvxck5BYnX_!!6000000000550-2-tps-800-800.png'"
        :alt="item.name"
        class="goods-image"
      />
      <span v-if="item.stock === 0" class="goods-badge sold-out">已售罄</span>
    </div>
    <div class="goods-card-body">
      <p class="goods-category">{{ item.category }}</p>
      <h3>{{ item.name }}</h3>
      <p class="goods-meta">库存 {{ item.stock }} · {{ item.sellerName || item.sellerUid || '校内用户' }}</p>
      <div class="goods-card-footer">
        <strong>{{ item.price }}</strong>
        <span class="goods-sales">已售 {{ item.salesCount || 0 }}</span>
      </div>
    </div>
  </article>
</template>
