<script setup>
import { onMounted, ref } from 'vue'
import { adminApi } from '../api/modules'

const orders = ref([])

onMounted(async () => {
  const response = await adminApi.orders()
  orders.value = response.data
})
</script>

<template>
  <section class="admin-panel">
    <div class="section-header compact">
      <div>
        <p class="eyebrow">订单记录</p>
        <h2>全站交易概览</h2>
      </div>
    </div>
    <div class="table-shell">
      <table class="data-table">
        <thead>
          <tr>
            <th>PID</th>
            <th>商品</th>
            <th>买家</th>
            <th>卖家</th>
            <th>价格</th>
            <th>数量</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.pid">
            <td>{{ order.pid }}</td>
            <td>{{ order.goodsName }}</td>
            <td>{{ order.buyerUid }}</td>
            <td>{{ order.sellerUid }}</td>
            <td>￥{{ order.price }}</td>
            <td>{{ order.quantity }}</td>
            <td>{{ $enumLabel('orderStatus', order.status) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
