<script setup>
import { onMounted, ref } from 'vue'
import { favoriteApi } from '../api/modules'
import GoodsCard from '../components/GoodsCard.vue'

const goodsList = ref([])

async function loadFavorites() {
  const response = await favoriteApi.list()
  goodsList.value = response.data
}

async function removeFavorite(item) {
  await favoriteApi.remove(item.gid)
  await loadFavorites()
}

onMounted(loadFavorites)
</script>

<template>
  <section class="section-header">
    <div>
      <h2>感兴趣的校园闲置</h2>
    </div>
  </section>
  <section v-if="!goodsList.length" class="empty-state">你还没有收藏商品</section>
  <section v-else class="goods-grid">
    <GoodsCard
      v-for="item in goodsList"
      :key="item.gid"
      :item="item"
      removable
      @remove="removeFavorite"
    />
  </section>
</template>
