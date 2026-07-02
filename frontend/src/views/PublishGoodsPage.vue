<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { catalogApi, sellerApi } from '../api/modules'
import { extractErrorMessage } from '../api/http'

const router = useRouter()
const categories = ref([])
const imageFile = ref(null)
const imagePreview = ref('')
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

onMounted(async () => {
  const response = await catalogApi.categories()
  categories.value = response.data
  if (!form.category && categories.value.length) {
    form.category = categories.value[0].label
  }
})

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
    await sellerApi.create(form)
    router.push('/seller/goods')
  } catch (error) {
    errorMessage.value = error.userMessage || extractErrorMessage(error, '发布失败')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="panel-card wide-panel">
    <p class="eyebrow">发布商品</p>
    <h2>上架新的校园闲置</h2>
    <div class="form-grid two">
      <input v-model="form.name" class="text-input" placeholder="商品名称" />
      <select v-model="form.category" class="select-input">
        <option v-for="item in categories" :key="item.code" :value="item.label">{{ item.label }}</option>
      </select>
      <input v-model.number="form.price" class="text-input" type="number" placeholder="价格" />
      <input v-model.number="form.stock" class="text-input" type="number" placeholder="库存" />
      <div class="image-upload-area">
        <label class="image-upload-label">
          <span v-if="!imagePreview">点击上传商品图片</span>
          <img v-else :src="imagePreview" class="image-preview" />
          <input type="file" accept="image/*" class="image-file-input" @change="onFileChange" />
        </label>
        <p v-if="imageFile" class="hint-text">{{ imageFile.name }} ({{ (imageFile.size / 1024).toFixed(0) }}KB)</p>
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
    <button class="primary-btn" :disabled="submitting" @click="submit">确认发布</button>
  </section>
</template>
