# SEWorld – Frontend

[![Vue 3](https://img.shields.io/badge/Vue-3.5-42b883.svg)](https://vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-6.2-646cff.svg)](https://vitejs.dev/)
[![Pinia](https://img.shields.io/badge/Pinia-3.0-ffd859.svg)](https://pinia.vuejs.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952b3.svg)](https://getbootstrap.com/)
[![Chart.js](https://img.shields.io/badge/Chart.js-4.4-ff6384.svg)](https://www.chartjs.org/)

The frontend of **SEWorld** is a single-page application (SPA) built with **Vue 3** and **Vite**. It has separate interfaces for students and instructors, covering topics and materials, interactive exercises with instant feedback, performance charts and the AI tutor.

> Part of the SEWorld university project (USI, SA4 2025). See the [root README](../README.md) for the overview, quick start and team.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Main Views](#main-views)
- [State Management](#state-management)
- [Authentication & API Communication](#authentication--api-communication)
- [Getting Started](#getting-started)
- [Recommended IDE Setup](#recommended-ide-setup)
- [Project Structure](#project-structure)

---

## Tech Stack

| Area         | Library                                                                                                                                                     |
|:-------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Framework    | [Vue 3](https://vuejs.org/) (Single File Components)                                                                                                        |
| Build tool   | [Vite 6](https://vitejs.dev/)                                                                                                                               |
| State        | [Pinia 3](https://pinia.vuejs.org/)                                                                                                                         |
| Routing      | [Vue Router 4](https://router.vuejs.org/) with an authentication guard                                                                                      |
| UI           | [Bootstrap 5](https://getbootstrap.com/), [Bootstrap Vue Next](https://bootstrap-vue-next.github.io/bootstrap-vue-next/), Bootstrap Icons                   |
| Charts       | [Chart.js](https://www.chartjs.org/) + [vue-chartjs](https://vue-chartjs.org/)                                                                              |
| Markdown     | [md-editor-v3](https://imzbf.github.io/md-editor-v3/en-US)                                                                                                  |
| Auto-imports | [unplugin-vue-components](https://github.com/unplugin/unplugin-vue-components), [unplugin-icons](https://github.com/unplugin/unplugin-icons) (Iconify sets) |
| HTTP         | Axios and the Fetch API, both sending the session cookie                                                                                                    |
| Formatting   | Prettier (`.prettierrc`)                                                                                                                                    |

---

## Main Views

| Route | View | Purpose |
|:---|:---|:---|
| `/login` | `LoginView` | Sign in with SWITCH edu-ID |
| `/topics` | `TopicListView` | List of topics with completion progress. Instructors can create, edit and delete topics |
| `/materials/:did` | `MaterialListView` | Materials of a topic: upload (up to 20 MB), download, edit, delete, and **Ask AI** about a material |
| `/exercises/:did` | `ExerciseListView` | Exercises of a topic, with search and filters |
| `/exercise/new/:topicDid?`, `/exercise/edit/:exerciseDid/:topicDid` | `ExerciseForm` | Exercise authoring: questions, variants, draft or published |
| `/exercises/:exercise_did/attempt/:attempt_did` | `AttemptView` | Take an exercise (Multiple Choice, True/False, Short Answer) and see the feedback |
| `/exercises/:exercise_did/ai_feedback` | `ExerciseAIFeedbackView` | AI explanations for an exercise |
| `/MyAI` | `MyAiView` | Your AI questions and responses. Instructors review them here (accept or deny) |
| `/statistic/:exerciseDid` | `ExerciseStatisticView` | Per-question statistics for instructors |
| `/profile/:user_info?` | `UserProfileView` | Grade charts and topic completion. Instructors can look up students |
| `/users`, `/add_User` | `UserListView`, `AddUserView` | User management |
| `/about` | `AboutView` | About the project |
| `/components` | `ComponentsView` | Showcase of UI components (development only) |

Users can also rate AI responses (1–5 stars) and comment on them.

---

## State Management

The Pinia stores live in `src/stores/`:

| Store                                     | What it manages                                                 |
|:------------------------------------------|:----------------------------------------------------------------|
| `user`                                    | Current session and profile, login state, role (`isInstructor`) |
| `users`                                   | User list *(not used at the moment)*                            |
| `topics`                                  | Topics, completion status, topic create/update/delete           |
| `material`, `materialFilter`              | Materials of the active topic, with type and name filters       |
| `exercises`, `exercise`                   | Exercise lists, the authoring form, draft/published state       |
| `attempts`                                | Current and past attempts, submissions                          |
| `questions`                               | Questions and variants                                          |
| `myAi`, `myAiFilter`, `myAiFilterUser`    | AI questions and responses, publishing state, filters           |
| `exerciseAIFeedback`                      | AI feedback for an exercise                                     |
| `statistics`, `exercisesStatisticsFilter` | Statistics for profiles and instructor dashboards               |
| `comment`, `rating`                       | Comments and ratings on AI responses                            |
| `search`                                  | Search state                                                    |
| `navigation`                              | Previous route, used by the back buttons                        |

`src/services/ahTopicsCommunicationService.js` is a shared reactive object. The grade graph uses it to highlight the topics that are selected or hovered.

---

## Authentication & API Communication

### Login Flow

1. Before each route, the router's `beforeEach` guard calls `userStore.refreshUser()`, which requests `GET /api/auth/me` with `credentials: 'include'`.
2. If you are not logged in, the guard redirects you to `/login`. If you are logged in and open `/`, it redirects you to `/topics`.
3. **Log In** opens `http://localhost:8080/oauth2/authorization/switch-edu-id`.
4. After signing in with SWITCH edu-ID, the backend redirects you to `http://localhost:3000/topics`.

> [!WARNING]
> The SWITCH edu-ID realm is USI test infrastructure, so login only works within that environment. See the [root README](../README.md#authentication).

### API Routes & Dev Proxy

The API paths are defined in `src/utils/constants.js`. During development, Vite forwards requests that start with `/api` to the backend:

```javascript
// vite.config.js
server: {
  proxy: {
    '/api': { target: 'http://localhost:8080/', changeOrigin: true, secure: false }
  }
}
```

Some routes (for example `/api/auth/me`), and the login URL in `LoginView.vue`, use the absolute base URL `http://localhost:8080` (`BASE_URL` in `constants.js`) and don't go through the proxy. The backend's CORS configuration allows `http://localhost:3000` for these requests. If the backend runs anywhere else, update `BASE_URL` and the login URL.

---

## Getting Started

### Prerequisites

- [Node.js](https://nodejs.org/) 20 or 22 LTS
- [Yarn](https://classic.yarnpkg.com/) 1.22+ (the version is pinned in `package.json` → `packageManager`)
- The backend running on `http://localhost:8080` (see the [backend README](../backend/README.md))

### Commands

All commands run from the `frontend/` directory.

| Command        | Description                                                     |
|:---------------|:----------------------------------------------------------------|
| `yarn install` | Install dependencies                                            |
| `yarn dev`     | Start the dev server with hot reload at `http://localhost:3000` |
| `yarn build`   | Build for production into `dist/`                               |
| `yarn preview` | Serve the production build locally                              |

---

## Recommended IDE Setup

- [VS Code](https://code.visualstudio.com/) with the [Vue – Official](https://marketplace.visualstudio.com/items?itemName=Vue.volar) extension. Disable the old **Vetur** extension.
- Turn on format-on-save with Prettier so the code follows `.prettierrc`.

---

## Project Structure

```
frontend/
├── public/                     # SEW_black.svg / SEW_white.svg (favicons, logos)
├── src/
│   ├── assets/                 # base.css, main.css, images/
│   ├── components/
│   │   ├── buttons/            # Primary, Secondary, Delete, Refresh, Rating, Accept/Deny
│   │   ├── cards/              # PrimaryCard, SecondaryCard, QACard
│   │   ├── icons/              # File-type and UI icons
│   │   ├── infographics/       # CompletionBar
│   │   ├── inputs/             # SearchBar, CommentBar
│   │   ├── popup/              # AskAIPopup, CommentPopup, GlobalPopup
│   │   ├── user/
│   │   │   ├── myai/           # My AI page (student and instructor versions)
│   │   │   └── profile/        # Profile, grade graph, completion cards, user search
│   │   └── *.vue               # Shared components: TopicCard, ExerciseCard, MaterialCard,
│   │                           # question types (MultipleChoice, TrueFalse, ShortAnswer),
│   │                           # ExerciseForm, Header, Loader, FAQ, modals, ...
│   ├── router/index.js         # Routes and authentication guard
│   ├── services/               # ahTopicsCommunicationService (shared reactive state)
│   ├── stores/                 # Pinia stores
│   ├── utils/                  # API route constants, file/email/uuid/statistics helpers, global popup
│   ├── views/                  # Pages
│   ├── App.vue                 # Root component
│   └── main.js                 # Sets up Pinia, the router and plugins
├── components.d.ts             # Generated by unplugin-vue-components
├── index.html                  # HTML entry point
├── jsconfig.json               # "@" → ./src alias
├── package.json
├── vite.config.js              # Plugins and /api proxy
├── yarn.lock
└── .gitlab-ci.yml              # Course GitLab pipeline (install, build)
```
