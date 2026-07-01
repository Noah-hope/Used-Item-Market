<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <div>
        <p class="eyebrow">管理后台</p>
        <h2>运营工作台</h2>
      </div>
      <button class="sidebar-link" @click="$router.push('/admin/dashboard')">数据看板</button>
      <button class="sidebar-link" @click="$router.push('/admin/users')">用户管理</button>
      <button class="sidebar-link" @click="$router.push('/admin/goods')">商品审核</button>
      <button class="sidebar-link" @click="$router.push('/admin/categories')">分类管理</button>
      <button class="sidebar-link" @click="$router.push('/admin/orders')">订单记录</button>
      <button class="sidebar-link" @click="$router.push('/')">返回前台</button>
    </aside>
    <section class="admin-content">
      <router-view />
    </section>
  </div>
</template>
