<script>
import { BButton, vBToggle } from 'bootstrap-vue-next';

export default {
	name: 'PrimaryButton',

	components: {
		BButton,
	},

	directives: {
		'b-toggle': vBToggle,
	},

	props: {
		// Dynamic string for v-b-toggle parameter
		toggleTarget: {
			type: String,
			required: false,
		},
		// Function to execute on click
		onClick: {
			type: Function,
			required: false,
		},
		// Label text
		label: {
			type: String,
			default: 'Button',
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
	},
};
</script>

<template>
	<BButton
		v-b-toggle="toggleTarget ? `collapse-${toggleTarget}` : null"
		class="gradient-bg-button px-2"
		:disabled="disable"
		@click="onClick"
	>
		<template v-if="$slots.default">
			<slot />
		</template>
		<template v-else>
			<span :class="`text-white ${fontSizeStyle}`">{{ label }}</span>
		</template>
	</BButton>
</template>
  
<style scoped>
.gradient-bg-button {
	background: linear-gradient(to right, #ee7724, #d8363a, #dd3675, #b44593);
	background-size: 300% 100%;
	font-weight: 800;
	border: none;
	transform: scale(1);

	transition: background-position 0.5s ease, transform 0.3s ease;
}

.gradient-bg-button:hover {
	background-position: 100% 0;
}

.gradient-bg-button:active {
	transform: scale(0.95);
	transition: transform 0.05s ease;
}

.text-white {
	color: white;
}
</style>