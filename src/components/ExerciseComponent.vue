<script setup>
// Imports
import { SquareCheckBig, X, RotateCcw, List, Repeat } from 'lucide-vue-next'
import { BButton, BCard } from 'bootstrap-vue-next'

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
    iconClass: 'text-danger',
    iconComponent: X,
  },
}

const getStatusConfig = (isCompleted) => {
  return statusConfig[isCompleted] || { iconClass: '', iconComponent: null }
}
</script>

<template>
  <div class="p-4 border">
    <b-card class="h-100">
      <h5>{{ exercise['exerciseTitle'] }}</h5>
      <p>{{ exercise['exerciseDescription'] }}</p>
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <Repeat aria-label="Tentativi" />
          <small>{{ exercise['exerciseCreatedDate'] }} tentativi</small>
        </div>
        <div>
          <b-button variant="outline-primary" size="sm"> <RotateCcw /> Ultimo tentativo </b-button>
          <b-button variant="outline-secondary" size="sm" class="ms-2"> <List /> Tutti </b-button>
        </div>
      </div>
      <component
        :is="getStatusConfig(props.exercise['exerciseIsCompleted']).iconComponent"
        :class="getStatusConfig(props.exercise['exerciseIsCompleted']).iconClass"
      />
    </b-card>
    <!--    {{ props.exercise['exerciseTitle'] }} - {{ props.exercise['exerciseIsCompleted'] }}-->
  </div>
</template>
