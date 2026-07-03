<script setup>
import { onMounted, ref } from 'vue'
import { adminApi } from '../api/modules'

const users = ref([])

async function loadUsers() {
  const response = await adminApi.users()
  users.value = response.data
}

async function disableUser(user) {
  await adminApi.disable(user.uid)
  await loadUsers()
}

async function enableUser(user) {
  await adminApi.enable(user.uid)
  await loadUsers()
}

async function resetPassword(user) {
  await adminApi.resetPassword(user.uid)
  await loadUsers()
}

onMounted(loadUsers)
</script>

<template>
  <section class="admin-panel">
    <div class="section-header compact">
      <div>
        <p class="eyebrow">用户管理</p>
        <h2>校内账号列表</h2>
      </div>
    </div>
    <div class="table-shell">
      <table class="data-table">
        <thead>
          <tr>
            <th>UID</th>
            <th>姓名</th>
            <th>邮箱</th>
            <th>手机号</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.uid">
            <td>{{ user.uid }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.phoneNumber }}</td>
            <td>{{ user.status }}</td>
            <td>
              <div class="inline-actions">
                <button class="ghost-btn" @click="resetPassword(user)">重置密码</button>
                <button
                  v-if="user.status === 'DISABLED'"
                  class="primary-btn"
                  @click="enableUser(user)"
                >
                  启用
                </button>
                <button
                  v-else
                  class="primary-btn danger"
                  @click="disableUser(user)"
                >
                  停用
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
