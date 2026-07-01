# API Endpoints with Role-Based Access Control

**API TYPES**

- The API have 2 different basic types now. One is `/auth/` and the other is `/public` . Later we can have more detailed role control for those APIs.

- APIs that require the user to log in before they can be used should start with x  `/auth` 

- APIs that do not require user login should start with `/public`

**Demo**

You can try with the test api `localhost:8080/auth/me`

## User Management
| ID  | Method  | Endpoint                   | Description                                  | Role               |
|---- |-------- |--------------------------|----------------------------------------------|--------------------|
| 1   | POST    | `/api/user/login`        | User login via SWITCH Edu-ID credentials   | All Users         |
| 2   | POST    | `/api/user/logout`       | User logout                                | All Users         |
| 3   | GET     | `/api/user/profile`      | Fetch user profile (USI identity & stats)  | All Users         |
| 4   | GET     | `/api/user/role`         | Get user role (instructor, student, etc.)  | All Users         |

## Topics & Materials Management
| ID  | Method  | Endpoint                        | Description                                      | Role               |
|---- |-------- |--------------------------------|------------------------------------------------|--------------------|
| 5   | GET     | `/api/topic`                   | Get list of topics (with completion stage for students) | All Users         |
| 6   | GET     | `/api/topic/materials`         | Get materials in a topic                        | All Users         |
| 7   | POST    | `/api/topic/materials/add`     | Add new materials (e.g., links, PDFs)           | Instructor/Teacher |
| 8   | POST    | `/api/topic/materials/edit`    | Edit existing materials                        | Instructor/Teacher |
| 9   | DELETE  | `/api/topic/materials/delete`  | Delete materials from a topic                  | Instructor/Teacher |

## Exercise Management
| ID  | Method  | Endpoint                           | Description                                                  | Role               |
|---- |-------- |----------------------------------|--------------------------------------------------------------|--------------------|
| 10  | GET     | `/api/topic/exercises`          | Get list of exercises in a topic                            | All Users         |
| 11  | GET     | `/api/topic/exercises/attempts` | View past exercise attempts and feedback                    | Student           |
| 12  | POST    | `/api/topic/exercises/attempt`  | Attempt an exercise (new or retry)                          | Student           |
| 13  | GET     | `/api/topic/exercises/stats`    | View exercise attempt statistics                            | Instructor/Teacher |
| 14  | POST    | `/api/topic/exercises/add`      | Add a new exercise (initially in draft status)              | Instructor/Teacher |
| 15  | POST    | `/api/topic/exercises/edit`     | Edit an exercise (only in draft status)                     | Instructor/Teacher |
| 16  | GET     | `/api/topic/exercises/preview`  | Preview an exercise before making it available              | Instructor/Teacher |
| 17  | POST    | `/api/topic/exercises/publish`  | Remove draft status and make an exercise available          | Instructor/Teacher |
| 18  | DELETE  | `/api/topic/exercises/delete`   | Delete an exercise (only if not yet attempted by students)  | Instructor/Teacher |

## Additional Functionalities
| ID  | Method  | Endpoint                 | Description                          | Role               |
|---- |-------- |------------------------|--------------------------------------|--------------------|
| 19  | GET     | `/api/topic/state`      | Get the current state of a topic    | All Users         |

## Notes:
- **Students**:
    - Can **view topics and materials**.
    - Can **view and attempt exercises** (including retries).
    - Can **see feedback** from past attempts.
- **Instructors/Teachers**:
    - Can **add, edit, and delete** topics, materials, and exercises.
    - Can **preview exercises** before publishing.
    - Can **publish exercises** (removing draft status).
    - Can **view statistics** on student attempts.


## 1. User Login

- **Endpoint**: `/api/user/login`
- **Method**: `POST`
- **Description**: User login via SWITCH Edu-ID credentials.

### Request Body

| Field    | Type   | Required | Description                          |
|----------|--------|----------|--------------------------------------|
| username | String | No       | User's username (if provided)        |
| email    | String | No       | User's email address (if provided)   |
| password | String | Yes      | User's password                      |

### Example Request

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```
### Response Example

- **Status Code**: `200 OK`

```json
{
  "status": "success",
  "status_code": 200,
  "message": "User logged in successfully.",
  "data": {
    "userid": 1,
    "username": "john_doe",
    "email": "john@example.com"
    }
}
```

- **Status Code**: `400 Bad Request`

```json
{
  "status": "error",
  "status_code": 400,
  "message": "Invalid credentials.",
  "data": null
}
```

## 2. Fetch List of Topics
- **Endpoint**: `/api/topic`
- **Method**: `GET`
- **Description**: Get list of topics (with completion stage for students)

### Response Example

- **Status Code**: `200 OK`

```json
{
  "status": "success",
  "status_code": 200,
  "message": "",
  "data": [
    {
      "topic_id": 1,
      "topic_name": "Introduction",
      "completion": "50%"
    },
    {
      "topic_id": 2,
      "topic_name": "Abstract Data Types",
      "completion": "25%"
    }
  ]
}
```

## 3. Add New Materials
- **Endpoint**: `/api/topic/materials/add`
- **Method**: `POST`
- **Description**: Add new materials (e.g., links, PDFs) to a topic.

### Request Body

| Field        | Type   | Required | Description                           |
|--------------|--------|----------|---------------------------------------|
| topic_id     | Number | Yes      | The ID of the topic                   |
| material     | String | Yes      | The link or URL of the material       |
| material_type| String | Yes      | Type of material (PDF, URL, etc.)     |

### Example Request

```json
{
  "topic_id": 1,
  "material": "https://example.com/introduction.pdf",
  "material_type": "PDF"
}
```
### Response Example
**Status Code**: `201 Created`

```json
{
  "status": "success",
  "status_code": 201,
  "message": "Material added successfully.",
  "data": {
    "material_id": 101,
    "topic_id": 1,
    "material": "https://example.com/introduction.pdf",
    "material_type": "PDF"
  }
}
```

## 4. Attempt an Exercise
- **Endpoint**: `/api/topic/exercises/attempt`
- **Method**: `POST`
- **Description**: Attempt an exercise (new or retry)

### Request Body
| Field       | Type   | Required | Description                          |
|-------------|--------|----------|--------------------------------------|
| exercise_id | Number | Yes      | The ID of the exercise               |
| answers     | Array  | Yes      | List of answers provided by the user |

### Example Request

```json
{
  "exercise_id": 1,
  "answers": ["A", "B", "C"]
}
```
### Response Example

- **Status Code**: `200 OK`

```json
{
  "status": "success",
  "status_code": 200,
  "message": "Exercise attempted successfully.",
  "data": {
    "exercise_id": 1,
    "attempt_id": 101,
    "answers": ["A", "B", "C"],
    "score": 75
  }
}
```

## 5. Get Exercise Attempt Statistics
- **Endpoint**: `/api/topic/exercises/stats`
- **Method**: `GET`
- **Description**: View exercise attempt statistics

### Response Example

- **Status Code**: `200 OK`

```json
{
  "status": "success",
  "status_code": 200,
  "message": "",
  "data": {
    "exercise_id": 1,
    "average_score": 80,
    "highest_score": 100,
    "lowest_score": 60
  }
}
```
