<template>
  <div class="multi-select-container" ref="dropdownRef" :class="{ 'is-disabled': disabled }">

    <div class="select-input-box" @click="openDropdown">

      <span
        v-for="user in selectedUsers"
        :key="user.id"
        class="tag"
      >
        {{ user.username }}
        <button
          type="button"
          class="remove-tag-btn"
          @click.stop="removeUser(user.id)"
          :disabled="disabled"
        >&times;</button>
      </span>

      <input
        type="text"
        v-model="searchQuery"
        class="search-input"
        :placeholder="selectedUsers.length === 0 ? placeholder : ''"
        :disabled="disabled"
        @focus="openDropdown"
      />
    </div>

    <!-- Das Aufklapp-Menü (Dropdown) -->
    <Transition name="fade">
      <ul v-if="isOpen" class="dropdown-list">
        <li
          v-for="user in filteredOptions"
          :key="user.id"
          @click="addUser(user.id)"
          class="dropdown-item"
        >
          {{ user.username }}
        </li>
        <li v-if="filteredOptions.length === 0" class="dropdown-item empty-msg">
          Keine weiteren Mitarbeiter gefunden.
        </li>
      </ul>
    </Transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  availableOptions: {
    type: Array,
    required: true,
    default: () => [] 
  },
  modelValue: {
    type: Array,
    required: true,
    default: () => [] 
  },
  placeholder: {
    type: String,
    default: 'Mitarbeiter suchen...'
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

// --- Interner State ---
const isOpen = ref(false)
const searchQuery = ref('')
const dropdownRef = ref(null)

// --- Computed Properties ---

// 1. Wandelt die IDs aus dem modelValue wieder in echte User-Objekte für die Tags um
const selectedUsers = computed(() => {
  return props.modelValue.map(id => {
    return props.availableOptions.find(u => u.id === id) || { id, username: 'Unbekannt' }
  })
})

const filteredOptions = computed(() => {
  return props.availableOptions.filter(user => {
    const isNotSelected = !props.modelValue.includes(user.id)
    const matchesSearch = user.username.toLowerCase().includes(searchQuery.value.toLowerCase())
    return isNotSelected && matchesSearch
  })
})


const openDropdown = () => {
  if (!props.disabled) isOpen.value = true
}

const closeDropdown = () => {
  isOpen.value = false
  searchQuery.value = ''
}

const addUser = (userId) => {
  const newValue = [...props.modelValue, userId]
  emit('update:modelValue', newValue)
  searchQuery.value = '' 
}

const removeUser = (userId) => {
  const newValue = props.modelValue.filter(id => id !== userId)
  emit('update:modelValue', newValue)
}

const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    closeDropdown()
  }
}

onMounted(() => {
  document.addEventListener('mousedown', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', handleClickOutside)
})
</script>

<style scoped>
.multi-select-container {
  position: relative;
  width: 100%;
  font-family: sans-serif;
}

.is-disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.select-input-box {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  padding: 6px;
  border: 1px solid #ccc;
  border-radius: 6px;
  background-color: white;
  min-height: 42px;
  align-items: center;
  cursor: text;
}

.tag {
  background-color: #3498db;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.9em;
  display: flex;
  align-items: center;
  gap: 6px;
}

.remove-tag-btn {
  background: none;
  border: none;
  color: white;
  font-size: 1.2em;
  line-height: 1;
  cursor: pointer;
  padding: 0;
}
.remove-tag-btn:hover {
  color: #ffcccc;
}

.search-input {
  border: none;
  outline: none;
  flex-grow: 1;
  min-width: 120px;
  padding: 4px;
  font-size: 0.95em;
  background: transparent;
}

.dropdown-list {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  background: white;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  max-height: 200px;
  overflow-y: auto;
  z-index: 100;
  list-style: none;
  padding: 0;
}

.dropdown-item {
  padding: 10px 12px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
}
.dropdown-item:last-child {
  border-bottom: none;
}
.dropdown-item:hover {
  background-color: #f1f8ff;
}
.empty-msg {
  color: #777;
  font-style: italic;
  cursor: default;
}
.empty-msg:hover {
  background-color: white;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>