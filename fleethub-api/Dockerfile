# ==========================================
# Etapa 1: Compilación con Maven y Java 21
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copiar pom.xml y descargar dependencias para aprovechar caché
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar el JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Etapa 2: Imagen liviana de ejecución (JRE 21)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear usuario sin privilegios por seguridad
RUN addgroup -S springgroup && adduser -S springuser -G springgroup
USER springuser:springgroup

# Copiar el artefacto generado
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
