<script>
import { BCol, BContainer, BRow, BPopover } from 'bootstrap-vue-next';

import IconBiCopy from '~icons/bi/copy';
import IconShare from '@/components/icons/IconShare.vue';

import { useUserStore } from '@/stores/user';
import { useStatisticsStore } from '@/stores/statistics';

// Grades card
import UserTopicsGradeCardVue from '@/components/user/profile/components/UserTopicsGradeCard.vue';

// Topics card
import UserTopicsCompletionCardVue from '@/components/user/profile/components/UserTopicsCompletionCard.vue';

// Communication Service
import { ahTopicsCommunicationService } from '@/services/ahTopicsCommunicationService';

import PrimaryButton from '@/components/buttons/PrimaryButton.vue';

import { isValidUUID } from '@/utils/uuid';
import { errorPopup, successPopup } from '@/utils/globalPopup';

// Search
import { useSearchStore } from '@/stores/search';
import SearchUserProfile from '@/components/user/profile/SearchUserProfile.vue';

export default {
	name: 'UserProfile',

	components: {
		BCol,
		BContainer,
		BRow,
		UserTopicsGradeCardVue,
		UserTopicsCompletionCardVue,
		IconBiCopy,
		IconShare,
		BPopover,
		PrimaryButton,
	},

	provide() {
		return {
			// Provide services to children
			ahTopicsCommunicationService,
		};
	},

	data() {
		return {
			isLoading: true,
			publicUserDid: undefined,
			user: undefined,
		};
	},

	methods: {
		userStore() {
			return useUserStore();
		},

		statisticsStore() {
			return useStatisticsStore();
		},

		searchStore() {
			return useSearchStore();
		},

		async handleRouteChange() {
			// I navigate from instructor to my profile view based on /did but it keeps rendering it as a user
			const user_info = this.$route.params.user_info;

			// clean topic exercises
			this.statisticsStore().clearTopicExercises();
			ahTopicsCommunicationService.clearAllTopics();

			if (isValidUUID(user_info)) {
				// we need to load a specific user view
				this.publicUserDid = user_info;

				// fetch specific user
				this.user = await this.userStore().getUserByDid(
					this.publicUserDid
				);

				// fetch user specific statistics
				await useStatisticsStore().fetchTopicsStatistics(
					this.publicUserDid
				);

				// Clean the probable search
				this.searchStore().resetWord();
			} else {
				this.publicUserDid = undefined;

				// refresh current
				this.user = await this.userStore().refreshUser();

				// fetch user statistics
				await this.statisticsStore().fetchTopicsStatistics();
			}

			this.isLoading = false;
		},

		copyUrlClipboard() {
			let url = window.location.href;

			if (url.endsWith('/profile')) {
				url = url.concat(`/${this.userStore().user.did}`);
			}

			navigator.clipboard
				.writeText(url)
				.then(() => {
					successPopup(
						'Profile Shared!',
						'URL copied to the clipboard'
					);
				})
				.catch((err) => {
					console.error(err);
					errorPopup(
						'URL Copy Error',
						'An error occurend when copying the profile url to the clipboard'
					);
				});
		},
	},

	computed: {
		computeRenderAsInstructor() {
			return this.publicUserDid
				? false
				: this.userStore().isInstructorField(this.user?.role);
		},

		computeIsPublicInstructorView() {
			return (
				this.publicUserDid &&
				this.userStore().isInstructorField(this.user?.role)
			);
		},

		computeIsSearchingForUser() {
			return (
				this.searchStore().getWord !== undefined &&
				this.searchStore().getWord.length > 0
			);
		},
	},

	async created() {
		this.handleRouteChange();
	},

	watch: {
		$route(_to, _from) {
			this.handleRouteChange();
		},
	},

	// on unmound clear store exercises since they are fetched only on missing
	unmounted() {
		this.statisticsStore().clearTopicExercises();
		ahTopicsCommunicationService.clearAllTopics();
	},
};
</script>

