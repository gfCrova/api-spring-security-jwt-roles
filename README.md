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

### Obtener usuario por id

`GET /api/users/{id}`

Requiere `ROLE_ADMIN`.

### Eliminar usuario

`DELETE /api/users/{id}`

Requiere `ROLE_ADMIN`.

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
