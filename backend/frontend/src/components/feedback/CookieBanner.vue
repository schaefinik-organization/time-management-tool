<template>
  <div
    v-if="visible"
    class="fixed inset-x-0 bottom-4 z-50 mx-auto w-[calc(100%-2rem)] max-w-3xl rounded-2xl border p-4 shadow-lg"
    :style="{ background: 'var(--surface)', borderColor: 'var(--border)' }"
  >
    <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <p class="font-medium">Cookies & lokale Einstellungen</p>
        <p class="text-sm text-muted">
          Aktuell wird dieses Banner nur als UI-Komponente vorbereitet. Tracking wird noch nicht verwendet.
        </p>
      </div>

      <div class="flex gap-2">
        <button class="btn-secondary" type="button" @click="hideBanner">
          Später
        </button>
        <button class="btn-primary" type="button" @click="hideBanner">
          Verstanden
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const visible = ref(true)

function hideBanner() {
  visible.value = false
}
</script>
