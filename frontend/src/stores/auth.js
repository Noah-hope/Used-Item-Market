import { defineStore } from 'pinia'
import { authApi } from '../api/modules'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('used-item-token') || '',
    user: JSON.parse(localStorage.getItem('used-item-user') || 'null'),
    initialized: false,
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token),
  },
  actions: {
    async bootstrap() {
      if (!this.token) {
        this.initialized = true
        return
      }
      try {
        const response = await authApi.me()
        this.user = response.data
        localStorage.setItem('used-item-user', JSON.stringify(this.user))
      } catch (error) {
        this.logout()
      } finally {
        this.initialized = true
      }
    },
    async login(payload) {
      const response = await authApi.login(payload)
      this.token = response.data.accessToken
      this.user = response.data.user
      localStorage.setItem('used-item-token', this.token)
      localStorage.setItem('used-item-user', JSON.stringify(this.user))
      this.initialized = true
      return response.data
    },
    async register(payload) {
      return authApi.register(payload)
    },
    logout() {
      this.token = ''
      this.user = null
      this.initialized = true
      localStorage.removeItem('used-item-token')
      localStorage.removeItem('used-item-user')
    },
  },
})
