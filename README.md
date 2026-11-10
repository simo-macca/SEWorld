# SEWorld

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-6db33f.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791.svg)](https://www.postgresql.org/)
[![Ollama](https://img.shields.io/badge/AI-Ollama%20%28deepseek--llm%29-black.svg)](https://ollama.com/)
[![Vue 3](https://img.shields.io/badge/Vue-3.5-42b883.svg)](https://vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-6.2-646cff.svg)](https://vitejs.dev/)
![University Project](https://img.shields.io/badge/USI-University%20Project-lightgrey.svg)

**SEWorld** is a full-stack, AI-assisted learning platform for Software Engineering courses. Students work through course topics, study materials, solve auto-graded exercises and ask questions to an AI tutor that runs on a local Large Language Model (LLM). Instructors write and publish content, review what the AI generates before students see it, and follow class performance through analytics.

> [!NOTE]
> **University project.** SEWorld was built by a student team as coursework for the *Software Atelier 4 (SA4)* course of the Bachelor in Informatics at [USI – Università della Svizzera italiana](https://www.usi.ch/) (2025). The team worked in Scrum-style sprints; the user stories, quality requirements and validation reports for each sprint are in [`batches/`](./batches). The project is a teaching prototype and is not maintained as a production service.

---

## Table of Contents

- [Features](#features)
- [System Architecture](#system-architecture)
- [Repository Structure](#repository-structure)
- [Quick Start](#quick-start)
  - [Prerequisites](#prerequisites)
  - [Option A: Backend in Docker + Local Frontend (Recommended)](#option-a-backend-in-docker--local-frontend-recommended)
  - [Option B: Full Local Development](#option-b-full-local-development)
  - [Stopping the Stack](#stopping-the-stack)
- [Default Ports and Services](#default-ports-and-services)
- [Authentication](#authentication)
- [Further Documentation](#further-documentation)

---

## Features

### For Students

- **Topics & Materials:** Browse course topics, track how much of each topic you have completed, and open learning materials (PDFs, source code, slides, external links).
- **Interactive Exercises:** Answer Multiple Choice, True/False and Short Answer questions, with multiple attempts allowed.
- **Instant Grading & Feedback:** Get your score right after submitting, see which answers were right or wrong, and read the explanation for each question.
- **AI Tutor ("My AI"):** Ask questions about a material or about your exercise answers. A local model (`deepseek-llm` via Ollama) answers them.
- **Personal Analytics:** See your grade distribution, completion progress and past attempt results.

### For Instructors

- **Course Management:** Create, edit and organize topics and materials, with file uploads up to 20 MB.
- **Exercise Authoring:** Write exercises as drafts, add question variants, preview them as a student would, and publish when ready.
- **AI Question Variants:** Generate alternative versions of a question with Spring AI and Ollama.
- **AI Review Workflow:** Accept or reject AI-generated explanations and answers before students can see them.
- **Class Analytics:** See aggregated attempt statistics, how difficult each question is, and results per topic.

---

## System Architecture

```
                      +---------------------------------------+
                      |         Client Browser (User)         |
                      +-------------------+-------------------+
                                          |
                                          | HTTP (port 3000)
                                          v
                      +---------------------------------------+
                      |        Frontend (Vue 3 + Vite)        |
                      |  - Pinia            - Bootstrap Vue   |
                      |  - Chart.js         - md-editor-v3    |
                      +-------------------+-------------------+
                                          |
                                          | REST API /api/auth/** (port 8080)
                                          | OAuth2 login via SWITCH edu-ID
                                          v
                      +---------------------------------------+
                      |      Backend (Spring Boot 3.4.3)      |
                      |  - Spring Security (OAuth2 / JWT)     |
                      |  - Spring Data JPA (Hibernate)        |
                      |  - Spring AI (Ollama client)          |
                      +---------+-------------------+---------+
                                |                   |
                JDBC (port 5432)|                   | HTTP (port 11434)
                                v                   v
       +----------------------------+   +----------------------------+
       |   PostgreSQL 17            |   |   Ollama                   |
       |   container: SA4-Postgres  |   |   container: SA4-Ollama    |
       |   db: users_tutorial_lab02 |   |   model: deepseek-llm      |
       +----------------------------+   +----------------------------+
```

---

## Repository Structure

```
SEWorld/
├── backend/                     # Spring Boot 3.4.3 REST API (Java 21)
│   ├── documentation/
│   │   └── api_documentation.md # REST endpoint reference
│   ├── gradle/wrapper/          # Gradle wrapper
│   ├── src/
│   │   ├── main/java/           # Controllers, services, entities, repositories, security
│   │   ├── main/resources/      # application.yml
│   │   └── test/java/           # JUnit 5 tests (embedded PostgreSQL)
│   ├── build.gradle             # Dependencies, Jacoco, Spotless
│   ├── Dockerfile               # Multi-stage image build
│   ├── docker-compose.yml       # PostgreSQL + Ollama + backend
│   ├── sonar-project.properties # SonarQube config (used by the course GitLab CI)
│   ├── .gitlab-ci.yml           # Original course GitLab pipeline
│   └── README.md                # Backend documentation
├── frontend/                    # Vue 3 + Vite single-page application
│   ├── public/                  # Logos (SEW_black.svg, SEW_white.svg)
│   ├── src/
│   │   ├── assets/              # Global CSS and images
│   │   ├── components/          # Reusable components (cards, buttons, popups, questions, charts)
│   │   ├── router/              # Routes and authentication guard
│   │   ├── services/            # Shared reactive services
│   │   ├── stores/              # Pinia stores
│   │   ├── utils/               # API route constants and helpers
│   │   ├── views/               # Pages
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js           # Plugins and /api proxy
│   ├── .gitlab-ci.yml           # Original course GitLab pipeline
│   └── README.md                # Frontend documentation
├── batches/                     # Sprint deliverables (PDF)
│   ├── batch_1/                 # Sprint 1: user stories, validation report
│   ├── batch_2/                 # Sprint 2: user stories, quality requirements, validation report
│   └── batch_3/                 # Sprint 3: user stories
└── README.md                    # This file
```

> The `.gitlab-ci.yml` files come from the course's GitLab instance, where the project was developed. They do not run on GitHub.

---

## Quick Start

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) with Docker Compose v2
- [Node.js](https://nodejs.org/) 20 or 22 LTS
- [Yarn](https://classic.yarnpkg.com/) 1.22+
- *(Only for running the backend outside Docker)* [JDK 21](https://adoptium.net/)
- About 5 GB of free disk space for the `deepseek-llm` model

> [!IMPORTANT]
> Login uses USI's SWITCH edu-ID test realm (see [Authentication](#authentication)). Without a USI account you can build and start the whole stack, but you won't be able to log in.

### Option A: Backend in Docker + Local Frontend (Recommended)

Docker runs PostgreSQL, Ollama and the Spring Boot API. The frontend runs locally with hot reload.

**1. Start the backend stack**

```bash
cd backend
docker compose up --build -d
```

On the first run, the Ollama container downloads `deepseek-llm`, which can take several minutes. Follow it with `docker logs -f SA4-Ollama`. The API is then available at `http://localhost:8080`.

To keep the logs in the foreground, leave out `-d`.

**2. Start the frontend** (in a second terminal)

```bash
cd frontend
yarn install
yarn dev
```

Open `http://localhost:3000`.

### Option B: Full Local Development

Use this when working on Java code in an IDE (e.g. IntelliJ IDEA), so you don't have to rebuild the container after every change.

```bash
# 1. Only database and AI engine in Docker
cd backend
docker compose up -d postgres ollama

# 2. Run the API with Gradle
./gradlew bootRun

# 3. Frontend (separate terminal)
cd ../frontend
yarn install
yarn dev
```

### Stopping the Stack

```bash
cd backend
docker compose down          # stop the containers
docker compose down -v       # stop them and also delete the database and model volumes
```

---

## Default Ports and Services

| Service         | Container         | Address                  | Description                       |
|:----------------|:------------------|:-------------------------|:----------------------------------|
| **Frontend**    | *(local process)* | `http://localhost:3000`  | Vue 3 SPA (Vite dev server)       |
| **Backend API** | `SA4-Backend`     | `http://localhost:8080`  | Spring Boot application           |
| **PostgreSQL**  | `SA4-Postgres`    | `localhost:5432`         | Database `users_tutorial_lab02`   |
| **Ollama**      | `SA4-Ollama`      | `http://localhost:11434` | LLM server running `deepseek-llm` |

---

## Authentication

Authentication uses **OAuth2 / OpenID Connect** with USI's **SWITCH edu-ID** Keycloak test realm (`https://auth.dev.si.usi.ch/auth/realms/simurbs`), which the course provided.

1. The frontend router checks `GET /api/auth/me` before every page. If you are not logged in, it sends you to `/login`.
2. **Log In** redirects to `http://localhost:8080/oauth2/authorization/switch-edu-id`.
3. The backend sends you to the SWITCH edu-ID realm to sign in.
4. After you sign in, Spring Security redirects you to `http://localhost:3000/topics`.
5. From then on, requests send the session cookie (`credentials: 'include'`). `GET /api/auth/me` returns your identity and role: `STUDENT` or `INSTRUCTOR`.

> [!WARNING]
> The realm and its OAuth client (`test-client`) belong to USI's course infrastructure, so outside that environment login fails. To run SEWorld elsewhere, register your own OpenID Connect provider (for example, a local Keycloak) in `backend/src/main/resources/application.yml`.

---

## Further Documentation

- [Backend README](./backend/README.md): tech stack, configuration, tests, code quality
- [API Documentation](./backend/documentation/api_documentation.md): every REST endpoint, with examples
- [Frontend README](./frontend/README.md): views, stores, routing, build
