<script setup>
import { Calendar, ExternalLink, File, FileText, Tag, UserRound } from 'lucide-vue-next'
import MaterialType from '@/components/MaterialComponents/MaterialType.vue'
import MarkdownIcon from '@/components/icons/MarkdownIcon.vue'

defineProps({
  material: { type: Object, required: true },
})

const typeBadgeStyles = {
  markdown: 'bg-blue-500 text-white dark:bg-blue-600',
  file: 'bg-green-500 text-white dark:bg-green-600',
  link: 'bg-purple-500 text-white dark:bg-purple-600',
  default: 'bg-gray-500 text-white dark:bg-gray-600',
}

function getTypeBadgeStyle(type) {
  return typeBadgeStyles[type.toLowerCase()] || typeBadgeStyles.default
}

const typeStyles = {
  markdown:
    'bg-gradient-to-br from-blue-50 to-blue-100 text-blue-700 dark:from-blue-900/30 dark:to-blue-800/30 dark:text-blue-300 border-blue-200 dark:border-blue-700',
  file: 'bg-gradient-to-br from-green-50 to-green-100 text-green-700 dark:from-green-900/30 dark:to-green-800/30 dark:text-green-300 border-green-200 dark:border-green-700',
  link: 'bg-gradient-to-br from-purple-50 to-purple-100 text-purple-700 dark:from-purple-900/30 dark:to-purple-800/30 dark:text-purple-300 border-purple-200 dark:border-purple-700',
  default:
    'bg-gradient-to-br from-gray-50 to-gray-100 text-gray-700 dark:from-gray-800/30 dark:to-gray-700/30 dark:text-gray-300 border-gray-200 dark:border-gray-600',
}

function getTypeStyle(type) {
  return typeStyles[type.toLowerCase()] || typeStyles.default
}

const materialIcon = {
  markdown: MarkdownIcon,
  file: FileText,
  link: ExternalLink,
  default: File,
}

function getIconComponent(type) {
  return materialIcon[type.toLowerCase()] || materialIcon.default
}
</script>

<template>
  <div
    class="relative flex h-full flex-col overflow-hidden rounded-xl border bg-white shadow-md hover:shadow-xl dark:border-gray-700 dark:bg-gray-800"
  >
    <!-- Decorative Top Bar -->
    <div :class="[getTypeStyle(material.type), 'h-2 w-full border-b']"></div>

    <!-- Card Content -->
    <div class="flex h-full flex-col p-5">
      <!-- Header Section -->
      <div class="mb-4">
        <div class="mb-3 flex items-start justify-between gap-3">
          <!-- Icon Badge -->
          <div
            :class="[getTypeStyle(material.type), 'rounded-lg border p-3 group-hover:scale-110']"
          >
            <component :is="getIconComponent(material.type)" class="h-6 w-6" />
          </div>

          <!-- Type Badge -->
          <span
            :class="[
              getTypeBadgeStyle(material.type),
              'rounded-full px-3 py-1 text-xs font-bold tracking-wider uppercase shadow-sm',
            ]"
          >
            {{ material.type }}
          </span>
        </div>

        <!-- Title -->
        <h3
          class="mb-2 line-clamp-2 text-xl font-bold text-gray-900 group-hover:text-blue-600 dark:text-white dark:group-hover:text-blue-400"
        >
          {{ material.title }}
        </h3>
      </div>

      <!-- MaterialType Component -->
      <div class="mb-4 grow">
        <MaterialType :material="material" />
      </div>

      <!-- Footer Section -->
      <div class="mt-auto space-y-3 border-t border-gray-100 pt-4 dark:border-gray-700">
        <!-- Metadata -->
        <div class="flex items-center justify-between text-sm text-gray-600 dark:text-gray-400">
          <div class="flex items-center gap-1.5 hover:text-gray-900 dark:hover:text-gray-200">
            <UserRound class="h-4 w-4" />
            <span class="font-medium">{{ material.author }}</span>
          </div>
          <div class="flex items-center gap-1.5">
            <Calendar class="h-4 w-4" />
            <span>{{ material.date }}</span>
          </div>
        </div>

        <!-- Tags -->
        <div class="flex flex-wrap gap-2" v-if="material.tags && material.tags.length > 0">
          <span
            v-for="tag in material.tags"
            :key="tag"
            class="inline-flex items-center gap-1 rounded-full bg-gray-100 px-2.5 py-1 text-xs font-medium text-gray-700 hover:bg-gray-200 dark:bg-gray-700 dark:text-gray-300 dark:hover:bg-gray-600"
          >
            <Tag class="h-3 w-3" />
            {{ tag }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
