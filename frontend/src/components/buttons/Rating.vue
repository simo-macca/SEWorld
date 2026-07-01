<script>
import BiArrowUpShort from '~icons/bi/arrow-up-short';
import BiArrowDownShort from '~icons/bi/arrow-down-short';
import { useRatingStore } from '@/stores/rating.js';

export default {
	name: 'Rating',

	components: {
		BiArrowUpShort,
		BiArrowDownShort,
	},

	props: {
		aiResponseDid: {
			type: String,
			required: true,
		},

		isAbleToVote: {
			type: Boolean,
			required: false,
			default: true,
		},

		compactView: {
			type: Boolean,
			required: false,
			default: false,
		},

		rating: {
			type: Number,
			required: false,
			default: 0,
		},

		vote: {
			type: Number,
			required: false,
			default: 0,
		},

		dark: {
			type: Boolean,
			required: false,
			default: false,
		},
	},

	data() {
		return {
			localVote: this.$props.vote,
			localRating: this.$props.rating,
		};
	},

	methods: {
		ratingStore() {
			return useRatingStore();
		},

		async handleUpVote() {
			this.handleVote(1);
		},

		async handleResetVote() {
			this.handleVote(0);
		},

		async handleDownVote() {
			this.handleVote(-1);
		},

		async handleVote(vote) {
			if (!this.isAbleToVote) return;
			const responseData = await this.ratingStore().toggleVote(
				this.aiResponseDid,
				vote
			);

			if (responseData) {
				const rating = responseData.rating;
				const userRating = responseData.userRating;

				this.localRating = rating;
				if (userRating == undefined) {
					this.localVote = vote;
				} else {
					this.localVote = userRating;
				}
			}
		},

		// Click logic
		upVoteClick() {
			if (this.localVote === 1) {
				this.handleResetVote();
			} else {
				this.handleUpVote();
			}
		},
		// Click logic
		downVoteClick() {
			if (this.localVote === -1) {
				this.handleResetVote();
			} else {
				this.handleDownVote();
			}
		},
	},
};
</script>

<template>
	<div
		:class="`d-flex ${
			compactView ? 'flex-column' : 'flex-row'
		} align-items-center justify-content-center gap-1`"
	>
		<div
			class="cursor-pointer d-flex align-items-center justify-content-center"
			style="width: 30px; height: 30px"
			:class="{
				upvote: localVote === 1,
				disablevote: !isAbleToVote,
				cw: !dark,
				cb: dark,
			}"
			@click="upVoteClick"
		>
			<BiArrowUpShort />
		</div>

		<div
			class="cursor-pointer d-flex align-items-center justify-content-center"
			:class="{
				cw: !dark,
				cb: dark,
			}"
			style="width: 30px; height: 30px"
		>
			<span v-if="localRating < 0">-</span>
			<span>{{ Math.abs(localRating) }}</span>
		</div>

		<div
			class="cursor-pointer d-flex align-items-center justify-content-center"
			style="width: 30px; height: 30px"
			:class="{
				downvote: localVote === -1,
				disablevote: !isAbleToVote,
				cw: !dark,
				cb: dark,
			}"
			@click="downVoteClick"
		>
			<BiArrowDownShort />
		</div>
	</div>
</template>

<style scoped>
.cw {
	color: white;
}

.cb {
	color: black;
}

span {
	font-weight: bold;
	font-size: 1.1rem;
}

.cursor-pointer {
	cursor: pointer;
}

svg {
	stroke-width: 5;
}

.upvote {
	color: #198754; /* green for upvote or */
}

.downvote {
	color: #dc3545; /* red for downvote */
}

.disablevote {
	opacity: 0.3;
}
</style>