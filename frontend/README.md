# SEWorld Frontend

[![Vue 3](https://img.shields.io/badge/Vue-3.5-emerald.svg)](https://vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-6.2-purple.svg)](https://vitejs.dev/)
[![Pinia](https://img.shields.io/badge/Pinia-3.0-yellow.svg)](https://pinia.vuejs.org/)
[![Bootstrap 5](https://img.shields.io/badge/Bootstrap-5.3-primary.svg)](https://getbootstrap.com/)
[![Chart.js](https://img.shields.io/badge/Chart.js-4.4-ff6384.svg)](https://www.chartjs.org/)

The frontend for **SEWorld** is a reactive Single Page Application (SPA) built with **Vue 3 (Composition API)** and powered by **Vite**. It provides an engaging and responsive interface for both students and instructors, featuring curriculum topic progression, interactive multi-format exercises, instant feedback scoring, performance visualization charts, and seamless interaction with local AI tutoring models.

---

## Table of Contents

- [Tech Stack & Libraries](#tech-stack--libraries)
- [Key Features & Views](#key-features--views)
- [Architecture & State Management](#architecture--state-management)
- [Authentication & Network Communication](#authentication--network-communication)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Development Server](#development-server)
  - [Building for Production](#building-for-production)
  - [Previewing Production Build](#previewing-production-build)
- [Recommended IDE Setup](#recommended-ide-setup)
- [Project Directory Structure](#project-directory-structure)

---

## Tech Stack & Libraries

- **Framework:** [Vue 3](https://vuejs.org/) (using Single File Components & Composition API)
- **Build Tool & Bundler:** [Vite 6](https://vitejs.dev/) (fast ESM-based HMR and production bundling)
- **State Management:** [Pinia 3](https://pinia.vuejs.org/) (modular reactive stores)
- **Routing:** [Vue Router 4](https://router.vuejs.org/) (with navigation guards for authentication)
- **UI & Styling:** [Bootstrap 5](https://getbootstrap.com/) and [Bootstrap Vue Next](https://bootstrap-vue-next.github.io/bootstrap-vue-next/)
- **Data Visualization:** [Chart.js](https://www.chartjs.org/) and [vue-chartjs](https://vue-chartjs.org/)
- **Markdown Rendering & Editing:** [md-editor-v3](https://imzbf.github.io/md-editor-v3/en-US)
- **Component & Icon Auto-Discovery:** [unplugin-vue-components](https://github.com/unplugin/unplugin-vue-components) & [unplugin-icons](https://github.com/unplugin/unplugin-icons)
- **HTTP Client:** Axios and native Fetch API with cookie/session forwarding

---

## Key Features & Views

### 1. Topics & Curriculum (`/topics`)
- Displays all available course topics with visual progress indicators.
- Allows students to track their completion stage across curriculum topics.
- Enables instructors to create, edit, search, and delete topics.

### 2. Materials Hub (`/materials/:did`)
- Topic-specific resource directory containing uploaded files (PDFs, code snippets, lecture slides) and external links.
- Interactive file download, upload modal (up to 20MB), and deletion controls.
- **Ask AI on Material:** Students can open the AI popup to query the local DeepSeek model directly about the contents of a study material.

### 3. Exercises & Attempt Runner (`/exercises/:did` & `/exercises/:exercise_did/attempt/:attempt_did`)
- Filter and search exercises within a topic.
- Exercise authoring workflow for instructors: create new exercises, add questions, toggle draft/published status, and preview.
- Interactive question types:
  - **Multiple Choice:** Radio and checkbox options.
  - **True / False:** Binary verification questions.
  - **Short Answer:** Free-text answer submissions.
- Real-time submission, attempt scoring, and instant answer feedback.

### 4. AI-Powered Hub ("My AI" & AI Feedback) (`/MyAI`, `/exercises/:exercise_did/ai_feedback`)
- **My AI Dashboard:** Central view for students and instructors to track AI interactions.
- **AI Explanations:** Automated generation of detailed explanations for student answers.
- **Instructor Review:** Instructors can inspect pending AI evaluations, approve (`accept`), or reject (`deny`) them.
- **Community Interaction:** Students and instructors can rate AI answers (1-5 stars) and post comments.

### 5. Performance Analytics & Profiles (`/profile/:user_info?`, `/statistic/:exerciseDid`)
- **Grade Visualizations:** Chart.js graphs displaying student grade distributions, average scores, and attempt histories.
- **Topic Completion Bars:** Visual progression bars indicating finished vs. pending coursework.
- **Exercise Statistics:** Detailed per-question metrics for instructors assessing question difficulty and class performance.

---

## Architecture & State Management

The frontend uses **Pinia** for centralized, modular state management. Stores are located in `src/stores/`:

| Store | Responsibility |
|:---|:---|
| `user` | Handles current user session, profile data, login status, and role detection (`isInstructor`). |
| `topics` | Stores topics list, student topic completion status, and topic CRUD actions. |
| `material` / `materialFilter` | Manages materials for the active topic and handles type/name filtering. |
| `exercises` / `exercise` | Manages exercise lists, exercise creation forms, and draft/publish statuses. |
| `attempts` | Tracks active and historical student exercise attempts, submissions, and status. |
| `questions` | Handles exercise questions and question variants. |
| `myAi` / `myAiFilter` / `myAiFilterUser` | Manages AI questions, responses, publishing states, and response filtering. |
| `statistics` / `exercisesStatisticsFilter` | Retrieves and formats statistical data for student profiles and instructor dashboards. |
| `comment` / `rating` | Handles user comments and ratings on AI responses. |
| `navigation` | Tracks navigation history and previous route names for breadcrumbs and return buttons. |

---

## Authentication & Network Communication

### Authentication Flow
1. When navigating to any route (except `/login`), the Vue Router `beforeEach` navigation guard calls `userStore.refreshUser()`.
2. `refreshUser()` queries `GET http://localhost:8080/api/auth/me` with `credentials: 'include'`.
3. If unauthenticated, the user is redirected to `/login`.
4. Clicking **Log In** on `/login` redirects the browser to the backend OAuth2 endpoint:  
   `http://localhost:8080/oauth2/authorization/switch-edu-id`
5. After authentication with SWITCH Edu-ID, the backend redirects back to `http://localhost:3000/topics`.

### Development Proxy
In development, the Vite server proxies all `/api` requests to the backend server at `http://localhost:8080`:

```javascript
// vite.config.js
server: {
  proxy: { 
    '/api': {
      target: 'http://localhost:8080/',
      changeOrigin: true,
      secure: false 
    }
  }
}
```

---

## Getting Started

### Prerequisites
- [Node.js](https://nodejs.org/) (v18.x or v20.x LTS recommended)
- [Yarn](https://yarnpkg.com/) (v1.22+)

### Installation
From the `frontend` directory, install all required dependencies:

```bash
yarn install
```

### Development Server
Starts the Vite development server with Hot Module Replacement (HMR) on port `3000`:

```bash
yarn dev
```
Open your browser at `http://localhost:3000`.

### Building for Production
Compiles and minifies the application assets for production into the `dist/` directory:

```bash
yarn build
```

### Previewing Production Build
Locally preview the generated production build:

```bash
yarn preview
```

---

## Recommended IDE Setup

- **Editor:** [VS Code](https://code.visualstudio.com/)
- **Extension:** [Vue - Official](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (formerly Volar).
  - *Note:* Make sure to disable the legacy **Vetur** extension to avoid conflicts.

---

## Project Directory Structure

```
frontend/
├── public/                      # Static logos and icons (SEW_white.svg, SEW_black.svg)
├── src/
│   ├── assets/                  # CSS stylesheets (main.css, base.css, images)
│   ├── components/              # Reusable Vue components
│   │   ├── buttons/             # Primary, Secondary, Delete, Refresh, Rating buttons
│   │   ├── cards/               # PrimaryCard, SecondaryCard, QACard, TopicCard, ExerciseCard
│   │   ├── icons/               # Custom file type and UI SVG icons
│   │   ├── infographics/        # CompletionBar and progress indicators
│   │   ├── inputs/              # SearchBar, CommentBar
│   │   ├── popup/               # AskAIPopup, CommentPopup, GlobalPopup
│   │   ├── user/                # Profile, search, MyAI, and grade graph components
│   │   └── ...                  # Question format components (MultipleChoice, TrueFalse, ShortAnswer)
│   ├── router/
│   │   └── index.js             # Route definitions and authentication navigation guards
│   ├── services/                # API communication services
│   ├── stores/                  # Pinia state stores (user, topics, exercises, AI, stats)
│   ├── utils/                   # Route constants, UUID helpers, email & file utilities
│   ├── views/                   # Top-level page views (Home, Login, Topics, Materials, Attempts, MyAI, Stats)
│   ├── App.vue                  # Root Vue component with persistent Header and layout
│   └── main.js                  # App bootstrap, Pinia and Router registration
├── index.html                   # HTML entrypoint
├── package.json                 # Project dependencies and npm scripts
├── vite.config.js               # Vite build configuration, plugins, and proxy settings
└── jsconfig.json                # JavaScript path alias configuration (@ -> ./src)
```
