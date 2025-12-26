# SEWorld Backend

The backend service for the SEWorld platform, a gamified educational application designed to manage software engineering topics, exercises, and questions. This application is built with Java 21 and Spring Boot, leveraging modern features like Spring AI for LLM integration and reactive caching.

## 🛠 Technologies & Stack

* **Language**: Java 21
* **Framework**: Spring Boot 3.5.3
* **Database**: PostgreSQL 17
* **AI & LLM**: Spring AI with Ollama (running DeepSeek-LLM)
* **Security**: Spring Security 6 (OAuth2 Resource Server)
* **Caching**: Caffeine Cache
* **Build Tool**: Gradle
* **Containerization**: Docker & Docker Compose
* **Code Formatting**: Spotless (Google Java Format)

## 🚀 Getting Started

### Prerequisites

* **Docker Desktop** (recommended for running dependencies like Postgres and Ollama)
* **Java 21 SDK** (if running locally without Docker)

### Installation & Setup

1. **Clone the repository:**
```bash
git clone <repository-url>
cd SEWorld/backend
```


2. **Environment Configuration:**
   Create a `.env` file in the `backend` root directory based on `.env.template`. Ensure the following variables are set:
```properties
DB_NAME=seworld_db
DB_USER=postgres
DB_PASSWORD=your_password
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
GITHUB_CLIENT_ID=...
GITHUB_CLIENT_SECRET=...
```



### 🐳 Running with Docker (Recommended)

The easiest way to run the entire stack (Database, AI Service, and Backend) is via Docker Compose.

```bash
docker-compose up --build
```

This command starts:

* **Postgres**: Database service on port `5432`.
* **Ollama**: AI model service on port `11434` (pulls `deepseek-llm` automatically).
* **SEWorld-Backend**: The Spring Boot application on port `8080`.

### 💻 Running Locally

If you prefer to run the application code locally (e.g., for debugging) while keeping services in Docker:

1. **Start dependencies:**
```bash
docker-compose up postgres ollama -d
```


2. **Run the application:**
```bash
./gradlew bootRun
```



## 🔌 API Endpoints

The API is secured using OAuth2. Most endpoints require authentication.

### Topics API (`/api/auth/topic`)

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/` | Retrieve all topics |
| `GET` | `/{slug}` | Get a specific topic by slug |
| `POST` | `/` | Create a new topic (Instructor only) |
| `PATCH` | `/{slug}` | Update an existing topic (Instructor only) |
| `DELETE` | `/{slug}` | Delete a specific topic (Instructor only) |

### Exercises API (`/api/auth/exercise`)

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/` | Retrieve all exercises |
| `GET` | `/{slug}` | Get specific exercise details |
| `GET` | `/get_by_topic/{slug}` | Get exercises for a specific topic |
| `POST` | `/` | Create a new exercise (Instructor only) |
| `PATCH` | `/update/{slug}` | Update an exercise (Instructor only) |
| `PATCH` | `/publish/{slug}` | Publish an exercise (make visible) |

### Questions API (`/api/auth/question`)

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/{slug}` | Get all questions for a specific exercise |
| `POST` | `/{slug}` | Add questions to an exercise (Instructor only) |
| `PATCH` | `/` | Update a list of questions (Instructor only) |
| `DELETE` | `/` | Delete a list of questions (Instructor only) |

## 🧪 Testing

The project uses JUnit 5 and `embedded-postgres` for integration testing.

```bash
./gradlew test
```

## 📦 Build & Deployment

To build the JAR file without running tests or linting checks:

```bash
./gradlew build -x test -x spotlessCheck
```

The Docker image is built using a multi-stage process:

1. **Builder**: Compiles the code using `gradle:8.5-jdk21`.
2. **Runner**: Runs the artifact on `openjdk:21-jdk-slim` for a minimal footprint.

## 🧹 Code Style

We use **Spotless** with **Google Java Format** to maintain code quality. To fix formatting issues automatically:

```bash
./gradlew spotlessApply
```