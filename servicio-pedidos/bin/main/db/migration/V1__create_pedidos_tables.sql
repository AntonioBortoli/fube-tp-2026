CREATE TABLE pedidos (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id            UUID NOT NULL,
    estado                VARCHAR(20) NOT NULL CHECK (estado IN ('CREADO', 'CONFIRMADO', 'CANCELADO', 'EXPIRADO')),
    monto_total           NUMERIC(12, 2) NOT NULL DEFAULT 0,
    fecha_creacion        TIMESTAMP NOT NULL DEFAULT now(),
    fecha_actualizacion   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE items_pedido (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pedido_id         UUID NOT NULL REFERENCES pedidos(id),
    producto_id       UUID NOT NULL,
    cantidad          INTEGER NOT NULL CHECK (cantidad > 0),
    precio_unitario   NUMERIC(12, 2) NOT NULL
);

CREATE INDEX idx_pedidos_cliente_id ON pedidos(cliente_id);
CREATE INDEX idx_items_pedido_pedido_id ON items_pedido(pedido_id);
