<script setup>
// Imports
import { ref } from 'vue'
import { BButton, BCardText } from 'bootstrap-vue-next'
import { useThemeStore } from '@/stores/isDark.js'
import { MdPreview } from 'md-editor-v3'
import { Download, ExternalLink } from 'lucide-vue-next'

// Props definition
const props = defineProps({
  material: {
    type: Object,
    required: true,
  },
})

// Component state
const open = ref(false)
const theme = useThemeStore()

// File download handler
async function download() {
  try {
    // 1. Extract URL and filename
    const fileUrl = props.material['content']
    const filename = fileUrl.split('/').pop()

    // 2. Fetch the file (CORS)
    const response = await fetch(fileUrl, { mode: 'cors' })
    if (!response.ok) {
      console.error(`Download failed: HTTP ${response.status}`)
      return
    }

    // 3. Convert response to Blob
    const fileBlob = await response.blob()

    // 4. Create a temporary URL for the Blob
    const blobUrl = URL.createObjectURL(fileBlob)

    // 5. Trigger download via anchor element
    const anchor = document.createElement('a')
    anchor.href = blobUrl
    anchor.download = filename
    document.body.appendChild(anchor)
    anchor.click()

    // 6. Cleanup resources
    document.body.removeChild(anchor)
    URL.revokeObjectURL(blobUrl)

    console.log(`Downloaded: ${filename}`)
  } catch (error) {
    console.error('Error during download:', error)
  }
}
</script>

<template>
  <b-card-text
    :class="`m-3 d-flex flex-column justify-content-between align-items-start`"
    style="min-height: 5em; overflow: auto"
  >
    <div class="d-flex align-items-center justify-content-between w-100">
      <span>{{ props.material['description'] }}</span>
      <button
        v-if="props.material['type'] === 'markdown'"
        :class="`btn ${theme.isDark ? 'btn-outline-info' : 'btn-info'}`"
        type="button"
        data-bs-toggle="collapse"
        :data-bs-target="`#collapse-${props.material['id']}`"
        aria-expanded="false"
        :aria-controls="`collapse-${props.material['id']}`"
        @click="open = !open"
      >
        {{ props.open ? 'Close' : 'Open' }} Markdown
      </button>
    </div>

    <div v-if="props.material['type'] === 'markdown'" class="w-100 mt-4">
      <div class="collapse collapsing" :id="`collapse-${props.material['id']}`">
        <MdPreview
          class="bg-transparent border border-light border-end-0 border-start-0 rounded-end p-2 card card-body"
          :modelValue="props.material['content']"
          language="en-US"
          :theme="theme.isDark ? 'dark' : 'light'"
          previewTheme="cyanosis"
          code-theme="qtcreator"
        />
      </div>
    </div>
    <div v-else-if="props.material['type'] === 'file'" class="w-100">
      <BButton
        :class="`border ${theme.isDark ? `bg-body-secondary` : `bg-secondary-subtle text-secondary-emphasis`} shadow-sm rounded mt-4 w-100 d-flex align-items-center justify-content-between p-2`"
        @click="download"
      >
        {{ props.material['content'].split('.')[0] }}
        <span class="d-flex align-items-center justify-content-center gap-2">
          {{ props.material['content'].split('.')[1] }}
          <Download />
        </span>
      </BButton>
    </div>
    <div v-else-if="props.material['type'] === 'link'" class="w-100">
      <BButton
        :class="`border ${theme.isDark ? `bg-body-secondary` : `bg-secondary-subtle text-secondary-emphasis`} shadow-sm rounded mt-4 w-100 d-flex align-items-center justify-content-between p-2`"
        :href="`${props.material['content']}`"
        target="_blank"
      >
        {{ props.material['content'] }}
        <ExternalLink />
      </BButton>
    </div>
    <div
      v-else
      class="mt-4 overflow-auto p-2 shadow-sm rounded border-top border-bottom"
      style="max-height: 13em; text-align: justify"
    >
      {{ props.material['content'] }}
    </div>

    <div class="d-flex flex-wrap gap-2 mt-4">
      <span
        v-for="t in props.material['tags']"
        :key="t"
        :class="`p-2 ${theme.isDark ? `bg-body-tertiary` : `bg-dark-subtle text-dark-emphasis`} rounded-pill`"
      >
        #{{ t }}
      </span>
    </div>
  </b-card-text>
</template>
