<!-- Ausschnitt aus dem Projekt-Modal -->
<div class="space-y-1">
  <label class="text-sm font-medium">Zugeordnete Benutzer</label>
  <div class="border rounded-md p-2 max-h-40 overflow-y-auto space-y-1">
    <label v-for="user in allUsers" :key="user.id" class="flex items-center gap-2 text-sm p-1 hover:bg-gray-50">
      <input type="checkbox" :value="user.id" v-model="form.assignedUserIds" />
      {{ user.username }}
    </label>
  </div>
</div>