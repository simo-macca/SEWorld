# SEWorld Backend

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0--M6-green.svg)](https://spring.io/projects/spring-ai)
[![Ollama](https://img.shields.io/badge/Ollama-deepseek--llm-black.svg)](https://ollama.ai/)
[![Spotless](https://img.shields.io/badge/Code%20Style-Spotless%20%28Google%20Java%29-blueviolet.svg)](https://github.com/diffplug/spotless)

The backend service for **SEWorld** is a high-performance RESTful API built with **Spring Boot 3.4.3** and **Java 21**. It manages educational topics, learning materials, multi-format exercises, automated feedback scoring, role-based user management, and AI tutoring workflows powered by **Spring AI** and a local **Ollama** model (`deepseek-llm`).

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Core Features & Modules](#core-features--modules)
- [Architecture & Design](#architecture--design)
- [API Conventions](#api-conventions)
- [Running the Backend](#running-the-backend)
  - [Method 1: Docker Compose (Recommended)](#method-1-docker-compose-recommended)
  - [Method 2: Hybrid Local Development](#method-2-hybrid-local-development)
- [Configuration & Environment Variables](#configuration--environment-variables)
- [Testing & Quality Assurance](#testing--quality-assurance)
  - [Running Tests](#running-tests)
  - [Code Coverage (Jacoco)](#code-coverage-jacoco)
  - [Code Formatting (Spotless)](#code-formatting-spotless)
  - [SonarQube Integration](#sonarqube-integration)
- [Project Directory Layout](#project-directory-layout)

---

## Tech Stack

- **Runtime & Language:** Java 21 (JDK 21)
- **Framework:** Spring Boot 3.4.3
- **Database & Persistence:** PostgreSQL 17, Spring Data JPA, Hibernate ORM
- **Security:** Spring Security, OAuth2 Client, OAuth2 Resource Server (JWT verification with SWITCH Edu-ID)
- **AI Framework:** Spring AI (`1.0.0-M6`) with Ollama Starter (`deepseek-llm` model)
- **Build & Dependency Management:** Gradle 8.5 with Gradle Wrapper
- **Testing Tools:** JUnit 5, Mockito, Zonky Embedded PostgreSQL (`io.zonky.test:embedded-postgres`)
- **Code Quality:** Spotless (Google Java Format), Jacoco, SonarQube

---

## Core Features & Modules

### 1. Topic & Material Management
- **Topic Hierarchy:** Manage curricular topics with completion tracking per student.
- **Learning Materials:** Support for external links and uploaded multimedia/documents (PDFs, slides, source code, text files) with a maximum request file size of 20MB.
- **Material Endpoints:** Upload, download, edit metadata, and delete materials (restricted to instructors).

### 2. Exercise & Question Engine
- **Question Types:**
  - `MultiChoiceQuestion`: Multiple choice questions with configurable correct options.
  - `TrueFalseQuestion`: Binary true/false verification questions.
  - `ShortAnswerQuestion`: Open-ended short text responses.
- **Variants Support:** Questions can have alternative variants to prevent memorization and assess core understanding.
- **Lifecycle Management:** Exercises can be drafted, edited, previewed, and published by instructors.

### 3. Attempts & Automated Feedback
- **Student Attempts:** Students can take and re-attempt published exercises.
- **Grading & Scoring:** Real-time answer scoring and evaluation upon attempt submission.
- **Detailed Feedback:** Comprehensive attempt summaries detailing question-by-question performance.

### 4. AI-Augmented Tutoring & Workflows
- **Ollama Integration:** Seamlessly interfaces with a local Ollama instance running `deepseek-llm`.
- **Material Q&A:** Students can ask AI-powered questions directly contextualized to learning materials (`/api/auth/AI/ask/materials/{material_did}`).
- **Exercise Explanations:** Automated generation of explanations for why a student's answer was correct or incorrect.
- **AI Question Variant Generation:** Instructors can request AI-generated question variants for existing exercises.
- **AI Evaluation Pipeline:** Instructors can review, approve (`accept_evaluation`), or reject (`deny_evaluation`) AI-generated content before publishing.

### 5. Analytics & Statistics
- **Student View:** Topic completion percentages, historical attempt grades, and graphical performance metrics.
- **Instructor View:** Aggregate exercise difficulty, class average scores, and cohort performance distributions.

---

## Architecture & Design

```
 ch.usi.inf.bsc.sa4.lab02spring
 ├── configuration/    # SecurityConfiguration (OAuth2/JWT), CorsConfig (CORS for localhost:3000)
 ├── controller/       # REST Controllers (@RequestMapping("/api/auth/..."))
 │   └── dto/          # Data Transfer Objects for input/output payloads
 ├── model/            # JPA Entities (User, Topic, Material, Exercise, Question, Attempt, AIResponse)
 ├── repository/       # Spring Data JPA Repositories & Specifications
 ├── service/          # Business logic, QuestionServiceRegistry, AI services
 ├── response/         # Standardized ResponseHandler utility
 └── utils/            # Helper utilities and role authorization checkers
```

### Security & CORS
- **CORS:** Configured in `CorsConfig.java` to allow requests from `http://localhost:3000` with HTTP methods (`GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`, `PATCH`) and credential forwarding (`allowCredentials: true`).
- **OAuth2 Authentication:** Configured in `SecurityConfiguration.java` using SWITCH Edu-ID Keycloak realm (`https://auth.dev.si.usi.ch/auth/realms/simurbs`). Protected endpoints require authentication (`/api/auth/**`).

---

## API Conventions

All API responses follow a uniform JSON structure defined by `ResponseHandler`:

```json
{
  "status": "success",
  "status_code": 200,
  "message": "Materials found",
  "data": [ ... ],
  "success": true
}
```

For complete endpoint documentation, path parameters, and request/response examples, refer to [api_documentation.md](./documentation/api_documentation.md).

---

## Running the Backend

### Method 1: Docker Compose (Recommended)

This starts the entire backend infrastructure including PostgreSQL, Ollama (with automatic `deepseek-llm` pull), and the Spring Boot application container.

```bash
docker-compose up --build
```
Or run in detached mode:
```bash
docker-compose up --build -d
```

#### Containers Started:
1. **`SA4-Postgres`** (Port `5432`): PostgreSQL 17 database instance.
2. **`SA4-Ollama`** (Port `11434`): Ollama inference server (automatically pulls `deepseek-llm` on first startup).
3. **`SA4-Backend`** (Port `8080`): Spring Boot application built from the local `Dockerfile`.

---

### Method 2: Hybrid Local Development

For active Java development with an IDE (e.g., IntelliJ IDEA, Eclipse, VS Code), run only the dependent services in Docker and run the Spring Boot app directly via Gradle:

1. **Start Database and AI Engine:**
   ```bash
   docker-compose up -d postgres ollama
   ```

2. **Verify Containers are Running:**
   ```bash
   docker ps
   ```

3. **Run the Spring Boot Application:**
   ```bash
   ./gradlew bootRun
   ```

The application will connect to the local Dockerized Postgres on port `5432` and Ollama on port `11434`.

---

## Configuration & Environment Variables

Default values are specified in `src/main/resources/application.yml` and can be overridden via environment variables:

| Environment Variable | Default Value | Description |
|:---|:---|:---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/users_tutorial_lab02` | PostgreSQL JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | `power_rangers` | PostgreSQL username |
| `SPRING_DATASOURCE_PASSWORD` | `usi_2025_sa4_project` | PostgreSQL password |
| `SPRING_AI_OLLAMA_BASE-URL` | `http://localhost:11434` | Ollama API base URL |

### Other Notable Properties
- **Multipart Upload Size:** Max file size and request size are set to `20MB` (`spring.servlet.multipart.max-file-size=20MB`).
- **JPA DDL Auto:** Set to `update` (`spring.jpa.hibernate.ddl-auto=update`) for automatic schema synchronizations.
- **AI Model:** Set to `deepseek-llm` with temperature `0.3`.

---

## Testing & Quality Assurance

### Running Tests
The backend uses **Zonky Embedded PostgreSQL** (`io.zonky.test:embedded-postgres`), allowing all integration tests to run with a real, embedded PostgreSQL instance without requiring an external database running.

To execute the test suite:
```bash
./gradlew test
```

### Code Coverage (Jacoco)
Jacoco is configured to automatically generate an XML coverage report after test execution:
```bash
./gradlew jacocoTestReport
```
The report is saved to:
`build/reports/jacoco/test/jacocoTestReport.xml`

### Code Formatting (Spotless)
The project enforces Google Java Format via Spotless.

Check formatting compliance:
```bash
./gradlew spotlessCheck
```

Automatically apply formatting fixes:
```bash
./gradlew spotlessApply
```

### SonarQube Integration
Static analysis and quality gates can be triggered using the configuration in `sonar-project.properties`:
```bash
./gradlew sonar
```

---

## Project Directory Layout

```
backend/
├── documentation/
│   ├── api_documentation.md      # Detailed REST API endpoint specifications
│   └── tutorial.md               # Additional developer setup guide
├── gradle/wrapper/               # Gradle wrapper executable binaries
├── src/
│   ├── main/
│   │   ├── java/ch/usi/inf/bsc/sa4/lab02spring/
│   │   │   ├── configuration/    # Security and WebMvc CORS configuration
│   │   │   ├── controller/       # REST API controllers and DTOs
│   │   │   ├── exception/        # Global exception handling
│   │   │   ├── model/            # JPA entities (Users, Topics, Exercises, etc.)
│   │   │   ├── repository/       # Spring Data JPA repositories & specifications
│   │   │   ├── response/         # Generic response envelope
│   │   │   ├── service/          # Business logic services
│   │   │   └── utils/            # Role checkers, JWT test utilities
│   │   └── resources/
│   │       └── application.yml   # Spring application configuration
│   └── test/
│       └── java/ch/usi/inf/bsc/sa4/lab02spring/
│           ├── controller/       # MockMvc controller tests
│           ├── model/            # Domain model unit tests
│           └── service/          # Service layer unit and integration tests
├── Dockerfile                    # Multi-stage production container build
├── docker-compose.yml            # Docker orchestration file
├── build.gradle                  # Gradle dependencies and tasks
├── settings.gradle               # Gradle project settings
└── sonar-project.properties      # SonarQube configuration
```
