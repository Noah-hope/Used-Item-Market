<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const router = useRouter()

const menuItems = computed(() => [
  { label: '首页', to: '/' },
  { label: '商品市场', to: '/market' },
  { label: '闲置求购', to: '/wanted' },
  { label: '私信消息', to: '/messages' },
  { label: '我的购物车', to: '/cart' },
  { label: '我的收藏', to: '/favorites' },
  { label: '我的订单', to: '/orders/purchases' },
  { label: '我的商品', to: '/seller/goods' },
])

function logout() {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="brand" @click="$router.push('/')">
        <div class="brand-mark">U</div>
        <div>
          <strong>UsedItemMarket</strong>
          <p>校园闲置流转平台</p>
        </div>
      </div>
      <nav class="topnav">
        <button v-for="item in menuItems" :key="item.to" class="topnav-link" @click="$router.push(item.to)">
          {{ item.label }}
        </button>
      </nav>
      <div class="topbar-actions">
        <template v-if="authStore.isAuthenticated">
          <button class="ghost-btn" @click="$router.push('/publish')">发布商品</button>
          <button class="ghost-btn" @click="$router.push('/profile')">{{ authStore.user?.username || '个人中心' }}</button>
          <button v-if="authStore.user?.admin" class="ghost-btn" @click="$router.push('/admin/dashboard')">后台</button>
          <button class="primary-btn" @click="logout">退出</button>
        </template>
        <template v-else>
          <button class="ghost-btn" @click="$router.push('/login')">登录</button>
          <button class="primary-btn" @click="$router.push('/register')">注册</button>
        </template>
      </div>
    </header>
    <main class="page-shell">
      <router-view />
    </main>
  </div>
</template>
