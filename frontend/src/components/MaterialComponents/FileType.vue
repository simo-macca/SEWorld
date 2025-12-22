<script setup>
import { Download, FileCheck } from 'lucide-vue-next'
import { useThemeStore } from '@/stores/isDark.js'
import { ref } from 'vue'

const props = defineProps({
  content: { type: String, required: true },
})

const theme = useThemeStore()
const isDownloading = ref(false)

async function download() {
  if (isDownloading.value) return

  isDownloading.value = true
  try {
    const fileUrl = props.content
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
  } finally {
    setTimeout(() => {
      isDownloading.value = false
    }, 1000)
  }
}

function getFileExtension() {
  const parts = props.content.split('.')
  return parts.length > 1 ? parts[parts.length - 1].toUpperCase() : 'FILE'
}

function getFileName() {
  return props.content.split('/').pop().split('.').slice(0, -1).join('.')
}
</script>

<template>
  <button
    @click="download"
    :disabled="isDownloading"
    class="group flex w-full items-center justify-between rounded-lg border p-4 shadow-sm focus:ring-2 focus:ring-offset-2 focus:outline-none disabled:cursor-not-allowed disabled:opacity-50"
    :class="
      theme.isDark
        ? 'to-gray-750 hover:from-gray-750 border-gray-700 bg-linear-to-br from-gray-800 text-gray-200 hover:to-gray-700 focus:ring-green-500'
        : 'border-gray-200 bg-linear-to-br from-gray-50 to-white text-gray-700 hover:from-white hover:to-gray-50 focus:ring-green-500'
    "
  >
    <span class="flex items-center gap-3 overflow-hidden">
      <span class="shrink-0 rounded-lg bg-green-500/10 p-2 text-green-600 dark:text-green-400">
        <FileCheck class="h-5 w-5" />
      </span>
      <span class="flex flex-col gap-1 overflow-hidden text-left">
        <span class="truncate text-sm font-medium">{{ getFileName() }}</span>
        <span class="text-xs text-gray-500 dark:text-gray-400">Click to download</span>
      </span>
    </span>
    <span class="flex shrink-0 items-center gap-3">
      <span
        class="rounded-full bg-green-500/20 px-2.5 py-1 text-xs font-bold text-green-700 dark:text-green-400"
      >
        {{ getFileExtension() }}
      </span>
      <Download
        class="h-5 w-5 group-hover:translate-y-0.5"
        :class="isDownloading ? 'animate-bounce' : ''"
      />
    </span>
  </button>
</template>

<style scoped></style>
