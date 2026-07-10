<template>
  <textarea
    :value="modelValue"
    :rows="rows"
    :placeholder="placeholder"
    class="input-base min-h-28 resize-y"
    @input="$emit('update:modelValue', $event.target.value)"
  />
</template>

<script setup>
defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  rows: {
    type: Number,
    default: 4
  },
  placeholder: {
    type: String,
    default: ''
  }
})

defineEmits(['update:modelValue'])
</script>
