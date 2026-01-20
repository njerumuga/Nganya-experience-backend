FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy project files
COPY . .

# Make mvnw executable
RUN chmod +x mvnw

# Build the project
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/your-app.jar"]
