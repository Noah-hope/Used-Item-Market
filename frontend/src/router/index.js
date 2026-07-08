import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import MainLayout from '../layouts/MainLayout.vue'
import MarketPage from '../views/MarketPage.vue'
import GoodsDetailPage from '../views/GoodsDetailPage.vue'
import LoginPage from '../views/LoginPage.vue'
import RegisterPage from '../views/RegisterPage.vue'
import CartPage from '../views/CartPage.vue'
import ProfilePage from '../views/ProfilePage.vue'
import PurchasesPage from '../views/PurchasesPage.vue'
import SalesPage from '../views/SalesPage.vue'
import SellerGoodsPage from '../views/SellerGoodsPage.vue'
import PublishGoodsPage from '../views/PublishGoodsPage.vue'
import EditGoodsPage from '../views/EditGoodsPage.vue'
import AdminUsersPage from '../views/AdminUsersPage.vue'
import AdminOrdersPage from '../views/AdminOrdersPage.vue'
import FavoritesPage from '../views/FavoritesPage.vue'
import WantedPage from '../views/WantedPage.vue'
import MessagesPage from '../views/MessagesPage.vue'
import AdminGoodsPage from '../views/AdminGoodsPage.vue'
import AdminDashboardPage from '../views/AdminDashboardPage.vue'
import AdminCategoriesPage from '../views/AdminCategoriesPage.vue'

const routes = [
  {
    path: '/',
    component: MainLayout,
    children: [
      { path: '', name: 'home', component: MarketPage },
      { path: 'market', name: 'market', component: MarketPage },
      { path: 'goods/:id', name: 'goods-detail', component: GoodsDetailPage, props: true },
      { path: 'cart', name: 'cart', component: CartPage, meta: { requiresAuth: true } },
      { path: 'profile', name: 'profile', component: ProfilePage, meta: { requiresAuth: true } },
      { path: 'favorites', name: 'favorites', component: FavoritesPage, meta: { requiresAuth: true } },
      { path: 'wanted', name: 'wanted', component: WantedPage },
      { path: 'messages', name: 'messages', component: MessagesPage, meta: { requiresAuth: true } },
      { path: 'orders/purchases', name: 'purchases', component: PurchasesPage, meta: { requiresAuth: true } },
      { path: 'orders/sales', name: 'sales', component: SalesPage, meta: { requiresAuth: true } },
      { path: 'seller/goods', name: 'seller-goods', component: SellerGoodsPage, meta: { requiresAuth: true } },
      { path: 'publish', name: 'publish', component: PublishGoodsPage, meta: { requiresAuth: true } },
      { path: 'seller/goods/:id/edit', name: 'edit-goods', component: EditGoodsPage, props: true, meta: { requiresAuth: true } },
      { path: 'admin/dashboard', name: 'admin-dashboard', component: AdminDashboardPage, meta: { requiresAuth: true, requiresAdmin: true } },
      { path: 'admin/users', name: 'admin-users', component: AdminUsersPage, meta: { requiresAuth: true, requiresAdmin: true } },
      { path: 'admin/goods', name: 'admin-goods', component: AdminGoodsPage, meta: { requiresAuth: true, requiresAdmin: true } },
      { path: 'admin/categories', name: 'admin-categories', component: AdminCategoriesPage, meta: { requiresAuth: true, requiresAdmin: true } },
      { path: 'admin/orders', name: 'admin-orders', component: AdminOrdersPage, meta: { requiresAuth: true, requiresAdmin: true } },
    ],
  },
  { path: '/login', name: 'login', component: LoginPage },
  { path: '/register', name: 'register', component: RegisterPage },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  if (!authStore.initialized) {
    await authStore.bootstrap()
  }
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && !authStore.user?.admin) {
    return { name: 'home' }
  }
  if (authStore.user?.admin) {
    const blockedForAdmin = new Set([
      'cart',
      'favorites',
      'messages',
      'purchases',
      'sales',
      'seller-goods',
      'publish',
      'edit-goods',
      'wanted',
      'profile',
    ])
    if (blockedForAdmin.has(to.name)) {
      return { name: 'home' }
    }
  }
  if ((to.name === 'login' || to.name === 'register') && authStore.isAuthenticated) {
    return { name: 'home' }
  }
  return true
})

export default router
