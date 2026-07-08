<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { resolveAssetUrl } from '../utils/assets'

const props = defineProps({
  item: {
    type: Object,
    required: true,
  },
  removable: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['remove'])
const router = useRouter()
const authStore = useAuthStore()
const isLocked = computed(() => props.item.status && props.item.status !== 'ACTIVE')
const isAdmin = computed(() => Boolean(authStore.user?.admin))

function openDetail() {
  if (isLocked.value) return
  router.push(`/goods/${props.item.gid}`)
}

function removeFavorite() {
  emit('remove', props.item)
}
</script>

<template>
  <article
    class="goods-card"
    :class="{ 'goods-card--locked': isLocked }"
    @click="openDetail"
  >
    <div class="goods-image-wrap">
      <img
        :src="resolveAssetUrl(item.image) || 'https://img.alicdn.com/imgextra/i1/O1CN01v3ZQ7I1Fvxck5BYnX_!!6000000000550-2-tps-800-800.png'"
        :alt="item.name"
        class="goods-image"
      />
      <div v-if="isLocked" class="goods-lock-overlay">商品已下架</div>
      <span v-else-if="item.stock === 0" class="goods-badge sold-out">已售罄</span>
    </div>
    <div class="goods-card-body">
      <p class="goods-category">{{ item.category }}</p>
      <h3>{{ item.name }}</h3>
      <div class="goods-meta-tags">
        <span class="goods-meta-tag">库存：{{ item.stock }}</span>
        <span class="goods-meta-tag">发布者：{{ item.sellerName || item.sellerUid || '校内用户' }}</span>
      </div>
      <div class="goods-card-footer">
        <strong>{{ item.price }}</strong>
        <button
          v-if="removable"
          class="ghost-btn small goods-remove-btn"
          type="button"
          @click.stop="removeFavorite"
        >
          移除
        </button>
        <span v-else class="goods-sales">{{ isAdmin ? `发布者 ${item.sellerName || item.sellerUid || '校内用户'}` : `已售 ${item.salesCount || 0}` }}</span>
      </div>
    </div>
  </article>
</template>
