<template>
  <div class="position-absolute top-50 start-50 translate-middle" style="text-align: justify">
    <p>{{ message || "There is no data to fetch about this page" }}</p>
  </div>
</template>
<script>
export default {
  name: 'NoInfoMessage',
  props: {
    message: {
      type: String,
      required: true
    }
  }
}
</script>