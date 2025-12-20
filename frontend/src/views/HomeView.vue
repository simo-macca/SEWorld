<template>
  <div class="flex min-h-[80vh] flex-col items-center justify-center">
    <div
      class="w-full max-w-sm rounded-lg border border-gray-200 bg-white p-6 shadow-md dark:border-gray-700 dark:bg-gray-800"
    >
      <h2 class="mb-6 text-center text-2xl font-bold text-gray-800 dark:text-gray-100">
        Welcome Back
      </h2>
      <div class="flex flex-col gap-4">
        <button
          @click="loginWith('google')"
          class="flex w-full items-center justify-center rounded border border-gray-300 bg-gray-100 px-4 py-2 text-gray-800 transition-colors hover:bg-gray-200"
        >
          <i class="fab fa-google mr-2"></i> Login with Google
        </button>

        <button
          @click="loginWith('github')"
          class="flex w-full items-center justify-center rounded border border-transparent bg-gray-900 px-4 py-2 text-white transition-colors hover:bg-gray-800"
        >
          <i class="fab fa-github mr-2"></i> Login with GitHub
        </button>

        <button
          @click="loginWith('switch-edu-id')"
          class="flex w-full items-center justify-center rounded border border-blue-600 bg-transparent px-4 py-2 text-blue-600 transition-colors hover:bg-blue-50 dark:border-blue-400 dark:text-blue-400 dark:hover:bg-gray-700"
        >
          Uni Login
        </button>
      </div>
    </div>
  </div>
</template>

<script>
const BASE_AUTH_URL = 'http://localhost:8080/oauth2/authorization/'

export default {
  name: 'LogIn',
  methods: {
    loginWith(provider) {
      window.location.href = `${BASE_AUTH_URL}${provider}`
    },
  },
}
</script>
