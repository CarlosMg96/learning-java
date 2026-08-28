# FleetHub API 🚗💨

API REST profesional desarrollada con **Spring Boot 3 (Java 21)**, **Spring Data JPA**, **PostgreSQL**, **Bean Validation**, **Actuator**, soporte para **AWS Lambda (Serverless)** y contenedorizada con **Docker** y **Docker Compose**.

---

## 🚀 Cómo ejecutar con Docker (Sin necesidad de Java instalado localmente)

El proyecto incluye un `Dockerfile` multi-etapa con **Java 21** y un `docker-compose.yml` que compila el código y levanta la base de datos automáticamente.

### Opción 1: Levantar todo (PostgreSQL + API)
```bash
docker compose up --build
```
> **Nota:** La primera vez descargará las imágenes de Maven y Eclipse Temurin 21, compilará el código y levantará ambos servicios.

### Opción 2: Levantar solo la base de datos PostgreSQL
Si en el futuro instalas Java y solo quieres la base de datos corriendo en Docker:
```bash
docker compose up -d postgres_db
```

### Detener los servicios:
```bash
docker compose down
# O si deseas borrar los datos persistentes del volumen:
docker compose down -v
```

---

## 📡 Endpoints de la API REST

Base URL local: `http://localhost:8080`

### 1. Actuator (Health check)
```bash
curl -X GET http://localhost:8080/actuator/health
```

### 2. Crear un Vehículo (`POST /api/v1/vehicles`)
```bash
curl -X POST http://localhost:8080/api/v1/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "ABC-1234",
    "brand": "Toyota",
    "model": "Hilux",
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
curl -X GET http://localhost:8080/api/v1/vehicles/plate/ABC-1234
```

### 5. Actualizar Vehículo (`PUT /api/v1/vehicles/{id}`)
```bash
curl -X PUT http://localhost:8080/api/v1/vehicles/1 \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "Toyota",
    "model": "Hilux 4x4",
    "year": 2024,
    "status": "IN_USE"
  }'
```

### 6. Eliminar Vehículo (`DELETE /api/v1/vehicles/{id}`)
```bash
curl -X DELETE http://localhost:8080/api/v1/vehicles/1
```

---

## ☁️ Conexión y Despliegue en AWS Lambda

El proyecto utiliza **AWS Serverless Java Container** (`StreamLambdaHandler`), lo que permite que todas las rutas de Spring Boot se ejecuten en AWS Lambda a través de **Amazon API Gateway** o **AWS Application Load Balancer (ALB)** sin modificar el código de los controladores.

### Configuración del Handler en AWS Lambda:
- **Handler**: `com.fleethub.api.lambda.StreamLambdaHandler::handleRequest`
- **Runtime**: `Java 21` o `Java 17`
- **Memoria recomendada**: 1024 MB - 2048 MB
- **Timeout**: 30 segundos

### Despliegue con AWS SAM:
```bash
sam build
sam deploy --guided
```
*(Ver archivo [`template.yaml`](file:///Users/carlosrodriguez/development/projects/learning-java/template.yaml) incluido en la raíz)*.