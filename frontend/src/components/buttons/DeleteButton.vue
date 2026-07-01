
<script>
import { BButton } from 'bootstrap-vue-next';
import IconBiTrash from '~icons/bi/trash';

export default {
	name: 'DeleteButton',

	components: {
		BButton,
		IconBiTrash,
	},

	props: {
		// Delete routine called once the deletion is confirmed
		deleteRoutine: {
			type: Function,
			required: true,
		},
		// The paramter passed when the delete routine is called
		routineParameter: {
			type: Object,
			required: false,
		},

		// The size of the button, available:
		// xs, sm, md, lg
		size: {
			type: String,
			default: 'md',
		},
		// disble or not the bbutton
		disable: {
			type: Boolean,
			default: false,
		},
	},

	data() {
		return {
			confirmDelete: false,
		};
	},

	computed: {
		fontSizeStyle() {
			switch (this.size) {
				case 'xs':
					return 'fs-6';
				case 'sm':
					return 'fs-4';
				case 'md':
					return 'fs-2';
				case 'lg':
					return 'fs-1';
				default:
					return 'fs-6';
			}
		},

		buttonSizeStyle() {
			return `bs-${this.size}`;
		},
	},

	methods: {
		deleteConfirmed() {
			this.deleteRoutine(this.routineParameter);
			this.confirmDelete = false;
		},
		enterDelete() {
			this.confirmDelete = true;
		},
		cancelDelete() {
			this.confirmDelete = false;
		},
	},
};
</script>

<template>
	<div class="const buttons-wrapper f-center">
		<BButton
			variant="danger"
			@click="deleteConfirmed"
			:class="`confirm-button click-squeeze f-center ${fontSizeStyle} ${buttonSizeStyle} ${
				confirmDelete ? 'confirm-button-visible' : ''
			}`"
		>
			Confirm
		</BButton>

		<div :class="`${buttonSizeStyle} f-center`">
			<BButton
				v-if="confirmDelete"
				variant="light"
				@click="cancelDelete"
				:class="`cancel-button click-squeeze f-center ${fontSizeStyle} ${buttonSizeStyle}`"
			>
				Cancel
			</BButton>
			<BButton
				v-else
				variant="danger"
				@click="enterDelete"
				:class="`delete-button click-squeeze ${fontSizeStyle}`"
			>
				<IconBiTrash />
			</BButton>
		</div>
	</div>
</template>

<style scoped>
.const {
	--red: #ff4d4d;
	--radius: 5px;
}

.f-center {
	display: flex;
	align-items: center;
	justify-content: center;
}

/* Buttons wrapper */
.buttons-wrapper {
	gap: 10px;
	min-width: 175px;
}

/* Confirm button styles */
.confirm-button {
	font-weight: 900;
	text-shadow: 0 0 0.25px currentColor;

	color: black;
	background-color: var(--red);

	border-radius: var(--radius);

	opacity: 0;
	transition: opacity 0.3s ease;
}
.confirm-button-visible {
	opacity: 1;
}

/* Cancel button styles */
.cancel-button {
	font-weight: 900;
	text-shadow: 0 0 0.25px currentColor;

	color: black;
	background-color: white;

	border-radius: var(--radius);
}

/* Delete button styles */
.delete-button {
	font-weight: 900;

	color: black;
	background-color: var(--red);

	border-radius: var(--radius);

	stroke: currentColor;
	stroke-width: 0.4px;
}

/* Button sizes */
.bs-xs {
	width: 90px;
	height: 35px;
}
.bs-sm {
	width: 115px;
	height: 45px;
}
.bs-md {
	width: 130px;
	height: 50px;
}
.bs-lg {
	width: 150px;
	height: 60px;
}

/* Squeeze effect */
.click-squeeze {
	transform: scale(1);
	transition: all 125ms ease;
}

.click-squeeze:active {
	transform: scale(0.95);
}
</style>