<template>
  <nav class="navbar">
    <div class="navbar-brand">
      <router-link to="/home" class="logo">
        <h2>DocumentosYa</h2>
      </router-link>
    </div>
    <div class="navbar-links">
      <router-link to="/home" class="navbar-item">Inicio</router-link>
      <router-link to="/cliente/pedidos-resumen" class="navbar-item">Resumen Pedidos</router-link>
      <router-link to="/repartidores/desempeno" class="navbar-item">Desempeño Repartidor</router-link>
      <router-link to="/empresas/mayorvolumen" class="navbar-item">Empresas Mayor Volumen</router-link>
      <router-link to="/productos/mas-pedidos" class="navbar-item">Productos</router-link>
      <button @click="logout" class="navbar-item logout-button">Cerrar sesión</button>
    </div>
  </nav>
</template>

<script>
export default {
  methods: {
    logout() {
      // Eliminar token de localStorage y redirigir a la página de login
      localStorage.removeItem("jwt");
      this.$router.push({ name: "login" });
    },
  },
};
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
}

.navbar .navbar-brand h2 {
  margin: 0;
  color : white;
}

.navbar-links {
  display: flex;
  gap: 20px;
}

.navbar-item {
  color: white;
  text-decoration: none;
  font-size: 16px;
}

.navbar-item:hover {
  text-decoration: underline;
}

.logout-button {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px 15px;
  cursor: pointer;
  border-radius: 4px;
}

.logout-button:hover {
  background-color: #c82333;
}
</style>
