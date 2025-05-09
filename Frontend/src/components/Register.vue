<template>
  <div class="register-container">
    <h2>Crear cuenta</h2>
    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <label for="name">Nombre</label>
        <input
          type="text"
          id="name"
          v-model="name"
          required
          placeholder="Ingresa tu nombre"
        />
      </div>

      <div class="form-group">
        <label for="email">Correo electrónico</label>
        <input
          type="email"
          id="email"
          v-model="email"
          required
          placeholder="Ingresa tu correo"
        />
      </div>

      <div class="form-group">
        <label for="password">Contraseña</label>
        <input
          type="password"
          id="password"
          v-model="password"
          required
          placeholder="Ingresa tu contraseña"
        />
      </div>

      <button type="submit">Registrarse</button>
    </form>
    <p>
      ¿Ya tienes cuenta? <router-link to="/login">Inicia sesión aquí</router-link>
    </p>
  </div>
</template>

<script>
import ClienteService from '@/services/Cliente.service'; // Importar el service

export default {
  data() {
    return {
      name: "",
      email: "",
      password: "",
    };
  },
  methods: {
    async handleRegister() {
      try {
        // Llamar al método del service para registrar el cliente
        const token = localStorage.getItem("jwt"); // Asegúrate de tener un token de autenticación (si es necesario)
        
        // Usar el service de registro
        const response = await ClienteService.registerCliente(
          {
            nombre_cliente: this.name,
            correo_cliente: this.email,
            contrasena_cliente: this.password,
          },
          token
        );

        // Si el registro es exitoso, redirigir al login
        this.$router.push({ name: "login" });
      } catch (error) {
        console.error("Error en el registro", error);
        alert("Hubo un error al registrar la cuenta.");
      }
    },
  },
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f7f7f7;
  border-radius: 8px;
}

.form-group {
  margin-bottom: 1em;
}

label {
  display: block;
  margin-bottom: 0.5em;
}

input {
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 10px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #218838;
}
</style>
