# Etapa de construcción
FROM maven:3.8.4-openjdk-21 AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml y los archivos fuente al contenedor
COPY Apli-Servi/pom.xml Apli-Servi/pom.xml
COPY Apli-Servi/src/ Apli-Servi/src/

# Construir la aplicación
RUN mvn -f Apli-Servi/pom.xml clean package

# Etapa de ejecución
FROM openjdk:21

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR desde la etapa de construcción
COPY --from=build /app/Apli-Servi/target/Apli-Servi-0.0.1-SNAPSHOT.jar /app/Apli-Servi-0.0.1-SNAPSHOT.jar

# Exponer el puerto en el que la aplicación se ejecutará
EXPOSE 8040

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/Apli-Servi-0.0.1-SNAPSHOT.jar"]
