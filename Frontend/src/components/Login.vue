<template>
  <div class="login-container">
    <h2>Iniciar sesión</h2>
    <form @submit.prevent="handleLogin">
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

      <button type="submit">Iniciar sesión</button>
    </form>
    <p>
      ¿No tienes cuenta? <router-link to="/register">Regístrate aquí</router-link>
    </p>
  </div>
</template>

<script>
import ClienteService from "@/services/Cliente.service"; // Importar el servicio de cliente
import { useRouter } from "vue-router"; // Importar Vue Router para redirigir al dashboard

export default {
  data() {
    return {
      email: "",
      password: "",
    };
  },
  methods: {
    async handleLogin() {
      try {
        // Llamada al método loginCliente del servicio ClienteService
        const response = await ClienteService.loginCliente(this.email, this.password);


        // Imprimir el token antes de guardarlo
        console.log("Token antes de guardarlo:", response.data);
        // Si el login es exitoso, guarda el token en el localStorage
        localStorage.setItem("jwt", response.data);
        
        // Ahora, obtenemos el id_cliente utilizando el correo electrónico y el token
        const clienteResponse = await ClienteService.buscarPorCorreo(this.email, response.data); // Llamamos al servicio con el correo y el token

        // Guardamos el id_cliente en localStorage
        localStorage.setItem("id_cliente", clienteResponse.data.id_cliente);

        // Imprimir el id_cliente guardado
        console.log("id_cliente guardado:", clienteResponse.data.id_cliente);

    
        // Imprimir el token después de guardarlo en localStorage
        console.log("Token guardado en localStorage:", localStorage.getItem("jwt"));

        // Redirige al dashboard o página principal
        this.$router.push({ name: "home" });
      } catch (error) {
        console.error("Error en el login", error);
        alert("Credenciales incorrectas.");
      }
    },
  },
};
</script>

<style scoped>
.login-container {
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
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
