<template>
  <div class="desempeno-repartidor-container">
    <h2>Desempeño de los Repartidores</h2>

    <!-- Filtro de reparto -->
    <div class="filters">
      <label for="repartidor">Seleccionar Repartidor:</label>
      <select v-model="repartidorSeleccionado" @change="filtrarDesempeno">
        <option value="">Todos</option>
        <option v-for="repartidor in repartidores" :key="repartidor.id" :value="repartidor.id">
          {{ repartidor.nombre_repartidor }}
        </option>
      </select>
    </div>

    <!-- Tabla de desempeño -->
    <section class="performance">
      <h3>Desempeño de Repartidores</h3>
      <table>
        <thead>
          <tr>
            <th>Repartidor</th>
            <th>Entregas Completadas</th>
            <th>Puntuación</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="repartidor in repartidoresDesempeno" :key="repartidor.id">
            <td>{{ repartidor.nombre_repartidor }}</td>
            <td>{{ repartidor.cantidad_entregas }}</td>
            <td>{{ repartidor.puntuacion }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<script>
import RepartidorService from "@/services/Repartidor.service"; // Importamos el servicio de Repartidor
import CalificacionService from "@/services/Calificacion.service"; // Importamos el servicio de Calificación

export default {
  data() {
    return {
      repartidores: [], // Lista de repartidores
      repartidoresDesempeno: [], // Desempeño filtrado de los repartidores
      repartidorSeleccionado: "", // Repartidor seleccionado para filtrar
    };
  },
  created() {
    this.obtenerRepartidores();
  },
  methods: {
    // Obtener todos los repartidores
    async obtenerRepartidores() {
      const token = localStorage.getItem("jwt"); // Obtener el token del localStorage
      try {
        const response = await RepartidorService.getAllRepartidores(token); // Llamar al servicio para obtener los repartidores
        this.repartidores = response.data; // Asignamos los repartidores obtenidos
        this.obtenerDesempenoRepartidores(); // Obtener el desempeño de los repartidores
      } catch (error) {
        console.error("Error al obtener repartidores", error); // En caso de error, lo mostramos en consola
      }
    },

    // Obtener desempeño de los repartidores
    async obtenerDesempenoRepartidores() {
      const token = localStorage.getItem("jwt"); // Obtener el token del localStorage
      try {
        const response = await RepartidorService.getAllRepartidores(token); // Llamamos al servicio para obtener los repartidores
        let repartidoresDesempeno = await Promise.all(response.data.map(async (repartidor) => {
          // Obtener calificación de cada repartidor
          const calificacionResponse = await CalificacionService.getCalificacionById(repartidor.id_repartidor, token);
          const calificacion = calificacionResponse.data;

          // Asociar los datos de desempeño
          return {
            ...repartidor,
            puntuacion: calificacion.puntuacion, // Tomamos la puntuación de la calificación
          };
        }));

        this.repartidoresDesempeno = repartidoresDesempeno; // Asignamos los datos de desempeño
      } catch (error) {
        console.error("Error al obtener desempeño de repartidores", error); // En caso de error, lo mostramos en consola
      }
    },

    // Filtrar desempeño según repartidor seleccionado
    filtrarDesempeno() {
      if (this.repartidorSeleccionado) {
        this.repartidoresDesempeno = this.repartidoresDesempeno.filter(
          (repartidor) => repartidor.id_repartidor === parseInt(this.repartidorSeleccionado)
        );
      } else {
        this.obtenerDesempenoRepartidores(); // Si no hay filtro, muestra todos los repartidores
      }
    },
  },
};
</script>

<style scoped>
.desempeno-repartidor-container {
  padding: 20px;
}

.filters {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filters label {
  margin-right: 10px;
}

.filters select {
  padding: 5px;
}

section {
  margin-bottom: 30px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

th,
td {
  padding: 10px;
  text-align: left;
  border: 1px solid #ddd;
}

th {
  background-color: #f2f2f2;
}

button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
}

button:hover {
  background-color: #0056b3;
}
</style>
