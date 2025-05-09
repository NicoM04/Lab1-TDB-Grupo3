<script setup>
import Navbar from './components/Navbar.vue';
</script>

<template>
  <main>
    <Navbar /> 
    <div class="wrapper">
      <router-view />
    </div>
  </main>
</template>

<style scoped>
@media (min-width: 1024px) {
  .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
    width: 100vw;
  }
  
}
</style>

