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


-- Procedimiento almacenado: Registrar un pedido completo
CREATE OR REPLACE PROCEDURE registrar_pedido_completo(
    p_id_cliente INT,
    p_id_empresa INT,
    p_id_repartidor INT,
    p_fecha_pedido DATE,
    p_fecha_entrega DATE,
    p_estado VARCHAR,
    p_urgente BOOLEAN,
    p_metodo_pago VARCHAR,
    p_productos INT[],         -- Array de IDs de producto
    p_cantidades INT[]         -- Array de cantidades correspondientes
)
LANGUAGE plpgsql AS $$
DECLARE
    i INT;
    v_id_pago INT;
    v_id_pedido INT;
    v_id_producto INT;
    v_cantidad INT;
    v_precio DECIMAL(10,2);
    v_subtotal DECIMAL(10,2);
    v_total DECIMAL(10,2) := 0;
BEGIN
    -- Calcular total del pedido recorriendo productos
    FOR i IN 1..array_length(p_productos, 1) LOOP
        v_id_producto := p_productos[i];
        v_cantidad := p_cantidades[i];

        SELECT precio_unitario INTO v_precio
        FROM ProductoServicio
        WHERE id_producto = v_id_producto;

        v_subtotal := v_precio * v_cantidad;
        v_total := v_total + v_subtotal;
    END LOOP;

    -- Insertar en medios de pago
    INSERT INTO Medios_de_pago (metodo_pago, fecha_pago, monto_total)
    VALUES (p_metodo_pago, CURRENT_DATE, v_total)
    RETURNING id_pago INTO v_id_pago;

    -- Insertar el pedido
    INSERT INTO Pedido (
        id_cliente, id_empresa, id_repartidor, id_pago,
        fecha_pedido, fecha_entrega, estado, urgente
    )
    VALUES (
        p_id_cliente, p_id_empresa, p_id_repartidor, v_id_pago,
        p_fecha_pedido, p_fecha_entrega, p_estado, p_urgente
    )
    RETURNING id_pedido INTO v_id_pedido;

    -- Insertar detalles de pedido
    FOR i IN 1..array_length(p_productos, 1) LOOP
        v_id_producto := p_productos[i];
        v_cantidad := p_cantidades[i];

        SELECT precio_unitario INTO v_precio
        FROM ProductoServicio
        WHERE id_producto = v_id_producto;

        v_subtotal := v_precio * v_cantidad;

        INSERT INTO Detalle_de_pedido (
            id_producto, id_pedido, cantidad, subtotal
        )
        VALUES (
            v_id_producto, v_id_pedido, v_cantidad, v_subtotal
        );
    END LOOP;
END;
$$;


-- Procedimiento almacenado: Descontar stock al confirmar pedido (si aplica)
CREATE OR REPLACE PROCEDURE confirmar_pedido_y_descontar_stock(p_id_pedido INT)
AS $$ 
DECLARE
    r RECORD;
    v_stock_actual INT;
BEGIN
    -- Verificamos que el pedido esté pendiente (opcional)
    IF EXISTS (
        SELECT 1 FROM Pedido WHERE id_pedido = p_id_pedido AND estado != 'Pendiente'
    ) THEN
        RAISE EXCEPTION 'El pedido ya fue confirmado o no está en estado Pendiente.';
    END IF;

    -- Recorremos los productos del pedido
    FOR r IN
        SELECT dp.id_producto, dp.cantidad, ps.stock
        FROM Detalle_de_pedido dp
        JOIN ProductoServicio ps ON dp.id_producto = ps.id_producto
        WHERE dp.id_pedido = p_id_pedido
    LOOP
        -- Verificamos si hay suficiente stock
        IF r.stock < r.cantidad THEN
            RAISE EXCEPTION 'No hay suficiente stock para el producto ID % (stock disponible: %, requerido: %)',
                r.id_producto, r.stock, r.cantidad;
        END IF;
    END LOOP;

    -- Si todo está bien, descontamos stock
    FOR r IN
        SELECT dp.id_producto, dp.cantidad
        FROM Detalle_de_pedido dp
        WHERE dp.id_pedido = p_id_pedido
    LOOP
        UPDATE ProductoServicio
        SET stock = stock - r.cantidad
        WHERE id_producto = r.id_producto;
    END LOOP;

    -- Cambiamos el estado del pedido a Confirmado
    UPDATE Pedido
    SET estado = 'Confirmado'
    WHERE id_pedido = p_id_pedido;
END;
$$ LANGUAGE plpgsql;
