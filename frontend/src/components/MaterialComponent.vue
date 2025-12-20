<script setup>
import { Download, ExternalLink } from 'lucide-vue-next'
import { MdPreview } from 'md-editor-v3'
import { ref } from 'vue'
import { useThemeStore } from '@/stores/isDark.js'

const props = defineProps({
  material: { type: Object, required: true },
})

const open = ref(false)
const theme = useThemeStore()

async function download() {
  try {
    const fileUrl = props.material['content']
    const filename = fileUrl.split('/').pop()
    const response = await fetch(fileUrl, { mode: 'cors' })
    if (!response.ok) return
    const fileBlob = await response.blob()
    const blobUrl = URL.createObjectURL(fileBlob)
    const anchor = document.createElement('a')
    anchor.href = blobUrl
    anchor.download = filename
    document.body.appendChild(anchor)
    anchor.click()
    document.body.removeChild(anchor)
    URL.revokeObjectURL(blobUrl)
  } catch (error) {
    console.error('Error during download:', error)
  }
}
</script>

<template>
  <div class="flex min-h-[5em] flex-col items-start justify-between p-1">
    <div class="flex w-full items-center justify-between">
      <span class="text-gray-700 dark:text-gray-300">{{ props.material['description'] }}</span>
      <button
        v-if="props.material['type'] === 'markdown'"
        type="button"
        @click="open = !open"
        class="rounded border px-3 py-1 text-sm font-medium transition-colors focus:outline-none"
        :class="
          theme.isDark
            ? 'border-cyan-400 text-cyan-400 hover:bg-gray-700'
            : 'border-cyan-600 text-cyan-700 hover:bg-cyan-50'
        "
      >
        {{ open ? 'Close' : 'Open' }} Markdown
      </button>
    </div>

    <div
      v-if="props.material['type'] === 'markdown'"
      class="mt-4 w-full transition-all duration-300"
    >
      <div
        v-if="open"
        class="rounded-r border border-gray-200 bg-transparent p-2 dark:border-gray-600"
      >
        <MdPreview
          class="!bg-transparent"
          :modelValue="props.material['content']"
          language="en-US"
          :theme="theme.isDark ? 'dark' : 'light'"
          previewTheme="cyanosis"
          code-theme="qtcreator"
        />
      </div>
    </div>

    <div v-else-if="props.material['type'] === 'file'" class="w-full">
      <button
        @click="download"
        class="mt-4 flex w-full items-center justify-between rounded border p-2 shadow-sm transition-colors"
        :class="
          theme.isDark
            ? 'border-gray-600 bg-gray-700 text-gray-200 hover:bg-gray-600'
            : 'border-gray-200 bg-gray-50 text-gray-700 hover:bg-gray-100'
        "
      >
        <span>{{ props.material['content'].split('.')[0] }}</span>
        <span class="flex items-center gap-2">
          {{ props.material['content'].split('.')[1] }}
          <Download class="h-4 w-4" />
        </span>
      </button>
    </div>

    <div v-else-if="props.material['type'] === 'link'" class="w-full">
      <a
        :href="props.material['content']"
        target="_blank"
        class="mt-4 flex w-full items-center justify-between rounded border p-2 no-underline shadow-sm transition-colors"
        :class="
          theme.isDark
            ? 'border-gray-600 bg-gray-700 text-gray-200 hover:bg-gray-600'
            : 'border-gray-200 bg-gray-50 text-gray-700 hover:bg-gray-100'
        "
      >
        <span class="truncate">{{ props.material['content'] }}</span>
        <ExternalLink class="h-4 w-4 flex-shrink-0" />
      </a>
    </div>

    <div
      v-else
      class="mt-4 max-h-[13em] w-full overflow-auto rounded border border-gray-200 p-2 text-justify text-gray-800 shadow-sm dark:border-gray-600 dark:text-gray-300"
    >
      {{ props.material['content'] }}
    </div>

    <div class="mt-4 flex flex-wrap gap-2">
      <span
        v-for="t in props.material['tags']"
        :key="t"
        class="rounded-full px-3 py-1 text-xs font-medium"
        :class="theme.isDark ? 'bg-gray-700 text-gray-300' : 'bg-gray-200 text-gray-700'"
      >
        #{{ t }}
      </span>
    </div>
  </div>
</template>
