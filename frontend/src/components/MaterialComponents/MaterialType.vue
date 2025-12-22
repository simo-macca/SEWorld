<script setup>
import LinkType from '@/components/MaterialComponents/LinkType.vue'
import FileType from '@/components/MaterialComponents/FileType.vue'
import MarkdownType from '@/components/MaterialComponents/MarkdownType.vue'

defineProps({
  material: { type: Object, required: true },
})
</script>

<template>
  <div class="flex flex-col">
    <!-- Description -->
    <p class="mb-3 line-clamp-2 text-sm text-gray-600 dark:text-gray-400">
      {{ material.description }}
    </p>

    <!-- Markdown Type -->
    <div v-if="material.type === 'markdown'" class="w-full">
      <MarkdownType :content="material.content" />
    </div>

    <!-- File Type -->
    <div v-else-if="material.type === 'file'" class="w-full">
      <FileType :content="material.content" />
    </div>

    <!-- Link Type -->
    <div v-else-if="material.type === 'link'" class="w-full">
      <LinkType :content="material.content" />
    </div>

    <!-- Other Type (Text Content) -->
    <div
      v-else
      class="custom-scrollbar max-h-50 overflow-auto rounded-lg border border-gray-200 bg-gray-50/50 p-4 text-sm leading-relaxed text-gray-700 dark:border-gray-700 dark:bg-gray-900/50 dark:text-gray-300"
    >
      <p class="whitespace-pre-wrap">{{ material.content }}</p>
    </div>
  </div>
</template>
