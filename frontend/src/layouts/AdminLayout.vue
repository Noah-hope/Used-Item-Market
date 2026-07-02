<script setup>
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const sidebarItems = [
  { label: '数据看板', to: '/admin/dashboard' },
  { label: '用户管理', to: '/admin/users' },
  { label: '商品审核', to: '/admin/goods' },
  { label: '分类管理', to: '/admin/categories' },
  { label: '订单记录', to: '/admin/orders' },
]

function isActive(path) {
  return route.path.startsWith(path)
}
</script>

<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div class="sidebar-brand">
        <div class="brand-mark admin-mark">管</div>
        <div>
          <p class="eyebrow">管理后台</p>
          <h2>运营工作台</h2>
        </div>
      </div>
      <nav class="sidebar-nav">
        <button
          v-for="item in sidebarItems"
          :key="item.to"
          class="sidebar-link"
          :class="{ active: isActive(item.to) }"
          @click="router.push(item.to)"
        >
          {{ item.label }}
        </button>
      </nav>
      <div class="sidebar-footer">
        <button class="sidebar-link" @click="router.push('/')">返回前台</button>
      </div>
    </aside>
    <section class="admin-content">
      <router-view />
    </section>
  </div>
</template>
