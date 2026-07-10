import { defineStore } from 'pinia'

function todayIso() {
  return new Date().toISOString().slice(0, 10)
}

export const useAppStore = defineStore('app', {
  state: () => ({
    selectedDate: todayIso()
  }),
  actions: {
    setSelectedDate(date) {
      this.selectedDate = date
    }
  }
})
