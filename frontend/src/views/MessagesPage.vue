<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { chatApi } from '../api/modules'
import { resolveAssetUrl } from '../utils/assets'

const route = useRoute()
const conversations = ref([])
const activeConversationKey = ref('')
const messages = ref([])
const draft = ref('')

const groupedConversations = computed(() => ({
  presale: conversations.value.filter((item) => !item.orderPid),
  postsale: conversations.value.filter((item) => !!item.orderPid),
}))

function resolveActiveConversationKey() {
  const requestedKey = typeof route.query.conversationKey === 'string' ? route.query.conversationKey : ''
  if (requestedKey && conversations.value.some((item) => item.conversationKey === requestedKey)) {
    return requestedKey
  }
  if (activeConversationKey.value && conversations.value.some((item) => item.conversationKey === activeConversationKey.value)) {
    return activeConversationKey.value
  }
  return conversations.value[0]?.conversationKey || ''
}

function activeConversation() {
  return conversations.value.find((item) => item.conversationKey === activeConversationKey.value) || null
}

function conversationTypeLabel(item) {
  return item?.orderPid ? '售后订单对话' : '售前咨询'
}

function conversationTitle(item) {
  if (!item) return '请选择一个会话'
  if (item.orderPid) {
    return item.goodsName || `订单 ${item.orderPid}`
  }
  return item.goodsName || '咨询聊天'
}

function conversationSubtitle(item) {
  if (!item) return ''
  if (item.orderPid) {
    return `${item.peerName || item.peerUid} · 订单 ${item.orderPid}`
  }
  return item.peerName || item.peerUid || '未知用户'
}

function cardImage(item) {
  return resolveAssetUrl(item?.goodsImage) || 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=200&q=80'
}

function unreadLabel(item) {
  const count = Number(item?.unreadCount || 0)
  if (!count) return ''
  return count > 99 ? '99+' : String(count)
}

function isMineMessage(item) {
  const currentConversation = activeConversation()
  return !!currentConversation && item?.senderUid !== currentConversation.peerUid
}

function markActiveConversationRead() {
  const conversation = activeConversation()
  if (!conversation) return
  conversation.unreadCount = 0
}

async function loadConversations() {
  const response = await chatApi.conversations()
  conversations.value = response.data
  const nextKey = resolveActiveConversationKey()
  if (nextKey) {
    activeConversationKey.value = nextKey
    await loadMessages()
  } else {
    activeConversationKey.value = ''
    messages.value = []
  }
}

async function loadMessages() {
  if (!activeConversationKey.value) return
  const response = await chatApi.messages(activeConversationKey.value)
  messages.value = response.data
  markActiveConversationRead()
}

async function sendMessage() {
  const conversation = activeConversation()
  if (!draft.value.trim() || !conversation) return
  await chatApi.send({
    receiverUid: conversation.peerUid,
    goodsId: conversation.goodsId,
    orderPid: conversation.orderPid,
    content: draft.value.trim(),
  })
  draft.value = ''
  await loadMessages()
  await loadConversations()
}

async function removeConversation() {
  const conversation = activeConversation()
  if (!conversation) return
  await removeConversationByItem(conversation)
}

async function removeConversationByItem(conversation) {
  if (!conversation?.conversationKey) return
  if (!window.confirm(`确认删除会话“${conversationTitle(conversation)}”吗？删除后该会话消息将不可恢复。`)) {
    return
  }
  await chatApi.removeConversation(conversation.conversationKey)
  await loadConversations()
}

onMounted(loadConversations)
</script>

<template>
  <section class="message-page">
    <aside class="message-sidebar">
      <div class="sidebar-header">
        <h2>对话列表</h2>
      </div>

      <div v-if="!conversations.length" class="sidebar-empty">
        还没有聊天记录
      </div>

      <template v-else>
        <section class="conversation-group">
          <div class="group-title"><span class="group-tag">售前咨询</span></div>
          <div v-if="!groupedConversations.presale.length" class="group-empty">暂无售前咨询</div>
          <button
            v-for="item in groupedConversations.presale"
            :key="item.conversationKey"
            class="conversation-card"
            :class="{ active: activeConversationKey === item.conversationKey }"
            @click="activeConversationKey = item.conversationKey; loadMessages()"
          >
            <img :src="cardImage(item)" class="conversation-thumb" />
            <div class="conversation-meta">
              <div class="conversation-topline">
                <strong class="conversation-name">{{ conversationTitle(item) }}</strong>
                <div class="conversation-side">
                  <button
                    class="conversation-delete"
                    title="删除会话"
                    @click.stop="removeConversationByItem(item)"
                  >
                    删除
                  </button>
                  <span class="conversation-time">{{ item.lastTime?.slice(11, 16) || '' }}</span>
                </div>
              </div>
              <div class="conversation-owner">{{ conversationSubtitle(item) }}</div>
            </div>
          </button>
        </section>

        <section class="conversation-group">
          <div class="group-title"><span class="group-tag">售后订单对话</span></div>
          <div v-if="!groupedConversations.postsale.length" class="group-empty">暂无售后订单对话</div>
          <button
            v-for="item in groupedConversations.postsale"
            :key="item.conversationKey"
            class="conversation-card"
            :class="{ active: activeConversationKey === item.conversationKey }"
            @click="activeConversationKey = item.conversationKey; loadMessages()"
          >
            <img :src="cardImage(item)" class="conversation-thumb" />
            <div class="conversation-meta">
              <div class="conversation-topline">
                <strong class="conversation-name">{{ conversationTitle(item) }}</strong>
                <div class="conversation-side">
                  <button
                    class="conversation-delete"
                    title="删除会话"
                    @click.stop="removeConversationByItem(item)"
                  >
                    删除
                  </button>
                  <span class="conversation-time">{{ item.lastTime?.slice(11, 16) || '' }}</span>
                </div>
              </div>
              <div class="conversation-owner">{{ conversationSubtitle(item) }}</div>
            </div>
          </button>
        </section>
      </template>
    </aside>

    <section class="message-main">
      <div class="message-main-header">
        <div>
          <h2>{{ conversationTitle(activeConversation()) }}</h2>
          <div class="hint-text">{{ conversationSubtitle(activeConversation()) }}</div>
        </div>
        <button v-if="activeConversationKey" class="ghost-btn" @click="removeConversation">删除会话</button>
      </div>

      <div v-if="!activeConversationKey" class="message-empty">
        在左侧选择会话后即可查看消息
      </div>

      <template v-else>
        <div class="message-list">
          <div
            v-for="item in messages"
            :key="item.id"
            class="message-bubble-row"
            :class="{ mine: isMineMessage(item) }"
          >
            <div class="message-bubble">
              <span>{{ item.content }}</span>
              <small>{{ item.createdAt }}</small>
            </div>
          </div>
        </div>
        <div class="message-editor">
          <input
            v-model="draft"
            class="text-input grow"
            placeholder="输入消息内容"
            @keyup.enter="sendMessage"
          />
          <button class="primary-btn" @click="sendMessage">发送</button>
        </div>
      </template>
    </section>
  </section>
