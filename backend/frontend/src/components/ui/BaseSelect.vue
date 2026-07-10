<template>
  <select
    :value="modelValue"
    class="input-base"
    :required="required"
    @change="$emit('update:modelValue', $event.target.value)"
  >
    <slot />
  </select>
</template>

<script setup>
defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  required: {
    type: Boolean,
    default: false
  }
})

defineEmits(['update:modelValue'])
</script>
