<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const router = useRouter()
const searchKeyword = ref('')

function doSearch() {
  const kw = searchKeyword.value.trim()
  if (kw) {
    router.push(`/market?keyword=${encodeURIComponent(kw)}`)
  } else {
    router.push('/market')
  }
}

const menuItems = computed(() => [
  { label: '首页', to: '/' },
  { label: '商品市场', to: '/market' },
  { label: '闲置求购', to: '/wanted' },
  { label: '消息', to: '/messages' },
  { label: '购物车', to: '/cart' },
  { label: '收藏', to: '/favorites' },
  { label: '订单', to: '/orders/purchases' },
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
        <div class="brand-mark">闲</div>
        <div>
          <strong>校园闲置</strong>
          <p>校内二手交易平台</p>
        </div>
      </div>

      <div class="topbar-search">
        <input
          v-model="searchKeyword"
          class="topbar-search-input"
          placeholder="搜索校园闲置商品"
          @keyup.enter="doSearch"
        />
        <button class="topbar-search-btn" @click="doSearch">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
        </button>
      </div>

      <div class="topbar-actions">
        <template v-if="authStore.isAuthenticated">
          <button class="ghost-btn" @click="$router.push('/publish')">发布闲置</button>
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

    <nav class="topnav-bar">
      <div class="topnav">
        <button v-for="item in menuItems" :key="item.to" class="topnav-link" @click="$router.push(item.to)">
          {{ item.label }}
        </button>
      </div>
    </nav>

    <main class="page-shell">
      <router-view />
    </main>
  </div>
</template>
