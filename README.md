# FleetHub API (Todo-en-Uno) 🚗💨

API REST profesional desarrollada con **Spring Boot 3 (Java 21)**, **Spring Data JPA**, **PostgreSQL**, **Bean Validation**, **Actuator**, soporte para **AWS Lambda (Serverless)** y contenedorizada en **un único contenedor Todo-en-Uno (All-in-One)** con Docker.

---

## 🚀 Cómo ejecutar (Un solo contenedor con Java 21 + PostgreSQL)

El proyecto incluye una imagen Docker Todo-en-Uno que contiene tanto el runtime de **Java 21** para la API como el servidor **PostgreSQL** interno y persistente en el mismo contenedor.

### Levantar el contenedor único:
```bash
docker compose up --build -d
```

### Ver logs en tiempo real:
```bash
docker logs -f fleethub_all_in_one
```

### Detener el contenedor:
```bash
docker compose down
# O para borrar también los datos de la base de datos:
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