<script setup>
import { BookOpen, Target, Users, Code2, CheckCircle2, Sparkles } from 'lucide-vue-next'

const topics = [
  {
    id: 1,
    name: 'Requirements Engineering',
    icon: Target,
    description: 'Master the art of gathering and documenting software requirements',
  },
  {
    id: 2,
    name: 'Software Specifications',
    icon: BookOpen,
    description: 'Learn to create precise and comprehensive software specifications',
  },
  {
    id: 3,
    name: 'Black Box Testing',
    icon: CheckCircle2,
    description: 'Develop skills in testing software without knowledge of internal code',
  },
]

const features = [
  {
    title: 'For Students',
    description: 'Structured learning paths with hands-on exercises and real-world examples',
    icon: BookOpen,
  },
  {
    title: 'For Educators',
    description: 'Comprehensive teaching resources and curriculum-aligned materials',
    icon: Users,
  },
  {
    title: 'For Enthusiasts',
    description: 'Self-paced learning with practical projects and continuous updates',
    icon: Code2,
  },
]
</script>

<template>
  <main
    class="min-h-screen bg-linear-to-br from-blue-50 to-cyan-50 dark:from-gray-900 dark:to-gray-900"
  >
    <!-- Hero Section -->
    <section class="relative overflow-hidden px-4 py-16 sm:px-6 lg:px-8">
      <div class="absolute inset-0 -z-10">
        <div
          class="absolute top-0 left-1/2 h-96 w-96 -translate-x-1/2 rounded-full bg-linear-to-r from-blue-400/20 to-cyan-400/20 blur-3xl"
        ></div>
      </div>

      <div class="mx-auto max-w-6xl">
        <div class="text-center">
          <div
            class="mb-6 inline-flex items-center gap-2 rounded-full bg-blue-100 px-4 py-2 text-sm font-semibold text-blue-700 dark:bg-blue-900/30 dark:text-blue-300"
          >
            <Sparkles class="h-4 w-4" />
            <span>Empowering Future Engineers</span>
          </div>

          <h1
            class="mb-6 bg-linear-to-r from-blue-600 via-cyan-600 to-blue-600 bg-clip-text text-5xl font-extrabold text-transparent sm:text-6xl lg:text-7xl dark:from-blue-400 dark:via-cyan-400 dark:to-blue-400"
          >
            SEWorld
          </h1>

          <p
            class="mx-auto mb-8 max-w-3xl text-lg leading-relaxed text-gray-700 sm:text-xl dark:text-gray-300"
          >
            A comprehensive platform dedicated to supporting the teaching of software engineering
            concepts. Our curriculum is aligned with core software engineering topics, ensuring a
            complete and structured learning experience.
          </p>
        </div>
      </div>
    </section>

    <!-- Topics Section -->
    <section class="px-4 py-16 sm:px-6 lg:px-8">
      <div class="mx-auto max-w-6xl">
        <div class="mb-12 text-center">
          <h2 class="mb-4 text-3xl font-bold text-gray-900 sm:text-4xl dark:text-white">
            Topics Covered
          </h2>
          <p class="text-lg text-gray-600 dark:text-gray-400">
            Master the essential concepts of software engineering
          </p>
        </div>

        <div class="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          <div
            v-for="topic in topics"
            :key="topic.id"
            class="group relative overflow-hidden rounded-2xl border border-gray-200 bg-white p-6 shadow-md transition-all hover:-translate-y-1 hover:shadow-xl dark:border-gray-700 dark:bg-gray-800"
          >
            <!-- Number Badge -->
            <div
              class="absolute top-4 right-4 flex h-10 w-10 items-center justify-center rounded-full bg-linear-to-br from-blue-500 to-cyan-500 text-sm font-bold text-white"
            >
              {{ topic.id }}
            </div>

            <!-- Icon -->
            <div
              class="mb-4 inline-flex h-14 w-14 items-center justify-center rounded-xl bg-linear-to-br from-blue-100 to-cyan-100 dark:from-blue-900/30 dark:to-cyan-900/30"
            >
              <component :is="topic.icon" class="h-7 w-7 text-blue-600 dark:text-blue-400" />
            </div>

            <!-- Content -->
            <h3 class="mb-2 text-xl font-bold text-gray-900 dark:text-white">
              {{ topic.name }}
            </h3>
            <p class="text-gray-600 dark:text-gray-400">
              {{ topic.description }}
            </p>

            <!-- Hover Effect -->
            <div
              class="absolute bottom-0 left-0 h-1 w-full scale-x-0 bg-linear-to-r from-blue-500 to-cyan-500 transition-transform group-hover:scale-x-100"
            ></div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="px-4 py-16 sm:px-6 lg:px-8">
      <div class="mx-auto max-w-6xl">
        <div class="mb-12 text-center">
          <h2 class="mb-4 text-3xl font-bold text-gray-900 sm:text-4xl dark:text-white">
            Who is SEWorld For?
          </h2>
          <p class="text-lg text-gray-600 dark:text-gray-400">
            Structured resources and hands-on approach for everyone
          </p>
        </div>

        <div class="grid gap-8 md:grid-cols-3">
          <div
            v-for="(feature, index) in features"
            :key="index"
            class="relative rounded-2xl border border-gray-200 bg-white p-8 shadow-md transition-all hover:shadow-xl dark:border-gray-700 dark:bg-gray-800"
          >
            <!-- Icon Background -->
            <div
              class="mb-6 inline-flex h-16 w-16 items-center justify-center rounded-2xl bg-linear-to-br from-blue-500 to-cyan-500 shadow-lg"
            >
              <component :is="feature.icon" class="h-8 w-8 text-white" />
            </div>

            <!-- Content -->
            <h3 class="mb-3 text-2xl font-bold text-gray-900 dark:text-white">
              {{ feature.title }}
            </h3>
            <p class="leading-relaxed text-gray-600 dark:text-gray-400">
              {{ feature.description }}
            </p>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>
