<script setup>
import { BModal } from 'bootstrap-vue-next'
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, required: true }, // per v-model
  title: { type: String, default: 'Confirm' },
  message: { type: String, default: 'Are you sure?' },
  okTitle: { type: String, default: 'OK' },
  okVariant: { type: String, default: 'primary' },
})

const emit = defineEmits(['update:modelValue', 'confirm'])

// stato interno legato a v-model
const show = ref(props.modelValue)

watch(
  () => props.modelValue,
  (val) => (show.value = val),
)
watch(show, (val) => emit('update:modelValue', val))

function handleOk() {
  emit('confirm')
}
</script>

<template>
  <b-modal v-model="show" :title="title" :ok-title="okTitle" :ok-variant="okVariant" @ok="handleOk">
    <p class="my-4">{{ message }}</p>
  </b-modal>
</template>
