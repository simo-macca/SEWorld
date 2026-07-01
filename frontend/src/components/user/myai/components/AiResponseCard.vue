<script>
import SecondaryCardVue from '@/components/cards/SecondaryCard.vue';
import PublishMaterialButtonVue from '@/components/PublishMaterialButton.vue';

import IconBichevroncompactup from '~icons/bi/chevron-compact-up';
import IconBichevroncompactdown from '~icons/bi/chevron-compact-down';

import { infoPopup } from '@/utils/globalPopup';
import PrimaryButtonVue from '@/components/buttons/PrimaryButton.vue';

import IconChatRoundVue from '@/components/icons/IconChatRound.vue';
import PublicPrivatePlaceholderButton from '@/components/buttons/PublicPrivatePlaceholderButton.vue';

import { BButton, BCollapse, vBToggle } from 'bootstrap-vue-next';

import { MdPreview } from 'md-editor-v3';
import { useUserStore } from '@/stores/user';
import Rating from '@/components/buttons/Rating.vue';

import CommentPopupVue from '@/components/popup/CommentPopup.vue';

export default {
	name: 'AiResponseCard',

	components: {
		SecondaryCardVue,
		PublishMaterialButtonVue,
		BButton,
		BCollapse,
		IconBichevroncompactup,
		IconBichevroncompactdown,
		MdPreview,
		PrimaryButtonVue,
		IconChatRoundVue,
		PublicPrivatePlaceholderButton,
		Rating,
		CommentPopupVue,
	},

	directives: {
		'b-toggle': vBToggle,
	},

	props: {
		responseDid: {
			typeof: String,
			required: true,
		},
		isPublic: {
			typeof: Boolean,
			required: true,
		},
		rating: {
			typeof: Number,
			required: true,
		},
		userRating: {
			typeof: Number,
			required: false,
		},
		question: {
			typeof: String,
			required: true,
		},
		highlighted: {
			typeof: String,
			required: true,
		},
		answer: {
			typeof: String,
			required: true,
		},
		type: {
			typeof: String,
			required: true,
		},
		userAnswer: {
			typeof: String,
			required: false,
		},
	},

	data() {
		return {
			isArrowUp: false,
		};
	},

	computed: {
		isInstructor() {
			return this.userStore().isInstructor;
		},
	},

	methods: {
		userStore() {
			return useUserStore();
		},

		emitPublishResponse(did) {
			this.$emit('publishResponse', did);
		},

		toggleArrow() {
			this.isArrowUp = !this.isArrowUp;
		},

		toBeImplemented() {
			infoPopup(
				'Missing Feature',
				'The feature is yet to be implemented'
			);
		},
	},
};
</script>


<template>
	<SecondaryCardVue
		:padding="`p-1`"
		:withBorder="true"
		class="d-flex align-items-center justify-content-center flex-column"
		style="border-radius: 12px"
	>
		<div
			class="rating-wrapper w-100 d-flex flex-column flex-md-row align-items-center justify-content-between gap-3 px-3 py-2"
			style="min-height: 130px"
		>
			<div class="question-wrapper-size custom-scrollbar scroll-visible">
				<h2 class="question-size m-0">
					{{ question }}
				</h2>
			</div>

			<div
				class="d-flex flew-row align-items-center justify-content-end gap-2 gap-md-4 responsive-width"
			>
				<div @click="toggleArrow" style="cursor: pointer">
					<div v-if="isArrowUp" class="arrow-up">
						<BButton
							v-b-toggle="`collapse-${responseDid}`"
							class="bg-transparent d-flex align-items-center justify-items-center"
							style="width: 40px; height: 40px"
						>
							<IconBichevroncompactup class="icon" />
						</BButton>
					</div>
					<div v-else>
						<BButton
							v-b-toggle="`collapse-${responseDid}`"
							class="bg-transparent d-flex align-items-center justify-items-center"
							style="width: 40px; height: 40px"
						>
							<IconBichevroncompactdown class="icon" />
						</BButton>
					</div>
				</div>

				<PublishMaterialButtonVue
					v-if="isInstructor && !isPublic"
					@publish="emitPublishResponse"
					:elementId="responseDid"
					:label="`By making an AI response public everyone will be able to see it`"
				/>

				<CommentPopupVue :aiResponseDid="responseDid" v-if="isPublic" />

				<PublicPrivatePlaceholderButton
					v-if="!isInstructor"
					:ai-response-did="responseDid"
					:is-public="isPublic"
				/>

				<Rating
					v-if="isInstructor"
					:ai-response-did="responseDid"
					:rating="rating"
					:isAbleToVote="false"
				/>
				<Rating
					v-else
					:ai-response-did="responseDid"
					:rating="rating"
					:vote="userRating"
				/>
			</div>
		</div>

		<BCollapse :id="`collapse-${responseDid}`" class="w-100">
			<div
				class="w-100 d-flex flex-column align-items-center justify-content-center gap-3"
			>
				<div style="height: 2px; width: 80%; background: gray" />
				<div
					class="w-100 px-3 d-flex flex-column align-items-start justify-content-center gap-4 py-2"
				>
					<div
						class="w-100 d-flex flex-column align-items-start justify-content-center gap-2"
					>
						<h2 class="highlight">
							{{
								type == 'EXERCISE' ? 'Response' : 'Highlighted'
							}}
						</h2>

						<div
							class="custom-scrollbar w-100"
							style="max-height: 500px"
						>
							<MdPreview
								id="markdown-highlighted"
								class="bg-transparent"
								:language="`en-US`"
								:previewTheme="`github`"
								:theme="'dark'"
								:modelValue="
									type == 'EXERCISE'
										? userAnswer
										: highlighted
								"
							/>
						</div>
					</div>
					<div
						class="w-100 d-flex flex-column align-items-start justify-content-start gap-2"
					>
						<h2 class="highlight">
							{{
								type == 'EXERCISE'
									? 'Why it is wrong'
									: 'Response'
							}}
						</h2>

						<div
							class="custom-scrollbar w-100"
							style="max-height: 500px"
						>
							<MdPreview
								id="markdown-answer"
								class="bg-transparent"
								:language="`en-US`"
								:previewTheme="`github`"
								:theme="'dark'"
								:modelValue="answer"
							/>
						</div>
					</div>
				</div>
			</div>
		</BCollapse>
	</SecondaryCardVue>
</template>


<style scoped>
.question-wrapper-size {
	max-height: 90px;
	width: 100%;
	overflow: scroll;
}
.question-size {
	width: 100%;
}

.responsive-width {
	width: 100%;
}

.scroll-visible {
	overflow-y: scroll;
}

@media (min-width: 768px) {
	.question-wrapper-size {
		width: 100%;
	}
	.question-size {
		max-width: 85%;
	}

	.responsive-width {
		width: auto;
	}
}
</style>