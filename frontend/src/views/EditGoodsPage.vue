<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { catalogApi, sellerApi } from '../api/modules'
import { extractErrorMessage } from '../api/http'
import { resolveAssetUrl } from '../utils/assets'

const route = useRoute()
const router = useRouter()
const categories = ref([])
const imageFile = ref(null)
const imagePreview = ref('')
const currentImage = ref('')
const submitting = ref(false)
const errorMessage = ref('')
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
  const response = await sellerApi.detail(route.params.id)
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
  currentImage.value = response.data.image || ''
}

function onFileChange(e) {
  const file = e.target.files[0]
  if (!file) return
  imageFile.value = file
  imagePreview.value = URL.createObjectURL(file)
}

async function submit() {
  if (submitting.value) return
  submitting.value = true
  errorMessage.value = ''
  try {
    if (imageFile.value) {
      const uploadRes = await sellerApi.uploadImage(imageFile.value)
      form.image = uploadRes.data
    }
    await sellerApi.update(route.params.id, form)
    router.push('/seller/goods')
  } catch (error) {
    errorMessage.value = error.userMessage || extractErrorMessage(error, '更新失败')
  } finally {
    submitting.value = false
  }
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
      <div class="image-upload-area">
        <label class="image-upload-label">
          <span v-if="!imagePreview && !currentImage">点击上传商品图片</span>
          <img v-else-if="imagePreview" :src="imagePreview" class="image-preview" />
          <img v-else-if="currentImage" :src="resolveAssetUrl(currentImage)" class="image-preview" />
          <input type="file" accept="image/*" class="image-file-input" @change="onFileChange" />
        </label>
        <p v-if="imageFile" class="hint-text">{{ imageFile.name }} ({{ (imageFile.size / 1024).toFixed(0) }}KB)</p>
        <p v-else-if="currentImage && !imagePreview" class="hint-text">点击可替换当前图片</p>
      </div>
      <select v-model="form.deliveryMode" class="select-input">
        <option value="SELF_PICKUP">自提</option>
        <option value="CAMPUS_DELIVERY">送货到校</option>
        <option value="BOTH">自提/送货均可</option>
      </select>
      <input v-model="form.pickupLocation" class="text-input" placeholder="自提点或送货说明" />
      <textarea v-model="form.comment" class="text-area" placeholder="商品描述"></textarea>
    </div>
    <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
    <button class="primary-btn" :disabled="submitting" @click="submit">保存修改</button>
  </section>
</template>
