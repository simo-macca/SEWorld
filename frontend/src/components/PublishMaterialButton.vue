<template>
	<div class="d-flex flex-column align-items-center justify-center">
		<BButton
			:id="'publish-btn-' + elementId"
			variant="success"
			class="publish-button button-size click"
			title="publish"
		>
			<IconBiEyeSlash />
		</BButton>

		<BPopover
			ref="publishPopover"
			:target="'publish-btn-' + elementId"
			:placement="popoverPlacement"
			:click="true"
			:close-on-hide="true"
			:delay="{ show: 0, hide: 0 }"
			class="custom-z-index"
		>
			<h5>Are you sure?</h5>
			{{ label }}
			<div class="d-flex justify-content-end mt-2">
				<BButton size="sm" variant="success" @click="confirmPublish"
					>Yes</BButton
				>
				<BButton
					size="sm"
					variant="secondary"
					@click="cancelPublish"
					class="ms-2"
					>Cancel</BButton
				>
			</div>
		</BPopover>
	</div>
</template>

<script>
import { BButton, BPopover } from 'bootstrap-vue-next';
import IconBiEyeSlash from '~icons/bi/eye-slash';

export default {
	components: {
		BButton,
		BPopover,
		IconBiEyeSlash,
	},
	props: {
		elementId: {
			type: [String, Number],
			required: true,
		},
		label: {
			type: String,
			required: false,
			default: `By publishing an exercise you won't be able to modify it anymore.`,
		},
		popoverPlacement: {
			type: String,
			required: false,
			default: `top`,
		},
	},
	emits: ['publish'],
	data() {
		return {
			showConfirm: false,
		};
	},
	methods: {
		confirmPublish() {
			this.$emit('publish', this.elementId);
			this.hidePopover();
		},
		cancelPublish() {
			this.hidePopover();
		},
		hidePopover() {
			this.$refs.publishPopover.hide();
		},
	},
};
</script>
