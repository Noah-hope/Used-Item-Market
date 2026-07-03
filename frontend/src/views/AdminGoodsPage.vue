<script setup>
import { onMounted, ref } from 'vue'
import { adminApi } from '../api/modules'

const pendingGoods = ref([])
const allGoods = ref([])
const reviewNote = ref({})

async function loadData() {
  const [pending, all] = await Promise.all([adminApi.pendingGoods(), adminApi.goods()])
  pendingGoods.value = pending.data
  allGoods.value = all.data
}

async function review(gid, action) {
  await adminApi.reviewGoods(gid, { action, note: reviewNote.value[gid] || '' })
  reviewNote.value[gid] = ''
  await loadData()
}

onMounted(loadData)
</script>

<template>
  <section class="admin-panel">
    <div class="section-header compact">
      <div>
        <p class="eyebrow">商品审核</p>
        <h2>待审核与全站商品</h2>
      </div>
    </div>

    <div class="panel-card inner-panel">
      <p class="eyebrow">待审核</p>
      <div v-if="!pendingGoods.length" class="empty-state">当前没有待审核商品</div>
      <div v-else class="list-panel">
        <div v-for="item in pendingGoods" :key="item.gid" class="list-row stacked">
          <div>
            <strong>{{ item.name }}</strong>
            <p>{{ item.category }} · 卖家 {{ item.sellerName || item.sellerUid }} · {{ $enumLabel('deliveryMode', item.deliveryMode) || item.deliveryMode }}</p>
            <p>{{ item.comment || '暂无描述' }}</p>
          </div>
          <div class="review-actions">
            <input v-model="reviewNote[item.gid]" class="text-input" placeholder="审核备注（可选）" />
            <div class="inline-actions">
              <button class="ghost-btn" @click="review(item.gid, 'reject')">驳回</button>
              <button class="primary-btn danger" @click="review(item.gid, 'ban')">违规下架</button>
              <button class="primary-btn" @click="review(item.gid, 'approve')">通过</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="table-shell">
      <table class="data-table">
        <thead>
          <tr>
            <th>GID</th>
            <th>商品</th>
            <th>分类</th>
            <th>卖家</th>
            <th>状态</th>
            <th>审核备注</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in allGoods" :key="item.gid">
            <td>{{ item.gid }}</td>
            <td>{{ item.name }}</td>
            <td>{{ item.category }}</td>
            <td>{{ item.sellerName || item.sellerUid }}</td>
            <td>{{ $enumLabel('goodsStatus', item.status) }}</td>
            <td>{{ item.reviewNote || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
