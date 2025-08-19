<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="app-container">
    <RouterView />
  </div>
</template>

<style scoped>
.app-container {
  max-width: 100%;
  margin: 0;
  padding: 0;
}

/* 移除原有的header样式，因为TicketView中已包含 */
</style>
