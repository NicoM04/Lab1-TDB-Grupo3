\connect TDB;

CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre_cliente VARCHAR(100) NOT NULL,
    contrasena_cliente VARCHAR(100) NOT NULL,
    correo_cliente VARCHAR(100) UNIQUE NOT NULL,
    direccion TEXT NOT NULL,
    telefono VARCHAR(20),
    fecha_registro DATE DEFAULT CURRENT_DATE
);

CREATE TABLE repartidor (
    id_repartidor SERIAL PRIMARY KEY,
    nombre_repartidor VARCHAR(100) NOT NULL,
    rut VARCHAR(20) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    fecha_contratacion DATE,
    activo BOOLEAN DEFAULT TRUE,
    cantidad_entregas INT DEFAULT 0,
    puntuacion INT DEFAULT 0
);

CREATE TABLE empresa_asociada (
    id_empresa SERIAL PRIMARY KEY,
    nombre_empresa VARCHAR(100) NOT NULL,
    rut_empresa VARCHAR(20) UNIQUE NOT NULL,
    correo_contacto VARCHAR(100),
    direccion TEXT
);
CREATE TABLE producto_servicio (
    id_producto SERIAL PRIMARY KEY,
    nombre_producto VARCHAR(100) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(50),
    precio_unitario NUMERIC(10,2) NOT NULL,
    stock INT DEFAULT 0
);

CREATE TABLE pedido (
    id_pedido SERIAL PRIMARY KEY,
    id_cliente INT REFERENCES cliente(id_cliente),
    id_empresa INT REFERENCES empresa_asociada(id_empresa),
    id_repartidor INT REFERENCES repartidor(id_repartidor),
    fecha_pedido DATE DEFAULT CURRENT_DATE,
    fecha_entrega DATE,
    estado VARCHAR(30) DEFAULT 'pendiente',
    urgente BOOLEAN DEFAULT FALSE
);

CREATE TABLE detalle_pedido (
    id_detalle SERIAL PRIMARY KEY,
    id_pedido INT REFERENCES pedido(id_pedido) ON DELETE CASCADE,
    id_producto INT REFERENCES producto_servicio(id_producto),
    cantidad INT NOT NULL CHECK (cantidad > 0),
    subtotal NUMERIC(10,2) NOT NULL
);

CREATE TABLE medio_pago (
    id_pago SERIAL PRIMARY KEY,
    id_pedido INT REFERENCES pedido(id_pedido) UNIQUE,
    metodo_pago VARCHAR(30) NOT NULL,
    fecha_pago DATE DEFAULT CURRENT_DATE,
    monto_total NUMERIC(10,2) NOT NULL
);

CREATE TABLE calificacion (
    id_calificacion SERIAL PRIMARY KEY,
    id_pedido INT REFERENCES pedido(id_pedido) ON DELETE SET NULL,
    id_cliente INT REFERENCES cliente(id_cliente),
    id_repartidor INT REFERENCES repartidor(id_repartidor),
    valor INT CHECK (valor BETWEEN 1 AND 5),
    comentario TEXT,
    fecha_calificacion DATE DEFAULT CURRENT_DATE
);

CREATE TABLE notificacion (
    id_notificacion SERIAL PRIMARY KEY,
    id_pedido INT REFERENCES pedido(id_pedido),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mensaje TEXT NOT NULL,
    tipo VARCHAR(50),
    leida BOOLEAN DEFAULT FALSE,
    descripcion TEXT
);