<script>
import { RouterLink } from 'vue-router';

import {
	BNavbar,
	BNavbarNav,
	BNavItem,
	BNavbarBrand,
	BCollapse,
	BNavbarToggle,
	BNavItemDropdown,
	BDropdownItem,
	BNavForm,
	BButton,
	vBColorMode,
} from 'bootstrap-vue-next';

import IconLens from './icons/IconLens.vue';
import IconUser from './icons/IconUser.vue';

import { useUserStore } from '@/stores/user';

import SearchBar from './inputs/SearchBar.vue';

const SEARCH_BAR_PAGES = [
	'Profile page',
	'Materials',
	'Topics List',
	'Exercises List',
];

export default {
	name: 'Header',

	components: {
		IconLens,
		IconUser,
		BNavbar,
		BNavbarNav,
		BNavItem,
		BNavbarBrand,
		BCollapse,
		BNavbarToggle,
		BNavItemDropdown,
		BDropdownItem,
		BNavForm,
		BButton,
		RouterLink,
		SearchBar,
	},

	directives: {
		'b-color-mode': vBColorMode,
	},

	data() {
		return {
			user: useUserStore(),
			SEARCH_BAR_PAGES,
		};
	},

	computed: {
		computePlaceholderSearchBar() {
			switch (this.$route.name) {
				case 'Profile page':
					return 'Search for a User';
				case 'Topics List':
					return 'Search for a Topic';
				case 'Materials':
					return 'Search for a Material';
				case 'Exercises List':
					return 'Search for an Exercise';
				default:
					return 'Search';
			}
		},
	},

	async mounted() {
		const user = useUserStore();
		await user.refreshUser();
	},
};
</script>

<template>
	<BNavbar v-b-color-mode="'dark'" toggleable="lg" class="bg-background">
		<div class="brand-wrapper">
			<BNavbarToggle class="navbar-toggler-white" target="nav-collapse" />
			<BNavbarBrand>
				<RouterLink to="/" class="link brand">
					<span class="highlight">SE</span>World
				</RouterLink>
			</BNavbarBrand>
		</div>

		<BNavItemDropdown right no-caret class="order-item">
			<template #button-content>
				<div
					v-if="user.isLoggedIn"
					class="d-flex flex-column align-items-center justify-content-center gap-2"
				>
					<div class="user-highlight">
						<h1 class="user-initials">{{ user.getUserInitials }}</h1>
					</div>
				<small class="text-muted user-role" style="font-size: 0.75rem;">
					{{ user.user?.role || '' }}
				</small>
			</div>
				<div v-else class="user-highlight">
					<IconUser />
				</div>
			</template>

			<BDropdownItem
				v-if="user.isLoggedIn"
				:to="
					$route.path === '/profile' &&
					$route.query.search == undefined
						? ''
						: '/profile'
				"
			>
				<RouterLink
					class="link"
					:to="
						$route.path === '/profile' &&
						$route.query.search == undefined
							? ''
							: '/profile'
					"
				>
					<span
						:class="{
							highlight:
								$route.path === '/profile' &&
								$route.query.search == undefined,
						}"
					>
						Profile
					</span>
				</RouterLink>
			</BDropdownItem>

			<BDropdownItem
				v-if="user.isLoggedIn"
				:to="
					$route.path === '/MyAI' && $route.query.search == undefined
						? ''
						: '/MyAI'
				"
			>
				<RouterLink
					class="link"
					:to="
						$route.path === '/MyAI' &&
						$route.query.search == undefined
							? ''
							: '/MyAI'
					"
				>
					<span
						:class="{
							highlight:
								$route.path === '/MyAI' &&
								$route.query.search == undefined,
						}"
					>
						My AI
					</span>
				</RouterLink>
			</BDropdownItem>

			<BDropdownItem v-if="user.isLoggedIn">
				<RouterLink
					:to="$route.path === '/login' ? '' : '/login'"
					class="link"
				>
					Sign Out
				</RouterLink>
			</BDropdownItem>
			<BDropdownItem v-else>
				<RouterLink class="link" to="/login">Sign In</RouterLink>
			</BDropdownItem>
		</BNavItemDropdown>

		<BCollapse class="order-collapse" id="nav-collapse" is-nav>
			<BNavbarNav>
				<BNavItem class="d-flex align-items-center justify-items-center">
					<RouterLink
						:to="$route.path === '/about' ? '' : '/about'"
						class="link2"
					>
						<span :class="{ highlight: $route.path === '/about' }">About</span>
					</RouterLink>
				</BNavItem>
				<BNavItem class="d-flex align-items-center justify-items-center">
					<RouterLink
						:to="$route.path === '/topics' ? '' : '/topics'"
						class="link2"
					>
						<span :class="{ highlight: $route.path === '/topics' }">Topics</span>
					</RouterLink>
				</BNavItem>
			</BNavbarNav>

			<div
				v-if="SEARCH_BAR_PAGES.includes($route.name)"
				class="w-searchbar-wrapper"
			>
				<SearchBar
					:placeholder="computePlaceholderSearchBar"
					class="align-items-start"
				/>
			</div>
		</BCollapse>
	</BNavbar>
</template>

<style scoped>
li,
ul {
	list-style: none;
	padding: 0;
	margin: 0;
}

.bg-background {
	background: #0a192f;
	position: sticky;
	top: 0;
	z-index: 9999;
	min-height: var(--header-height);
}

.link {
	color: var(--primary-text-color);
	font-weight: 800;
	font-size: x-large;
	border: transparent;
	text-decoration: none;
}

.link2 {
	font-weight: 800;
	font-size: x-large;
	border: transparent;
	text-decoration: none;
	color: var(--primary-text-color);
}

.brand {
	font-size: 45px;
	color: var(--primary-text-color);
}

.highlight {
	font-weight: bolder;
	background: linear-gradient(to right, #ee7724, #d8363a, #dd3675, #b44593);
	-webkit-background-clip: text;
	background-clip: text;
	-webkit-text-fill-color: transparent;
	display: inline-block;
}

.user-highlight {
	padding: 6px;
	width: 60px;
	height: 60px;
	background: linear-gradient(to right, #ee7724, #d8363a, #dd3675, #b44593);
	border-radius: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.user-initials {
	margin: 0;
	color: white;
	font-size: 1.2rem;
}

.user-role {
  font-size: 0.8rem;
  font-weight: 500;
  text-transform: capitalize;
}

.brand-wrapper {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 10px;
}

.w-searchbar-wrapper {
	width: 100%;
}

@media (min-width: 992px) {
	.order-item {
		order: 2;
	}

	.order-collapse {
		order: 1;
	}

	.w-searchbar-wrapper {
		width: 75%;
	}
}
</style>
