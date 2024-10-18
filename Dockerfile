# Utiliser l'image de base OpenJDK avec une version JRE slim
FROM openjdk:11-jre-slim

# Définir une variable d'argument pour le fichier JAR
ARG JAR_FILE=target/*.jar

# Ouvrir le port 8081 pour la communication
EXPOSE 8081

# Copier le fichier JAR dans l'image
COPY ${JAR_FILE} gestion-station-ski.jar

# Spécifier le point d'entrée pour exécuter l'application
ENTRYPOINT ["java", "-jar", "/gestion-station-ski.jar"]

