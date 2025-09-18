<script setup>
// Imports
import { SquareCheckBig, RotateCcw, List, Repeat, SquareIcon, Upload, Trash } from 'lucide-vue-next'
import { BButton, BCard } from 'bootstrap-vue-next'
import { useUsersStore } from '@/stores/usersStore.js'
import { computed, ref } from 'vue'
import ConfirmModalComponent from '@/components/ConfirmModalComponent.vue'
import { useExercisesStore } from '@/stores/exerciseStore.js'
import { useToast } from 'vue-toastification'

// Props definition
const props = defineProps({
  exercise: {
    type: Object,
    required: true,
  },
})

const statusConfig = {
  true: {
    iconClass: 'text-success',
    iconComponent: SquareCheckBig,
  },
  false: {
    iconClass: 'text-secondary-light',
    iconComponent: SquareIcon,
  },
}

const getStatusConfig = (isCompleted) => {
  return statusConfig[isCompleted] || { iconClass: '', iconComponent: null }
}

const toast = useToast()
const userStore = useUsersStore()
const isInstructor = computed(() => userStore.isInstructor)

const showPublishModal = ref(false)
const showDeleteModal = ref(false)

async function confirmPublish(slug) {
  const updated = await useExercisesStore().publishExercise(slug)
  toast.success(updated.data.message || 'Published successfully')
}

async function confirmDelete(slug) {
  const updated = await useExercisesStore().deleteExercise(slug)
  toast.success(updated.data.message || 'Deleted successfully')
}
</script>

<template>
  <div class="p-4 border">
    <b-card class="h-100">
      <!-- Header -->
      <div class="d-flex justify-content-between align-items-center">
        <h5>{{ exercise['exerciseTitle'] }}</h5>
        <div class="d-flex align-items-center gap-2" v-if="!isInstructor">
          <component
            :is="getStatusConfig(props.material['exerciseIsCompleted']).iconComponent"
            :class="getStatusConfig(props.material['exerciseIsCompleted']).iconClass"
          />
          <div>
            <Repeat aria-label="Tentativi" />
            <span>10</span>
          </div>
        </div>
      </div>

      <!-- Description -->
      <p>{{ exercise['exerciseDescription'] }}</p>

      <!-- Footer -->
      <div class="d-flex justify-content-between align-items-center pt-3">
        <small class="d-flex flex-column row-gap-1">
          <span v-if="exercise['exerciseCreatedDate']" class="text-secondary-light"
            >Created on: {{ exercise['exerciseCreatedDate'] }}</span
          >
          <span v-if="exercise['exerciseOwner']" class="text-secondary-light"
            >by: {{ exercise['exerciseOwner'] }}</span
          >
        </small>

        <div v-if="isInstructor">
          <!-- Publish -->
          <b-button
            variant="outline-primary"
            size="sm"
            v-if="exercise['exerciseIsDraft']"
            @click="showPublishModal = true"
          >
            <Upload /> Public
          </b-button>
          <ConfirmModalComponent
            v-model="showPublishModal"
            title="Publish Exercise"
            :message="`Are you sure you want to publish this material ${exercise['exerciseTitle']}? This action cannot be undone.`"
            ok-title="Publish"
            ok-variant="outline-success"
            @confirm="confirmPublish(exercise['exerciseSlug'])"
          />

          <!-- Modify -->
          <b-button
            variant="outline-secondary"
            size="sm"
            class="ms-2"
            v-if="exercise['exerciseIsDraft']"
            ><List /> Modify</b-button
          >

          <!-- Delete -->
          <b-button variant="danger" size="sm" class="ms-2" @click="showDeleteModal = true">
            <Trash /> Delete
          </b-button>
          <ConfirmModalComponent
            v-model="showDeleteModal"
            title="Delete Exercise"
            :message="`Are you sure to delete ${exercise['exerciseTitle']}? This action is irreversible.`"
            ok-title="Delete"
            ok-variant="outline-danger"
            @confirm="confirmDelete(exercise['exerciseSlug'])"
          />
        </div>
        <div v-else>
          <b-button variant="outline-primary" size="sm"><RotateCcw /> Last attempt</b-button>
          <b-button variant="outline-secondary" size="sm" class="ms-2"
            ><List /> All attempt
          </b-button>
        </div>
      </div>
    </b-card>
  </div>
</template>
