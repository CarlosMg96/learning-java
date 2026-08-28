# ==========================================
# Etapa 1: Compilación con Maven y Java 21
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copiar pom.xml y descargar dependencias para aprovechar la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar el JAR (sin requerir Java en tu máquina host)
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Etapa 2: Imagen final liviana con JRE 21
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear usuario sin privilegios por seguridad
RUN addgroup -S springgroup && adduser -S springuser -G springgroup
USER springuser:springgroup

# Copiar el artefacto generado en la etapa de compilación
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

# Parámetros JVM optimizados para contenedores
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
