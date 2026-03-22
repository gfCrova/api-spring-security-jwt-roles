# API Spring Security JWT Roles

API REST construida con Spring Boot para autenticacion con JWT, autorizacion por roles y gestion basica de usuarios.

## Descripcion

El proyecto expone un flujo simple de registro, login y acceso protegido a endpoints administrativos. Usa Spring Security, JWT, JPA y PostgreSQL como base de persistencia principal.

Tambien incluye mejoras de robustez y seguridad:

- Validacion de entrada en login y registro.
- Passwords cifrados con BCrypt.
- Roles asignados de forma controlada.
- Manejador global de errores con respuestas JSON consistentes.
- Configuracion CORS centralizada.
- Tests aislados con H2 para no depender de una base externa.

## Stack

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (`jjwt`)
- Lombok
- JUnit 5 / Mockito / H2

## Estructura

```text
src/main/java/com/example/demogc
|- config/security        Configuracion de Spring Security y JWT
|- controller             Endpoints REST
|- dto                    Objetos de entrada y salida
|- exception              Errores de negocio y handler global
|- mapper                 Transformacion entre entidades y DTOs
|- model                  Entidades JPA
|- repository             Acceso a datos
|- service                Contratos y logica de negocio
```

## Funcionalidades

- Registro de usuarios.
- Inicio de sesion con JWT.
- Consulta de usuarios solo para `ADMIN`.
- Consulta de usuario por id solo para `ADMIN`.
- Eliminacion de usuarios solo para `ADMIN`.

## Seguridad

### Registro

El endpoint `/api/register` crea usuarios con rol `USER` por defecto.

No se concede el rol `ADMIN` automaticamente por dominio de email ni por datos controlados por el cliente.

### Bootstrap del primer ADMIN

Puedes crear el primer administrador de forma segura al arrancar la aplicacion usando variables de entorno. Solo se crea si:

- `ADMIN_BOOTSTRAP_ENABLED=true`
- no existe todavia ningun usuario con rol `ADMIN`

Variables:

```bash
ADMIN_BOOTSTRAP_ENABLED=true
ADMIN_USERNAME=superadmin
ADMIN_EMAIL=superadmin@example.com
ADMIN_PASSWORD=ChangeMeNow123!
ADMIN_NAME=Administrador Inicial
ADMIN_PHONE=123456789
ADMIN_BUSINESS_TITLE=Platform Administrator
```

Despues del primer arranque, la recomendacion es desactivar:

```bash
ADMIN_BOOTSTRAP_ENABLED=false
```

### Login

El endpoint `/api/authentication` autentica credenciales y devuelve un token JWT.

### JWT

El token incluye el nombre del usuario autenticado y sus autoridades. Debe enviarse en el header:

```http
Authorization: Bearer <token>
```

### Roles

- `ROLE_USER`: usuario registrado.
- `ROLE_ADMIN`: acceso a endpoints administrativos.
- `ROLE_MANAGER`: rol semilla disponible en base de datos.

## Configuracion

### Variables de entorno

```bash
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SIGNING_KEY=change-this-default-jwt-secret-key-to-a-long-random-value
APP_ALLOWED_ORIGINS=http://localhost:3000
```

### application.properties

Configuracion principal incluida:

- PostgreSQL en `jdbc:postgresql://localhost:5432/demo_gc_db`
- `ddl-auto=update`
- inicializacion SQL activa para sembrar roles
- expiracion JWT configurable

## Base de datos

Al iniciar la aplicacion se insertan roles base desde `src/main/resources/data.sql`:

- `ADMIN`
- `MANAGER`
- `USER`

## Ejecucion

### 1. Clonar el repositorio

```bash
git clone <repo-url>
cd api-spring-security-jwt-roles
```

### 2. Configurar PostgreSQL

Crea una base llamada `demo_gc_db` y define las variables de entorno necesarias.

### 3. Ejecutar la aplicacion

En Windows:

```bash
./mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

## Testing

Los tests usan un perfil `test` con H2 en memoria, por lo que no requieren PostgreSQL.

```bash
./mvnw.cmd test
```

## OpenAPI y Swagger UI

La documentacion interactiva queda disponible en:

- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/v3/api-docs`

Swagger UI soporta autenticacion Bearer JWT. Primero autentica en `/api/authentication`, copia el token y luego usa el boton `Authorize`.

## Endpoints

### Registro

`POST /api/register`

Body:

```json
{
  "username": "gianc",
  "email": "gianc@example.com",
  "password": "Secret123",
  "name": "Giancarlo",
  "phone": 123456789,
  "businessTitle": "Backend Developer"
}
```

Respuesta esperada: `201 Created`

### Login

`POST /api/authentication`

Body:

```json
{
  "username": "gianc",
  "password": "Secret123"
}
```

Respuesta:

```json
{
  "token": "<jwt>"
}
```

### Obtener usuarios

`GET /api/users`

Requiere `ROLE_ADMIN`.

### Obtener mi perfil

`GET /api/users/me`

Disponible para cualquier usuario autenticado.

### Actualizar mi perfil

`PATCH /api/users/me`

Disponible para cualquier usuario autenticado.

Body:

```json
{
  "email": "nuevo@email.com",
  "name": "Nombre Actualizado",
  "phone": 123456789,
  "businessTitle": "Backend Engineer"
}
```

### Cambiar mi contraseña

`PATCH /api/users/me/password`

Disponible para cualquier usuario autenticado.

Body:

```json
{
  "currentPassword": "Secret123",
  "newPassword": "Secret456"
}
```

### Eliminar mi cuenta

`DELETE /api/users/me`

Disponible para cualquier usuario autenticado. Elimina la cuenta asociada al token actual.

### Obtener usuario por id

`GET /api/users/{id}`

Requiere `ROLE_ADMIN`.

### Eliminar usuario

`DELETE /api/users/{id}`

Requiere `ROLE_ADMIN`.

### Actualizar roles de un usuario

`PATCH /api/users/{id}/roles`

Requiere `ROLE_ADMIN`.

Body:

```json
{
  "roles": ["ADMIN", "USER"]
}
```

Este endpoint es la via correcta para promover o degradar privilegios. Un `USER` no puede autoasignarse roles.

## Respuestas de error

Los errores ahora devuelven un formato JSON consistente:

```json
{
  "timestamp": "2026-03-21T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error",
  "path": "/api/register",
  "validationErrors": {
    "email": "must be a well-formed email address"
  }
}
```

## Mejoras futuras recomendadas

- Añadir refresh tokens y revocacion de sesiones.
- Implementar paginacion en `/api/users`.
- Añadir endpoints para gestion explicita de roles por administradores.
- Incorporar auditoria y rate limiting.
- Reemplazar el secreto JWT por una clave rotada desde un secret manager.

## Licencia

Pendiente de definir.
