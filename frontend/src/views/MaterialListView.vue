<script>
import * as bootstrap from 'bootstrap';

// Stores
import { useUserStore } from '@/stores/user';
import { useMaterialStore } from '@/stores/material';
import { useTopicsStore } from '@/stores/topics';

// Components
import Header from '@/components/Header.vue';
import MaterialCard from '@/components/MaterialCard.vue';
import CreateMaterialModal from '@/components/CreateMaterialModal.vue';
import EditMaterialModal from '@/components/EditMaterialModal.vue';

// Icons
import IconBiEyeFill from '~icons/bi/EyeFill';
import IconBiEyeSlash from '~icons/bi/EyeSlash';
import IconBiTrash from '~icons/bi/trash';
import IconSortGradient from '@/components/icons/IconSortGradient.vue';
import IconFilterGradient from '@/components/icons/IconFilterGradient.vue';

import {
	BButton,
	BFormCheckboxGroup,
	BPopover,
	BFormRadioGroup,
} from 'bootstrap-vue-next';

// Filter
import { useMaterialFilterStore } from '@/stores/materialFilter';

const SORTING_OPTIONS = [
	{ value: 'A-Z', text: 'A-Z' },
	{ value: 'Z-A', text: 'Z-A' },
];

import { FileType } from '@/utils/file';
import { useSearchStore } from '@/stores/search';

const MATERIAL_TYPES = ['md', 'link', 'file'];
const FILE_TYPES_ARRAY = Object.values(FileType);
const FILE_TYPE_OPTIONS = [
	{ value: 'PDF_FILE', text: 'PDF' },
	{ value: 'ZIP_FILE', text: 'Zip' },
	{ value: 'CODE_FILE', text: 'Code' },
	{ value: 'TEXT_FILE', text: 'Text' },
	{ value: 'IMAGE_FILE', text: 'Image' },
	{ value: 'PRESENTATION_FILE', text: 'Presentation' },
	{ value: 'AUDIO_FILE', text: 'Audio' },
	{ value: 'UNKNOWN', text: 'Others' },
];

