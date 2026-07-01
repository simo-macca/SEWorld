<script>
import PrimaryCard from '@/components/cards/PrimaryCard.vue';

export default {
	name: 'UserSearchCard',

	components: {
		PrimaryCard,
	},

	props: {
		user: {
			type: Object,
			required: true,
		},
	},

	methods: {
		click() {
			// redirecto to /profile/:user_info
			this.$router.push({
				name: 'Profile page',
				params: { user_info: this.user.did },
			});
		},

		highlightedName(name) {
			return name.replace(
				/[A-Z]/g,
				(match) => `<span class="highlight">${match}</span>`
			);
		},
	},
};
</script>


<template>
	<PrimaryCard
		class="card-wrapper shadow click"
		:with-hover="true"
		@click="click"
	>
		<div class="card">
			<h1 v-html="highlightedName(user.name)" />
			<h3>{{ user.email }}</h3>
		</div>
	</PrimaryCard>
</template>


<style scoped>
.card-wrapper {
	width: 100%;

	max-width: 750px;
	cursor: pointer;
}

.card {
	color: white;
	background: transparent;

	display: flex;
	align-items: center;
	justify-content: center;

	flex-direction: column;
}
</style>