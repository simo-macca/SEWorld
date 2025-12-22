<script setup>
import { ExternalLink } from 'lucide-vue-next'
import { useThemeStore } from '@/stores/isDark.js'

defineProps({
  content: { type: String, required: true },
})
const theme = useThemeStore()
</script>

<template>
  <a
    :href="content"
    target="_blank"
    rel="noopener noreferrer"
    class="group flex w-full items-center justify-between rounded-lg border p-4 no-underline shadow-sm hover:shadow-md focus:ring-2 focus:ring-offset-2 focus:outline-none"
    :class="
      theme.isDark
        ? 'border-purple-700 bg-linear-to-br from-purple-900/30 to-purple-800/30 text-purple-300 hover:from-purple-800/40 hover:to-purple-700/40 focus:ring-purple-500'
        : 'border-purple-200 bg-linear-to-br from-purple-50 to-white text-purple-700 hover:from-white hover:to-purple-50 focus:ring-purple-500'
    "
  >
    <div class="flex flex-1 items-center gap-3 overflow-hidden">
      <div class="shrink-0 rounded-lg bg-purple-500/10 p-2">
        <ExternalLink class="h-5 w-5" />
      </div>
      <div class="overflow-hidden">
        <p class="mb-0.5 text-sm font-medium">Visit Resource</p>
        <p class="truncate text-xs opacity-75">{{ content }}</p>
      </div>
    </div>
    <ExternalLink
      class="ml-2 h-5 w-5 shrink-0 group-hover:translate-x-0.5 group-hover:-translate-y-0.5"
    />
  </a>
</template>
