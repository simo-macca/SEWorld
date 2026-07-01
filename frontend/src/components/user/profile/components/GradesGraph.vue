<script>
import {
	ACCENT_COLOR_1,
	ACCENT_COLOR_1_BRIGHTER,
	AVERAGE_COLOR,
	ACCENT_COLOR_3,
	PRIMARY_BG_COLOR,
	WHITE,
} from '@/utils/constants';

import { useStatisticsStore } from '@/stores/statistics';

import { Chart } from 'vue-chartjs';
import {
	Chart as ChartJS,
	BarElement,
	LineElement,
	PointElement,
	BarController,
	LineController,
	CategoryScale,
	LinearScale,
	Title,
	Tooltip,
	Legend,
} from 'chart.js';

ChartJS.register(
	Title,
	Tooltip,
	Legend,
	BarElement,
	BarController,
	LineController,
	CategoryScale,
	LineElement,
	LinearScale,
	PointElement
);

export default {
	name: 'GradesGraph',

	components: {
		Chart,
	},

	props: {
		renderAsInstructor: {
			type: Boolean,
			required: false,
			default: false,
		},
	},

	inject: ['ahTopicsCommunicationService'],

	data() {
		return {
			windowWidth: window.innerWidth,

			chartData: {
				// Topic names
				// undefined
				labels: this.statisticsStore().getTopicTitles,

				// data
				datasets: this.renderAsInstructor
					? [
							{
								type: 'bar',
								label: 'Users Average Grade',
								// undefined
								data: this.statisticsStore()
									.getTopicAvgUsersGrades,
								backgroundColor: this.getInitialBarColors(),
								borderColor: PRIMARY_BG_COLOR,
								borderWidth: 0,
								order: 1,
								maxBarThickness: 100,
							},
					  ]
					: [
							{
								type: 'bar',
								label: 'Your Grade',
								data: this.statisticsStore().getTopicUserGrades,
								backgroundColor: this.getInitialBarColors(),
								borderColor: PRIMARY_BG_COLOR,
								borderWidth: 0,
								order: 1,
								maxBarThickness: 100,
							},
							// Average dots: Users Average Topic Grades
							{
								type: 'line',
								label: 'Users Average Grade',
								pointStyle: 'rect',
								data: this.statisticsStore()
									.getTopicAvgUsersGrades,
								borderColor: PRIMARY_BG_COLOR,
								backgroundColor: AVERAGE_COLOR,
								pointBackgroundColor: AVERAGE_COLOR,
								borderWidth: 0,
								pointRadius: 10,
								pointHoverRadius: 7,
								order: 0,
							},
					  ],
			},
		};
	},

	methods: {
		statisticsStore() {
			return useStatisticsStore();
		},

		// Chart animation methods
		getInitialBarColors() {
			// Return an array of colors for each bar
			return this.statisticsStore().topics.map(() => ACCENT_COLOR_1);
		},
		updateBarColors() {
			const service = this.ahTopicsCommunicationService;

			// Create a new array of colors based on active/hovered state
			const newColors = this.statisticsStore().getTopicDids.map(
				(topicDid) => {
					if (service.activeTopicsDid.includes(topicDid)) {
						return ACCENT_COLOR_3;
					} else if (service.hoveredTopicsDid.includes(topicDid)) {
						return ACCENT_COLOR_1_BRIGHTER;
					} else {
						return ACCENT_COLOR_1;
					}
				}
			);

			// Update the chart's dataset
			this.$refs.gradesChart.chart.data.datasets[0].backgroundColor =
				newColors;
			this.$refs.gradesChart.chart.update();
		},

		// Resizing styles updates
		updateWidth() {
			this.windowWidth = window.innerWidth;
		},
	},

	// Watch for changes through the service in the other component
	watch: {
		// Watch for changes in the service's active topics
		'ahTopicsCommunicationService.activeTopicsDid': {
			handler() {
				this.updateBarColors();
			},
			deep: true,
		},

		// Watch for changes in the service's hovered topics
		'ahTopicsCommunicationService.hoveredTopicsDid': {
			handler() {
				this.updateBarColors();
			},
			deep: true,
		},
	},
	computed: {
		computeLabelsFontSize() {
			if (this.windowWidth < 600) {
				// Small screen
				return 8;
			} else if (this.windowWidth < 1200) {
				// Medium screen
				return 10;
			} else {
				// Large screen
				return 14;
			}
		},

		chartOptions() {
			return {
				responsive: true,
				maintainAspectRatio: false,
				scales: {
					y: {
						grid: {
							color: WHITE,

							color: (context) => {
								// Get the current tick value
								const lastTick = context.tick
									? context.tick.value
									: null;
								if (lastTick > 10) {
									return 'rgba(0, 0, 0, 0)';
								}

								return 'rgb(255, 255, 255)';
							},
						},
						beginAtZero: true,
						max: 10.5,
						ticks: {
							stepSize: 1,
							color: WHITE,
							callback: function (value) {
								return value > 10 ? '' : value;
							},
						},

						border: {
							display: false,
						},
					},

					x: {
						grid: {
							color: WHITE,
							display: false,
						},
						ticks: {
							color: WHITE,

							font: {
								size: this.computeLabelsFontSize,
							},
						},
					},
				},
				plugins: {
					legend: {
						position: 'top',
						labels: {
							// Generate labels overrides the text color, small imprecision
							color: WHITE,
						},
					},

					tooltip: {
						callbacks: {
							label: function (tooltipItem) {
								const label = tooltipItem.dataset.label || '';
								const yValue = tooltipItem.formattedValue;

								return `${label}: ${yValue}`;
							},
							titleColor: WHITE,
							bodyColor: WHITE,
						},
					},
				},
			};
		},
	},

	mounted() {
		window.addEventListener('resize', this.updateWidth);
	},
	unmounted() {
		window.removeEventListener('resize', this.updateWidth);
	},
};
</script>


<template>
	<Chart
		v-if="statisticsStore().areTopicsFetched"
		ref="gradesChart"
		class="w-100 h-100"
		:type="'bar'"
		:data="chartData"
		:options="chartOptions"
	/>
</template>


<style scoped>
</style>