</template>

<style scoped>
.message-page {
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  gap: 20px;
  min-height: calc(100vh - 240px);
}

.message-sidebar,
.message-main {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e9edf3;
  box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06);
}

.message-sidebar {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header,
.message-main-header {
  padding: 24px 24px 16px;
  border-bottom: 1px solid #eef2f7;
}

.message-main-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.conversation-group {
  padding: 16px 14px 8px;
}

.group-title {
  margin: 0 6px 12px;
}

.group-tag {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 12px;
  border-radius: 999px;
  background: #f3f4f6;
  color: #6b7280;
  font-size: 12px;
  font-weight: 700;
}

.group-empty,
.sidebar-empty,
.message-empty {
  color: #9aa4b2;
  font-size: 14px;
}

.sidebar-empty {
  margin: 24px;
  padding: 40px 16px;
  text-align: center;
  border: 1px dashed #e5e7eb;
  border-radius: 8px;
}

.group-empty {
  padding: 8px 10px 14px;
}

.conversation-card {
  width: 100%;
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr);
  gap: 12px;
  align-items: center;
  padding: 12px 10px;
  border: 1px solid #edf1f5;
  background: #fff;
  border-radius: 14px;
  text-align: left;
  cursor: pointer;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.03);
}

.conversation-card:hover {
  background: #f8fafc;
  border-color: #dbe2ea;
}

.conversation-card.active {
  background: #fff3ec;
  border-color: #ffcfbf;
  box-shadow: 0 6px 16px rgba(255, 90, 31, 0.08);
}

.conversation-thumb {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  object-fit: cover;
  background: #f1f5f9;
}

.conversation-meta {
  min-width: 0;
}

.conversation-topline {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 8px;
  align-items: center;
}

.conversation-side {
  display: flex;
  align-items: center;
  gap: 8px;
}

.conversation-delete {
  border: none;
  background: transparent;
  color: #98a2b3;
  font-size: 12px;
  line-height: 1;
  padding: 2px 4px;
  cursor: pointer;
}

.conversation-delete:hover {
  color: #ff5a1f;
}

.conversation-name,
.conversation-owner {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.conversation-name {
  font-size: 16px;
  color: #111827;
}

.conversation-time {
  font-size: 12px;
  color: #9aa4b2;
}

.conversation-owner {
  margin-top: 4px;
  color: #667085;
  font-size: 13px;
}

.message-main {
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.message-list {
  flex: 1;
  min-height: 420px;
  max-height: calc(100vh - 420px);
  overflow-y: auto;
  padding: 24px;
  background: #f8fafc;
  border-radius: 0;
}

.message-bubble-row {
  display: flex;
  margin-bottom: 14px;
}

.message-bubble-row.mine {
  justify-content: flex-end;
}

.message-bubble {
  max-width: min(520px, 78%);
  background: #fff;
  border-radius: 16px;
  padding: 12px 14px;
  border: 1px solid #edf1f5;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.05);
}

.message-bubble-row.mine .message-bubble {
  background: #fff3ec;
  border-color: #ffd8ca;
}

.message-bubble span {
  display: block;
  color: #111827;
  line-height: 1.6;
  word-break: break-word;
}

.message-bubble small {
  display: block;
  margin-top: 8px;
  color: #9aa4b2;
}

.message-editor {
  display: flex;
  gap: 12px;
  padding: 16px 24px 24px;
  border-top: 1px solid #eef2f7;
  background: #fff;
  border-radius: 0 0 16px 16px;
  align-items: flex-end;
}

.message-editor .text-input {
  min-height: 48px;
  border-radius: 14px;
}

.grow {
  flex: 1;
}

@media (max-width: 1100px) {
  .message-page {
    grid-template-columns: 1fr;
  }

  .message-list {
    max-height: none;
  }
}
</style>
