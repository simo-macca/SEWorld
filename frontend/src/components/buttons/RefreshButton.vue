<template>
  <div class="d-flex flex-column align-items-center justify-center">
    <BButton
        :id="'refresh-btn-' + elementId"
        variant="success"
        class="button-size click"
        title="refresh AI explanation"
    >
      <MdiHeadRefreshOutline  height="30px" width="30px"/>
    </BButton>

    <BPopover
        ref="refreshPopover"
        :target="'refresh-btn-' + elementId"
        :placement="popoverPlacement"
        :click="true"
        :close-on-hide="true"
        :delay="{ show: 0, hide: 0 }"
        class="custom-z-index"
    >
      <h5>Are you sure?</h5>
      {{ label }}
      <div class="d-flex justify-content-end mt-2">
        <BButton size="sm" variant="success" @click="confirmRefresh"
        >Yes</BButton
        >
        <BButton
            size="sm"
            variant="secondary"
            @click="cancelRefresh"
            class="ms-2"
        >Cancel</BButton
        >
      </div>
    </BPopover>
  </div>
</template>

<script>
import { BButton, BPopover } from 'bootstrap-vue-next';
import MdiHeadRefreshOutline from '~icons/mdi/head-refresh-outline';

export default {
  components: {
    BButton,
    BPopover,
    MdiHeadRefreshOutline,
  },
  props: {
    elementId: {
      type: [String, Number],
      required: true,
    },
    label: {
      type: String,
      required: false,
      default: `If you refresh the given AI answer you'll get a new one without being able to recover the previous one.`,
    },
    popoverPlacement: {
      type: String,
      required: false,
      default: `top`,
    },
  },
  emits: ['refresh'],
  data() {
    return {
      showConfirm: false,
    };
  },
  methods: {
    confirmRefresh() {
      this.$emit('refresh', this.elementId);
      this.hidePopover();
    },
    cancelRefresh() {
      this.hidePopover();
    },
    hidePopover() {
      this.$refs.refreshPopover.hide();
    },
  },
};
</script>
