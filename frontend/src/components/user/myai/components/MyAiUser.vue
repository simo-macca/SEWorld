<script>
import {
	SORTING_OPTIONS,
	TYPE_OPTIONS_USER,
	TYPE_VISIBILITY_USER,
	useMyAiFilterStoreUser,
} from '@/stores/myAiFilterUser';

import { useMyAiStore } from '@/stores/myAi';

import Loader from '@/components/Loader.vue';

import { useUserStore } from '@/stores/user';

import { BPopover, BButton, BFormRadioGroup } from 'bootstrap-vue-next';

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

			sortBy: 'A-Z',

			aiResponses: [],

			SORTING_OPTIONS,
			TYPE_OPTIONS_USER,
			TYPE_VISIBILITY_USER,
		};
	},

	methods: {
		myAiFilterStoreUser() {
			return useMyAiFilterStoreUser();
		},

		myAiStore() {
			return useMyAiStore();
		},

		userStore() {
			return useUserStore();
		},

		async computeAiResponsesElementsView() {
			await this.myAiStore().getAiResponsesUser(
				this.userStore().getUserDid
			);

			// Filter by private and public
			let responses;

			const visibilityTypesFilters =
				this.myAiFilterStoreUser().getVisibilityTypesSelected;
			if (
				visibilityTypesFilters.includes('PRIVATE') &&
				visibilityTypesFilters.includes('PUBLIC')
			) {
				responses = this.myAiStore().getResponses;
			} else if (visibilityTypesFilters.includes('PUBLIC')) {
				responses = this.myAiStore().getPublicResponses;
			} else if (visibilityTypesFilters.includes('PRIVATE')) {
				responses = this.myAiStore().getPrivateResponses;
			} else {
				responses = this.myAiStore().getResponses;
			}

			console.log(responses);

			const mainTypesFilters =
				this.myAiFilterStoreUser().getMainTypesSelected.length == 0
					? [...TYPE_OPTIONS_USER.map((to) => to.value)]
					: this.myAiFilterStoreUser().getMainTypesSelected;

			// Filter responses type
			responses = this.myAiStore().filterByResponseTypeUser(
				responses,
				mainTypesFilters
			);

			// Sort responses
			const sortBy = this.myAiFilterStoreUser().getSortBy;
			switch (sortBy) {
				case 'A-Z':
					responses = this.myAiStore().sortedQuestionAsc(responses);
					break;
				case 'Z-A':
					responses = this.myAiStore().sortedQuestionDesc(responses);
					break;
				case 'BR':
					responses = this.myAiStore().sortedRatingDesc(responses);
					break;
				case 'WR':
					responses = this.myAiStore().sortedRatingAsc(responses);
					break;

				default:
					responses = this.myAiStore().sortedQuestionAsc(responses);
					break;
			}

			// Return view
			this.aiResponses = responses;
		},

		toggleSortBy(type) {
			// update
			this.myAiFilterStoreUser().setSortBy(type);
			this.computeAiResponsesElementsView();
		},

		toggleMainTypes(type) {
			// update
			this.myAiFilterStoreUser().toggleMainType(type);
			this.computeAiResponsesElementsView();
		},

		toggleVisibilityTypes(type) {
			// update
			this.myAiFilterStoreUser().toggleVisibilityType(type);
			this.computeAiResponsesElementsView();
		},

		isMainTypeSelected(type) {
			return this.myAiFilterStoreUser().isMainTypeSelected(type);
		},

		isVisibilityTypeSelected(type) {
			return this.myAiFilterStoreUser().isVisibiltyTypeSelected(type);
		},
	},

	watch: {
		sortBy(newVal, _) {
			this.toggleSortBy(newVal);
		},
	},

	async created() {
		await this.myAiStore().getAiResponsesUser(this.userStore().getUserDid);

		this.myAiFilterStoreUser().loadFromStorage();
		// always set private by default
		if (!this.isVisibilityTypeSelected('PRIVATE')) {
			this.myAiFilterStoreUser().toggleVisibilityType('PRIVATE');
		}

		this.sortBy = this.myAiFilterStoreUser().getSortBy;

		await this.computeAiResponsesElementsView();

		this.isLoading = false;
	},
};
</script>

<template>
	<div v-if="isLoading"><Loader /></div>
	<div
		v-else
		class="w-100 d-flex flex-column align-items-center justify-content-center py-2"
		style="padding-bottom: 100px !important"
	>
		<div
			class="section-header width-95-85 d-flex flex-row align-items-center justify-content-between gap-4 py-3"
			style="margin-top: 15px"
		>
			<h3 class="fs-1 m-0">Your AI responses</h3>

			<!-- Sorting options -->
			<div
				class="d-flex flex-row align-items-center justify-content-end w-sorting-options flex-wrap gap-4"
			>
				<b-button class="sort-by-button" :id="`sort-by-popover`">
					<span
						class="d-flex align-items-center justify-content-center gap-2 fs-5"
					>
						<IconSortGradient class="icon-sort" />
						Sort
					</span>
				</b-button>
				<b-popover
					:target="`sort-by-popover`"
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
						v-model="sortBy"
						:options="SORTING_OPTIONS"
						:name="`sorting-options`"
						stacked
					/>

					<!-- Should be made as a component -->
					<div
						class="btn-group d-flex flex-row align-items-center justify-content-center flex-wrap mt-2"
						role="group"
					>
						<b-button
							v-for="to in TYPE_OPTIONS_USER"
							:key="`button-${to.value}`"
							:class="`click main-type-buttons ${
								isMainTypeSelected(to.value)
									? 'main-type-buttons-selected'
									: ''
							}`"
							:variant="
								isMainTypeSelected(to.value)
									? 'primary'
									: 'outline-primary'
							"
							@click="toggleMainTypes(to.value)"
							>{{ to.text }}</b-button
						>
					</div>

					<div
						class="btn-group d-flex flex-row align-items-center justify-content-center flex-wrap mt-2"
						role="group"
					>
						<b-button
							v-for="to in TYPE_VISIBILITY_USER"
							:key="`button-${to.value}`"
							:class="`click main-type-buttons ${
								isVisibilityTypeSelected(to.value)
									? 'main-type-buttons-selected'
									: ''
							}`"
							:variant="
								isVisibilityTypeSelected(to.value)
									? 'primary'
									: 'outline-primary'
							"
							@click="toggleVisibilityTypes(to.value)"
							>{{ to.text }}</b-button
						>
					</div>
				</b-popover>
			</div>
		</div>

		<!-- Empty Section Placeholder -->
		<div
			v-if="aiResponses.length === 0"
			class="section-placeholder width-95-85 border-opaque height-screen d-flex flex-row align-items-center justify-content-center"
		>
			You do not have AI responses yet
		</div>

		<!-- Regular Secondary Card -->
		<div
			v-else
			class="width-95-85 border-opaque height-screen custom-scrollbar d-flex flex-column align-items-center justify-content-start py-2"
		>
			<div
				class="width-85-75 d-flex flex-column align-items-center justify-content-center gap-3"
			>
				<template v-for="res in aiResponses" :key="res.aiResponseDid">
					<AiResponseCard
						:isPublic="res.public"
						:responseDid="res.aiResponseDid"
						:rating="res.rating"
						:userRating="res.userRating"
						:question="res.questionText"
						:highlighted="res.highlightedText"
						:answer="res.answer"
						:type="res.type"
						:userAnswer="res.userAnswer"
					/>
				</template>
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
