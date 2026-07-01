// URLs
// Base URL
export const BASE_URL = 'http://localhost:8080';

// Auth path
export const BASE_AUTH_URL = '/api/auth';

// Naming: GET/POST/PATCH _ ENTITY _ ...DESCRIPTIO... _ ROUTE [_ ...BY _ FIELD...]
// [] := zero or more
// too much?

// Attempt Routes
export const GET_EXERCISE_BY_ATTEMPT_DID_ROUTE = `${BASE_AUTH_URL}/topic/exercises/question/attempt`;
export const PATCH_UPDATE_ATTEMPT_AS_SUBMITTED = `${BASE_AUTH_URL}/topic/exercises/attempts/update`;
export const CREATE_ANSWER_ROUTE = `${BASE_AUTH_URL}/topic/exercises/answers/create`;
export const GET_FEEDBACK_ROUTE = `${BASE_AUTH_URL}/feedback`;
export const GET_ATTEMPT_STATUS_ROUTE = `${BASE_AUTH_URL}/topic/exercises/attempts/get_by_did/`;
export const GET_EXERCISE_BY_EXERCISE_DID = `${BASE_AUTH_URL}/topic/exercises/get_by_exercise_did`;
export const DELETE_ATTEMPT_BY_ATTEMPT_DID = `${BASE_AUTH_URL}/topic/exercises/attempts/delete`;

// Exercise Routes
export const GET_QUESTIONS_BY_EXERCISE_DID = `${BASE_AUTH_URL}/topic/exercises/question/exercise/{exercise_did}`;
export const GET_EXERCISE_ROUTE_BY_EXERCISE_DID = `${BASE_AUTH_URL}/topic/exercises/get_by_exercise_did/{exercise_did}`;

// User Routes
export const GET_USER_ME_ROUTE = `${BASE_URL}${BASE_AUTH_URL}/me`;
export const GET_INSTRUCTOR_ME_ROUTE = `${BASE_URL}${BASE_AUTH_URL}/instructor/me`;
export const GET_ALL_USERS_ROUTE = `${BASE_URL}${BASE_AUTH_URL}/all`;
export const GET_USER_ROUTE_BY_USER_DID = `${BASE_URL}${BASE_AUTH_URL}/{user_did}`;
export const GET_USER_ROUTE_BY_USERNAME_SEARCH = `${BASE_URL}${BASE_AUTH_URL}/search?query={user_name}`;

// Topics Routes
export const GET_LIST_OF_TOPICS_ROUTE = `${BASE_URL}/api/auth/topic`;
export const GET_TOPICS_COMPLETION_ROUTE = `${BASE_URL}/api/auth/topic/completion`;

// Material Routes
export const AUTH_GET_MATERIAL = `${BASE_URL}/api/auth/material`;
export const DOWNLOAD_TOPIC_MATERIAL = `${BASE_URL}/api/auth/material`;
export const GET_ALL_EXERCISES_IN_TOPIC = `${BASE_URL}/api/auth/topic/exercises/get_all_exercises_in_topic`;
export const AUTH_DELETE_MATERIAL = `${BASE_URL}/api/auth/material/delete`;

// Attempts routes
export const CREATE_NEW_ATTEMPT_BY_EXERCISE_DID = `${BASE_URL}${BASE_AUTH_URL}/topic/exercises/attempts/create/{exercise_did}`;
export const GET_ALL_ATTEMPTS_FOR_USER_BY_EXERCISE_DID = `${BASE_URL}${BASE_AUTH_URL}/topic/exercises/attempts/get_all_by_user_exercise/{exercise_did}`;
export const GET_ALL_ATTEMPTS_FOR_USER_BY_ATTEMPT_DID = `${BASE_URL}${BASE_AUTH_URL}/topic/exercises/attempts/get_by_did/{did}`;
export const DELETE_ATTEMPT = `${BASE_AUTH_URL}/topic/exercises/attempts/delete`;

// Statistic Routes
export const GET_STATISTICS_GENERAL_VIEW_ROUTE = `${BASE_URL}/api/auth/statistics/general_view_statistics`;
export const GET_STATISTICS_GENERAL_VIEW_ROUTE_BY_USER_DID = `${BASE_URL}/api/auth/statistics/general_view_statistics/{user_did}`;
export const GET_STATISTICS_TOPIC_EXERCISES_ROUTE_BY_TOPIC_DID = `${BASE_URL}/api/auth/statistics/topic/{topic_did}`;
export const GET_STATISTICS_SINGLE_EXERCISE = `${BASE_URL}/api/auth/statistics/exercise`;
export const GET_STATISTICS_TOPIC_EXERCISES_ROUTE_BY_TOPIC_DID_USER_DID = `${BASE_URL}/api/auth/statistics/topic/{topic_did}/{user_did}`;

// AI routes
export const GET_ALL_AI_RESPONSES_ROUTE = `${BASE_URL}/api/auth/AI/response`;
export const GET_ALL_USER_AI_RESPONSES_ROUTE = `${BASE_URL}/api/auth/AI/response/mine`;
export const PATCH_PUBLISH_AI_RESPONSE_ROUTE_BY_RESPONSE_DID = `${BASE_URL}/api/auth/AI/response/publish/{response_did}`;
export const PATCH_AI_RESPONSE_RATING = `${BASE_AUTH_URL}/AI/response/rate/{response_did}`;

export const GET_AI_EVALUATIONS = `/api/auth/AI_Evaluation/get_AI_evaluations`;
export const ACCEPT_AI_EVALUATION = `${BASE_AUTH_URL}/AI_Evaluation/accept_evaluation`;
export const DENY_AI_EVALUATION = `${BASE_AUTH_URL}/AI_Evaluation/deny_evaluation`;

// Comment routes
export const GET_ALL_COMMENTS_ROUTE_BY_RESPONSE_DID = `${BASE_URL}/api/auth/AI/responses/comment/{response_did}/allComments`;
export const POST_COMMENT_ROUTE_BY_RESPONSE_DID = `${BASE_URL}/api/auth/AI/responses/comment/{response_did}`;
export const DELETE_COMMENT_ROUTE_BY_COMMENT_DID = `${BASE_URL}/api/auth/AI/responses/comment/{comment_did}/delete`;

// CSS constants
export const WHITE = '#ffffff';
export const PRIMARY_BG_COLOR = '#0a192f';
export const PRIMARY_TEXT_COLOR = '#e6f1ff';
export const SECONDARY_TEXT_COLOR = '#8892b0';
export const ACCENT_COLOR_1 = '#ee7724';
export const ACCENT_COLOR_1_BRIGHTER = '#f4a46d';
export const ACCENT_COLOR_2 = '#dd3675';
export const ACCENT_COLOR_3 = '#b44593';
export const AVERAGE_COLOR = '#497D74';

export const HEADER_HEIGHT = '100px';

// DB Roles
export const ROLE_INSTRUCTOR = 'INSTRUCTOR';
export const ROLE_STUDENT = 'STUDENT';
