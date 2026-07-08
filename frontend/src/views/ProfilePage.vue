<script setup>
import { onMounted, reactive, ref } from 'vue'
import { addressApi, profileApi } from '../api/modules'

const profile = reactive({
  uid: '',
  username: '',
  email: '',
  phoneNumber: '',
  studentNo: '',
  realName: '',
  avatar: '',
  bio: '',
})

const passwordForm = reactive({
  password: '',
  confirmPassword: '',
})

const message = ref('')
const addresses = ref([])
const addressForm = reactive({
  receiverName: '',
  phoneNumber: '',
  campusArea: '',
  detailAddress: '',
  defaultAddress: false,
})

async function loadProfile() {
  const response = await profileApi.get()
  Object.assign(profile, response.data)
  const addressResponse = await addressApi.list()
  addresses.value = addressResponse.data
}

async function saveProfile() {
  const response = await profileApi.update(profile)
  Object.assign(profile, response.data)
  message.value = '个人信息已更新'
}

async function savePassword() {
  await profileApi.updatePassword(passwordForm)
  passwordForm.password = ''
  passwordForm.confirmPassword = ''
  message.value = '密码已更新'
}

async function saveAddress() {
  await addressApi.create(addressForm)
  addressForm.receiverName = ''
  addressForm.phoneNumber = ''
  addressForm.campusArea = ''
  addressForm.detailAddress = ''
  addressForm.defaultAddress = false
  const response = await addressApi.list()
  addresses.value = response.data
}

async function removeAddress(id) {
  await addressApi.remove(id)
  const response = await addressApi.list()
  addresses.value = response.data
}

onMounted(loadProfile)
</script>

<template>
  <section class="profile-grid">
    <div class="panel-card profile-panel">
      <h2>基本信息</h2>
      <div class="form-grid">
        <input v-model="profile.uid" class="text-input" disabled />
        <input v-model="profile.studentNo" class="text-input" disabled />
        <input v-model="profile.username" class="text-input" placeholder="姓名" />
        <input v-model="profile.realName" class="text-input" placeholder="实名信息" />
        <input v-model="profile.email" class="text-input" placeholder="邮箱" />
        <input v-model="profile.phoneNumber" class="text-input" placeholder="手机号" />
        <input v-model="profile.avatar" class="text-input" placeholder="头像 URL（可选）" />
        <textarea v-model="profile.bio" class="text-area" placeholder="个人简介"></textarea>
      </div>
      <button class="primary-btn" @click="saveProfile">保存信息</button>
    </div>
    <div class="panel-card profile-panel password-panel">
      <h2>修改密码</h2>
      <div class="password-row">
        <input v-model="passwordForm.password" class="text-input" type="password" placeholder="新密码" />
        <input v-model="passwordForm.confirmPassword" class="text-input" type="password" placeholder="确认密码" />
      </div>
      <button class="primary-btn" @click="savePassword">更新密码</button>
      <p v-if="message" class="success-text">{{ message }}</p>
    </div>
    <div class="panel-card profile-panel">
      <h2>校内地址管理</h2>
      <div class="form-grid">
        <input v-model="addressForm.receiverName" class="text-input" placeholder="收货人" />
        <input v-model="addressForm.phoneNumber" class="text-input" placeholder="手机号" />
        <input v-model="addressForm.campusArea" class="text-input" placeholder="校区 / 宿舍区" />
        <input v-model="addressForm.detailAddress" class="text-input" placeholder="详细地址" />
        <label class="checkbox-row">
          <input v-model="addressForm.defaultAddress" type="checkbox" />
          <span>设为默认地址</span>
        </label>
      </div>
      <button class="primary-btn" @click="saveAddress">新增地址</button>
      <div v-if="addresses.length" class="list-panel">
        <div v-for="item in addresses" :key="item.id" class="list-row">
          <div>
            <strong>{{ item.receiverName }} {{ item.defaultAddress ? '（默认）' : '' }}</strong>
            <p>{{ item.phoneNumber }} · {{ item.campusArea }} · {{ item.detailAddress }}</p>
          </div>
          <button class="ghost-btn" @click="removeAddress(item.id)">删除</button>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.profile-panel > h2 {
  margin-bottom: 16px;
}

.profile-panel > .primary-btn,
.profile-panel > .success-text,
.profile-panel > .list-panel {
  margin-top: 16px;
}

.password-panel {
  align-self: start;
}

.password-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 12px;
}

@media (max-width: 720px) {
  .password-row {
    grid-template-columns: 1fr;
  }
}
</style>
