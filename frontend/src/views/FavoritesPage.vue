<script setup>
import { onMounted, ref } from 'vue'
import { favoriteApi } from '../api/modules'
import GoodsCard from '../components/GoodsCard.vue'

const goodsList = ref([])

async function loadFavorites() {
  const response = await favoriteApi.list()
  goodsList.value = response.data
}

onMounted(loadFavorites)
</script>

<template>
  <section class="section-header">
    <div>
      <p class="eyebrow">我的收藏</p>
      <h2>感兴趣的校园闲置</h2>
    </div>
  </section>
  <section v-if="!goodsList.length" class="empty-state">你还没有收藏商品</section>
  <section v-else class="goods-grid">
    <GoodsCard v-for="item in goodsList" :key="item.gid" :item="item" />
  </section>
</template>
