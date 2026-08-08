<template>
  <section class="space-y-8">
    <div class="grid gap-6 lg:grid-cols-[1.4fr_1fr]">
      <BaseCard>
        <div class="space-y-4 p-1">
          <span
            class="inline-flex rounded-full px-3 py-1 text-xs font-medium"
            :style="{ background: 'var(--bg-muted)', color: 'var(--text-muted)' }"
          >
          </span>

          <div class="space-y-3">
            <h1 class="text-3xl font-semibold tracking-tight">
              Projektzeiten einfach erfassen und sauber pro Nutzer strukturieren
            </h1>
            <p class="max-w-2xl text-base text-muted">
              Diese Anwendung bildet die Grundlage für projektbezogene Zeiterfassung mit späterem Login,
              nutzergebundenen Einträgen und geschützten Bereichen.
            </p>
          </div>

          <div class="flex flex-wrap gap-3">
            <RouterLink :to="{ name: 'reports' }" class="btn-primary no-underline">
              Projekte öffnen
            </RouterLink>
            <RouterLink :to="{ name: 'tracker' }" class="btn-secondary no-underline">
              Zeiteinträge öffnen
            </RouterLink>
          </div>
        </div>
      </BaseCard>

      <BaseCard>
        <div class="space-y-4">
          <h2 class="text-lg font-semibold">Nächste Ausbaustufen</h2>

          <ul class="space-y-3 text-sm text-muted">
            <li>Login und geschützte Routen</li>
            <li>Zeiteinträge pro Nutzer und Projekt</li>
            <li>Wochen- und Monatsauswertungen</li>
            <li>Formular-Validierung und UX-Polish</li>
          </ul>
        </div>
      </BaseCard>
    </div>

    <div class="grid gap-6 lg:grid-cols-3">
      <BaseCard>
        <div class="space-y-2">
          <h2 class="text-lg font-semibold">Projektverwaltung</h2>
          <p class="text-sm text-muted">
            Projekte anlegen, bearbeiten und als Grundlage für Zeiterfassung nutzen.
          </p>
        </div>
      </BaseCard>

      <BaseCard>
        <div class="space-y-2">
          <h2 class="text-lg font-semibold">Zeiteinträge</h2>
          <p class="text-sm text-muted">
            Start- und Endzeiten pro Tag pflegen und später Nutzerkonten zuordnen.
          </p>
        </div>
      </BaseCard>

      <BaseCard>
        <div class="space-y-2">
          <h2 class="text-lg font-semibold">Authentifizierung</h2>
          <p class="text-sm text-muted">
            Geschützte Bereiche sind strukturell vorbereitet und werden im nächsten Schritt aktiviert.
          </p>
        </div>
      </BaseCard>
    </div>
  </section>
</template>

<script setup>
import { RouterLink } from 'vue-router'
import BaseCard from '@/components/ui/BaseCard.vue'
</script>
