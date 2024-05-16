# Usar una imagen base de Java
FROM openjdk:21

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR de la aplicación al contenedor
COPY Apli-Servi-0.0.1-SNAPSHOT.jar /app/Apli-Servi-0.0.1-SNAPSHOT.jar

# Exponer el puerto en el que la aplicación se ejecutará
EXPOSE 8040

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/Apli-Servi-0.0.1-SNAPSHOT.jar"]
