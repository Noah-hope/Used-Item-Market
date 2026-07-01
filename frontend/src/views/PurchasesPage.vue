<script setup>
import { onMounted, ref, watch } from 'vue'
import { orderApi } from '../api/modules'
import StatusTabs from '../components/StatusTabs.vue'

const status = ref('PENDING_CONTACT')
const orders = ref([])

const tabItems = [
  { label: '待沟通', value: 'PENDING_CONTACT' },
  { label: '待自提', value: 'PENDING_PICKUP' },
  { label: '已完成', value: 'COMPLETED' },
]

async function loadOrders() {
  const response = await orderApi.purchases(status.value)
  orders.value = response.data
}

async function receive(order) {
  await orderApi.receive(order.pid)
  await loadOrders()
}

watch(status, loadOrders)
onMounted(loadOrders)
</script>

<template>
  <section class="section-header">
    <div>
      <p class="eyebrow">我的购买</p>
      <h2>订单记录</h2>
    </div>
  </section>
  <StatusTabs v-model="status" :items="tabItems" />
  <div v-if="!orders.length" class="empty-state">当前状态下没有订单</div>
  <div v-else class="list-panel">
    <div v-for="order in orders" :key="order.pid" class="list-row">
      <div>
        <strong>{{ order.goodsName }}</strong>
        <p>{{ order.category }} · {{ order.date }} {{ order.time }}</p>
        <p>交付：{{ order.deliveryMode }} · 地址：{{ order.addressSnapshot || '待补充' }}</p>
      </div>
      <div class="inline-actions">
        <span class="tag">{{ order.status }}</span>
        <button v-if="status === 'PENDING_PICKUP'" class="primary-btn" @click="receive(order)">确认完成</button>
      </div>
    </div>
  </div>
</template>
