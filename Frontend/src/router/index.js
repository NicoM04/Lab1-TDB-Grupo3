import { createRouter, createWebHistory } from 'vue-router';


//Definir las rutas de la aplicacion
const routes = [

];

//Crear el router
const router = createRouter({
    history: createWebHistory(),
    routes,
  });
  
//Proteger las rutas exclusivas para usuarios admin
router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAdmin)) {
    const userRole = localStorage.getItem("userRole");
    if (userRole === 'admin') {
      next();
    } else {
      next({ name: 'products' });
    }
  } else {
    next();
  }
});
  
export default router;