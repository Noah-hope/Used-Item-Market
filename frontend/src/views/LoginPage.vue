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
      <h1>登录</h1>
      <div class="role-switch" aria-label="登录身份">
        <button
          type="button"
          class="role-switch__item"
          :class="{ 'role-switch__item--active': !form.admin }"
          @click="form.admin = false"
        >
          普通用户
        </button>
        <button
          type="button"
          class="role-switch__item"
          :class="{ 'role-switch__item--active': form.admin }"
          @click="form.admin = true"
        >
          管理员
        </button>
      </div>
      <input v-model="form.studentNo" class="text-input" placeholder="学号" />
      <input v-model="form.password" class="text-input" type="password" placeholder="密码" />
      <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
      <button class="primary-btn wide" :disabled="loading" @click="submit">登录</button>
      <button class="ghost-btn wide" @click="$router.push('/register')">去注册</button>
    </div>
  </div>
</template>

<style scoped>
.role-switch {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 14px;
}

.role-switch__item {
  border: 1px solid #f3d0b8;
  background: #fff7f1;
  color: #8a4b20;
  border-radius: 10px;
  padding: 10px 14px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.role-switch__item--active {
  border-color: #ff8a00;
  background: linear-gradient(135deg, #ff9f1a, #ff7a00);
  color: #fff;
  box-shadow: 0 10px 22px rgba(255, 138, 0, 0.2);
}
</style>
