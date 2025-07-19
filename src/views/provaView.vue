<script>
import api from '@/api/api'
import { useToast } from 'vue-toastification'

const toast = useToast()

export default {
  data() {
    return {
      message: '',
    }
  },
  async mounted() {
    try {
      const res = await api.get('/message')
      this.message = res.data.body
      console.log(res.data.body)
      toast.success(res.data.message)
    } catch (err) {
      toast.error(err.response.data.message || err.message)
    }
  },
}
</script>

<template>
  <div v-for="(obj, idx) in message" :key="idx">{{ obj }}</div>
</template>
