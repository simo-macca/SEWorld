<script>
import { BButton, vBToggle } from 'bootstrap-vue-next';

export default {
	name: 'SecondaryButton',

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
		:class="`highlight px-2 click-squeeze`"
		:disabled="disable"
		@click="onClick"
	>
		<template v-if="$slots.default">
			<slot />
		</template>
		<template v-else>
			<span :class="`${fontSizeStyle}`">{{ label }}</span>
		</template>
	</BButton>
</template>


  
<style scoped>
.click-squeeze {
	transform: scale(1);
	transition: all 125ms ease;
}

.click-squeeze:active {
	transform: scale(0.95);
}

.highlight:hover {
	opacity: 1;
}
</style>