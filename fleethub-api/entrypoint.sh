#!/bin/bash
set -e

echo "🐘 [1/3] Iniciando PostgreSQL interno..."

# Directorio de datos de PostgreSQL
PGDATA="/var/lib/postgresql/data"

# Si el directorio está vacío, inicializar la base de datos
if [ -z "$(ls -A "$PGDATA" 2>/dev/null)" ]; then
    echo "⚙️ Inicializando clúster de PostgreSQL..."
    chown -R postgres:postgres "$PGDATA"
    su-exec postgres initdb -D "$PGDATA" --auth=trust

    # Configurar para aceptar conexiones locales
    echo "host all all 127.0.0.1/32 trust" >> "$PGDATA/pg_hba.conf"
    echo "host all all ::1/128 trust" >> "$PGDATA/pg_hba.conf"
fi

# Arrancar el servicio de PostgreSQL en segundo plano
chown -R postgres:postgres "$PGDATA"
su-exec postgres pg_ctl -D "$PGDATA" -l /var/log/postgresql.log start

# Esperar a que PostgreSQL esté listo
echo "⏳ Esperando a que PostgreSQL esté listo..."
until su-exec postgres pg_isready -h 127.0.0.1 -p 5432; do
    sleep 1
done

# Crear base de datos fleethub y usuario si no existen
echo "🗄️ [2/3] Verificando base de datos 'fleethub'..."
su-exec postgres psql -h 127.0.0.1 -p 5432 -tc "SELECT 1 FROM pg_database WHERE datname = 'fleethub'" | grep -q 1 || \
su-exec postgres psql -h 127.0.0.1 -p 5432 -c "CREATE DATABASE fleethub;"

# Configurar contraseña de usuario postgres
su-exec postgres psql -h 127.0.0.1 -p 5432 -c "ALTER USER postgres WITH PASSWORD 'postgres';"

echo "🚀 [3/3] Iniciando la API Spring Boot (Java 21)..."

# Ejecutar Spring Boot como proceso principal del contenedor
exec java -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar /app/app.jar
