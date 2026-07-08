import http from './http'

export const authApi = {
  login(payload) {
    return http.post('/auth/login', payload)
  },
  register(payload) {
    return http.post('/auth/register', payload)
  },
  me() {
    return http.get('/auth/me')
  },
}

export const catalogApi = {
  list(params) {
    return http.get('/catalog/goods', { params })
  },
  detail(id) {
    return http.get(`/catalog/goods/${id}`)
  },
  categories() {
    return http.get('/catalog/categories')
  },
}

export const addressApi = {
  list() {
    return http.get('/addresses')
  },
  create(payload) {
    return http.post('/addresses', payload)
  },
  update(id, payload) {
    return http.put(`/addresses/${id}`, payload)
  },
  remove(id) {
    return http.delete(`/addresses/${id}`)
  },
}

export const favoriteApi = {
  list() {
    return http.get('/favorites')
  },
  add(gid) {
    return http.post(`/favorites/${gid}`)
  },
  remove(gid) {
    return http.delete(`/favorites/${gid}`)
  },
}

export const chatApi = {
  conversations() {
    return http.get('/chat/conversations')
  },
  removeConversation(conversationKey) {
    return http.delete(`/chat/conversations/${encodeURIComponent(conversationKey)}`)
  },
  messages(conversationKey) {
    return http.get(`/chat/messages/${encodeURIComponent(conversationKey)}`)
  },
  send(payload) {
    return http.post('/chat/messages', payload)
  },
}

export const wantedApi = {
  list() {
    return http.get('/wanted/open')
  },
  mine() {
    return http.get('/wanted/mine')
  },
  create(payload) {
    return http.post('/wanted', payload)
  },
  remove(id) {
    return http.delete(`/wanted/${id}`)
  },
}

export const sellerApi = {
  list() {
    return http.get('/seller/goods')
  },
  detail(id) {
    return http.get(`/seller/goods/${id}`)
  },
  create(payload) {
    return http.post('/seller/goods', payload)
  },
  update(id, payload) {
    return http.put(`/seller/goods/${id}`, payload)
  },
  remove(id) {
    return http.delete(`/seller/goods/${id}`)
  },
  permanentDelete(id) {
    return http.delete(`/seller/goods/${id}/permanent`)
  },
  uploadImage(file) {
    const fd = new FormData()
    fd.append('imageFile', file)
    return http.post('/seller/goods/upload-image', fd)
  },
}

export const cartApi = {
  list() {
    return http.get('/cart')
  },
  add(payload) {
    return http.post('/cart/items', payload)
  },
  update(id, payload) {
    return http.put(`/cart/items/${id}`, payload)
  },
  remove(id) {
    return http.delete(`/cart/items/${id}`)
  },
}

export const orderApi = {
  create(payload) {
    return http.post('/orders', payload)
  },
  purchases(status) {
    return http.get('/orders/purchases', { params: { status } })
  },
  sales(status) {
    return http.get('/orders/sales', { params: { status } })
  },
  ship(pid) {
    return http.post(`/orders/${pid}/ship`)
  },
  receive(pid) {
    return http.post(`/orders/${pid}/receive`)
  },
  remove(pid) {
    return http.delete(`/orders/${pid}`)
  },
}

export const profileApi = {
  get() {
    return http.get('/profile')
  },
  update(payload) {
    return http.put('/profile', payload)
  },
  updatePassword(payload) {
    return http.put('/profile/password', payload)
  },
}

export const adminApi = {
  users() {
    return http.get('/admin/users')
  },
  updateUser(uid, payload) {
    return http.put(`/admin/users/${uid}`, payload)
  },
  resetPassword(uid) {
    return http.post(`/admin/users/${uid}/reset-password`)
  },
  disable(uid) {
    return http.post(`/admin/users/${uid}/disable`)
  },
  enable(uid) {
    return http.post(`/admin/users/${uid}/enable`)
  },
  orders() {
    return http.get('/admin/orders')
  },
  pendingGoods() {
    return http.get('/admin/goods/pending')
  },
  goods() {
    return http.get('/admin/goods')
  },
  reviewGoods(gid, payload) {
    return http.post(`/admin/goods/${gid}/review`, payload)
  },
  categories() {
    return http.get('/admin/categories')
  },
  saveCategory(payload) {
    return http.post('/admin/categories', payload)
  },
  dashboard() {
    return http.get('/admin/dashboard')
  },
}
