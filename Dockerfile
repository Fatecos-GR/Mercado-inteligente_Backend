FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY . .

RUN chmod +x mvnw

ENTRYPOINT ["./mvnw", "spring-boot:run"]