export default {
	name: 'MaterialListView',

	components: {
		Header,
		CreateMaterialModal,
		EditMaterialModal,
		MaterialCard,
		BButton,
		IconBiEyeFill,
		IconBiEyeSlash,
		IconBiTrash,
		BPopover,
		BFormCheckboxGroup,
		BFormRadioGroup,
		IconSortGradient,
		IconFilterGradient,
	},

	data() {
		return {
			isLoading: true,

			selectedMaterial: null,
			topicDid: null,

			newMaterial: {
				title: '',
				type: '',
				description: '',
			},

			// Sorting, default is asc by name
			// v-model
			sortBy: 'A-Z',
			filterFileTypes: [],
			FILE_TYPE_OPTIONS,
			SORTING_OPTIONS,
		};
	},

	computed: {
		currentTopic() {
			return (
				this.topicStore().topics.find(
					(topic) => topic.did === this.topicDid
				) || {}
			);
		},

		// Sorting
		filterAndSortMaterials() {
			let MATERIALS = [];

			const filterMainTypes = this.materialFilterStore().filterMainTypes;
			const filterFileTypes = this.materialFilterStore().filterFileTypes;
			const sortBy = this.materialFilterStore().sortBy;

			// Filter by mateerial type
			const fmts =
				filterMainTypes.length == 0 ? MATERIAL_TYPES : filterMainTypes;
			MATERIALS = this.materialStore().filterByMaterialsType(fmts);

			// Filter by file type if necessary
			const doFilterByFileType = fmts.includes('file');
			const ffts =
				filterFileTypes.length == 0
					? FILE_TYPES_ARRAY
					: filterFileTypes;
			if (doFilterByFileType) {
				MATERIALS = this.materialStore().filterByMaterialFilesType(
					ffts,
					MATERIALS
				);
			}

			// sort based on choice
			switch (sortBy) {
				case 'A-Z':
					MATERIALS =
						this.materialStore().sortedByTitleAsc(MATERIALS);
					break;
				case 'Z-A':
					MATERIALS =
						this.materialStore().sortedByTitleDesc(MATERIALS);
					break;
				default:
					MATERIALS =
						this.materialStore().sortedByTitleAsc(MATERIALS);
					break;
			}

			// add by word filter
			const searchWord = this.searchStore().getWord;
			if (
				searchWord !== undefined &&
				typeof searchWord === 'string' &&
				searchWord.length > 0
			) {
				MATERIALS = [...MATERIALS].filter(
					(m) =>
						m.title &&
						m.title.toLowerCase().includes(searchWord.toLowerCase())
				);
			}

			return MATERIALS;
		},
	},

	methods: {
		userStore() {
			return useUserStore();
		},

		materialStore() {
			return useMaterialStore();
		},

    materialFilterStore() {
	    return useMaterialFilterStore();
    },

		topicStore() {
			return useTopicsStore();
		},

		searchStore() {
			return useSearchStore();
		},

		isInstructor() {
			return this.userStore().isInstructor;
		},

		areThereAnyMaterials() {
			return this.materialStore().materials.length != 0;
		},

		async submitNewMaterial(data) {
			try {
				await this.materialStore().submitNewMaterial(
					this.topicDid,
					data
				);
				await this.materialStore().fetchMaterials(this.topicDid);
			} catch (error) {
				console.error('Error creating material: ', error);
				alert('Something went wrong while creating the material.');
			}
		},

		openEditModal(material) {
			const modalEl = document.getElementById(
				`editMaterialModal-${material.materialDid}`
			);
			const modalInstance = new bootstrap.Modal(modalEl);
			modalInstance.show();
		},

		// Sorting
		toggleSort(sortBy) {
			this.materialFilterStore().setSortBy(sortBy);

			// Reload materials
			this.filterAndSortMaterials;
		},

		toggleMainTypes(type) {
			// Set state
			this.materialFilterStore().toggleMainType(type);

			// update materials
			this.filterAndSortMaterials;
		},

		toggleFileTypes(types) {
			// Set state
			this.materialFilterStore().toggleFileType(types);

			// update materials
			this.filterAndSortMaterials;
		},

		isMainTypeSelected(type) {
			return this.materialFilterStore().filterMainTypes.includes(type);
		},
	},

	watch: {
		sortBy(newVal, _) {
			this.toggleSort(newVal);
		},

		filterFileTypes(newVal, _) {
			this.toggleFileTypes(newVal);
		},
	},

	async created() {
		// Parse topic Did from path
		this.topicDid = this.$route.params.did;

		// Load User
		await this.userStore().refreshUser();

		// Load Materials
		await this.materialStore().fetchMaterials(this.topicDid);

		// Load Topics
		if (!this.topicStore().topics.length) {
			await this.topicStore().fetchTopics();
		}

		// load filters from local storage
		this.materialFilterStore().loadFromStorage();

		this.sortBy = this.materialFilterStore().sortBy;
		this.filterFileTypes = this.materialFilterStore().filterFileTypes;

		this.isLoading = false;
	},
};
</script>

