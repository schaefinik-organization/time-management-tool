<template>
  <button
    :type="type"
    :disabled="disabled"
    :class="buttonClass"
  >
    <slot />
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: {
    type: String,
    default: 'button'
  },
  variant: {
    type: String,
    default: 'primary'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const buttonClass = computed(() => {
  if (props.variant === 'secondary') return 'btn-secondary'
  if (props.variant === 'danger') return 'btn-danger'
  return 'btn-primary'
})
</script>
