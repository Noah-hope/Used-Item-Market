<script setup>
defineProps({
  modelValue: {
    type: String,
    required: true,
  },
  items: {
    type: Array,
    required: true,
  },
})

const emit = defineEmits(['update:modelValue'])
</script>

<template>
  <div class="status-tabs">
    <button
      v-for="item in items"
      :key="item.value"
      class="status-tab"
      :class="{ active: modelValue === item.value }"
      @click="emit('update:modelValue', item.value)"
    >
      {{ item.label }}
    </button>
  </div>
</template>
