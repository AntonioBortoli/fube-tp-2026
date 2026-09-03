# TiendaOnline — Sistema de Gestión de Pedidos, Inventario y Clientes

Monorepo con arquitectura de microservicios para el TP final. Ver el enunciado completo en
[`tp_final_enunciado.md`](./tp_final_enunciado.md).

## Estructura del repositorio

```
tp/
├── servicio-clientes/        # API de clientes
├── servicio-inventario/      # API de stock/inventario
└── servicio-pedidos/         # API de pedidos
```

El repositorio es un multi-project build de Gradle: un único `settings.gradle`/`build.gradle`
en la raíz declara los tres módulos y la configuración común (Spring Boot 3.3, Java 17,
plugins, wrapper). Cada servicio tiene su propia base de datos PostgreSQL y su propio esquema
versionado con Flyway. No comparten código ni base de datos entre sí: toda comunicación entre
servicios será vía HTTP (o mocks en tests).

Para abrir el proyecto en IntelliJ: **File → Open** sobre la carpeta raíz `tp` (no sobre cada
subcarpeta) — IntelliJ importa los tres módulos de una sola vez como proyecto Gradle.

## Servicios y puertos

| Servicio             | Puerto app | Base de datos   |
|----------------------|-----------|------------------|
| servicio-clientes    | 8081      | clientes_db      |
| servicio-inventario  | 8082      | inventario_db    |
| servicio-pedidos     | 8083      | pedidos_db       |

Las tres bases viven en la misma instancia local de PostgreSQL, puerto `5432`, con usuario/contraseña
`postgres` / `postgres` (solo entorno local).

## Cómo levantar el entorno

1. Levantar una instancia de PostgreSQL (versión 16 o superior). Dos opciones, elegir una:

   **Opción A: instalador nativo**

   - Descargar el instalador desde https://www.postgresql.org/download/ para tu sistema operativo
     (en Windows, el instalador de EnterpriseDB incluye pgAdmin).
   - Durante la instalación, cuando pida la contraseña del superusuario `postgres`, poner `postgres`.
   - Dejar el puerto por defecto, `5432`.

   **Opción B: Docker**

   - Con Docker Desktop instalado y corriendo, ejecutar:

     ```bash
     docker run --name postgres-tp -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16
     ```

   - Esto levanta un contenedor con el superusuario `postgres` / contraseña `postgres` en el
     puerto `5432`, igual que la opción A. pgAdmin no viene incluido en la imagen: instalarlo
     aparte desde https://www.pgadmin.org/download/ para poder hacer el paso 2.

2. Crear las tres bases de datos con pgAdmin:

   - Abrir pgAdmin, conectarse al servidor local (`localhost`, puerto `5432`, usuario `postgres`,
     contraseña `postgres`).
   - En el árbol de la izquierda, click derecho sobre **Databases** → **Create** → **Database...**
   - Crear una base llamada `clientes_db`, owner `postgres`. Repetir para `inventario_db` y `pedidos_db`.
   - No hace falta crear tablas a mano: cada servicio corre sus propias migraciones de Flyway al
     arrancar.

3. Correr cada servicio. Dos formas, elegir una:

   **Opción A: línea de comandos**, en terminales separadas desde la raíz del repo:

   ```bash
   ./gradlew :servicio-clientes:bootRun
   ./gradlew :servicio-inventario:bootRun
   ./gradlew :servicio-pedidos:bootRun
   ```

   **Opción B: desde IntelliJ**

   - Abrir la clase principal del servicio (por ejemplo
     `servicio-clientes/src/main/java/.../ServicioClientesApplication.java`).
   - Click en el ícono ▶ al lado de la declaración de la clase (o click derecho → **Run**).
   - Repetir para `ServicioInventarioApplication` y `ServicioPedidosApplication`.
   - Cada Run abre su propia pestaña en la consola de IntelliJ, así que los tres quedan
     corriendo en paralelo sin pisarse.

   Al arrancar, cada servicio ejecuta automáticamente sus migraciones de Flyway contra su base.
   Los tres pueden correr en paralelo sin conflicto, ya que usan puertos de app distintos y cada
   uno apunta a su propia base dentro de la misma instancia de Postgres.

4. Para correr los tests de un servicio (requieren la base levantada, ya que el contexto de
   Spring corre las migraciones de Flyway al arrancar):

   ```bash
   ./gradlew :servicio-clientes:test
   ```

   O `./gradlew test` desde la raíz para correr los tests de los tres servicios.

## Estado actual

Por ahora cada servicio es un esqueleto: aplicación Spring Boot que levanta, se conecta a su
base y aplica las migraciones iniciales de Flyway con las tablas del modelo de datos del
enunciado. Todavía no hay endpoints REST, lógica de negocio, comunicación entre servicios,
job batch, feature flags ni observabilidad — se irán agregando de forma incremental.

### Próximos pasos (según el enunciado)

- [ ] Endpoints REST de cada servicio (OpenAPI/Swagger, validaciones, manejo de errores)
- [ ] Comunicación servicio-pedidos → servicio-clientes / servicio-inventario al crear un pedido
- [ ] Endpoint `GET /clientes/{id}/ordenes` (servicio-clientes → servicio-pedidos)
- [ ] Job batch nocturno de expiración de pedidos (Spring Batch)
- [ ] Feature flag de estrategia de asignación de stock
- [ ] Tests (TDD, Mockito, MockServer para integración entre servicios)
- [ ] Observabilidad: Actuator/Micrometer, logs estructurados con correlation id, tracing distribuido
