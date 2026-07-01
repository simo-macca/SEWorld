<script>
import { useMyAiStore } from '@/stores/myAi';
import {
	SORTING_OPTIONS,
	TYPE_OPTIONS,
	useMyAiFilterStore,
} from '@/stores/myAiFilter';

import Loader from '@/components/Loader.vue';

import { BPopover, BButton, BFormRadioGroup } from 'bootstrap-vue-next';
import { errorPopup, successPopup } from '@/utils/globalPopup';

export default {
	components: {
		BPopover,
		BButton,
		BFormRadioGroup,
		Loader,
	},

	data() {
		return {
			isLoading: true,

			publicAiResponses: [],
			privateAiResponses: [],

			sortByPublic: 'A-Z',
			sortByPrivate: 'A-Z',

			SORTING_OPTIONS,
			TYPE_OPTIONS,
		};
	},

	computed: {
		computeBothAiResponsesElementsView() {
			this.computePrivateAiResponsesElementsView;
			this.computePublicAiResponsesElementsView;
		},
		computePublicAiResponsesElementsView() {
			let publicResponses = this.myAiStore().getPublicResponses;

			// Filter public responses
			const filters = this.myAiFilterStore().getMainTypesSelectedPublic;
			publicResponses = this.myAiStore().filterByResponseType(
				publicResponses,
				filters
			);

			// Sort public responses
			const sortBy = this.myAiFilterStore().getSortByPublic;
			switch (sortBy) {
				case 'A-Z':
					publicResponses =
						this.myAiStore().sortedQuestionAsc(publicResponses);
					break;
				case 'Z-A':
					publicResponses =
						this.myAiStore().sortedQuestionDesc(publicResponses);
					break;
				case 'BR':
					publicResponses =
						this.myAiStore().sortedRatingDesc(publicResponses);
					break;
				case 'WR':
					publicResponses =
						this.myAiStore().sortedRatingAsc(publicResponses);
					break;

				default:
					publicResponses =
						this.myAiStore().sortedQuestionAsc(publicResponses);
					break;
			}

			// Return view
			this.publicAiResponses = publicResponses;
		},
		computePrivateAiResponsesElementsView() {
			let privateResponses = this.myAiStore().getPrivateResponses;

			// Filter private responses
			const filters = this.myAiFilterStore().getMainTypesSelectedPrivate;
			privateResponses = this.myAiStore().filterByResponseType(
				privateResponses,
				filters
			);

			// Sort private responses
			const sortBy = this.myAiFilterStore().getSortByPrivate;
			switch (sortBy) {
				case 'A-Z':
					privateResponses =
						this.myAiStore().sortedQuestionAsc(privateResponses);
					break;
				case 'Z-A':
					privateResponses =
						this.myAiStore().sortedQuestionDesc(privateResponses);
					break;
				case 'BR':
					privateResponses =
						this.myAiStore().sortedRatingDesc(privateResponses);
					break;
				case 'WR':
					privateResponses =
						this.myAiStore().sortedRatingAsc(privateResponses);
					break;

				default:
					privateResponses =
						this.myAiStore().sortedQuestionAsc(privateResponses);
					break;
			}

			// Return view
			this.privateAiResponses = privateResponses;
		},
	},

	methods: {
		myAiStore() {
			return useMyAiStore();
		},

		myAiFilterStore() {
			return useMyAiFilterStore();
		},

		async publishResponse(did) {
			console.log('PUBLISHING');

			const success = await this.myAiStore().publishAiResponse(did);

			// fetch responses
			if (success) {
				await this.myAiStore().getAiResponses();
				this.computeBothAiResponsesElementsView;
				successPopup(
					'Published Response',
					'The response is now public for everyone to see'
				);
			} else {
				errorPopup(
					'Error Publishing Response',
					'The response has not been published due to an error'
				);
			}
		},

		toggleSortBy(type, isPublic) {
			// update
			this.myAiFilterStore().setSortBy(isPublic, type);

			if (isPublic) {
				this.computePublicAiResponsesElementsView;
			} else {
				this.computePrivateAiResponsesElementsView;
			}
		},

		toggleMainTypes(type, isPublic) {
			// update
			this.myAiFilterStore().toggleMainType(isPublic, type);

			if (isPublic) {
				this.computePublicAiResponsesElementsView;
			} else {
				this.computePrivateAiResponsesElementsView;
			}
		},

		isMainTypeSelected(type, isPublic) {
			return this.myAiFilterStore().isMainTypeSelected(isPublic, type);
		},
	},

	watch: {
		sortByPublic(newVal, _) {
			this.toggleSortBy(newVal, true);
		},

		sortByPrivate(newVal, _) {
			this.toggleSortBy(newVal, false);
		},
	},

	async created() {
		await this.myAiStore().getAiResponses();

		// set default selected filter and sort values
		this.myAiFilterStore().loadFromStorage();

		this.sortByPrivate = this.myAiFilterStore().getSortByPrivate;
		this.sortByPublic = this.myAiFilterStore().getSortByPublic;

		this.computeBothAiResponsesElementsView;

		this.isLoading = false;
	},
};
</script>

