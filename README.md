# SEWorld

> ⚠️ **Work in Progress**: This project is currently under active development and is not yet finished. Features may be incomplete or subject to change.

## About The Project

SEWorld is an educational platform designed to manage topics, exercises, and interactions between instructors and students. It leverages local AI models (via Ollama) to enhance the learning experience.

**Origin & Attribution**
The original concept for this project was conceived by my university professor. This repository represents my personal reimplementation and evolution of the work originally developed during my university studies.

## Getting Started

The project is split into a **backend** (Java/Spring Boot with Docker) and a **frontend** (Vue 3).

### Prerequisites

* [Docker Desktop](https://www.docker.com/products/docker-desktop/)
* [Node.js](https://nodejs.org/) (for the frontend)

### 1. Running the Backend

The backend infrastructure (PostgreSQL, Ollama with `deepseek-llm`, and the Spring Boot application) is containerized.

1. Navigate to the backend directory:

    ```bash
    cd backend
    ```

2. Start the services using Docker Compose:

    ```bash
    docker-compose up --build
    ```

    *This will start the database on port 5432, Ollama on port 11434, and the Backend API on port 8080.*

### 2. Running the Frontend

The frontend is a standard Vue application.

1. Open a new terminal and navigate to the frontend directory:

    ```bash
    cd frontend
    ```

2. Install dependencies:

    ```bash
    npm install
    ```

3. Start the development server:

    ```bash
    npm run dev
    ```

    *Access the web application at the URL provided in the terminal ([http://localhost:3000](http://localhost:3000)).*
