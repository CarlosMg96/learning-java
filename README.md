# FleetHub API 🚗💨

API REST profesional desarrollada con **Spring Boot 3 (Java 21)**, **Spring Data JPA**, **PostgreSQL 16**, **Bean Validation**, **Actuator**, soporte para **AWS Lambda (Serverless)** y arquitectura de contenedores con **Docker Compose**.

---

## 🏗️ Arquitectura de Contenedores

Siguiendo las mejores prácticas de la industria, la aplicación está desacoplada en 2 servicios comunicados internamente:

1. **`fleethub_postgres`**: Contenedor con la imagen oficial `postgres:16-alpine` (puerto `5432`).
2. **`fleethub_api`**: Contenedor con la compilación y ejecución de tu API Spring Boot en **Java 21** (`Dockerfile` multi-etapa, puerto `8080`).

---

## 🚀 Cómo ejecutar con Docker

```bash
docker compose up --build -d
```

### Comandos útiles:
```bash
# Ver logs en tiempo real de la API
docker logs -f fleethub_api

# Detener los servicios
docker compose down

# Detener y limpiar los datos de PostgreSQL
docker compose down -v
```

---

## 🧪 Ejecutar pruebas unitarias y de mocks (Maven en Docker)
```bash
docker run --rm -v "$(pwd)/fleethub-api:/app" -w /app maven:3.9.9-eclipse-temurin-21-alpine mvn test
```

---

## 📡 Endpoints de la API REST

Base URL: `http://localhost:8080` (o usa el archivo interactivo [`api-requests.http`](file:///Users/carlosrodriguez/development/projects/learning-java/api-requests.http) en VS Code).

### 1. Actuator (Health check)
```bash
curl -X GET http://localhost:8080/actuator/health
```

### 2. Crear un Vehículo (`POST /api/v1/vehicles`)
```bash
curl -X POST http://localhost:8080/api/v1/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "FLEET-001",
    "brand": "Toyota",
    "model": "Hilux 4x4",
    "year": 2024,
    "status": "AVAILABLE"
  }'
```

### 3. Listar Vehículos (`GET /api/v1/vehicles`)
```bash
# Todos los vehículos
curl -X GET http://localhost:8080/api/v1/vehicles

# Filtrar por estado (AVAILABLE, IN_USE, MAINTENANCE, RETIRED)
curl -X GET "http://localhost:8080/api/v1/vehicles?status=AVAILABLE"

# Filtrar por marca
curl -X GET "http://localhost:8080/api/v1/vehicles?brand=Toyota"
```

### 4. Obtener por ID o por Placa
```bash
curl -X GET http://localhost:8080/api/v1/vehicles/1
curl -X GET http://localhost:8080/api/v1/vehicles/plate/FLEET-001
```

### 5. Actualizar Vehículo (`PUT /api/v1/vehicles/{id}`)
```bash
curl -X PUT http://localhost:8080/api/v1/vehicles/1 \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "Toyota",
    "model": "Hilux GR Sport",
    "year": 2024,
    "status": "IN_USE"
  }'
```

### 6. Eliminar Vehículo (`DELETE /api/v1/vehicles/{id}`)
```bash
curl -X DELETE http://localhost:8080/api/v1/vehicles/1
```