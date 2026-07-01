
<script>
import { BButton, BPopover } from 'bootstrap-vue-next';
import IconBiCheckLg from '~icons/bi/check-lg';
import IconBiXLg from '~icons/bi/x-lg';

export default {
	components: {
		BButton,
		BPopover,
		IconBiCheckLg,
		IconBiXLg,
	},

	props: {
		elementId: {
			type: [String, Number],
			required: true,
		},
		acceptLabel: {
			type: String,
			required: false,
			default: 'Are you sure you want to accept this evaluation?',
		},
		denyLabel: {
			type: String,
			required: false,
			default: 'Are you sure you want to deny this evaluation?',
		},
		popoverPlacement: {
			type: String,
			required: false,
			default: 'top',
		},
	},

	emits: ['accept', 'deny'],

	methods: {
		// accept
		confirmAccept() {
			this.$emit('accept', this.elementId);
			this.hideAcceptPopover();
		},
		cancelAccept() {
			this.hideAcceptPopover();
		},
		hideAcceptPopover() {
			this.$refs.acceptPopover.hide();
		},

		// Deny
		confirmDeny() {
			this.$emit('deny', this.elementId);
			this.hideDenyPopover();
		},
		cancelDeny() {
			this.hideDenyPopover();
		},
		hideDenyPopover() {
			this.$refs.denyPopover.hide();
		},
	},
};
</script>

<template>
	<div class="d-flex flex-column align-items-center justify-center">
		<div class="btn-group" role="group">
			<BButton
				:id="'accept-btn-' + elementId"
				variant="success"
				class="click"
				title="Accept"
				style="width: 80px; height: 50px"
			>
				<IconBiCheckLg width="36" height="36" />
			</BButton>
			<BButton
				:id="'deny-btn-' + elementId"
				variant="danger"
				class="click"
				title="Deny"
				style="width: 80px; height: 50px"
			>
				<IconBiXLg width="34" height="34" />
			</BButton>
		</div>

		<!-- Accept Popover -->
		<BPopover
			ref="acceptPopover"
			:target="'accept-btn-' + elementId"
			placement="top"
			:click="true"
			:close-on-hide="true"
			:delay="{ show: 0, hide: 0 }"
			class="custom-z-index"
		>
			<h5>Confirm Accept</h5>
			{{ acceptLabel }}
			<div class="d-flex justify-content-end mt-2">
				<BButton size="sm" variant="success" @click="confirmAccept"
					>Yes</BButton
				>
				<BButton
					size="sm"
					variant="secondary"
					@click="cancelAccept"
					class="ms-2"
					>Cancel</BButton
				>
			</div>
		</BPopover>

		<!-- Deny Popover -->
		<BPopover
			ref="denyPopover"
			:target="'deny-btn-' + elementId"
			placement="top"
			:click="true"
			:close-on-hide="true"
			:delay="{ show: 0, hide: 0 }"
			class="custom-z-index"
		>
			<h5>Confirm Deny</h5>
			{{ denyLabel }}
			<div class="d-flex justify-content-end mt-2">
				<BButton size="sm" variant="danger" @click="confirmDeny"
					>Yes</BButton
				>
				<BButton
					size="sm"
					variant="secondary"
					@click="cancelDeny"
					class="ms-2"
					>Cancel</BButton
				>
			</div>
		</BPopover>
	</div>
</template>
  
<style scoped>
.custom-z-index {
	z-index: 1050;
}
</style>