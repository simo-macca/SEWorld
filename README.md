# SEWorld

Welcome to the SEWorld project repository. This project consists of a **Spring Boot** backend interacting with a **PostgreSQL** database and an **Ollama** AI service, paired with a **Vue 3 + Vite** frontend.

## Quick Start Guide

To run the entire project, you will need to start both the backend services and the frontend development server.

### Prerequisites
* [Docker](https://www.docker.com/) and Docker Compose
* [Node.js](https://nodejs.org/) (v18+)
* [Yarn](https://yarnpkg.com/) (v1.22+)

### Step 1: Start the Backend Infrastructure
The backend is fully containerized. Docker Compose will automatically provision the PostgreSQL database, download the `deepseek-llm` AI model via Ollama, and start the Spring Boot application.

```bash
cd backend
docker-compose up --build -d
```

*Wait a few moments for the Ollama container to download the deepseek model. The backend API will be available at `http://localhost:8080`.*

### Step 2: Start the Frontend

Open a new terminal window, navigate to the frontend directory, install the dependencies, and start the Vite development server.

```bash
cd frontend
yarn install
yarn dev
```

*The frontend application will be available at `http://localhost:3000`.*

---

**Note:** For more detailed instructions on development setups, please refer to the specific READMEs in the [Backend Directory](./backend/README.md) and [Frontend Directory](./frontend/README.md).
