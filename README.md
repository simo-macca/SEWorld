# SEWorld

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![Ollama](https://img.shields.io/badge/AI-Ollama%20%28DeepSeek--LLM%29-black.svg)](https://ollama.ai/)
[![Vue 3](https://img.shields.io/badge/Vue-3.5-emerald.svg)](https://vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-6.2-purple.svg)](https://vitejs.dev/)
[![License](https://img.shields.io/badge/License-Academic-lightgrey.svg)]()

**SEWorld** is a full-stack, AI-augmented educational platform developed for Software Engineering courses at USI (Università della Svizzera italiana). It delivers an interactive learning environment for students and comprehensive management tools for instructors, including course topic progression, multi-format exercises, instant feedback, performance analytics, and automated AI assistance powered by local Large Language Models (LLMs).

---

## Table of Contents

- [Features](#features)
- [System Architecture](#system-architecture)
- [Repository Structure](#repository-structure)
- [Quick Start Guide](#quick-start-guide)
  - [Prerequisites](#prerequisites)
  - [Option A: Containerized Backend + Local Frontend (Recommended)](#option-a-containerized-backend--local-frontend-recommended)
  - [Option B: Full Local Development](#option-b-full-local-development)
  - [Option C: Fully Containerized Backend via Docker](#option-c-fully-containerized-backend-via-docker)
- [Default Ports and Services](#default-ports-and-services)
- [Authentication Flow](#authentication-flow)
- [Submodule Documentation](#submodule-documentation)

---

## Features

### For Students
- **Curriculum & Topics:** Navigate course topics, track individual completion rates, and access learning materials (PDFs, code files, slides, external links).
- **Interactive Exercises:** Solve exercises with various question formats (Multiple Choice, True/False, Short Answer) with multiple attempts.
- **Instant Grading & Feedback:** Receive real-time scoring, answer validations, and detailed solution explanations.
- **AI-Powered Tutoring ("My AI"):** Ask contextual questions about course materials and exercise answers using an integrated local AI model (`deepseek-llm`).
- **Personal Analytics:** View grade distributions, completion progress bars, and historical attempt performance.

### For Instructors
- **Course Administration:** Create, update, and organize topics and study materials with file upload support (up to 20MB).
- **Exercise Management:** Author exercises in draft mode, add multiple question variants, preview from a student perspective, and publish when ready.
- **AI Question Variant Generator:** Automatically generate alternative question variations using Spring AI and Ollama.
- **AI Evaluation Pipeline:** Review, accept, or deny AI-generated explanations and exercise responses before they become visible to students.
- **Cohort Analytics:** Inspect aggregated student attempt statistics, question difficulty metrics, and per-topic performance.

---

## System Architecture

```
                      +---------------------------------------+
                      |       Client Browser (User)          |
                      +-------------------+-------------------+
                                          |
                        HTTP / Web UI     | (Port 3000)
                                          v
                      +---------------------------------------+
                      |         Frontend (Vue 3 + Vite)       |
                      |  - Pinia State  - Bootstrap Vue Next  |
                      |  - Chart.js     - Md-Editor-V3        |
                      +-------------------+-------------------+
                                          |
                      Vite Proxy / CORS   | REST API (/api/auth/**)
                      (Port 8080)         | OAuth2 SWITCH Edu-ID
                                          v
                      +---------------------------------------+
                      |      Backend (Spring Boot 3.4.3)      |
                      |  - Spring Security (OAuth2 / JWT)     |
                      |  - Spring Data JPA (Hibernate)        |
                      |  - Spring AI (Ollama Client)          |
                      +---------+-------------------+---------+
                                |                   |
               JDBC (Port 5432) |                   | HTTP (Port 11434)
                                v                   v
       +----------------------------+   +----------------------------+
       |   PostgreSQL 17 Database   |   |   Ollama LLM Engine        |
       |  (Container: SA4-Postgres) |   |  (Container: SA4-Ollama)   |
       |  users_tutorial_lab02      |   |  Model: deepseek-llm       |
       +----------------------------+   +----------------------------+
```

---

## Repository Structure

```
SEWorld/
├── backend/                     # Spring Boot 3.4.3 Backend Application
│   ├── documentation/           # API specs, guides, and tutorials
│   │   ├── api_documentation.md # Detailed REST endpoint documentation
│   │   └── tutorial.md          # Setup guides
│   ├── gradle/                  # Gradle wrapper binaries & configuration
│   ├── src/
│   │   ├── main/java/           # Application controllers, services, models, security
│   │   ├── main/resources/      # application.yml and runtime properties
│   │   └── test/java/           # JUnit 5 & Zonky Embedded Postgres tests
│   ├── build.gradle             # Build script, dependencies, and Jacoco config
│   ├── Dockerfile               # Multi-stage Docker build for backend service
│   ├── docker-compose.yml       # Docker orchestration (PostgreSQL, Ollama, App)
│   ├── sonar-project.properties # SonarQube quality configuration
│   └── README.md                # Backend-specific documentation
├── frontend/                    # Vue 3 + Vite Frontend Application
│   ├── public/                  # Static assets & SVG logos
│   ├── src/
│   │   ├── assets/              # Global styles, color palettes, CSS
│   │   ├── components/          # Reusable Vue components, modals, cards, charts
│   │   ├── router/              # Vue Router configuration and auth navigation guards
│   │   ├── services/            # API communication services
│   │   ├── stores/              # Pinia state management stores
│   │   ├── utils/               # Constants, route definitions, helpers
│   │   ├── views/               # Page views (Topics, Exercises, Attempts, AI, Stats)
│   │   ├── App.vue              # Main Vue application component
│   │   └── main.js              # Application entrypoint
│   ├── index.html               # Vite HTML entrypoint
│   ├── package.json             # NPM dependencies and development scripts
│   ├── vite.config.js           # Vite configuration & backend proxy setup
│   └── README.md                # Frontend-specific documentation
├── batches/                     # User stories, requirements, and validation PDFs
└── README.md                    # Root project documentation (this file)
```

---

## Quick Start Guide

### Prerequisites

Ensure you have the following software installed on your machine:
- [Docker](https://docs.docker.com/get-docker/) & [Docker Compose](https://docs.docker.com/compose/) (v2+)
- [Node.js](https://nodejs.org/) (v18.x or v20.x LTS)
- [Yarn](https://yarnpkg.com/) (v1.22+)
- *(Optional for local backend development)*: [Java Development Kit (JDK) 21](https://adoptium.net/)

---

### Option A: Containerized Backend + Local Frontend (Recommended)

This mode runs the entire backend stack (Database, Ollama AI, and Spring Boot API) in Docker, while running the frontend locally with Vite hot-reloading.

#### 1. Start the Backend Infrastructure
```bash
cd backend
docker-compose up --build -d
```
> [!NOTE]
> On the first run, the Ollama container will automatically download the `deepseek-llm` model. This may take a few minutes depending on your internet connection. Check the logs with `docker logs -f SA4-Ollama`.
> The backend API will be available at `http://localhost:8080`.

#### 2. Start the Frontend Development Server
In a new terminal window:
```bash
cd frontend
yarn install
yarn dev
```
Open your browser and navigate to `http://localhost:3000`.

---

### Option B: Full Local Development

If you are developing Java code in an IDE (e.g., IntelliJ IDEA) and want fast compilation without rebuilding the container:

#### 1. Start only the Database and Ollama AI containers
```bash
cd backend
docker-compose up -d postgres ollama
```

#### 2. Run the Spring Boot Application locally
```bash
cd backend
./gradlew bootRun
```

#### 3. Run the Frontend locally
```bash
cd frontend
yarn install
yarn dev
```

---

### Option C: Fully Containerized Backend via Docker

To run the complete backend stack in the foreground and follow container logs:
```bash
cd backend
docker-compose up --build
```
To stop the services:
```bash
docker-compose down
```

---

## Default Ports and Services

| Service | Container Name | Host Address | Description |
|:---|:---|:---|:---|
| **Frontend UI** | *(Host Process)* | `http://localhost:3000` | Vue 3 Single Page Application |
| **Backend REST API** | `SA4-Backend` | `http://localhost:8080` | Spring Boot 3.4.3 Application |
| **PostgreSQL Database** | `SA4-Postgres` | `localhost:5432` | PostgreSQL 17 Database (`users_tutorial_lab02`) |
| **Ollama AI Engine** | `SA4-Ollama` | `localhost:11434` | Ollama server running `deepseek-llm` |

---

## Authentication Flow

Authentication is integrated with **SWITCH Edu-ID** via OAuth2 / OpenID Connect:
1. When you access protected pages on the frontend (`http://localhost:3000`), unauthenticated users are directed to `/login`.
2. Clicking **Log In** triggers a redirect to the backend OAuth2 endpoint:  
   `http://localhost:8080/oauth2/authorization/switch-edu-id`
3. The backend delegates authentication to the SWITCH Edu-ID Keycloak realm (`https://auth.dev.si.usi.ch/auth/realms/simurbs`).
4. Upon successful login, Spring Security redirects back to the frontend (`http://localhost:3000/topics`).
5. Frontend requests include session credentials (`credentials: 'include'`) and query `GET /api/auth/me` to determine user identity and role (`STUDENT` or `INSTRUCTOR`).

---

## Submodule Documentation

For in-depth architecture, API endpoints, test instructions, and contribution guidelines, see:
- [Backend README](./backend/README.md) & [API Documentation](./backend/documentation/api_documentation.md)
- [Frontend README](./frontend/README.md)
