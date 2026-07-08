<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()
const searchKeyword = ref('')
const isAdmin = computed(() => Boolean(authStore.user?.admin))

const menuItems = computed(() => {
  if (isAdmin.value) {
    return [
      { label: '商场首页', to: '/', match: (path) => path === '/' || path === '/market' || path.startsWith('/goods/') },
      { label: '数据看板', to: '/admin/dashboard', match: (path) => path.startsWith('/admin/dashboard') },
      { label: '用户管理', to: '/admin/users', match: (path) => path.startsWith('/admin/users') },
      { label: '商品审核', to: '/admin/goods', match: (path) => path.startsWith('/admin/goods') },
      { label: '分类管理', to: '/admin/categories', match: (path) => path.startsWith('/admin/categories') },
      { label: '订单记录', to: '/admin/orders', match: (path) => path.startsWith('/admin/orders') },
    ]
  }
  return [
    { label: '首页', to: '/', match: (path) => path === '/' || path === '/market' },
    { label: '闲置求购', to: '/wanted', match: (path) => path.startsWith('/wanted') },
    { label: '消息', to: '/messages', match: (path) => path.startsWith('/messages') },
    { label: '购物车', to: '/cart', match: (path) => path.startsWith('/cart') },
    { label: '收藏', to: '/favorites', match: (path) => path.startsWith('/favorites') },
    { label: '我的购买', to: '/orders/purchases', match: (path) => path.startsWith('/orders/purchases') },
    { label: '我的售卖', to: '/orders/sales', match: (path) => path.startsWith('/orders/sales') },
    { label: '我的商品', to: '/seller/goods', match: (path) => path.startsWith('/seller/goods') || path.startsWith('/publish') },
  ]
})

function isActive(item) {
  return item.match(route.path)
}

function go(item) {
  router.push(item.to)
}

function syncKeywordFromRoute() {
  searchKeyword.value = typeof route.query.keyword === 'string' ? route.query.keyword : ''
}

function doSearch() {
  const keyword = searchKeyword.value.trim()
  if (keyword) {
    router.push({ path: '/', query: { ...route.query, keyword } })
    return
  }
  const nextQuery = { ...route.query }
  delete nextQuery.keyword
  router.push({ path: '/', query: nextQuery })
}

function logout() {
  authStore.logout()
  router.push('/login')
}

watch(() => route.fullPath, syncKeywordFromRoute, { immediate: true })
</script>

<template>
  <div class="main-app">
    <header class="main-topbar">
      <button class="app-name" @click="$router.push('/')">淘多多</button>

      <div class="main-search">
        <input
          v-model="searchKeyword"
          class="main-search-input"
          placeholder="搜索闲置商品"
          @keyup.enter="doSearch"
        />
        <button class="main-search-btn" @click="doSearch">搜索</button>
      </div>

      <div class="main-actions">
        <template v-if="authStore.isAuthenticated">
          <button class="user-chip" @click="$router.push('/profile')">
            {{ authStore.user?.username || '当前用户' }}
          </button>
          <button class="logout-btn" @click="logout">退出</button>
        </template>
        <template v-else>
          <button class="user-chip" @click="$router.push('/login')">登录</button>
          <button class="logout-btn" @click="$router.push('/register')">注册</button>
        </template>
      </div>
    </header>

    <div class="main-body">
      <aside class="side-nav">
        <button
          v-for="item in menuItems"
          :key="item.to"
          class="side-nav-item"
          :class="{ active: isActive(item) }"
          @click="go(item)"
        >
          {{ item.label }}
        </button>
      </aside>

      <main class="content-shell">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.main-app {
  min-height: 100vh;
  background: #f5f5f5;
  color: #333;
}

.main-topbar {
  height: 64px;
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr) auto;
  align-items: center;
  gap: 20px;
  padding: 0 20px;
  background: linear-gradient(90deg, #ff8a1f 0%, #ff6a1f 55%, #ff5a1f 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 0 8px 24px rgba(255, 106, 31, 0.16);
  position: sticky;
  top: 0;
  z-index: 50;
}

.app-name {
  background: transparent;
  color: #fff;
  font-size: 26px;
  font-weight: 700;
  text-align: left;
  padding: 0;
}

.main-search {
  max-width: 75%;
  width: 100%;
  justify-self: center;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 999px;
  padding: 4px;
  border: 1px solid rgba(255, 255, 255, 0.16);
}

.main-search-input {
  flex: 1;
  height: 36px;
  border: none;
  background: transparent;
  padding: 0 16px;
  color: #fff;
  outline: none;
}

.main-search-input::placeholder {
  color: rgba(255, 255, 255, 0.72);
}

.main-search-btn {
  height: 40px;
  min-width: 88px;
  border-radius: 999px;
  background: #fff;
  color: #ff5a1f;
  font-weight: 600;
  padding: 0 22px;
  box-shadow: 0 8px 20px rgba(255, 255, 255, 0.22);
}

.main-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-chip,
.logout-btn {
  min-height: 42px;
  padding: 0 20px;
  border-radius: 999px;
  font-weight: 600;
  line-height: 1.2;
}

.user-chip {
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.16);
}

.logout-btn {
  background: linear-gradient(135deg, #ff7a1f, #ff5a1f);
  color: #fff;
  box-shadow: 0 8px 18px rgba(255, 90, 31, 0.2);
}

.main-body {
  display: grid;
  grid-template-columns: 168px minmax(0, 1fr);
  min-height: calc(100vh - 64px);
}

.side-nav {
  background: linear-gradient(180deg, #fff4ec 0%, #fffaf6 100%);
  border-right: 1px solid #ffd7c2;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: sticky;
  top: 64px;
  height: calc(100vh - 64px);
}

.side-nav-item {
  min-height: 44px;
  border-radius: 10px;
  background: transparent;
  color: #8a4b22;
  text-align: left;
  padding: 0 14px;
  font-weight: 600;
}

.side-nav-item:hover {
  background: #ffe7d8;
  color: #ff5a1f;
}

.side-nav-item.active {
  background: linear-gradient(135deg, #ff7a1f, #ff5a1f);
  color: #fff;
  box-shadow: 0 8px 20px rgba(255, 90, 31, 0.18);
}

.content-shell {
  min-width: 0;
  padding: 18px 20px 32px;
}

@media (max-width: 960px) {
  .main-topbar {
    grid-template-columns: 1fr;
    height: auto;
    padding: 12px 16px;
  }

  .main-search {
    max-width: none;
  }

  .main-body {
    grid-template-columns: 1fr;
  }

  .side-nav {
    position: static;
    height: auto;
    border-right: none;
    border-bottom: 1px solid #ececec;
    flex-direction: row;
    flex-wrap: wrap;
  }
}
</style>
