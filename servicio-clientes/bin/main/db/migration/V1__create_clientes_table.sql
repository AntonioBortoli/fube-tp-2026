CREATE TABLE clientes (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre      VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    fecha_alta  TIMESTAMP NOT NULL DEFAULT now()
);
