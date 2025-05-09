<script setup>
import NavBar from './components/NavBar.vue';
</script>

<template>
  <main>
    <NavBar />
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
