# SEWorld Backend

This is the backend service for the SEWorld project, built with Spring Boot 3.4.3 and Java 21. It provides a RESTful API, handles security via OAuth2, integrates with PostgreSQL for persistence, and leverages Spring AI interacting with a local Ollama instance.

## Tech Stack
* **Java:** 21
* **Framework:** Spring Boot 3.4.3
* **Database:** PostgreSQL 17 + Spring Data JPA
* **AI Integration:** Spring AI + Ollama (`deepseek-llm` model)
* **Build Tool:** Gradle

## Running with Docker (Recommended)

The easiest way to run the backend and its dependencies is using Docker Compose. This starts PostgreSQL, Ollama, and the Spring Boot App.

```bash
docker-compose up --build
```

### Services Started:

1. **Postgres (`SA4-Postgres`):** Running on port `5432`.
2. **Ollama (`SA4-Ollama`):** Running on port `11434`. (Automatically pulls the `deepseek-llm` model on startup).
3. **Spring Boot App (`SA4-Backend`):** Running on port `8080`.

## Local Development (Without App Container)

If you prefer to run the Spring Boot application locally via your IDE or Gradle for faster debugging, you can run only the dependent services in Docker:

1. Start only the Database and AI containers:
    ```bash
    docker-compose up -d postgres ollama
    ```

2. Run the application using the Gradle wrapper:
    ```bash
    ./gradlew bootRun
    ```

### Default Environment Variables

If running locally, ensure your application points to the correct resources (already configured in `docker-compose.yml`):

* `SPRING_DATASOURCE_URL`: jdbc:postgresql://localhost:5432/users_tutorial_lab02
* `SPRING_DATASOURCE_USERNAME`: power_rangers
* `SPRING_DATASOURCE_PASSWORD`: usi_2025_sa4_project
* `SPRING_AI_OLLAMA_BASE-URL`: http://localhost:11434

## Testing and Formatting

To run tests (including embedded PostgreSQL tests) and generate a Jacoco coverage report:

```bash
./gradlew test
```

To format your code using Spotless (Google Java Format):

```bash
./gradlew spotlessApply
```
