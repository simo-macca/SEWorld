# SEWorld – Backend

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-6db33f.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791.svg)](https://www.postgresql.org/)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0--M6-6db33f.svg)](https://spring.io/projects/spring-ai)
[![Ollama](https://img.shields.io/badge/Ollama-deepseek--llm-black.svg)](https://ollama.com/)
[![Code Style](https://img.shields.io/badge/Code%20Style-Google%20Java%20Format-blueviolet.svg)](https://github.com/diffplug/spotless)

The backend of **SEWorld** is a REST API built with **Spring Boot 3.4.3** and **Java 21**. It handles topics, learning materials, exercises and automatic grading, role-based access for students and instructors, and the AI tutoring features. The AI features use **Spring AI** with a local **Ollama** model (`deepseek-llm`).

> Part of the SEWorld university project (USI, SA4 2025). See the [root README](../README.md) for the overview, quick start and team.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Core Features](#core-features)
- [Architecture](#architecture)
- [API Conventions](#api-conventions)
- [Running the Backend](#running-the-backend)
  - [Method 1: Docker Compose (Recommended)](#method-1-docker-compose-recommended)
  - [Method 2: Hybrid Local Development](#method-2-hybrid-local-development)
- [Configuration](#configuration)
- [Testing & Code Quality](#testing--code-quality)
- [Project Layout](#project-layout)

---

## Tech Stack

| Area               | Technology                                                                  |
|:-------------------|:----------------------------------------------------------------------------|
| Language & runtime | Java 21                                                                     |
| Framework          | Spring Boot 3.4.3 (Web, Validation)                                         |
| Persistence        | PostgreSQL 17, Spring Data JPA, Hibernate                                   |
| Security           | Spring Security, OAuth2 Client, OAuth2 Resource Server (JWT), SWITCH edu-ID |
| AI                 | Spring AI `1.0.0-M6` + Ollama (`deepseek-llm`, temperature 0.3)             |
| Build              | Gradle 8.5 (wrapper included)                                               |
| Testing            | JUnit 5, Mockito, Spring Security Test, Zonky Embedded PostgreSQL           |
| Code quality       | Spotless (Google Java Format), Jacoco, SonarQube (course CI)                |

---

## Core Features

### 1. Topics & Materials

- Course topics, with completion tracked for each student.
- Materials can be external links or uploaded files (PDF, slides, source code, text, and more), up to **20 MB** per request.
- Instructors can upload, download, edit and delete materials.

### 2. Exercises & Questions

- Three question types:
  - `MultiChoiceQuestion`: one or more correct options
  - `TrueFalseQuestion`: true/false statements
  - `ShortAnswerQuestion`: free-text answers
- **Variants:** a question can have alternative versions, which makes memorizing answers harder.
- **Lifecycle:** instructors create exercises as drafts, edit and preview them, then publish.
- `QuestionServiceRegistry` sends each question to the service for its type.

### 3. Attempts & Feedback

- Students can attempt a published exercise more than once.
- Answers are graded when the attempt is submitted.
- Feedback shows the result for each question, with explanations.

### 4. AI Features

- **Material Q&A:** students ask questions about a specific material (`POST /api/auth/AI/ask/materials/{material_did}`).
- **Answer explanations:** the AI explains why an answer is right or wrong.
- **Question variant generation:** instructors can have the AI generate new variants of a question.
- **Review workflow:** instructors accept or reject AI-generated content (`accept_evaluation` / `deny_evaluation`) before students can see it.
- **Ratings & comments:** students and instructors can rate AI responses and comment on them.

### 5. Statistics

- **Students:** completion per topic, grade history, performance charts.
- **Instructors:** difficulty of each question, class averages, results per topic and per exercise.

---

## Architecture

The code follows the classic layered layout: controllers call services, and services use repositories.

```
ch.usi.inf.bsc.sa4.lab02spring
├── configuration/   # SecurityConfiguration (OAuth2 + JWT), CorsConfig
├── controller/      # REST controllers under /api/auth/**
│   └── dto/         # Request/response DTOs and projections
├── exception/       # GlobalExceptionHandler
├── model/           # JPA entities (users, topics, materials, exercises, questions, attempts, AI responses)
├── repository/      # Spring Data JPA repositories and Specifications
├── response/        # ResponseHandler: uniform JSON envelope
├── service/         # Business logic, QuestionServiceRegistry, AI services
└── utils/           # User and role-check helpers
```

> The package name `lab02spring` comes from the course's lab template the project started from.

### Security & CORS

- **Authentication:** `SecurityConfiguration` configures OAuth2 login against the SWITCH edu-ID realm `https://auth.dev.si.usi.ch/auth/realms/simurbs`, and validates JWTs as a resource server. All `/api/auth/**` endpoints require authentication, while `/api/public/**` is open to everyone.
- **CORS:** `CorsConfig` allows `http://localhost:3000` to call `/api/**` with `GET, POST, PUT, PATCH, DELETE, OPTIONS` and credentials.
- After login, users are redirected to `http://localhost:3000/topics`.

> [!WARNING]
> The OAuth client (`test-client`) and realm are USI test infrastructure. Outside USI, configure your own OpenID Connect provider under `spring.security.oauth2` in `application.yml`.

---

## API Conventions

Every endpoint returns the same JSON envelope, built by `ResponseHandler`:

```json
{
  "status": "success",
  "status_code": 200,
  "message": "Materials found",
  "data": [ ... ],
  "success": true
}
```

The full endpoint reference, with parameters and examples, is in **[documentation/api_documentation.md](./documentation/api_documentation.md)**.

---

## Running the Backend

All commands run from the `backend/` directory.

### Method 1: Docker Compose (Recommended)

This starts PostgreSQL, Ollama (which downloads `deepseek-llm` on its first run) and the Spring Boot app:

```bash
docker compose up --build        # logs in the foreground
docker compose up --build -d     # in the background
```

| Container      | Port    | Role                                      |
|:---------------|:--------|:------------------------------------------|
| `SA4-Postgres` | `5432`  | PostgreSQL 17                             |
| `SA4-Ollama`   | `11434` | Ollama server (`deepseek-llm`)            |
| `SA4-Backend`  | `8080`  | Spring Boot API (built from `Dockerfile`) |

To follow the model download, run `docker logs -f SA4-Ollama`. To stop everything, run `docker compose down` (add `-v` to also delete the data volumes).

### Method 2: Hybrid Local Development

This runs only the database and Ollama in Docker, and the API from your IDE or Gradle:

```bash
docker compose up -d postgres ollama
docker ps                 # check that both containers are running
./gradlew bootRun
```

The app connects to `localhost:5432` (PostgreSQL) and `localhost:11434` (Ollama).

---

## Configuration

`src/main/resources/application.yml` contains the configuration. You can override any value with an environment variable:

| Environment variable         | Default                                                 | Description       |
|:-----------------------------|:--------------------------------------------------------|:------------------|
| `SPRING_DATASOURCE_URL`      | `jdbc:postgresql://localhost:5432/users_tutorial_lab02` | JDBC URL          |
| `SPRING_DATASOURCE_USERNAME` | `power_rangers`                                         | Database user     |
| `SPRING_DATASOURCE_PASSWORD` | *(see `docker-compose.yml`)*                            | Database password |
| `SPRING_AI_OLLAMA_BASE_URL`  | `http://localhost:11434`                                | Ollama base URL   |

> [!CAUTION]
> The database credentials in `docker-compose.yml` and `application.yml` are development values from the course. Never reuse them for a deployment that other people can reach. Supply your own values through environment variables or an untracked `.env` file.

Other settings:

- **Upload limit:** `spring.servlet.multipart.max-file-size` and `max-request-size` are both `20MB`.
- **Schema:** `spring.jpa.hibernate.ddl-auto=update`, so Hibernate creates and updates tables automatically.
- **AI model:** `deepseek-llm`, temperature `0.3`.
- **Logging:** Spring Security logs at `DEBUG` level. Lower it outside development.

---

## Testing & Code Quality

### Tests

The suite has about **300 tests**: unit tests for models and services, and MockMvc tests for controllers. They use **Zonky Embedded PostgreSQL**, so no external database is needed:

```bash
./gradlew test
```

### Coverage (Jacoco)

A coverage report is generated automatically after `test`. To run it on its own:

```bash
./gradlew jacocoTestReport
```

The XML report is written to `build/reports/jacoco/test/jacocoTestReport.xml`.

### Formatting (Spotless)

The code is formatted with Google Java Format:

```bash
./gradlew spotlessCheck   # check formatting
./gradlew spotlessApply   # fix formatting
```

### Javadoc

```bash
./gradlew javadoc         # output: build/docs/javadoc
```

### SonarQube

Static analysis ran in the course's GitLab pipeline (`.gitlab-ci.yml`) against USI's SonarQube instance. `sonar-project.properties` reads the project key and name from GitLab CI variables, so it won't work in a local checkout without that environment.

---

## Project Layout

```
backend/
├── documentation/
│   └── api_documentation.md        # REST API reference
├── gradle/wrapper/                 # Gradle wrapper
├── src/
│   ├── main/
│   │   ├── java/ch/usi/inf/bsc/sa4/lab02spring/
│   │   │   ├── configuration/      # Security and CORS
│   │   │   ├── controller/         # REST controllers
│   │   │   │   └── dto/            # DTOs
│   │   │   ├── exception/          # Global exception handling
│   │   │   ├── model/              # JPA entities
│   │   │   ├── repository/         # Repositories and specifications
│   │   │   ├── response/           # Response envelope
│   │   │   ├── service/            # Business logic
│   │   │   └── utils/              # User / role helpers
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/ch/usi/inf/bsc/sa4/lab02spring/
│           ├── controller/         # MockMvc controller tests
│           ├── model/              # Entity unit tests
│           ├── service/            # Service tests
│           └── utils/              # Test helpers (JwtTestUtil)
├── .gitlab-ci.yml                  # Course GitLab pipeline (build, test, quality)
├── Dockerfile                      # Multi-stage image build
├── docker-compose.yml              # PostgreSQL + Ollama + backend
├── build.gradle
├── settings.gradle
└── sonar-project.properties
```
