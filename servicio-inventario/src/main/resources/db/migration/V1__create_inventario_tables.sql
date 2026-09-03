CREATE TABLE productos (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sku                  VARCHAR(64) NOT NULL UNIQUE,
    nombre               VARCHAR(255) NOT NULL,
    cantidad_disponible  INTEGER NOT NULL DEFAULT 0 CHECK (cantidad_disponible >= 0)
);

CREATE TABLE reservas (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    producto_id      UUID NOT NULL REFERENCES productos(id),
    pedido_id        UUID NOT NULL,
    cantidad         INTEGER NOT NULL CHECK (cantidad > 0),
    estado           VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVA', 'LIBERADA')),
    fecha_creacion   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_reservas_pedido_id ON reservas(pedido_id);
CREATE INDEX idx_reservas_producto_id ON reservas(producto_id);
