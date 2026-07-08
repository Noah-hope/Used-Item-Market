<script setup>
import { onMounted, ref } from 'vue'
import { adminApi } from '../api/modules'
import { resolveAssetUrl } from '../utils/assets'

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
      <div class="admin-section-tag">待审核</div>
      <div v-if="!pendingGoods.length" class="empty-state">当前没有待审核商品</div>
      <div v-else class="admin-review-list">
        <article v-for="item in pendingGoods" :key="item.gid" class="admin-review-card">
          <div class="admin-review-image-wrap">
            <img
              :src="resolveAssetUrl(item.image) || 'https://img.alicdn.com/imgextra/i1/O1CN01v3ZQ7I1Fvxck5BYnX_!!6000000000550-2-tps-800-800.png'"
              :alt="item.name"
              class="admin-review-image"
            />
          </div>
          <div class="admin-review-body">
            <div class="admin-review-head">
              <p class="goods-category">{{ item.category }}</p>
              <h3>{{ item.name }}</h3>
            </div>
            <div class="goods-meta-tags admin-review-tags">
              <span class="goods-meta-tag">GID：{{ item.gid }}</span>
              <span class="goods-meta-tag">卖家：{{ item.sellerName || item.sellerUid }}</span>
              <span class="goods-meta-tag">价格：¥{{ item.price }}</span>
              <span class="goods-meta-tag">库存：{{ item.stock }}</span>
              <span class="goods-meta-tag">交付方式：{{ $enumLabel('deliveryMode', item.deliveryMode) || item.deliveryMode }}</span>
            </div>
            <div class="admin-review-note">
              <span class="admin-review-note-label">商品描述</span>
              <p>{{ item.comment || '暂无描述' }}</p>
            </div>
            <div class="admin-review-note">
              <span class="admin-review-note-label">审核员备注</span>
              <input v-model="reviewNote[item.gid]" class="text-input" placeholder="审核备注（可选）" />
            </div>
            <div class="review-actions admin-review-actions">
              <button class="ghost-btn" @click="review(item.gid, 'reject')">驳回</button>
              <button class="primary-btn danger" @click="review(item.gid, 'ban')">违规下架</button>
              <button class="primary-btn" @click="review(item.gid, 'approve')">通过</button>
            </div>
          </div>
        </article>
      </div>
    </div>

    <div class="admin-table-block">
      <div class="admin-section-tag">全站商品</div>
      <div class="table-shell admin-table-shell">
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
    </div>
  </section>
</template>
