<script>
export default {
	name: 'PrimaryCard',

	props: {
		withBorder: {
			type: Boolean,
			default: false,
		},

		withHover: {
			type: Boolean,
			default: false,
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
		:class="`const rounded ${
			withBorder
				? 'background-card-wrapper-border'
				: `background-card-wrapper `.concat(
						withHover && isHovered ? 'hovered' : ''
				  )
		}`"
	>
		<div
			class="const rounded background-card p-3"
			@mouseenter="isHovered = true"
			@mouseleave="isHovered = false"
		>
			<slot />
		</div>
	</div>
</template>

<style scoped>
.const {
	--border-width: 1.25px;
}

.background-card-wrapper-border {
	width: 100%;
	height: 100%;

	padding: var(--border-width);
	background: var(--main-gradient-bottom);
}

.background-card-wrapper {
	width: 100%;
	height: 100%;

	padding: 0px;
	margin: var(--border-width);
	background: var(--main-gradient-bottom);

	border: 0;

	transition: all 0.125s ease;
}

.background-card {
	width: 100%;
	height: 100%;

	display: flex;
	align-items: center;
	justify-content: center;

	background: var(--primary-bg-color);
}

.hovered {
	padding: var(--border-width);
	margin: 0px;
}
</style>
