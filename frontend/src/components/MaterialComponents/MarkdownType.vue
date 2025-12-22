<script setup>
import { ChevronDown, ChevronUp, FileCheck } from 'lucide-vue-next'
import { MdPreview } from 'md-editor-v3'
import { useThemeStore } from '@/stores/isDark.js'
import { ref } from 'vue'
import 'md-editor-v3/lib/style.css'

defineProps({
  content: { type: String, required: true },
})

const theme = useThemeStore()
const open = ref(false)
</script>

<template>
  <button
    type="button"
    @click="open = !open"
    class="flex w-full items-center justify-between rounded-lg border px-4 py-2.5 text-sm font-medium focus:ring-2 focus:ring-offset-2 focus:outline-none"
    :class="
      theme.isDark
        ? 'border-cyan-500/50 bg-cyan-900/20 text-cyan-400 hover:bg-cyan-900/30 focus:ring-cyan-500'
        : 'border-cyan-500 bg-cyan-50 text-cyan-700 hover:bg-cyan-100 focus:ring-cyan-500'
    "
  >
    <span class="flex items-center gap-2">
      <FileCheck class="h-4 w-4" />
      {{ open ? 'Close' : 'Preview' }} Markdown
    </span>
    <component :is="open ? ChevronUp : ChevronDown" class="h-4 w-4" />
  </button>

  <div class="overflow-hidden" :style="{ maxHeight: open ? '400px' : '0' }">
    <div
      v-if="open"
      class="custom-scrollbar mt-3 max-h-87.5 overflow-auto rounded-lg border border-gray-200 bg-gray-50/50 p-4 dark:border-gray-700 dark:bg-gray-900/50"
    >
      <MdPreview
        class="bg-transparent!"
        :modelValue="content"
        language="en-US"
        :theme="theme.isDark ? 'dark' : 'light'"
        previewTheme="cyanosis"
        code-theme="qtcreator"
      />
    </div>
  </div>
</template>

<style scoped></style>
