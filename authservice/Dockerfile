FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests
CMD ["java", "-jar", "target/auth-service-0.0.1-SNAPSHOT.jar"]