import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

function extractErrorMessage(error, fallback = '请求失败') {
  const responseMessage = error.response?.data?.message
  if (responseMessage) {
    return responseMessage
  }

  const status = error.response?.status
  if (status === 404) {
    return '接口不存在或后端服务未正确启动'
  }
  if (status === 500) {
    return '服务器内部错误'
  }
  if (status === 403) {
    return '没有权限执行该操作'
  }
  if (status === 400) {
    return '请求参数不正确'
  }

  if (error.code === 'ECONNABORTED') {
    return '请求超时，请检查后端服务是否正常'
  }

  if (!error.response) {
    return '无法连接到后端服务'
  }

  return fallback
}

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('used-item-token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('used-item-token')
      localStorage.removeItem('used-item-user')
      if (window.location.pathname !== '/login') {
        window.location.href = `/login?redirect=${encodeURIComponent(window.location.pathname)}`
      }
    }
    error.userMessage = extractErrorMessage(error)
    return Promise.reject(error)
  },
)

export default http
export { extractErrorMessage }
