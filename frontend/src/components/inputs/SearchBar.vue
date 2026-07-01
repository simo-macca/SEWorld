<script>
import IconBiSearch from '~icons/bi/search';

import { useSearchStore } from '@/stores/search';

export default {
	name: 'SearchBar',

	components: {
		IconBiSearch,
	},

	data() {
		return {
			query: '',
		};
	},

	props: {
		// A possible default search bar value passed in
		value: {
			type: String,
			required: false,
		},
		// The rotine will be called when any action occurs,
		// and as argument it will receive the current input
		// text
		customRoutine: {
			type: Function,
			required: false,
			default: undefined,
		},
		// Placeholder is the placeholder text
		placeholder: {
			type: String,
			required: false,
			default: 'Search',
		},
		// This function is a custom function that will override
		// the standard input function if passed in
		customOnInput: {
			type: Function,
			required: false,
			default: null,
		},
		// This function is a custom function that will override
		// the standard onKey function if passed in
		customOnKeyDown: {
			type: Function,
			required: false,
			default: null,
		},
		// This is the max width that the input component will
		// have, if not specified defaults to 500.
		maxWidth: {
			type: Number,
			required: false,
			default: 500,
		},
	},

	watch: {
		query(newQuery) {
			// we write to the store
			this.searchStore.writeWord(newQuery);

			if (this.customRoutine) {
				this.customRoutine(newQuery);
			}
		},

		'searchStore.word'(val) {
			if (val === '') this.query = '';
		},
	},

	computed: {
		searchStore() {
			return useSearchStore();
		},
	},

	created() {
		this.query = this.value;
	},
};
</script>

<template>
	<div class="search-bar-container flex-center p-3">
		<div
			class="search-bar-wrapper flex-center"
			:style="`max-width: ${maxWidth}px;`"
		>
			<div class="search-bar flex-center gap-2">
				<div class="flex-center">
					<IconBiSearch class="search-icon" />
				</div>
				<input
					v-model="query"
					:type="'text'"
					:placeholder="placeholder"
					class="search-input shadow-lg"
				/>
			</div>
		</div>
	</div>
</template>

<style scoped>
.flex-center {
	display: flex;
	align-items: center;
	justify-content: center;
}

.search-bar-container {
	--padding-size: 2.5px;

	width: 100%;
	height: 100%;
}

.search-icon {
	width: 24px;
	cursor: pointer;
}

.search-bar-wrapper {
	flex-grow: 1;
	width: 100%;

	border: 1px solid #ccc;
	border-radius: 8px;

	padding: 0px;

	background: var(--main-gradient-bottom);

	transition: all 0.125s ease;
}

.search-bar-wrapper:focus-within {
	padding: var(--padding-size);
}

.search-bar {
	flex-grow: 1;

	border-radius: 8px;
	background: var(--primary-bg-color);

	padding: calc(var(--padding-size) + 0.5rem) calc(var(--padding-size) + 1rem);

	transition: all 0.125s ease;
}

.search-bar:focus-within {
	padding: calc(0px + 0.5rem) calc(0px + 1rem);
}

.search-input {
	outline: none;

	width: 100%;

	padding: 0.25rem 0.5rem;

	border-radius: 8px;
	border: transparent;

	background: var(--primary-bg-color);
	color: var(--primary-text-color);

	font-size: 1rem;
}

@media screen and (max-width: 500px) {
	.search-icon {
		width: 18px;
	}
}
</style>