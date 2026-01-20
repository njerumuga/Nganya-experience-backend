# Use Java 17 JDK
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Make mvnw executable (fixes permission denied)
RUN chmod +x mvnw

# Build project (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the jar (replace 'your-app.jar' with actual jar name)
CMD ["java", "-jar", "target/Nganya-experience-backend-0.0.1-SNAPSHOT.jar"]