<template>
	<Header />

	<div
		class="material-view-wrapper d-flex flex-column align-items-center justify-content-start py-4 px-5"
	>
		<div
			class="d-flex flex-row align-items-center justify-content-between w-100"
		>
			<h1 class="mb-0" style="font-size: 50px; font-weight: bolder">
				Materials for {{ currentTopic.title || '...' }}
			</h1>

			<BButton
				v-if="isInstructor()"
				class="button"
				variant="primary"
				data-bs-toggle="modal"
				data-bs-target="#createMaterialModal"
				>+</BButton
			>
		</div>
		<div
			class="pt-3 pb-3 w-100 flex-grow-1 d-flex flex-column align-items-center justify-content-center"
		>
			<div
				v-if="isLoading"
				class="d-flex align-items-center justify-content-center w-100 flex-grow-1 gap-3"
			>
				<h2 class="sr-only m-0">Loading</h2>
				<div class="spinner-border" role="status" />
			</div>

			<div
				v-else-if="!areThereAnyMaterials()"
				class="d-flex align-items-center justify-content-center w-100 flex-grow-1"
			>
				<h1>
					There are no
					<span class="highlight">materials</span> available for this
					topic.
				</h1>
			</div>

			<div
				v-else
				class="d-flex flex-column align-items-center justify-content-start w-100 flex-grow-1 py-4 gap-5"
			>
				<!-- Sorting options -->
				<div
					class="d-flex flex-row align-items-center justify-content-end w-sorting-options flex-wrap gap-4"
				>
					<b-button class="sort-by-button" id="sort-by-popover">
						<span
							class="d-flex align-items-center justify-content-center gap-2 fs-5"
						>
							<IconSortGradient class="icon-sort" />
							Sort
						</span>
					</b-button>
					<b-popover
						target="sort-by-popover"
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
							name="sorting-options"
							stacked
						/>
					</b-popover>

					<b-button id="filter-by-popover" class="filter-by-button">
						<span
							class="d-flex align-items-center justify-content-center gap-2 fs-5"
						>
							<IconFilterGradient class="icon-filter" />
							Filter
						</span></b-button
					>
					<b-popover
						target="filter-by-popover"
						triggers="hover"
						placement="bottom"
						auto-close="outside"
						class="popover-filter-form popover-form"
					>
						<template #title
							><span class="title-popover-filter"
								>Select Filters</span
							></template
						>

						<!-- Multi-Select Main Type Buttons -->
						<div
							class="btn-group d-flex flex-row align-items-center justify-content-center flex-wrap"
							role="group"
						>
							<b-button
								:class="`click main-type-buttons ${
									isMainTypeSelected('link')
										? 'main-type-buttons-selected'
										: ''
								}`"
								:variant="
									isMainTypeSelected('link')
										? 'primary'
										: 'outline-primary'
								"
								@click="toggleMainTypes('link')"
								>Link</b-button
							>

							<b-button
								:class="`click main-type-buttons ${
									isMainTypeSelected('md')
										? 'main-type-buttons-selected'
										: ''
								}`"
								:variant="
									isMainTypeSelected('md')
										? 'primary'
										: 'outline-primary'
								"
								@click="toggleMainTypes('md')"
								>Markdown</b-button
							>
							<b-button
								:class="`click main-type-buttons ${
									isMainTypeSelected('file')
										? 'main-type-buttons-selected'
										: ''
								}`"
								:variant="
									isMainTypeSelected('file')
										? 'primary'
										: 'outline-primary'
								"
								@click="toggleMainTypes('file')"
								id="file-type-popover-btn"
								>File</b-button
							>
						</div>

						<b-form-checkbox-group
							v-if="
								materialFilterStore().filterMainTypes.includes(
									'file'
								)
							"
							v-model="filterFileTypes"
							:options="FILE_TYPE_OPTIONS"
							stacked
						/>
					</b-popover>
				</div>

				<!-- Materials -->
				<div
					v-if="filterAndSortMaterials.length === 0"
					class="d-flex align-items-center justify-content-center w-100 flex-grow-1"
				>
					<h1>
						There are no
						<span class="highlight">such materials</span> available
						for this topic.
					</h1>
				</div>
				<div
					v-else
					class="w-100 d-flex flex-column gap-3 align-items-center justify-content-center"
				>
					<div
						v-for="material in filterAndSortMaterials"
						:key="material.materialDid"
						class="d-flex flex-column align-items-center justify-content-center w-material-wrapper"
					>
						<EditMaterialModal
							:topicDid="topicDid"
							:material="material"
							:id="`editMaterialModal-${material.materialDid}`"
						/>

						<MaterialCard
							:materialData="material"
							@edit="openEditModal"
						/>
					</div>
				</div>
			</div>
		</div>
	</div>

	<CreateMaterialModal
		v-if="!isLoading"
		:topicDid="topicDid"
		@submit-material="submitNewMaterial"
	/>
</template>


<style>
p {
	font-size: 21px;
}

li {
	font-size: 19px;
}
</style>


<style scoped>
.material-view-wrapper {
	min-height: calc(100vh - var(--header-height));
}

.span-title-popover {
	color: black;
}

.button {
	padding: 10px 20px;
	font-size: 20px;
}

.w-material-wrapper {
	width: 100%;
}

.w-sorting-options {
	width: 100%;
}

/* Sort */
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

/* Filter */
.icon-filter {
	width: 30px;
	height: 30px;
}
.filter-by-button {
	border-radius: 0px;
	background: transparent;
	border: 1px solid white;
	padding: 7px 20px 7px 20px;
}
.filter-by-button:focus {
	background: transparent;
}
:deep(.popover-filter-form .popover-body) {
	display: flex;
	align-content: center;
	justify-content: center;
	flex-direction: column;

	gap: 15px;
}
.title-popover-filter {
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

@media (min-width: 992px) {
	.w-material-wrapper {
		width: 75%;
	}
	.w-sorting-options {
		width: 75%;
	}
}
</style>