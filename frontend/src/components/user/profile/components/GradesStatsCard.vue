<script>
export default {
	props: {
		cardName: {
			type: String,
			required: true,
		},
		value: {
			type: String,
			required: true,
		},
	},
};
</script>

<template>
	<div
		class="d-flex flex-column align-items-center justify-center content-wrapper"
	>
		<h5>{{ cardName }}</h5>
		<div class="container-grades rounded fw-bold">
			{{ value }}
		</div>
	</div>
</template>

<style scoped>
.content-wrapper {
	border-radius: 15px;
	padding: 15px;
	/* I need the rgba for the opacity, same color as #0a192f */
	background: rgba(10, 25, 47, 0.6);
}

.container-grades {
	position: relative;
	padding: 3px 10px 3px 10px;
	background: linear-gradient(to right, #ee7724, #d8363a, #dd3675, #b44593);
}
</style>