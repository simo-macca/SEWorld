
<script>
import { BButton, BPopover } from 'bootstrap-vue-next';
import IconBiTrash from '~icons/bi/trash';

export default {
	name: 'DeleteMaterialButton',

	components: {
		BPopover,
		BButton,
		IconBiTrash,
	},

	emits: ['delete'],

	props: {
		elementId: {
			type: [String, Number],
			required: true,
		},
		deletionSpecText: String,
	},

	data() {
		return {
			confirmDelete: false,
		};
	},

	methods: {
		confirmDeletion() {
			console.log(this.elementId);
			this.$emit('delete', this.elementId);
			this.hidePopover();
		},
		cancelDeletion() {
			this.hidePopover();
		},
		hidePopover() {
			this.$refs.deletionPopover.hide();
		},
	},
};
</script>

<template>
	<div class="d-flex flex-column align-items-center justify-center">
		<BButton
			:id="'delete-btn-' + elementId"
			variant="danger"
			class="delete-button button-size click"
			title="delete"
		>
			<IconBiTrash />
		</BButton>

		<BPopover
			ref="deletionPopover"
			:target="'delete-btn-' + elementId"
			placement="top"
			:click="true"
			:close-on-hide="true"
			:delay="{ show: 0, hide: 0 }"
		>
			<h5>Are you sure?</h5>
			By deleting {{ this.deletionSpecText }} you won't be able to recover
			it anymore.
			<div class="d-flex justify-content-end mt-2">
				<BButton size="sm" variant="danger" @click="confirmDeletion"
					>Yes</BButton
				>
				<BButton
					size="sm"
					variant="secondary"
					@click="cancelDeletion"
					class="ms-2"
					>Cancel</BButton
				>
			</div>
		</BPopover>
	</div>
</template>

<style scoped>
.button-size {
	width: 75px;
	height: 38px;
}

.delete-button {
	background-color: #ff4d4d;
	color: rgb(6, 5, 5);
	border: none;
	padding: 5px 10px;
	border-radius: 5px;

	font-size: small;
}
</style>