<template>
	<div
		v-if="!computeIsSearchingForUser"
		class="d-flex flex-column align-items-center justify-content-start gap-4 px-2 px-lg-5 py-3 py-sm-4 py-lg-5 custom-height"
	>
		<!-- Header with user info -->
		<div
			class="w-100 user-info-header d-flex align-items-center justify-content-start gap-3 flex-column"
		>
			<div
				v-if="publicUserDid"
				class="d-flex flex-column align-items-start justify-content-center w-100"
			>
				<h1 class="fw-bold">You are viewing the profile of</h1>
			</div>

			<div
				class="d-flex flex-column flex-sm-row align-items-center justify-content-between gap-3 gap-sm-0 w-100"
			>
				<div
					class="d-flex flex-column flex-sm-row align-items-sm-end align-items-center justify-content-start gap-5 w-100"
				>
					<div
						class="d-flex flex-column align-items-center align-items-sm-start justify-content-center"
					>
						<h1 v-if="isLoading" class="fw-bold">Loading...</h1>
						<h1 v-else-if="user" class="fw-bold">
							{{ user.name }}
						</h1>
						<h1 v-else class="fw-bold">User not found</h1>

						<div
							class="d-flex flex-row flex-wrap gap-2 gap-sm-5 align-items-end justify-content-center"
							style=""
						>
							<div
								class="d-flex align-items-center justify-content-center"
								style="height: 35px"
							>
								<small>{{ user?.email || '' }}</small>
							</div>

							<div
								class="d-flex align-items-center justify-content-center"
								style="height: 35px"
							>
								<h3 class="mb-0">
									<span class="highlight">{{
										user?.role || ''
									}}</span>
								</h3>
							</div>
						</div>
					</div>
				</div>

				<div class="d-flex flex-row align-items-end">
					<div class="d-flex flex-row align-items-center gap-4">
						<IconShare
							id="hover-target"
							@click="copyUrlClipboard"
							class="copy-icon click"
						/>
						<b-popover
							target="hover-target"
							triggers="hover"
							placement="left"
							custom-class="custom-popover"
						>
							Click to copy the URL to share the profile with your
							friends!
						</b-popover>
					</div>
				</div>
			</div>
		</div>

		<!-- Main content area -->
		<b-row
			v-if="
				// Not loading
				!this.isLoading &&
				// Topics are here
				statisticsStore().areTopicsFetched &&
				// Statistics are here
				!statisticsStore().areTopicsStatisticsEmpty &&
				// you are not viewing an instructor profile
				!computeIsPublicInstructorView
			"
			class="h-100 w-100"
		>
			<!-- Right column with topic details -->
			<b-col
				cols="12"
				md="5"
				class="px-2 py-4 py-sm-2 order-2 order-md-1"
			>
				<!-- Topics Completion Card -->
				<UserTopicsCompletionCardVue
					:userDid="publicUserDid"
					:renderAsInstructor="computeRenderAsInstructor"
				/>
			</b-col>

			<!-- Left column with grade graph -->
			<b-col cols="12" md="7" class="order-1 order-md-2">
				<!-- Grade Card -->
				<UserTopicsGradeCardVue
					:renderAsInstructor="computeRenderAsInstructor"
				/>
			</b-col>
		</b-row>
		<b-row
			v-else-if="
				// We are still loading
				this.isLoading
			"
			class="h-100 w-100 d-flex justify-content-center align-items-center"
		>
			<h1
				class="m-0 p-0 d-flex justify-content-center align-items-center w-100 gap-2"
			>
				Loading available <span class="highlight">Statistics</span>
			</h1>
		</b-row>
		<b-row
			v-else-if="
				// We are viewing a public instructor view
				computeIsPublicInstructorView
			"
			class="h-100 w-100 d-flex justify-content-center align-items-center"
		>
			<!-- Instructor related public view -->
			<h1
				class="m-0 p-0 d-flex justify-content-center align-items-center w-100 gap-2"
			>
				Reach out for help!
			</h1>
		</b-row>
		<b-row
			v-else
			class="h-100 w-100 d-flex justify-content-center align-items-center"
		>
			<!-- No data available to load -->
			<h1
				class="m-0 p-0 d-flex justify-content-center align-items-center w-100 gap-2"
			>
				No <span class="highlight">Statistics</span> available to
				display
			</h1>
		</b-row>
	</div>
	<div v-else>
		<SearchUserProfile :searchName="searchStore().getWord" />
	</div>
</template>

<style scoped>
.custom-height {
	height: calc(100vh - var(--header-height));
}

.copy-icon {
	width: 26px;
	height: 38px;
	stroke-width: 0.05;
	stroke: currentColor;
	outline: none;
}

.user-info-header {
	padding-bottom: 10px;
	border-bottom: solid 1px rgba(255, 255, 255, 0.6);
}

@media screen and (max-width: 576px) {
	.user-info-header {
		padding-bottom: 20px;
	}
}
</style>
