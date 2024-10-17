# Étape 1 : Utiliser une image de base OpenJDK 11 en version légère (slim)
FROM openjdk:17-jre-alpine

# Étape 2 : Définir une variable d'argument pour le fichier JAR à copier
ARG JAR_LOCATION=target/*.jar

# Étape 3 : Copier le JAR construit dans le conteneur
COPY ${JAR_LOCATION} app.jar

# Étape 4 : Exposer le port sur lequel l'application écoutera
EXPOSE 8081

# Étape 5 : Définir le point d'entrée du conteneur
ENTRYPOINT ["java", "-jar", "/app.jar"]
