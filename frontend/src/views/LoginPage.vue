<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { extractErrorMessage } from '../api/http'

const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()
const loading = ref(false)
const errorMessage = ref('')
const form = reactive({
  studentNo: '',
  password: '',
  admin: false,
})

async function submit() {
  loading.value = true
  errorMessage.value = ''
  try {
    await authStore.login(form)
    router.push(route.query.redirect || '/')
  } catch (error) {
    errorMessage.value = error.userMessage || extractErrorMessage(error, '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-shell">
    <div class="auth-card">
      <p class="eyebrow">登录</p>
      <h1>进入校园闲置市场</h1>
      <input v-model="form.studentNo" class="text-input" placeholder="学号" />
      <input v-model="form.password" class="text-input" type="password" placeholder="密码" />
      <label class="checkbox-row">
        <input v-model="form.admin" type="checkbox" />
        <span>以管理员身份登录</span>
      </label>
      <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
      <button class="primary-btn wide" :disabled="loading" @click="submit">登录</button>
      <button class="ghost-btn wide" @click="$router.push('/register')">去注册</button>
    </div>
  </div>
</template>
