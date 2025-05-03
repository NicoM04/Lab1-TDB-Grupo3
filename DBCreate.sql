-- 1. Crear la base de datos

CREATE DATABASE "lab1_grupo1"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Chile.1252'
    LC_CTYPE = 'Spanish_Chile.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False
    TEMPLATE = template0;

-- 2. Conectarse a la base de datos
\connect lab1_grupo1

-- 3. Crear las tablas

-- Tabla: Cliente
CREATE TABLE Cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre_cliente VARCHAR(100),
    contrasena_cliente VARCHAR(100),
    correo_cliente VARCHAR(100),
    direccion VARCHAR(100),
    telefono VARCHAR(15),
    fecha_registro DATE
);

-- Tabla: Empresas_Asociadas
CREATE TABLE Empresas_Asociadas (
    id_empresa SERIAL PRIMARY KEY,
    nombre_empresa VARCHAR(100),
    rut_empresa VARCHAR(20),
    correo_contacto VARCHAR(100),
    direccion VARCHAR(100)
);

-- Tabla: Repartidores
CREATE TABLE Repartidores (
    id_repartidor SERIAL PRIMARY KEY,
    nombre_repartidor VARCHAR(100),
    rut VARCHAR(20),
    telefono VARCHAR(15),
    fecha_contratacion DATE,
    activo BOOLEAN,
    cantidad_entregas INT
);

-- Tabla: Medios_de_pago
CREATE TABLE Medios_de_pago (
    id_pago SERIAL PRIMARY KEY,
    metodo_pago VARCHAR(100),
    fecha_pago DATE,
    monto_total INT
);

-- Tabla: Pedido
CREATE TABLE Pedido (
    id_pedido SERIAL PRIMARY KEY,
    id_cliente INT REFERENCES Cliente(id_cliente),
    id_empresa INT REFERENCES Empresas_Asociadas(id_empresa),
    id_repartidor INT REFERENCES Repartidores(id_repartidor),
    id_pago INT REFERENCES Medios_de_pago(id_pago),
    fecha_pedido DATE,
    fecha_entrega DATE NULL,  -- Aquí se permite que fecha_entrega sea NULL
    estado VARCHAR(100),
    urgente BOOLEAN
);

-- Tabla: ProductoServicio
CREATE TABLE ProductoServicio (
    id_producto SERIAL PRIMARY KEY,
    nombre_producto VARCHAR(255),
    descripcion TEXT,
    categoria VARCHAR(50),
    precio_unitario DECIMAL(10,2),
    stock INT
);

-- Tabla: Detalle_de_pedido
CREATE TABLE Detalle_de_pedido (
    id_detalle SERIAL PRIMARY KEY,
    id_producto INT REFERENCES ProductoServicio(id_producto),
    id_pedido INT REFERENCES Pedido(id_pedido),
    cantidad INT,
    subtotal DECIMAL(10,2)
);

-- Tabla: Notificacion
CREATE TABLE Notificacion (
    id_notificacion SERIAL PRIMARY KEY,
    id_pedido INT REFERENCES Pedido(id_pedido),
    fecha_creacion DATE,
    mensaje VARCHAR(100),
    tipo VARCHAR(50),
    leida BOOLEAN,
    descripcion VARCHAR(200)
);

-- Tabla: Calificaciones
CREATE TABLE Calificaciones (
    id_calificacion SERIAL PRIMARY KEY,
    id_repartidor INT REFERENCES Repartidores(id_repartidor),
    puntuacion INT,
    comentario VARCHAR(100),
    fecha_calificacion DATE
);

