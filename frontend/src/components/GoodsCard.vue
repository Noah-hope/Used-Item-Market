<script setup>
defineProps({
  item: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <article class="goods-card">
    <div class="goods-image-wrap">
      <img :src="item.image || 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=900&q=80'" class="goods-image" />
    </div>
    <div class="goods-card-body">
      <p class="goods-category">{{ item.category }}</p>
      <h3>{{ item.name }}</h3>
      <p class="goods-meta">库存 {{ item.stock }} · 卖家 {{ item.sellerUid || '校内用户' }}</p>
      <div class="goods-card-footer">
        <strong>￥{{ item.price }}</strong>
        <button class="primary-btn small" @click="$router.push(`/goods/${item.gid}`)">查看详情</button>
      </div>
    </div>
  </article>
</template>
