<script setup>
import { onMounted, ref } from 'vue'
import { cartApi, orderApi } from '../api/modules'

const items = ref([])

async function loadCart() {
  const response = await cartApi.list()
  items.value = response.data
}

async function updateQuantity(item, quantity) {
  const response = await cartApi.update(item.gid, { gid: item.gid, quantity })
  items.value = response.data
}

async function removeItem(item) {
  const response = await cartApi.remove(item.gid)
  items.value = response.data
}

async function checkout(item) {
  await orderApi.create({ gid: item.gid, quantity: item.quantity, fromCart: true })
  await loadCart()
}

onMounted(loadCart)
</script>

<template>
  <section class="section-header">
    <div>
      <p class="eyebrow">购物车</p>
      <h2>待结算商品</h2>
    </div>
  </section>
  <div v-if="!items.length" class="empty-state">购物车还是空的</div>
  <div v-else class="list-panel">
    <div v-for="item in items" :key="item.gid" class="list-row">
      <div>
        <strong>{{ item.name }}</strong>
        <p>{{ item.category }} · ￥{{ item.price }}</p>
      </div>
      <div class="inline-actions">
        <input :value="item.quantity" type="number" min="1" class="number-input small" @change="updateQuantity(item, Number($event.target.value))" />
        <button class="ghost-btn" @click="removeItem(item)">移除</button>
        <button class="primary-btn" @click="checkout(item)">立即下单</button>
      </div>
    </div>
  </div>
</template>
