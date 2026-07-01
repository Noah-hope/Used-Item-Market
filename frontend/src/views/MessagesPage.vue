<script setup>
import { onMounted, ref } from 'vue'
import { chatApi } from '../api/modules'

const conversations = ref([])
const activePeer = ref('')
const messages = ref([])
const draft = ref('')

async function loadConversations() {
  const response = await chatApi.conversations()
  conversations.value = response.data
  if (!activePeer.value && conversations.value.length) {
    activePeer.value = conversations.value[0].peerUid
    await loadMessages()
  }
}

async function loadMessages() {
  if (!activePeer.value) return
  const response = await chatApi.messages(activePeer.value)
  messages.value = response.data
}

async function sendMessage() {
  if (!draft.value.trim() || !activePeer.value) return
  await chatApi.send({ receiverUid: activePeer.value, content: draft.value.trim() })
  draft.value = ''
  await loadMessages()
  await loadConversations()
}

onMounted(loadConversations)
</script>

<template>
  <section class="message-layout">
    <aside class="panel-card conversation-list">
      <p class="eyebrow">私信会话</p>
      <h2>联系买家和卖家</h2>
      <div v-if="!conversations.length" class="empty-state">还没有会话</div>
      <button
        v-for="item in conversations"
        :key="item.peerUid"
        class="conversation-item"
        :class="{ active: activePeer === item.peerUid }"
        @click="activePeer = item.peerUid; loadMessages()"
      >
        <strong>{{ item.peerName || item.peerUid }}</strong>
        <span>{{ item.lastMessage || '开始打个招呼吧' }}</span>
      </button>
    </aside>

    <section class="panel-card chat-panel">
      <p class="eyebrow">消息内容</p>
      <h2>{{ activePeer || '请选择一个会话' }}</h2>
      <div v-if="!activePeer" class="empty-state">左侧选择会话后即可查看消息</div>
      <template v-else>
        <div class="message-list">
          <div v-for="item in messages" :key="item.id" class="message-bubble" :class="{ mine: item.senderUid !== activePeer }">
            <span>{{ item.content }}</span>
            <small>{{ item.createdAt }}</small>
          </div>
        </div>
        <div class="search-row">
          <input v-model="draft" class="text-input grow" placeholder="输入消息内容" @keyup.enter="sendMessage" />
          <button class="primary-btn" @click="sendMessage">发送</button>
        </div>
      </template>
    </section>
  </section>
</template>
