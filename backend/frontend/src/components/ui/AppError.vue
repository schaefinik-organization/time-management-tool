<template>
  <div
    v-if="message"
    class="rounded-xl border px-4 py-3 text-sm transition-all animate-in fade-in slide-in-from-top-1"
    :style="{ borderColor: 'rgb(248 113 113 / 0.35)', color: 'var(--danger)', background: 'rgb(254 242 242)' }"
  >
    <div class="flex flex-col gap-1">
      <span class="font-semibold">{{ message }}</span>
      <ul v-if="errors && Object.keys(errors).length > 0" class="list-inside list-disc pl-2">
        <li v-for="(err, field) in errors" :key="field">
          {{ err }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
defineProps({
  message: String,
  errors: Object // Hier kommen die validationErrors vom Backend rein
})
</script>