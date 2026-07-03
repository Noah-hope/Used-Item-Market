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
  const response = await orderApi.sales(status.value)
  orders.value = response.data
}

async function ship(order) {
  await orderApi.ship(order.pid)
  await loadOrders()
}

watch(status, loadOrders)
onMounted(loadOrders)
</script>

<template>
  <section class="section-header">
    <div>
      <p class="eyebrow">我的售卖</p>
      <h2>卖家订单</h2>
    </div>
  </section>
  <StatusTabs v-model="status" :items="tabItems" />
  <div v-if="!orders.length" class="empty-state">当前状态下没有订单</div>
  <div v-else class="list-panel">
    <div v-for="order in orders" :key="order.pid" class="list-row">
      <div>
        <strong>{{ order.goodsName }}</strong>
        <p>买家 {{ order.buyerUid }} · {{ order.date }} {{ order.time }}</p>
        <p>交付：{{ $enumLabel('deliveryMode', order.deliveryMode) || order.deliveryMode }} · 地址：{{ order.addressSnapshot || '待补充' }}</p>
      </div>
      <div class="inline-actions">
        <span class="tag">{{ $enumLabel('orderStatus', order.status) }}</span>
        <button v-if="status === 'PENDING_CONTACT'" class="primary-btn" @click="ship(order)">确认沟通完成</button>
      </div>
    </div>
  </div>
</template>
