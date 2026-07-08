<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { catalogApi, chatApi, orderApi } from '../api/modules'
import StatusTabs from '../components/StatusTabs.vue'
import { resolveAssetUrl } from '../utils/assets'

const router = useRouter()
const status = ref('PENDING_SHIPMENT')
const orders = ref([])

const tabItems = [
  { label: '待发货', value: 'PENDING_SHIPMENT' },
  { label: '待自提', value: 'PENDING_PICKUP' },
  { label: '待收货', value: 'PENDING_RECEIPT' },
  { label: '已完成', value: 'COMPLETED' },
]

async function loadOrders() {
  const response = await orderApi.sales(status.value)
  const orderList = response.data || []
  const detailList = await Promise.all(
    orderList.map(async (order) => {
      try {
        const detail = await catalogApi.detail(order.gid)
        return { gid: order.gid, image: detail.data?.image || '' }
      } catch {
        return { gid: order.gid, image: '' }
      }
    }),
  )
  const imageMap = new Map(detailList.map((item) => [item.gid, item.image]))
  orders.value = orderList.map((order) => ({
    ...order,
    image: imageMap.get(order.gid) || '',
  }))
}

async function ship(order) {
  await orderApi.ship(order.pid)
  await loadOrders()
}

function canDelete(order) {
  return ['PENDING_SHIPMENT', 'PENDING_PICKUP', 'COMPLETED'].includes(order.status)
}

async function contactBuyer(order) {
  await chatApi.send({
    receiverUid: order.buyerUid,
    goodsId: order.gid,
    orderPid: order.pid,
    content: `你好，关于订单“${order.goodsName}”，我们可以沟通一下交付时间和地点。`,
  })
  router.push({
    path: '/messages',
    query: { conversationKey: `ORDER:${order.pid}` },
  })
}

async function removeOrder(order) {
  const message = ['PENDING_SHIPMENT', 'PENDING_PICKUP'].includes(order.status)
    ? '删除后订单会取消，库存会恢复。聊天会话不会一起删除，确认删除吗？'
    : '删除后仅移除订单记录，聊天会话不会一起删除，确认删除吗？'
  if (!window.confirm(message)) {
    return
  }
  await orderApi.remove(order.pid)
  await loadOrders()
}

watch(status, loadOrders)
onMounted(loadOrders)
</script>

<template>
  <section class="section-header">
    <div>
      <h2>卖家订单</h2>
    </div>
  </section>
  <StatusTabs v-model="status" :items="tabItems" />
  <div v-if="!orders.length" class="empty-state">当前状态下没有订单</div>
  <div v-else class="order-card-list">
    <article v-for="order in orders" :key="order.pid" class="order-card">
      <div class="order-card-image-wrap">
        <img
          :src="resolveAssetUrl(order.image) || 'https://img.alicdn.com/imgextra/i1/O1CN01v3ZQ7I1Fvxck5BYnX_!!6000000000550-2-tps-800-800.png'"
          :alt="order.goodsName"
          class="order-card-image"
        />
      </div>
      <div class="order-card-status">
        <span class="tag">{{ $enumLabel('orderStatus', order.status) }}</span>
      </div>
      <div class="order-card-info">
        <h3>{{ order.goodsName }}</h3>
        <div class="goods-meta-tags">
          <span class="goods-meta-tag">买家：{{ order.buyerUid }}</span>
          <span class="goods-meta-tag">时间：{{ order.date }} {{ order.time }}</span>
          <span class="goods-meta-tag">交付：{{ $enumLabel('deliveryMode', order.deliveryMode) || order.deliveryMode }}</span>
          <span class="goods-meta-tag">地址：{{ order.addressSnapshot || '待补充' }}</span>
        </div>
      </div>
      <div class="order-card-actions">
        <button class="ghost-btn" @click="contactBuyer(order)">联系买家</button>
        <button v-if="status === 'PENDING_SHIPMENT'" class="primary-btn" @click="ship(order)">确认发货</button>
        <button v-if="canDelete(order)" class="ghost-btn" @click="removeOrder(order)">删除订单</button>
      </div>
    </article>
  </div>
</template>
