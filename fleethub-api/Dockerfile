# ==========================================
# Etapa 1: Compilación con Maven y Java 21
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copiar pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Etapa 2: Imagen Todo-en-Uno (Java 21 + PostgreSQL)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Instalar PostgreSQL, su-exec y herramientas necesarias
RUN apk add --no-cache postgresql postgresql-contrib su-exec bash curl \
    && mkdir -p /var/lib/postgresql/data /run/postgresql /var/log \
    && chown -R postgres:postgres /var/lib/postgresql /run/postgresql /var/log

# Copiar el artefacto de Spring Boot compilado
COPY --from=builder /build/target/*.jar /app/app.jar

# Copiar el script de inicialización todo-en-uno
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Exponer el puerto de la API (8080) y opcionalmente PostgreSQL (5432)
EXPOSE 8080 5432

ENTRYPOINT ["/app/entrypoint.sh"]
