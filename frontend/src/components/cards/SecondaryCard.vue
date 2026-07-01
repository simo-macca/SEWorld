<script>
export default {
	name: 'SecondaryCard',

	props: {
		withBorder: {
			type: Boolean,
			default: false,
		},

		withHover: {
			type: Boolean,
			default: false,
		},

		padding: {
			type: String,
			require: false,
			default: 'p-3',
		},
	},

	data() {
		return {
			isHovered: false,
		};
	},
};
</script>

<template>
	<div
		:class="`const ${padding} ${
			withBorder
				? 'background-card-border'
				: `background-card `.concat(
						withHover && isHovered ? 'hovered' : ''
				  )
		}`"
		@mouseenter="isHovered = true"
		@mouseleave="isHovered = false"
	>
		<slot />
	</div>
</template>

<style scoped>
.const {
	--border-width: 2px;
}

.background-card-border {
	width: 100%;
	height: 100%;

	display: flex;
	align-items: center;
	justify-content: center;

	border: solid var(--border-width) black;

	background: #05050d23;

	transition: all 0.125s ease;
}

.background-card {
	width: 100%;
	height: 100%;

	display: flex;
	align-items: center;
	justify-content: center;

	border: solid var(--border-width) transparent;

	background: #05050d23;

	transition: all 0.125s ease;
}

.hovered {
	border-color: black;
}
</style>
