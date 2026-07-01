<script>
import { vBToggle, BCol, BRow, BCollapse, BButton } from 'bootstrap-vue-next';

import { useStatisticsStore } from '@/stores/statistics';

import IconBiBarChart from '~icons/bi/barChart';

import { RouterLink } from 'vue-router';

export default {
	name: 'TopicCompletionCard',

	components: {
		BCollapse,
		BButton,
		BCol,
		BRow,
		RouterLink,
		IconBiBarChart,
	},

	directives: {
		'b-toggle': vBToggle,
	},

	props: {
		renderAsInstructor: {
			type: Boolean,
			required: false,
			default: false,
		},
		userDid: {
			type: String,
			required: false,
			default: undefined,
		},
		topic: {
			type: Object,
			required: true,
		},
	},

	inject: ['ahTopicsCommunicationService'],

	data() {
		return {
			isCollapsed: true,
			isHovered: false,
		};
	},

	computed: {},

	methods: {
		highlightStyle(completion) {
			completion = Number(completion);

			// I'll leave it here as we might revise the logic
			// background: `linear-gradient(to top,
			// 					#ee7724 ${completion * 0.25}%,
			// 					#d8363a ${completion * 0.5}%,
			// 					#dd3675 ${completion * 0.75}%,
			// 					#b44593 ${completion}%,
			// 					white ${completion}%)`,

			return {
				background: `white`,
				WebkitBackgroundClip: 'text',
				backgroundClip: 'text',
				WebkitTextFillColor: 'transparent',
				fontWeight: 'bold',
				display: 'inline-block',
				transition: 'opacity 0.3s ease',
			};
		},

		statisticsStore() {
			return useStatisticsStore();
		},

		// Mouse hover logic
		handleMouseEnter() {
			// hover status
			this.isHovered = true;
			this.ahTopicsCommunicationService.addHoveredTopic(
				this.topic.topicDid
			);

			// load exercise
			this.statisticsStore().fetchTopicExercisesStatisticsByDid(
				this.topic.topicDid,
				this.userDid
			);
		},
		handleMouseLeave() {
			// hover status
			this.isHovered = false;
			this.ahTopicsCommunicationService.removeHoveredTopic(
				this.topic.topicDid
			);

			if (!this.isCollapsed) {
				// leave but it is still open
				return;
			}
		},

		// Mouse click logic (toggle exercise infos)
		toggleCollapse() {
			this.isCollapsed = !this.isCollapsed;

			if (this.isCollapsed) {
				this.ahTopicsCommunicationService.removeActiveTopic(
					this.topic.topicDid
				);
			} else {
				this.ahTopicsCommunicationService.addActiveTopic(
					this.topic.topicDid
				);
			}
		},

		handleRouteChange() {
			// reset all cards styles to closed status
			this.isHovered = false;
			this.isCollapsed = true;
			this.$refs.collapseEl?.hide();
		},
	},

	watch: {
		$route(_to, _from) {
			this.handleRouteChange();
		},
	},
};
</script>

<template>
	<div
		class="rounded background-border w-full"
		:class="{ 'active-border shadow': !isCollapsed || isHovered }"
		@mouseenter="handleMouseEnter"
		@mouseleave="handleMouseLeave"
	>
		<div
			class="rounded background-card p-3 d-flex justify-items-center align-items-start flex-column w-100"
		>
			<div
				class="d-flex justify-content-between align-items-center m-0 gap-1 w-100 flex-column flex-md-row pb-2"
			>
				<h3 class="m-0">
					{{ topic.topicTitle }}
				</h3>

				<div
					class="d-flex justify-content-between align-items-center gap-3 flex-row"
				>
					<div v-if="!renderAsInstructor">
						<!-- User parameter -->
						<span class="fw-bold">
							<span :style="highlightStyle(topic.completionStage)"
								>{{ Number(topic.completionStage).toFixed(0) }}%</span
							>
						</span>
					</div>

					<BButton
						v-b-toggle="`collapse-${topic.topicDid}`"
						class="highlight toggle-button px-2"
						style="width: 108px"
						:disabled="
							statisticsStore().doesTopicExerciseStatisticsExistByDid(
								topic.topicDid
							)
								? statisticsStore().isTopicExerciseStatisticsEmpty(
										topic.topicDid
								  )
								: false
						"
						@click="toggleCollapse"
						>{{
							statisticsStore().doesTopicExerciseStatisticsExistByDid(
								topic.topicDid
							)
								? statisticsStore().isTopicExerciseStatisticsEmpty(
										topic.topicDid
								  )
									? 'No Statistics'
									: isCollapsed
									? 'Expand'
									: 'Collapse'
								: 'Expand'
						}}</BButton
					>
				</div>
			</div>

			<BCollapse
				ref="collapseEl"
				:id="`collapse-${topic.topicDid}`"
				class="w-100"
			>
				<!-- Exercise list -->
				<ul
					class="m-0 p-0 d-flex align-items-start justify-items-center flex-column text-white"
					v-if="
						statisticsStore().doesTopicExerciseStatisticsExistByDid(
							topic.topicDid
						)
					"
				>
					<li
						v-for="e in statisticsStore().getTopicExercisesStatisticsByDid(
							topic.topicDid
						)"
						:key="e.exerciseDid"
						class="d-flex justify-content-between align-items-center w-100 px-4 m-0"
					>
						<div>
							<!-- <span>•</span> -->
							{{ e.exerciseTitle }}
						</div>

						<div
							:class="`d-flex gap-4 align-items-center justify-items-between`"
							style="min-width: 100px"
						>
							<span
								v-if="!renderAsInstructor && e.successful"
								class="fs-6 d-flex align-items-center justify-content-center"
								style="height: 42px"
							>
								✅
							</span>
							<span
								v-else-if="!renderAsInstructor"
								class="fs-6 d-flex align-items-center justify-content-center"
								style="height: 42px"
							>
								❌
							</span>

							<router-link
								v-if="renderAsInstructor"
								class="bar-chart-link"
								style="height: 42px"
								:to="`/statistic/${e.exerciseDid}`"
							>
								<IconBiBarChart
									style="width: 25px"
									class="bi bi-bar-chart click"
								/>
							</router-link>

							<span
								class="d-flex align-items-center justify-content-center"
								style="width: 45px; height: 42px"
							>
								<span style="height: 36px">
									{{
										renderAsInstructor
											? e.avgUsersGrade
											: e.userGrade
									}}
								</span>
							</span>
						</div>
					</li>
				</ul>

				<!-- Average grade for topic -->
				<div
					class="w-100 d-flex align-items-center justify-content-end gap-3 px-4"
					v-if="
						statisticsStore().doesTopicExerciseStatisticsExistByDid(
							topic.topicDid
						)
					"
				>
					<span class="fs-5 fs-md-3">Average:</span>

					<span class="fw-bold">{{
						statisticsStore().getTopicExercisesAverageByDid(
							topic.topicDid
						)
					}}</span>
				</div>
			</BCollapse>
		</div>
	</div>
</template>

<style scoped>
.background-card {
	background: var(--primary-bg-color);
	cursor: pointer;
}

.background-border {
	padding: 0px;
	background: linear-gradient(to right, #ee7724, #d8363a, #dd3675, #b44593);
	transition: all 125ms ease;
}

.background-border:hover,
.active-border {
	padding: 1.75px;
}

.bar-chart-link {
	color: white;
}

.bar-chart-link:hover {
	color: #b44593;
}
</style>
