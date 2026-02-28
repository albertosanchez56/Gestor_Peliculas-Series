# FilmScore API — Backend de Microservicios

> API REST escalable para gestión de películas, usuarios y reseñas. Arquitectura de microservicios con Spring Boot, Spring Cloud, seguridad JWT y comunicación entre servicios vía Feign.

[![Java 17](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk)](https://openjdk.org/)
[![Spring Boot 3.3](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0-6DB33F)](https://spring.io/projects/spring-cloud)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1?logo=mysql)](https://www.mysql.com/)

---

## Arquitectura

```
                    ┌─────────────────┐
                    │   Frontend      │
                    │   (Angular)     │
                    └────────┬────────┘
                             │ HTTP
                             ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                        API GATEWAY (Puerto 9090)                            │
│                    Enrutamiento · CORS · Load Balancing                     │
└──────────────────────────┬─────────────────────────────────────────────────┘
                           │
        ┌──────────────────┼──────────────────┬─────────────────────┐
        ▼                  ▼                  ▼                     ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐   ┌─────────────────┐
│ USER-SERVICE  │  │ MOVIE-SERVICE │  │ REVIEW-SERVICE│   │ FAVORITELIST    │
│ Auth · Users  │  │ Películas     │  │ Reseñas       │   │ (en desarrollo) │
│ JWT · Roles   │  │ TMDB · Cast   │  │ Stats         │   │                 │
└───────┬───────┘  └───────┬───────┘  └───────┬───────┘   └─────────────────┘
        │                  │                  │
        │    Feign         │                  │  Feign
        └──────────────────┴──────────────────┘
                           │
              ┌────────────┴────────────┐
              ▼                         ▼
     ┌─────────────────┐      ┌─────────────────┐
     │  EUREKA         │      │  CONFIG SERVER  │
     │  Service Disc.  │      │  Centralizado   │
     │  (8761)         │      │  (8081)         │
     └─────────────────┘      └─────────────────┘
```

---

## Stack Tecnológico

| Categoría | Tecnología |
|-----------|------------|
| **Runtime** | Java 17 |
| **Framework** | Spring Boot 3.3 |
| **Cloud** | Spring Cloud 2023.0 |
| **Seguridad** | Spring Security, JWT (JJWT 0.12) |
| **BD** | MySQL 8, JPA/Hibernate |
| **Comunicación** | OpenFeign, RestTemplate |
| **Discovery** | Netflix Eureka |
| **Gateway** | Spring Cloud Gateway |
| **Resiliencia** | Resilience4j (Circuit Breaker) |
| **Observabilidad** | Actuator, Micrometer, Zipkin |
| **Documentación** | SpringDoc OpenAPI (Swagger) |

---

## Microservicios

### 1. Config Service (Puerto 8081)
Servidor de configuración centralizada. Los microservicios obtienen su configuración desde aquí en el arranque.

### 2. Eureka Service (Puerto 8761)
Registro y descubrimiento de servicios. Dashboard: `http://localhost:8761`

### 3. API Gateway (Puerto 9090)
Punto de entrada único. Enruta todas las peticiones hacia los microservicios correspondientes.

| Ruta base | Servicio destino |
|-----------|------------------|
| `/usuario/**` | User Service |
| `/peliculas/**` | Movie Service |
| `/generos/**` | Movie Service |
| `/directores/**` | Movie Service |
| `/tmdb/**` | Movie Service |
| `/reviews/**` | Review Service |

### 4. User Service
- **Autenticación**: registro, login, JWT
- **Perfil**: `/me`, actualización, cambio de contraseña
- **Admin**: gestión de usuarios, roles, estados
- **API interna**: resolución de usuarios por ID (Feign)

### 5. Movie Service
- **Películas**: CRUD, paginación, búsqueda
- **Directores y géneros**: CRUD
- **Cast**: reparto, importación desde TMDB
- **Integración TMDB**: importar películas populares
- **Agregados**: sincronización de ratings con Review Service

### 6. Review Service
- **Reseñas**: crear, editar, borrar (propias)
- **Listado público**: por película con displayName
- **Estadísticas**: media y contador por película
- **Comunicación**: Feign con User Service (displayName) y Movie Service (agregados)

### Favoritelist Service (en desarrollo — no incluido en el flujo actual)

El módulo **favoritelist-service** existe en el repositorio como proyecto Spring Boot base pero **no está integrado en el Gateway ni expuesto en la API**. La funcionalidad de “lista de películas favoritas por usuario” queda prevista para una fase posterior. Para usar la aplicación (frontend + backend) no es necesario arrancar ni configurar este servicio.

---

## Seguridad

- **JWT stateless** con expiración configurable
- **Roles**: `USER`, `ADMIN`
- **Internal API Key** (`X-Internal-Token`) para llamadas entre microservicios
- **CORS** configurado en el Gateway para el frontend

---

## Modelo de datos (relaciones entre entidades)

Cada microservicio posee su propia base de datos. Las relaciones lógicas entre servicios se mantienen mediante IDs (no hay FKs cruzadas).

### Base de datos: pruebagestorusuarios (User Service)

```
users
├── id (PK)
├── email (unique)
├── username (unique)
├── password_hash
├── display_name
├── role (USER, ADMIN)
├── status (ACTIVE, BANNED)
├── created_at
└── updated_at
```

### Base de datos: pruebagestorpeliculas (Movie Service)

```
director                    genre
├── id (PK)                 ├── id (PK)
├── name                    ├── name (unique)
├── slug (unique)           ├── slug (unique)
├── birth_date              ├── description
├── death_date              ├── color_hex
├── nationality             ├── icon
├── photo_url               ├── tmdb_id
├── biography               ├── created_at
├── imdb_id                 └── updated_at
├── tmdb_id
├── created_at
└── updated_at

pelicula
├── id (PK)
├── title
├── description
├── release_date
├── director_id (FK -> director)
├── duration_minutes
├── original_language
├── poster_url, backdrop_url, trailer_url
├── average_rating, vote_count (denormalizado de reviews)
├── slug, imdb_id, tmdb_id
├── created_at
└── updated_at

movie_genres (N:M pelicula <-> genre)
├── movie_id (FK -> pelicula)
└── genre_id (FK -> genre)

cast_credit
├── id (PK)
├── movie_id (FK -> pelicula)
├── tmdb_person_id
├── person_name
├── character_name
├── order_index
├── profile_url
├── created_at
└── updated_at
```

### Base de datos: pruebagestorreviews (Review Service)

```
reviews
├── id (PK)
├── movie_id  (referencia lógica -> pelicula.id)
├── user_id   (referencia lógica -> users.id)
├── rating (1-10)
├── comment
├── contains_spoilers
├── status (VISIBLE, HIDDEN)
├── edited
├── created_at
└── updated_at

UNIQUE (movie_id, user_id)  -- un usuario, una reseña por película
```

### Diagrama de relaciones (lógicas entre servicios)

```
        [users]                    [pelicula]
             \                           |
              \    user_id               | director_id
               \     |                   v
                \    |              [director]
                 \   |
                  v  v
               [reviews]  ---- movie_id ----> [pelicula]
                    |                              |
                    |                              +-- N:M --> [genre]
                    |                              |
                    |                              +-- 1:N --> [cast_credit]
                    +-- (Feign) obtiene displayName desde User Service
```

---

## Cómo ejecutar

### Requisitos previos
- Java 17+
- Maven 3.8+
- MySQL 8+
- Cuenta en [TMDB](https://www.themoviedb.org/) (API key para importar películas)

### Bases de datos
Crear las bases de datos:

```sql
CREATE DATABASE pruebagestorusuarios;
CREATE DATABASE pruebagestorreviews;
CREATE DATABASE pruebagestorpeliculas;
```

### Variables de entorno recomendadas

```bash
# JWT (obligatorio en producción)
export JWT_SECRET="tu-clave-secreta-muy-larga-y-segura"

# API interna entre microservicios
export INTERNAL_API_KEY="clave-interna-segura"

# TMDB (solo si usas importación)
export TMDB_API_KEY="tu-api-key-de-tmdb"
```

**Import TMDB y error 400**: Si al importar desde TMDB recibes **400** con mensaje tipo "TMDB rechazó la API key", suele ser porque `TMDB_API_KEY` no llega al proceso de movie-service. Si abres el workspace como **carpeta padre (FILMSCORE)**, en VS Code/Cursor usa la configuración de launch **"3 Movie Service (env en backend)"**, que carga el `.env` desde `Gestor_PeliculasYSeries_Microservicios/.env`. Comprueba en los logs de movie-service al arrancar: debe aparecer "TMDB: api-key configurada (X caracteres). Bearer auth: true". Si sale "tmdb.api-key está vacía", la variable no se está cargando.

### Orden de arranque

```bash
# 1. Config Server
cd config-service3 && mvn spring-boot:run

# 2. Eureka
cd eureka-service3 && mvn spring-boot:run

# 3. Microservicios (en terminales separadas)
cd user-service && mvn spring-boot:run
cd movie-service && mvn spring-boot:run
cd review-service && mvn spring-boot:run

# 4. Gateway
cd gateway-service3 && mvn spring-boot:run
```

**Punto de entrada**: `http://localhost:9090`

### Alternativa: Docker Compose

Puedes levantar todo el backend (MySQL + Config + Eureka + user, movie, review + Gateway) con un solo comando:

**Requisitos:** Docker y Docker Compose instalados.

```bash
# Desde la raíz del backend (Gestor_PeliculasYSeries_Microservicios)
cp .env.docker.example .env
# Edita .env y rellena al menos MYSQL_ROOT_PASSWORD y JWT_SECRET

docker-compose up -d --build
```

- **MySQL** crea automáticamente las tres bases de datos al iniciar (script en `docker/init-dbs.sql`).
- **Config Server** arranca con perfil `native` y lee la carpeta `config-data` local.
- Los servicios se conectan entre sí por nombre (`config-service`, `eureka-service`, `mysql`). El API Gateway queda en `http://localhost:9090`.

Para parar todo: `docker-compose down`. Los datos de MySQL se conservan en un volumen.

---

## API principal (via Gateway)

### Auth
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/usuario/auth/register` | Registrar usuario |
| POST | `/usuario/auth/login` | Iniciar sesión (JWT) |
| GET | `/usuario/auth/me` | Perfil (requiere JWT) |
| PATCH | `/usuario/auth/me` | Actualizar perfil |
| PATCH | `/usuario/auth/me/password` | Cambiar contraseña |

### Películas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/peliculas/peliculas` | Listado paginado |
| GET | `/peliculas/peliculas/{id}` | Detalle |
| GET | `/peliculas/top-rated` | Mejor valoradas |
| GET | `/peliculas/search` | Búsqueda |
| GET | `/peliculas/cast/{id}` | Reparto |

### Reseñas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/reviews/movie/{id}` | Reseñas de una película |
| GET | `/reviews/movie/{id}/stats` | Estadísticas |
| POST | `/reviews` | Crear reseña (JWT) |
| PATCH | `/reviews/{id}` | Editar reseña propia |
| DELETE | `/reviews/{id}` | Borrar reseña propia |

### Documentación Swagger
- User Service: `http://localhost:{port}/swagger-ui.html`
- Review Service: `http://localhost:{port}/swagger-ui.html`

*(Los puertos son dinámicos con Eureka; consultar el dashboard)*

---

## Estructura del proyecto

```
Gestor_PeliculasYSeries_Microservicios/
├── config-data/              # Configuración por servicio
├── config-service3/          # Config Server
├── eureka-service3/          # Service Discovery
├── gateway-service3/         # API Gateway
├── user-service/             # Usuarios y auth
├── movie-service/            # Películas, directores, géneros, cast
├── review-service/           # Reseñas
├── favoritelist-service/     # En desarrollo; no incluido en el flujo actual
├── docker/                   # init-dbs.sql para MySQL en Docker
├── docker-compose.yml        # Levantar todo con Docker Compose
└── .env.docker.example       # Variables para Docker; copiar a .env
```

---

## Testing

```bash
cd user-service
mvn test
```

Incluye tests unitarios para `AuthService`, `AuthController` y flujo JWT.

---

## Próximos pasos

- Completar **favoritelist-service** (lista de películas favoritas por usuario) e integrarlo en el Gateway cuando se implemente.
- Implementar **refresh token** para renovar JWT sin re-login
- Añadir **rate limiting** en el Gateway para proteger endpoints sensibles
- Definir **versionado de API** (ej. `/api/v1/...`)
- ~~Configurar **Docker Compose** para levantar todos los servicios con un comando~~ ✅ Hecho (ver sección «Alternativa: Docker Compose»)
- Sustituir Config Server Git por perfil **native** con `config-data` local para despliegue autónomo

---

## Licencia

Proyecto de uso personal y educativo.
