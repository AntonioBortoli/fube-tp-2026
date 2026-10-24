INSERT INTO productos (id, sku, nombre, cantidad_disponible)
VALUES
    ('a0000000-0000-0000-0000-000000000001', 'PROD-AURICULARES', 'Auriculares Inalámbricos Bluetooth', 50),
    ('a0000000-0000-0000-0000-000000000002', 'PROD-TECLADO', 'Teclado Mecánico RGB', 30),
    ('a0000000-0000-0000-0000-000000000003', 'PROD-MOUSE', 'Mouse Gamer 16000 DPI', 40),
    ('a0000000-0000-0000-0000-000000000004', 'PROD-MONITOR', 'Monitor Curvo 27 Pulgadas 144Hz', 15)
ON CONFLICT (sku) DO NOTHING;
