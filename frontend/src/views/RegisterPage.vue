<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { extractErrorMessage } from '../api/http'

const authStore = useAuthStore()
const router = useRouter()
const message = ref('')
const errorMessage = ref('')
const form = reactive({
  studentNo: '',
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phoneNumber: '',
})

async function submit() {
  errorMessage.value = ''
  message.value = ''
  try {
    await authStore.register(form)
    message.value = '注册成功，请直接登录'
    router.push('/login')
  } catch (error) {
    errorMessage.value = error.userMessage || extractErrorMessage(error, '注册失败')
  }
}
</script>

<template>
  <div class="auth-shell">
    <div class="auth-card large">
      <p class="eyebrow">注册</p>
      <h1>创建校内交易账号</h1>
      <div class="form-grid two">
        <input v-model="form.studentNo" class="text-input" placeholder="学号" />
        <input v-model="form.username" class="text-input" placeholder="姓名" />
        <input v-model="form.email" class="text-input" placeholder="邮箱" />
        <input v-model="form.phoneNumber" class="text-input" placeholder="手机号" />
        <input v-model="form.password" class="text-input" type="password" placeholder="密码" />
        <input v-model="form.confirmPassword" class="text-input" type="password" placeholder="确认密码" />
      </div>
      <p v-if="message" class="success-text">{{ message }}</p>
      <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
      <button class="primary-btn wide" @click="submit">提交注册</button>
    </div>
  </div>
</template>
