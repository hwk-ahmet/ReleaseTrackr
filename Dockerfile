# --- Stage 1: Build both frontend + backend ---
FROM maven:3.9.4-eclipse-temurin-17 as build

# Set working directory
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build backend and potentially frontend (if integrated build)
# If your React is separate, add copying that too
RUN mvn clean package -DskipTests

# --- Stage 2: Run the app ---
FROM eclipse-temurin:17-jre

# Expose the port your backend listens on
# If using Spring Boot default, this is 8080
EXPOSE 8080

# Copy the built jar from the builder stage
COPY --from=build /app/target/*.jar app.jar

# Set environment vars for OAuth / Spotify (set via fly secrets later)
ENV SPOTIFY_CLIENT_ID=""
ENV SPOTIFY_CLIENT_SECRET=""
ENV SPOTIFY_REDIRECT_URI=""

# Command to run the backend
ENTRYPOINT ["java", "-jar", "/app.jar"]