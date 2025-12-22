<template>
  <div
    class="relative flex min-h-screen flex-col items-center justify-center overflow-hidden bg-linear-to-br from-blue-50 via-white to-purple-50 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900"
  >
    <!-- Animated Background Elements -->
    <div class="absolute inset-0 overflow-hidden opacity-30">
      <div
        class="animate-blob absolute -top-40 -right-40 h-80 w-80 rounded-full bg-linear-to-br from-blue-400 to-purple-400 blur-3xl"
      ></div>
      <div
        class="animate-blob animation-delay-2000 absolute -bottom-40 -left-40 h-80 w-80 rounded-full bg-linear-to-br from-purple-400 to-pink-400 blur-3xl"
      ></div>
      <div
        class="animate-blob animation-delay-4000 absolute top-1/2 left-1/2 h-80 w-80 -translate-x-1/2 -translate-y-1/2 rounded-full bg-linear-to-br from-blue-300 to-cyan-400 blur-3xl"
      ></div>
    </div>

    <!-- Main Content -->
    <div class="relative z-10 w-full max-w-md px-6">
      <!-- Logo/Brand Section -->
      <div class="mb-8 text-center">
        <div
          class="mb-4 inline-flex transform items-center justify-center rounded-2xl bg-linear-to-br from-blue-500 to-purple-600 p-4 shadow-2xl transition-transform hover:scale-105"
        >
          <svg class="h-12 w-12 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9"
            />
          </svg>
        </div>
        <h1
          class="mb-2 bg-linear-to-r from-blue-600 to-purple-600 bg-clip-text text-4xl font-bold text-transparent dark:from-blue-400 dark:to-purple-400"
        >
          SEWorld
        </h1>
        <p class="text-sm text-gray-600 dark:text-gray-400">
          Your Software Engineering Learning Platform
        </p>
      </div>

      <!-- Login Card -->
      <div class="group relative">
        <!-- Glow Effect -->
        <div
          class="absolute -inset-0.5 rounded-2xl bg-linear-to-r from-blue-500 to-purple-600 opacity-20 blur transition duration-1000 group-hover:opacity-30"
        ></div>

        <!-- Card Content -->
        <div
          class="relative rounded-2xl border border-gray-200 bg-white/80 p-8 shadow-2xl backdrop-blur-xl dark:border-gray-700 dark:bg-gray-800/80"
        >
          <h2 class="mb-2 text-center text-2xl font-bold text-gray-800 dark:text-gray-100">
            Welcome Back
          </h2>
          <p class="mb-8 text-center text-sm text-gray-600 dark:text-gray-400">
            Sign in to continue your learning journey
          </p>

          <div class="flex flex-col gap-3">
            <!-- Google Login -->
            <button
              @click="loginWith('google')"
              :disabled="isLoading"
              class="group/btn dark:hover:bg-gray-650 relative flex w-full items-center justify-center gap-3 rounded-xl border border-gray-300 bg-white px-4 py-3 font-medium text-gray-800 transition-all duration-200 hover:-translate-y-0.5 hover:shadow-lg focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:outline-none disabled:cursor-not-allowed disabled:opacity-50 dark:border-gray-600 dark:bg-gray-700 dark:text-gray-200"
            >
              <svg class="h-5 w-5" viewBox="0 0 24 24">
                <path
                  fill="#4285F4"
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                />
                <path
                  fill="#34A853"
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                />
                <path
                  fill="#FBBC05"
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                />
                <path
                  fill="#EA4335"
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                />
              </svg>
              <span>Continue with Google</span>
            </button>

            <!-- GitHub Login -->
            <button
              @click="loginWith('github')"
              :disabled="isLoading"
              class="group/btn relative flex w-full items-center justify-center gap-3 rounded-xl border border-gray-800 bg-gray-900 px-4 py-3 font-medium text-white transition-all duration-200 hover:-translate-y-0.5 hover:bg-gray-800 hover:shadow-lg focus:ring-2 focus:ring-gray-700 focus:ring-offset-2 focus:outline-none disabled:cursor-not-allowed disabled:opacity-50 dark:border-gray-700"
            >
              <svg class="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                <path
                  d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"
                />
              </svg>
              <span>Continue with GitHub</span>
            </button>

            <!-- Divider -->
            <div class="relative my-2">
              <div class="absolute inset-0 flex items-center">
                <div class="w-full border-t border-gray-300 dark:border-gray-600"></div>
              </div>
              <div class="relative flex justify-center text-sm">
                <span class="bg-white px-4 text-gray-500 dark:bg-gray-800 dark:text-gray-400">
                  or
                </span>
              </div>
            </div>

            <!-- University Login -->
            <button
              @click="loginWith('switch-edu-id')"
              :disabled="isLoading"
              class="group/btn relative flex w-full items-center justify-center gap-3 rounded-xl border-2 border-blue-600 bg-linear-to-r from-blue-50 to-purple-50 px-4 py-3 font-semibold text-blue-700 transition-all duration-200 hover:-translate-y-0.5 hover:from-blue-600 hover:to-purple-600 hover:text-white hover:shadow-lg focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 focus:outline-none disabled:cursor-not-allowed disabled:opacity-50 dark:from-blue-900/30 dark:to-purple-900/30 dark:text-blue-400 dark:hover:from-blue-600 dark:hover:to-purple-600 dark:hover:text-white"
            >
              <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                />
              </svg>
              <span>University Login</span>
              <svg
                class="h-5 w-5 transition-transform group-hover/btn:translate-x-1"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13 7l5 5m0 0l-5 5m5-5H6"
                />
              </svg>
            </button>
          </div>

          <!-- Footer -->
          <div class="mt-8 text-center">
            <p class="text-xs text-gray-500 dark:text-gray-400">
              By continuing, you agree to SEWorld's
              <a href="#" class="text-blue-600 hover:underline dark:text-blue-400"
                >Terms of Service</a
              >
              and
              <a href="#" class="text-blue-600 hover:underline dark:text-blue-400"
                >Privacy Policy</a
              >
            </p>
          </div>
        </div>
      </div>

      <!-- Additional Info -->
      <!-- <div class="mt-8 text-center"> -->
      <!--   <p class="text-sm text-gray-600 dark:text-gray-400"> -->
      <!--     New to SEWorld? -->
      <!--     <a href="#" class="font-semibold text-blue-600 hover:underline dark:text-blue-400"> -->
      <!--       Learn more -->
      <!--     </a> -->
      <!--   </p> -->
      <!-- </div> -->
    </div>
  </div>
</template>

<script>
const BASE_AUTH_URL = 'http://localhost:8080/oauth2/authorization/'

export default {
  name: 'LogIn',
  data() {
    return {
      isLoading: false,
    }
  },
  methods: {
    loginWith(provider) {
      this.isLoading = true
      window.location.href = `${BASE_AUTH_URL}${provider}`
    },
  },
}
</script>

<style scoped>
@keyframes blob {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

.animate-blob {
  animation: blob 7s infinite;
}

.animation-delay-2000 {
  animation-delay: 2s;
}

.animation-delay-4000 {
  animation-delay: 4s;
}
</style>
