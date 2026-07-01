<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { catalogApi, sellerApi } from '../api/modules'

const route = useRoute()
const router = useRouter()
const categories = ref([])
const form = reactive({
  name: '',
  category: '',
  price: 0,
  stock: 1,
  image: '',
  comment: '',
  deliveryMode: 'SELF_PICKUP',
  pickupLocation: '',
})

async function loadDetail() {
  const categoryResponse = await catalogApi.categories()
  categories.value = categoryResponse.data
  const response = await catalogApi.detail(route.params.id)
  Object.assign(form, {
    name: response.data.name,
    category: response.data.category,
    price: response.data.price,
    stock: response.data.stock,
    image: response.data.image,
    comment: response.data.comment,
    deliveryMode: response.data.deliveryMode || 'SELF_PICKUP',
    pickupLocation: response.data.pickupLocation || '',
  })
}

async function submit() {
  await sellerApi.update(route.params.id, form)
  router.push('/seller/goods')
}

onMounted(loadDetail)
</script>

<template>
  <section class="panel-card wide-panel">
    <p class="eyebrow">编辑商品</p>
    <h2>调整商品信息</h2>
    <div class="form-grid two">
      <input v-model="form.name" class="text-input" placeholder="商品名称" />
      <select v-model="form.category" class="select-input">
        <option v-for="item in categories" :key="item.code" :value="item.label">{{ item.label }}</option>
      </select>
      <input v-model.number="form.price" class="text-input" type="number" placeholder="价格" />
      <input v-model.number="form.stock" class="text-input" type="number" placeholder="库存" />
      <input v-model="form.image" class="text-input" placeholder="图片 URL（可选）" />
      <select v-model="form.deliveryMode" class="select-input">
        <option value="SELF_PICKUP">自提</option>
        <option value="CAMPUS_DELIVERY">送货到校</option>
        <option value="BOTH">自提/送货均可</option>
      </select>
      <input v-model="form.pickupLocation" class="text-input" placeholder="自提点或送货说明" />
      <textarea v-model="form.comment" class="text-area" placeholder="商品描述"></textarea>
    </div>
    <button class="primary-btn" @click="submit">保存修改</button>
  </section>
</template>