<template>
	<div v-if="isLoading"><Loader /></div>
	<div
		v-else
		class="w-100 d-flex flex-column align-items-center justify-content-center py-2"
		style="padding-bottom: 100px !important; gap: 125px"
	>
		<div
			class="d-flex flex-column align-items-center justify-content-center w-100"
		>
			<!-- Private -->
			<div
				class="section-header width-95-85 d-flex flex-row align-items-center justify-content-between gap-4 py-3"
				style="margin-top: 15px"
			>
				<h3 class="fs-1 m-0">Private AI Responses</h3>

				<!-- Sorting options -->
				<div
					class="d-flex flex-row align-items-center justify-content-end w-sorting-options flex-wrap gap-4"
				>
					<b-button
						class="sort-by-button"
						:id="`sort-by-popover-private`"
					>
						<span
							class="d-flex align-items-center justify-content-center gap-2 fs-5"
						>
							<IconSortGradient class="icon-sort" />
							Sort
						</span>
					</b-button>
					<b-popover
						:target="`sort-by-popover-private`"
						triggers="hover"
						placement="bottom"
						auto-close="outside"
						class="popover-form"
					>
						<template #title
							><span class="title-popover-sort"
								>Select Sorting</span
							></template
						>

						<b-form-radio-group
							v-model="sortByPrivate"
							:options="SORTING_OPTIONS"
							:name="`sorting-options-private`"
							stacked
						/>

						<!-- Should be made as a component -->
						<div
							class="btn-group d-flex flex-row align-items-center justify-content-center flex-wrap mt-2"
							role="group"
						>
							<b-button
								v-for="to in TYPE_OPTIONS"
								:key="`button-${to.value}`"
								:class="`click main-type-buttons ${
									isMainTypeSelected(to.value, false)
										? 'main-type-buttons-selected'
										: ''
								}`"
								:variant="
									isMainTypeSelected(to.value, false)
										? 'primary'
										: 'outline-primary'
								"
								@click="toggleMainTypes(to.value, false)"
								>{{ to.text }}</b-button
							>
						</div>
					</b-popover>
				</div>
			</div>

			<!-- Empty Section Card -->
			<div
				v-if="privateAiResponses.length == 0"
				class="section-placeholder width-95-85 border-opaque height-screen d-flex flex-column align-items-center justify-content-center"
			>
				There are no private AI responses yet
			</div>
			<!-- Regular Secondary Card -->
			<div
				v-else
				class="width-95-85 border-opaque height-screen custom-scrollbar d-flex flex-column align-items-center justify-content-start py-2"
			>
				<div
					class="width-85-75 d-flex flex-column align-items-center justify-content-center gap-3"
				>
					<template
						v-for="res in privateAiResponses"
						:key="res.aiResponseDid"
					>
						<AiResponseCard
							:isPublic="false"
							:responseDid="res.aiResponseDid"
							:rating="res.rating"
							:question="res.questionText"
							:highlighted="res.highlightedText"
							:answer="res.answer"
							@publishResponse="publishResponse"
							:type="res.type"
							:userAnswer="res.userAnswer"
						/>
					</template>
				</div>
			</div>
		</div>

		<div
			class="d-flex flex-column align-items-center justify-content-center w-100"
		>
			<!-- Public -->
			<div
				class="section-header width-95-85 d-flex flex-row align-items-center justify-content-between gap-4 py-3"
				style="margin-top: 15px"
			>
				<h3 class="fs-1 m-0">Public AI Responses</h3>

				<!-- Sorting options -->
				<div
					class="d-flex flex-row align-items-center justify-content-end w-sorting-options flex-wrap gap-4"
				>
					<b-button
						class="sort-by-button"
						:id="`sort-by-popover-public`"
					>
						<span
							class="d-flex align-items-center justify-content-center gap-2 fs-5"
						>
							<IconSortGradient class="icon-sort" />
							Sort
						</span>
					</b-button>
					<b-popover
						:target="`sort-by-popover-public`"
						triggers="hover"
						placement="bottom"
						auto-close="outside"
						class="popover-form"
					>
						<template #title
							><span class="title-popover-sort"
								>Select Sorting</span
							></template
						>

						<b-form-radio-group
							v-model="sortByPublic"
							:options="SORTING_OPTIONS"
							:name="`sorting-options-public`"
							stacked
						/>

						<!-- Should be made as a component -->
						<div
							class="btn-group d-flex flex-row align-items-center justify-content-center flex-wrap mt-2"
							role="group"
						>
							<b-button
								v-for="to in TYPE_OPTIONS"
								:key="`button-${to.value}`"
								:class="`click main-type-buttons ${
									isMainTypeSelected(to.value, true)
										? 'main-type-buttons-selected'
										: ''
								}`"
								:variant="
									isMainTypeSelected(to.value, true)
										? 'primary'
										: 'outline-primary'
								"
								@click="toggleMainTypes(to.value, true)"
								>{{ to.text }}</b-button
							>
						</div>
					</b-popover>
				</div>
			</div>

			<!-- Empty Section Card -->
			<div
				v-if="publicAiResponses.length == 0"
				class="section-placeholder width-95-85 border-opaque height-screen d-flex flex-column align-items-center justify-content-center"
			>
				There are no public AI responses yet
			</div>
			<!-- Regular Secondary Card -->
			<div
				v-else
				class="width-95-85 border-opaque height-screen custom-scrollbar d-flex flex-column align-items-center justify-content-start py-2"
			>
				<div
					class="width-85-75 d-flex flex-column align-items-center justify-content-center gap-3"
				>
					<template
						v-for="res in publicAiResponses"
						:key="res.aiResponseDid"
					>
						<AiResponseCard
							:isPublic="true"
							:responseDid="res.aiResponseDid"
							:rating="res.rating"
							:question="res.questionText"
							:highlighted="res.highlightedText"
							:answer="res.answer"
							:topicDid="res.topicDid"
							@publishResponse="publishResponse"
							:type="res.type"
							:userAnswer="res.userAnswer"
						/>
					</template>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
.width-95-85 {
	width: 95%;
}

.border-opaque {
	border-bottom: 2px solid rgba(255, 255, 255, 0.2);
}

.width-85-75 {
	width: 90%;
}

.height-screen {
	height: 70vh;
	overflow: scroll;
}

/* Popover */
.icon-sort {
	width: 27px;
	height: 27px;
	stroke-width: 2px;
}
.sort-by-button {
	border-radius: 0px;
	background: transparent;
	border: 1px solid white;
	padding: 7px 20px 7px 20px;
}
.sort-by-button:focus {
	background: transparent;
}
.title-popover-sort {
	font-weight: 500;
}

.main-type-buttons {
	color: white;
	border-color: var(--accent-color-1);
}

.main-type-buttons:hover {
	color: white;
	background: var(--accent-color-2);
}

.main-type-buttons:focus {
	color: white;
	border-color: var(--accent-color-1);
}

.main-type-buttons-selected {
	color: white;
	background: var(--accent-color-3);
}

/* Popover background general styles */
:deep(.popover-form .popover-header) {
	background: var(--main-gradient-right) !important;
	color: white;
}
:deep(.popover-form .popover-body) {
	/* border: 1px solid white !important; */
	background-color: var(--primary-bg-color-deep-dark) !important;
	color: white;
}

@media (min-width: 768px) {
	.width-95-85 {
		width: 85%;
	}
	.width-85-75 {
		width: 80%;
	}
}
</style>