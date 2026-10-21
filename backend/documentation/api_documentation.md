# SEWorld REST API Documentation

The **SEWorld Backend API** provides a RESTful interface for curriculum management, learning material distribution, exercise evaluation, student progress analytics, and AI-driven tutoring powered by Spring AI and Ollama (`deepseek-llm`).

---

## Table of Contents

- [General Conventions](#general-conventions)
  - [Base URL & Protocol](#base-url--protocol)
  - [Authentication & Authorization](#authentication--authorization)
  - [Standard Response Envelope](#standard-response-envelope)
  - [Error Handling](#error-handling)
- [Summary of Endpoints](#summary-of-endpoints)
- [1. Authentication & User Management](#1-authentication--user-management)
- [2. Topic Management](#2-topic-management)
- [3. Learning Materials Management](#3-learning-materials-management)
- [4. Exercise Management](#4-exercise-management)
- [5. Question & Variant Management](#5-question--variant-management)
- [6. Attempt Management](#6-attempt-management)
- [7. Answer Management](#7-answer-management)
- [8. Feedback](#8-feedback)
- [9. AI Tutoring & Question Explanations](#9-ai-tutoring--question-explanations)
- [10. AI Evaluation Management](#10-ai-evaluation-management)
- [11. AI Response Comments](#11-ai-response-comments)
- [12. Statistics & Analytics](#12-statistics--analytics)

---

## General Conventions

### Base URL & Protocol
- **Base URL:** `http://localhost:8080`
- **Frontend URL:** `http://localhost:3000`
- **Protected Endpoints Base:** `/api/auth/**`

### Authentication & Authorization
Authentication is managed via **OAuth2 / OpenID Connect** with **SWITCH Edu-ID**:
- **Login Endpoint:** `/oauth2/authorization/switch-edu-id`
- **Role Hierarchy:**
  - `STUDENT`: Enrolled student capable of attempting exercises, reading materials, asking AI questions, and viewing their own statistics.
  - `INSTRUCTOR`: Course instructor/teacher with administrative rights to manage topics, upload materials, create/publish exercises, review AI evaluations, and inspect cohort statistics.
- All protected endpoints require a valid session or JWT Bearer token issued during OAuth2 login.

### Standard Response Envelope
All API endpoints return JSON conforming to the `ResponseHandler` wrapper:

```json
{
  "status": "success",
  "status_code": 200,
  "message": "Operation description or confirmation message",
  "data": { ... },
  "success": true
}
```

### Error Handling
When an error occurs, the standard envelope returns with an error status and descriptive message:

```json
{
  "status": "error",
  "status_code": 401,
  "message": "Student unauthorized to upload material",
  "data": null,
  "success": false
}
```

Common HTTP Status Codes:
- `200 OK`: Request succeeded.
- `201 CREATED`: Resource successfully created.
- `400 BAD REQUEST`: Invalid payload or missing parameters.
- `401 UNAUTHORIZED`: Authentication required or invalid credentials.
- `403 FORBIDDEN`: Insufficient role privileges for the requested operation.
- `404 NOT FOUND`: The requested resource does not exist.

---

## Summary of Endpoints

| Category | Method | Path | Role | Description |
|:---|:---|:---|:---|:---|
| **Users** | `GET` | `/api/auth/me` | Authenticated | Fetch current authenticated user's profile |
| | `GET` | `/api/auth/instructor/me` | `INSTRUCTOR` | Verify instructor privileges |
| | `GET` | `/api/auth/all` | Authenticated | List all registered users |
| | `GET` | `/api/auth/{did}` | Authenticated | Get user profile by DID |
| | `GET` | `/api/auth/search?query=...` | Authenticated | Search users by partial name |
| **Topics** | `GET` | `/api/auth/topic` | Authenticated | List all topics with material & exercise availability |
| | `GET` | `/api/auth/topic/{did}` | Authenticated | Get single topic by DID |
| | `GET` | `/api/auth/topic/completion` | Authenticated | Get student's completion progress for all topics |
| | `GET` | `/api/auth/topic/completion/{did}` | Authenticated | Get student's completion progress for a specific topic |
| | `POST` | `/api/auth/topic/create` | `INSTRUCTOR` | Create a new topic |
| | `PATCH` | `/api/auth/topic/update/{did}` | `INSTRUCTOR` | Update an existing topic |
| | `DELETE` | `/api/auth/topic/delete/{did}` | `INSTRUCTOR` | Delete a topic |
| | `POST` | `/api/auth/topic/search` | Authenticated | Search topics by title and description keywords |
| | `GET` | `/api/auth/topic/exercises/get_topic_did_by_exercises/{did}` | Authenticated | Get parent topic DID for an exercise |
| **Materials** | `GET` | `/api/auth/material/{topic_did}/all_materials` | Authenticated | Get all materials associated with a topic |
| | `GET` | `/api/auth/material/{did}` | Authenticated | Get single material metadata by DID |
| | `GET` | `/api/auth/material/{did}/download` | Authenticated | Download binary file content of a file material |
| | `POST` | `/api/auth/material/{topic_did}/upload_file` | `INSTRUCTOR` | Upload file material (up to 20MB) |
| | `POST` | `/api/auth/material/{topic_did}/upload` | `INSTRUCTOR` | Upload non-file material (links, text) |
| | `PATCH` | `/api/auth/material/update/{did}` | `INSTRUCTOR` | Update material metadata / replace file |
| | `DELETE` | `/api/auth/material/delete/{did}` | `INSTRUCTOR` | Delete a material |
| **Exercises** | `GET` | `/api/auth/topic/exercises/get_all_exercises_in_topic/{topic_did}` | Authenticated | List all exercises in topic (students only see published) |
| | `GET` | `/api/auth/topic/exercises/get_by_exercise_did/{exercise_did}` | Authenticated | Get exercise details by DID |
| | `POST` | `/api/auth/topic/exercises/create/{topic_did}` | `INSTRUCTOR` | Create new exercise in draft mode |
| | `POST` | `/api/auth/topic/exercises/search/{topic_did}` | Authenticated | Search exercises in topic by keywords |
| | `PATCH` | `/api/auth/topic/exercises/teacher/change_draft/{exercise_did}` | `INSTRUCTOR` | Toggle draft status (publish/unpublish) |
| | `PATCH` | `/api/auth/topic/exercises/teacher/update_exercise/{exercise_did}` | `INSTRUCTOR` | Update exercise title and description |
| | `PATCH` | `/api/auth/topic/exercises/teacher/update_exercise_complete/{exercise_did}` | `INSTRUCTOR` | Update title, description, and questions |
| | `DELETE` | `/api/auth/topic/exercises/teacher/delete_exercise/{exercise_did}` | `INSTRUCTOR` | Delete an exercise |
| | `DELETE` | `/api/auth/topic/exercises/teacher/delete_all_exercises_in_topic/{topic_did}` | `INSTRUCTOR` | Delete all exercises within a topic |
| **Questions** | `GET` | `/api/auth/topic/exercises/question/attempt/{attempt_did}` | Authenticated | Get randomized questions for an attempt |
| | `GET` | `/api/auth/topic/exercises/question/exercise/{exercise_did}` | `INSTRUCTOR` | Get all questions for an exercise |
| | `DELETE` | `/api/auth/topic/exercises/question/delete/{question_did}` | `INSTRUCTOR` | Delete a question |
| | `POST` | `/api/auth/topic/exercises/question/save_all/{exercise_did}` | `INSTRUCTOR` | Save questions for an exercise |
| | `PATCH` | `/api/auth/topic/exercises/question/update/{exercise_did}` | `INSTRUCTOR` | Reorder / swap question variants |
| **Attempts** | `GET` | `/api/auth/topic/exercises/attempts` | Authenticated | List all attempts in the system |
| | `GET` | `/api/auth/topic/exercises/attempts/get_all_by_user` | Authenticated | List attempts for currently logged-in user |
| | `GET` | `/api/auth/topic/exercises/attempts/get_all_by_exercise/{exercise_did}` | Authenticated | List all attempts for a given exercise |
| | `GET` | `/api/auth/topic/exercises/attempts/get_all_by_user_exercise/{exercise_did}` | Authenticated | List user's attempts on an exercise |
| | `GET` | `/api/auth/topic/exercises/attempts/get_by_did/{did}` | Authenticated | Get attempt by DID |
| | `POST` | `/api/auth/topic/exercises/attempts/create/{exercise_did}` | Authenticated | Start a new attempt on an exercise |
| | `PATCH` | `/api/auth/topic/exercises/attempts/update/{did}` | Authenticated | Submit attempt (finalize submission) |
| | `DELETE` | `/api/auth/topic/exercises/attempts/delete/{did}` | Authenticated | Delete an attempt |
| **Answers** | `GET` | `/api/auth/topic/exercises/answers/get/{attempt_did}/{question_did}` | Authenticated | Get answer for a question in an attempt |
| | `GET` | `/api/auth/topic/exercises/answers/getall/{attempt_did}` | Authenticated | Get all answers for an attempt |
| | `POST` | `/api/auth/topic/exercises/answers/create/{attempt_did}/{question_did}` | Authenticated | Save answer for a question in an attempt |
| | `POST` | `/api/auth/topic/exercises/answers/update/{did}` | Authenticated | Update existing answer content |
| | `DELETE` | `/api/auth/topic/exercises/answers/delete/{did}` | Authenticated | Delete an answer |
| **Feedback** | `GET` | `/api/auth/feedback/{attempt_did}` | Authenticated | Get calculated score and feedback for an attempt |
| **AI Tutoring** | `POST` | `/api/auth/AI/ask/materials/{material_did}` | `STUDENT` | Ask AI question about highlighted text in material |
| | `PATCH` | `/api/auth/AI/response/rate/{response_did}` | `STUDENT` | Rate an AI response (1-5 stars) |
| | `GET` | `/api/auth/AI/response` | `INSTRUCTOR` | List all AI responses |
| | `PATCH` | `/api/auth/AI/response/publish/{response_did}` | `INSTRUCTOR` | Publish an AI response publicly |
| | `PATCH` | `/api/auth/AI/response/public/{material_did}` | Authenticated | Fetch published AI responses for a material |
| | `GET` | `/api/auth/AI/response/mine` | `STUDENT` | List student's own AI responses |
| | `POST` | `/api/auth/AI/generate/question_variant` | `INSTRUCTOR` | Generate question variants with AI |
| | `GET` | `/api/auth/AI/responses/{exercise_did}/all_generated_AI_explanations` | `INSTRUCTOR` | Get all AI explanations for exercise questions |
| | `GET` | `/api/auth/AI/ask/questions/explanation/new/{answer_did}` | `STUDENT` | Generate new explanation for student answer |
| | `GET` | `/api/auth/AI/ask/questions/explanation/refresh/{answer_did}` | `STUDENT` | Regenerate explanation for student answer |
| | `GET` | `/api/auth/AI/ask/questions/explanation/published/{answer_did}` | Authenticated | Get published explanations for student answer |
| **AI Evaluation** | `GET` | `/api/auth/AI_Evaluation/get_AI_evaluations/{exercise_did}` | `INSTRUCTOR` | Get pending AI evaluations for an exercise |
| | `DELETE` | `/api/auth/AI_Evaluation/deny_evaluation/{AI_evaluation_did}` | `INSTRUCTOR` | Reject a pending AI evaluation |
| | `PATCH` | `/api/auth/AI_Evaluation/accept_evaluation/{AI_evaluation_did}` | `INSTRUCTOR` | Approve a pending AI evaluation |
| **Comments** | `POST` | `/api/auth/AI/responses/comment/{response_did}` | `INSTRUCTOR` | Post comment on an AI response |
| | `DELETE` | `/api/auth/AI/responses/comment/{comment_did}/delete` | `INSTRUCTOR` | Delete comment on an AI response |
| | `GET` | `/api/auth/AI/responses/comment/{responseDid}/allComments` | Authenticated | Get all comments for an AI response |
| **Statistics** | `GET` | `/api/auth/statistics/general_view_statistics` | Authenticated | General profile stats (role-aware) |
| | `GET` | `/api/auth/statistics/general_view_statistics/{user_did}` | Authenticated | Student stats by user DID |
| | `GET` | `/api/auth/statistics/topic/{topic_did}` | Authenticated | Topic exercise stats (role-aware) |
| | `GET` | `/api/auth/statistics/topic/{topic_did}/{user_did}` | Authenticated | Topic exercise stats for specific user |
| | `GET` | `/api/auth/statistics/exercise/{exercise_did}` | Authenticated | Single exercise stats (role-aware) |

---

## 1. Authentication & User Management

### `GET /api/auth/me`
Retrieves the profile of the currently logged-in user. If the user logs in for the first time via SWITCH Edu-ID, the profile is created automatically.

- **Role:** Authenticated
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "",
  "data": {
    "did": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Jane Doe",
    "email": "jane.doe@usi.ch",
    "role": "STUDENT"
  },
  "success": true
}
```

### `GET /api/auth/instructor/me`
Validates that the currently authenticated user possesses instructor privileges.

- **Role:** `INSTRUCTOR`
- **Response `200 OK`:** Returns user DTO if instructor.
- **Response `403 FORBIDDEN`:** Returned if user has `STUDENT` role.

### `GET /api/auth/all`
Returns a list of all registered users in the platform.

- **Role:** Authenticated
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "",
  "data": [
    {
      "did": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Jane Doe",
      "email": "jane.doe@usi.ch",
      "role": "STUDENT"
    }
  ],
  "success": true
}
```

### `GET /api/auth/{did}`
Fetch user information by user DID.

- **Path Parameter:** `did` (UUID) - User identifier.
- **Role:** Authenticated
- **Response `200 OK`:** User object found.
- **Response `404 NOT FOUND`:** User not found.

### `GET /api/auth/search?query={partialName}`
Searches users by partial name matching.

- **Query Parameter:** `query` (String) - Search keyword.
- **Role:** Authenticated
- **Response `200 OK`:** List of matching `UserDTO` records.

---

## 2. Topic Management

### `GET /api/auth/topic`
Retrieves all curriculum topics along with flags indicating whether materials or exercises exist for each topic.

- **Role:** Authenticated
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "Topics found",
  "data": [
    {
      "did": "8a3a2d5e-6c84-4861-8ff6-77864f1d01b1",
      "title": "Design Patterns",
      "description": "Introduction to Creational, Structural, and Behavioral patterns",
      "materialsAvailable": true,
      "exercisesAvailable": true
    }
  ],
  "success": true
}
```

### `GET /api/auth/topic/{did}`
Retrieves a specific topic by its DID.

- **Path Parameter:** `did` (UUID) - Topic identifier.
- **Role:** Authenticated
- **Response `200 OK`:** Single `TopicDTO` object.

### `GET /api/auth/topic/completion`
Retrieves completion percentages for all topics for the calling student.

- **Role:** Authenticated
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "Topics completion found",
  "data": [
    {
      "topicDid": "8a3a2d5e-6c84-4861-8ff6-77864f1d01b1",
      "completionPercentage": 75.0,
      "hasExercises": true
    }
  ],
  "success": true
}
```

### `GET /api/auth/topic/completion/{did}`
Retrieves completion percentage for a single specified topic.

- **Path Parameter:** `did` (UUID) - Topic identifier.
- **Role:** Authenticated
- **Response `200 OK`:** Single `CompletionTopicDTO` object.

### `POST /api/auth/topic/create`
Creates a new course topic.

- **Role:** `INSTRUCTOR`
- **Request Body:**
```json
{
  "title": "Software Testing & CI/CD",
  "description": "Unit testing, integration testing, and automated pipelines"
}
```
- **Response `200 OK`:** Created `TopicDTO` with generated `did`.
- **Response `401 UNAUTHORIZED`:** If caller is not an instructor.

### `PATCH /api/auth/topic/update/{did}`
Updates the title or description of an existing topic.

- **Path Parameter:** `did` (UUID) - Topic identifier.
- **Role:** `INSTRUCTOR`
- **Request Body:** `TopicDTO` fields to update.
- **Response `200 OK`:** Updated `TopicDTO`.

### `DELETE /api/auth/topic/delete/{did}`
Deletes a topic from the system.

- **Path Parameter:** `did` (UUID) - Topic identifier.
- **Role:** `INSTRUCTOR`
- **Response `200 OK`:** Confirmation message.

### `POST /api/auth/topic/search`
Searches topics based on keywords matched against titles and descriptions.

- **Role:** Authenticated
- **Request Body:**
```json
{
  "titleKeywords": ["Pattern", "Architecture"],
  "descriptionKeywords": ["Object Oriented"]
}
```
- **Response `200 OK`:** List of matching `TopicDTO` objects.

### `GET /api/auth/topic/exercises/get_topic_did_by_exercises/{did}`
Resolves the parent topic DID that contains the given exercise DID.

- **Path Parameter:** `did` (UUID) - Exercise identifier.
- **Role:** Authenticated
- **Response `200 OK`:** Topic UUID.

---

## 3. Learning Materials Management

### `GET /api/auth/material/{topic_did}/all_materials`
Retrieves all learning materials linked to a specific topic.

- **Path Parameter:** `topic_did` (UUID) - Topic identifier.
- **Role:** Authenticated
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "Materials found",
  "data": [
    {
      "materialDid": "771e89b2-32a5-4e78-9e6b-0b5c163f9f4a",
      "title": "Factory Pattern Lecture Notes",
      "description": "Overview of Factory and Abstract Factory patterns",
      "materialType": "file",
      "materialFileName": "factory_pattern.pdf"
    }
  ],
  "success": true
}
```

### `GET /api/auth/material/{did}`
Fetches metadata for a single material by its DID.

- **Path Parameter:** `did` (UUID) - Material identifier.
- **Role:** Authenticated
- **Response `200 OK`:** Single `MaterialDTO`.

### `GET /api/auth/material/{did}/download`
Downloads the binary content of a file-based material.

- **Path Parameter:** `did` (UUID) - Material identifier.
- **Role:** Authenticated
- **Headers Returned:** `Content-Disposition: attachment; filename="..."`, `Content-Type: application/octet-stream`
- **Response `200 OK`:** Raw file bytes.
- **Response `400 BAD REQUEST`:** If material is not of type `file`.

### `POST /api/auth/material/{topic_did}/upload_file`
Uploads a new file-based learning material (max file size 20MB).

- **Role:** `INSTRUCTOR`
- **Content-Type:** `multipart/form-data`
- **Form Parameters:**
  - `file`: Multipart file binary.
  - `materialDTO`: JSON string representing `MaterialDTO`:
```json
{
  "title": "Design Patterns Lecture Slides",
  "description": "Lecture slides for week 4",
  "materialType": "file"
}
```
- **Response `201 CREATED`:** Saved `MaterialDTO`.

### `POST /api/auth/material/{topic_did}/upload`
Uploads non-file materials (e.g., links, text references).

- **Role:** `INSTRUCTOR`
- **Request Body:**
```json
{
  "title": "Refactoring Guru: Factory Method",
  "description": "Interactive external reference",
  "materialType": "link",
  "materialFileName": "https://refactoring.guru/design-patterns/factory-method"
}
```
- **Response `201 CREATED`:** Saved `MaterialDTO`.

### `PATCH /api/auth/material/update/{did}`
Updates material metadata and optionally replaces the uploaded file.

- **Role:** `INSTRUCTOR`
- **Content-Type:** `multipart/form-data`
- **Parameters:** `materialDTO` (Part), `file` (Optional Part).
- **Response `200 OK`:** Updated `MaterialDTO`.

### `DELETE /api/auth/material/delete/{did}`
Deletes a material by its DID.

- **Path Parameter:** `did` (UUID) - Material identifier.
- **Role:** `INSTRUCTOR`
- **Response `200 OK`:** Confirmation message.

---

## 4. Exercise Management

### `GET /api/auth/topic/exercises/get_all_exercises_in_topic/{topic_did}`
Retrieves all exercises for a topic. For students, draft exercises are hidden and the `isCompleted` flag indicates student completion.

- **Path Parameter:** `topic_did` (UUID) - Topic identifier.
- **Role:** Authenticated
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "Exercises with topic DID ... have been loaded",
  "data": [
    {
      "exerciseDID": "2d1a3f6b-8b9a-4e2b-bb47-79813b29c972",
      "exerciseTitle": "Creational Patterns Assessment",
      "exerciseDescription": "Test your knowledge on Singleton and Factory patterns",
      "isDraft": false,
      "isCompleted": true
    }
  ],
  "success": true
}
```

### `GET /api/auth/topic/exercises/get_by_exercise_did/{exercise_did}`
Retrieves a specific exercise by its DID.

- **Path Parameter:** `exercise_did` (UUID) - Exercise identifier.
- **Role:** Authenticated
- **Response `200 OK`:** `ExerciseDTO`.

### `POST /api/auth/topic/exercises/create/{topic_did}`
Creates a new exercise under a topic in draft status.

- **Role:** `INSTRUCTOR`
- **Request Body:**
```json
{
  "exerciseTitle": "Structural Patterns Quiz",
  "exerciseDescription": "Adapter and Decorator patterns assessment",
  "questions": []
}
```
- **Response `201 CREATED`:** Created `ExerciseDTO`.

### `POST /api/auth/topic/exercises/search/{topic_did}`
Searches exercises in a topic using title and description keywords.

- **Role:** Authenticated
- **Request Body:**
```json
{
  "titleKeywords": ["Factory"],
  "descriptionKeywords": ["Singleton"],
  "draft": false
}
```
- **Response `200 OK`:** List of matching `ExerciseDTO` objects.

### `PATCH /api/auth/topic/exercises/teacher/change_draft/{exercise_did}`
Changes the publication status of an exercise.

- **Role:** `INSTRUCTOR`
- **Request Body:**
```json
{
  "exerciseIsDraft": false
}
```
- **Response `200 OK`:** Updated `ExerciseDTO`.

### `PATCH /api/auth/topic/exercises/teacher/update_exercise/{exercise_did}`
Updates an exercise's title and description.

- **Role:** `INSTRUCTOR`
- **Request Body:**
```json
{
  "exerciseTitle": "Updated Quiz Title",
  "exerciseDescription": "Updated Quiz Description"
}
```
- **Response `200 OK`:** Updated `ExerciseDTO`.

### `PATCH /api/auth/topic/exercises/teacher/update_exercise_complete/{exercise_did}`
Updates an exercise's title, description, and replaces all question definitions.

- **Role:** `INSTRUCTOR`
- **Request Body:** `CreateExerciseDTO` containing title, description, and `questions` list.
- **Response `200 OK`:** Updated `ExerciseDTO`.

### `DELETE /api/auth/topic/exercises/teacher/delete_exercise/{exercise_did}`
Deletes an exercise by its DID.

- **Role:** `INSTRUCTOR`
- **Response `200 OK`:** Deletion confirmation.

### `DELETE /api/auth/topic/exercises/teacher/delete_all_exercises_in_topic/{topic_did}`
Deletes all exercises within a topic.

- **Role:** `INSTRUCTOR`
- **Response `200 OK`:** Deletion confirmation.

---

## 5. Question & Variant Management

Questions are polymorphic and can be one of three types:
- `multiChoice`: Multiple choice question with choices array and index of correct option.
- `trueFalse`: Binary question with boolean `correctAnswer`.
- `shortAnswer`: Open question with list of acceptable answer strings.

### `GET /api/auth/topic/exercises/question/attempt/{attempt_did}`
Retrieves all randomized questions for an active student attempt.

- **Path Parameter:** `attempt_did` (UUID) - Attempt identifier.
- **Role:** Authenticated
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "List of all randomized questions",
  "data": [
    {
      "type": "multiChoice",
      "questionDid": "9f3e1a2b-c4d5-4678-8901-abcdef123456",
      "questionTitle": "Which pattern ensures only one instance of a class exists?",
      "choices": ["Factory", "Observer", "Singleton", "Adapter"],
      "exerciseDid": "2d1a3f6b-8b9a-4e2b-bb47-79813b29c972",
      "correctAnswer": 2,
      "variantDid": "11223344-5566-7788-99aa-bbccddeeff00"
    }
  ],
  "success": true
}
```

### `GET /api/auth/topic/exercises/question/exercise/{exercise_did}`
Retrieves all questions configured for an exercise.

- **Role:** `INSTRUCTOR`
- **Response `200 OK`:** List of `QuestionDTO` objects.

### `DELETE /api/auth/topic/exercises/question/delete/{question_did}`
Deletes a question by its DID.

- **Role:** `INSTRUCTOR`
- **Response `200 OK`:** Deletion confirmation.

### `POST /api/auth/topic/exercises/question/save_all/{exercise_did}`
Bulk saves a list of questions for an exercise.

- **Role:** `INSTRUCTOR`
- **Request Body:** List of `QuestionDTO` objects.
- **Response `200 OK`:** Save confirmation.

### `PATCH /api/auth/topic/exercises/question/update/{exercise_did}`
Reorders questions or swaps question variants.

- **Role:** `INSTRUCTOR`
- **Request Body:** `SwapIndexQuestionsDTO` containing `questionDTOs` and variant swap `pairs`.
- **Response `200 OK`:** Update confirmation.

---

## 6. Attempt Management

### `GET /api/auth/topic/exercises/attempts`
Retrieves all attempts in the platform.

- **Role:** Authenticated
- **Response `200 OK`:** List of `AttemptDTO` objects.

### `GET /api/auth/topic/exercises/attempts/get_all_by_user`
Retrieves all attempts made by the currently authenticated user.

- **Role:** Authenticated
- **Response `200 OK`:** List of `AttemptDTO` objects.

### `GET /api/auth/topic/exercises/attempts/get_all_by_exercise/{exercise_did}`
Retrieves all attempts submitted for a specific exercise.

- **Role:** Authenticated
- **Response `200 OK`:** List of `AttemptDTO` objects.

### `GET /api/auth/topic/exercises/attempts/get_all_by_user_exercise/{exercise_did}`
Retrieves all attempts made by the currently authenticated user on a specific exercise.

- **Role:** Authenticated
- **Response `200 OK`:** List of `AttemptDTO` objects.

### `GET /api/auth/topic/exercises/attempts/get_by_did/{did}`
Retrieves attempt metadata by its DID.

- **Role:** Authenticated
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "Attempt founds",
  "data": {
    "attemptDID": "6c4e2a1b-3f5d-4e78-9a01-bcdef1234567",
    "attemptIsSubmitted": true,
    "attemptIsCompleted": true,
    "exerciseDid": "2d1a3f6b-8b9a-4e2b-bb47-79813b29c972"
  },
  "success": true
}
```

### `POST /api/auth/topic/exercises/attempts/create/{exercise_did}`
Starts a new attempt on an exercise.

- **Role:** Authenticated
- **Response `201 CREATED`:** Created `AttemptDTO`.

### `PATCH /api/auth/topic/exercises/attempts/update/{did}`
Finalizes and submits an attempt. Triggers automated score evaluation.

- **Role:** Authenticated
- **Response `200 OK`:** Updated `AttemptDTO` with `attemptIsSubmitted: true`.

### `DELETE /api/auth/topic/exercises/attempts/delete/{did}`
Deletes an attempt by its DID.

- **Role:** Authenticated
- **Response `200 OK`:** Deletion confirmation.

---

## 7. Answer Management

### `GET /api/auth/topic/exercises/answers/get/{attempt_did}/{question_did}`
Retrieves a student's answer for a specific question within an attempt.

- **Role:** Authenticated
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "Answer successfully retrieved",
  "data": {
    "did": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
    "answerContent": "2",
    "attemptDid": "6c4e2a1b-3f5d-4e78-9a01-bcdef1234567",
    "questionDid": "9f3e1a2b-c4d5-4678-8901-abcdef123456"
  },
  "success": true
}
```

### `GET /api/auth/topic/exercises/answers/getall/{attempt_did}`
Retrieves all student answers associated with an attempt.

- **Role:** Authenticated
- **Response `200 OK`:** List of `AnswerDTO` objects.

### `POST /api/auth/topic/exercises/answers/create/{attempt_did}/{question_did}`
Creates and records an answer for a question in an attempt.

- **Role:** Authenticated
- **Request Body:**
```json
{
  "answerContent": "Singleton"
}
```
- **Response `201 CREATED`:** Created `AnswerDTO`.

### `POST /api/auth/topic/exercises/answers/update/{did}`
Updates the answer content for an existing answer.

- **Role:** Authenticated
- **Request Body:** `AnswerDTO` containing updated `answerContent`.
- **Response `200 OK`:** Updated `AnswerDTO`.

### `DELETE /api/auth/topic/exercises/answers/delete/{did}`
Deletes an answer by its DID.

- **Role:** Authenticated
- **Response `200 OK`:** Deletion confirmation.

---

## 8. Feedback

### `GET /api/auth/feedback/{attempt_did}`
Calculates and returns the grading summary, score percentage, and per-question evaluation for a submitted attempt.

- **Path Parameter:** `attempt_did` (UUID) - Identifier of the submitted attempt.
- **Role:** Authenticated
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "Attempt feedback calculated",
  "data": {
    "totalCorrect": 4,
    "totalQuestions": 5,
    "percentage": 80.0,
    "feedbackData": [
      {
        "questionDid": "9f3e1a2b-c4d5-4678-8901-abcdef123456",
        "questionTitle": "Which pattern ensures only one instance of a class exists?",
        "userAnswer": "2",
        "correctAnswer": 2,
        "isCorrect": true
      }
    ]
  },
  "success": true
}
```

---

## 9. AI Tutoring & Question Explanations

Powered by **Spring AI** connecting to local **Ollama** (`deepseek-llm`).

### `POST /api/auth/AI/ask/materials/{material_did}`
Students can highlight text in a learning material and submit an AI question about it.

- **Role:** `STUDENT` only
- **Request Body:**
```json
{
  "highlightedText": "The Abstract Factory interface declares a set of methods for creating each of the abstract products.",
  "question": "Can you explain the difference between Factory Method and Abstract Factory with a code example?"
}
```
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "Question asked",
  "data": {
    "aiResponseDid": "44332211-5566-7788-99aa-bbccddeeff00",
    "isPublic": false,
    "rating": 0,
    "userRating": 0,
    "answer": "Factory Method uses inheritance to decide which object to instantiate, whereas Abstract Factory..."
  },
  "success": true
}
```

### `PATCH /api/auth/AI/response/rate/{response_did}`
Rate an AI response (1 to 5).

- **Role:** `STUDENT`
- **Request Body:**
```json
{
  "rate": 5
}
```
- **Response `200 OK`:** Updated response DTO with new average rating.

### `GET /api/auth/AI/response`
Lists all generated AI responses in the system.

- **Role:** `INSTRUCTOR` only
- **Response `200 OK`:** List of `AiAbstractResponseDTO`.

### `PATCH /api/auth/AI/response/publish/{response_did}`
Publishes an AI response so that all students can view it.

- **Role:** `INSTRUCTOR` only
- **Response `200 OK`:** Response DTO with `isPublic: true`.

### `PATCH /api/auth/AI/response/public/{material_did}`
Retrieves all publicly visible AI responses associated with a specific material.

- **Role:** Authenticated
- **Response `200 OK`:** List of public `AiAbstractResponseDTO`.

### `GET /api/auth/AI/response/mine`
Retrieves all AI queries and answers asked by the currently authenticated student.

- **Role:** `STUDENT` only
- **Response `200 OK`:** List of student's own `AiAbstractResponseDTO`.

### `POST /api/auth/AI/generate/question_variant`
Generates question variations using the LLM for instructor review.

- **Role:** `INSTRUCTOR` only
- **Request Body:** List of existing `QuestionDTO` objects.
- **Response `200 OK`:** List of generated question variant objects.

### `GET /api/auth/AI/responses/{exercise_did}/all_generated_AI_explanations`
Retrieves all AI explanations generated for the questions and answers of an exercise.

- **Role:** `INSTRUCTOR` only
- **Response `200 OK`:** List of `QuestionAndUserAnswerDTO` objects.

### `GET /api/auth/AI/ask/questions/explanation/new/{answer_did}`
Generates a new AI explanation explaining why a student's answer was correct or incorrect.

- **Role:** `STUDENT` only
- **Response `200 OK`:** `AiAbstractResponseDTO` with detailed explanation.

### `GET /api/auth/AI/ask/questions/explanation/refresh/{answer_did}`
Requests the AI to regenerate/refresh an explanation for an answer.

- **Role:** `STUDENT` only
- **Response `200 OK`:** Refreshed `AiAbstractResponseDTO`.

### `GET /api/auth/AI/ask/questions/explanation/published/{answer_did}`
Retrieves published explanations for a specific answer.

- **Role:** Authenticated
- **Response `200 OK`:** List of public `AiAbstractResponseDTO`.

---

## 10. AI Evaluation Management

Instructors can review, approve, or deny AI evaluations before they become permanent answers.

### `GET /api/auth/AI_Evaluation/get_AI_evaluations/{exercise_did}`
Retrieves all pending AI evaluations awaiting instructor moderation for an exercise.

- **Role:** `INSTRUCTOR` only
- **Response `200 OK`:** List of `AIEvalAndQuestionDTO` records.

### `PATCH /api/auth/AI_Evaluation/accept_evaluation/{AI_evaluation_did}`
Approves an AI evaluation, creating an official AI exercise response.

- **Role:** `INSTRUCTOR` only
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "Evaluation accepted",
  "data": "New did is: 99887766-5544-3322-1100-aabbccddeeff",
  "success": true
}
```

### `DELETE /api/auth/AI_Evaluation/deny_evaluation/{AI_evaluation_did}`
Rejects and deletes a pending AI evaluation.

- **Role:** `INSTRUCTOR` only
- **Response `200 OK`:** Deletion confirmation.

---

## 11. AI Response Comments

Instructors can leave feedback comments on AI-generated responses.

### `POST /api/auth/AI/responses/comment/{response_did}`
Adds a comment to an AI response.

- **Role:** `INSTRUCTOR` only
- **Request Body:**
```json
{
  "comment": "Accurate explanation, but note that modern implementations often use dependency injection instead of singletons."
}
```
- **Response `201 CREATED`:** Returns generated comment UUID.

### `DELETE /api/auth/AI/responses/comment/{comment_did}/delete`
Deletes a comment.

- **Role:** `INSTRUCTOR` only
- **Response `200 OK`:** Deletion confirmation.

### `GET /api/auth/AI/responses/comment/{responseDid}/allComments`
Retrieves all comments on an AI response.

- **Role:** Authenticated
- **Response `200 OK`:**
```json
{
  "status": "success",
  "status_code": 201,
  "message": "Comments found successfully",
  "data": [
    {
      "commentDid": "33221100-4455-6677-8899-aabbccddeeff",
      "instructorName": "Prof. Smith",
      "isCallerTheOwner": true,
      "commentContent": "Accurate explanation...",
      "timeStamp": "2026-10-21T12:00:00"
    }
  ],
  "success": true
}
```

---

## 12. Statistics & Analytics

### `GET /api/auth/statistics/general_view_statistics`
Role-aware profile dashboard statistics:
- **For Students:** Returns `StatisticsStudentDTO` containing `minGrade`, `avgGrade`, `maxGrade`, and per-topic statistics.
- **For Instructors:** Returns `StatisticsInstructorDTO` containing cohort statistics across all topics.

- **Role:** Authenticated
- **Response `200 OK` (Student View Example):**
```json
{
  "status": "success",
  "status_code": 200,
  "message": "Statistics uploaded",
  "data": {
    "minGrade": 4.5,
    "avgGrade": 5.2,
    "maxGrade": 6.0,
    "topics": [
      {
        "topicDid": "8a3a2d5e-6c84-4861-8ff6-77864f1d01b1",
        "topicTitle": "Design Patterns",
        "avgGrade": 5.5,
        "completedExercises": 3,
        "totalExercises": 4
      }
    ]
  },
  "success": true
}
```

### `GET /api/auth/statistics/general_view_statistics/{user_did}`
Retrieves the general statistics view for a specific student DID.

- **Role:** Authenticated
- **Response `200 OK`:** `StatisticsStudentDTO`.

### `GET /api/auth/statistics/topic/{topic_did}`
Retrieves statistics for exercises under a given topic. Tailored based on the requester's role.

- **Role:** Authenticated
- **Response `200 OK`:** `TopicExercisesStatsDTO`.

### `GET /api/auth/statistics/topic/{topic_did}/{user_did}`
Retrieves topic exercise statistics for a specific user.

- **Role:** Authenticated
- **Response `200 OK`:** `TopicExercisesStatsDTO`.

### `GET /api/auth/statistics/exercise/{exercise_did}`
Detailed statistics for a single exercise.
- **For Instructors:** Returns `ExerciseStatsDTO` with `exerciseTitle`, `avgUsersGrade`, `studentsWhoAttempted`, and per-question success breakdown (`questions`).
- **For Students:** Returns `StudentGradeDTO` with `userGrade`.

- **Role:** Authenticated
- **Response `200 OK`:** `ExerciseStatsDTO` or `StudentGradeDTO`.
