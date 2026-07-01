<template>
  <div class="position-absolute top-50 start-50 translate-middle" style="text-align: justify">
    <p>Ops, we are so sorry there was a problem. <br> {{ errorMessage }} <br> Try later.</p>
  </div>
</template>
<script>
export default {
  name: 'ErrorMessage',
  props: {
    error: {
      type: String,
      required: true
    }
  },

  computed: {
    errorMessage() {
      return this.error.endsWith('.') ?
          this.error :
          this.error + '.'
    }
  }
}
</script>