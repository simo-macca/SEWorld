<template>
  <Header/>

  <section
      v-if="loading"
      class="py-4 px-3 d-flex align-items-start justify-items-start min-h-screen-header flex-column">
    <Loader></Loader>
  </section>

  <section
      v-else-if="isEmpty"
      class="py-4 px-3 d-flex align-items-start justify-items-start min-h-screen-header flex-column"
  >
    <GoBackPage :page-before="previousViewName"/>
    <div class="col-12 col-md-6">
      <h1 class="fw-bolder">
        Exercise Statistic: "{{ exerciseInfo.exerciseTitle }}"
      </h1>
    </div>
    <div class="d-flex align-items-center justify-content-center w-100 flex-grow-1">
      <h1>
        There are
        <span class="highlight">no statistics available</span> for this
        exercise
      </h1>
    </div>
  </section>

  <section
      v-else-if="error"
      class="py-4 px-3 d-flex align-items-start justify-items-start min-h-screen-header flex-column">
    <GoBackPage :page-before="previousViewName"/>
    <ErrorMessage
        :error="`We cannot retrieve your statistics for this exercise: ${exerciseInfo.exerciseTitle}.`"
    ></ErrorMessage>
  </section>

  <section v-else class="container-fluid p-4">
    <GoBackPage :page-before="previousViewName"/>
    <!-- Header & Summary Badges -->
    <div class="row align-items-center mb-4">
      <div class="col-12 col-md-6">
        <h1 class="fw-bolder">
          Exercise Statistic: "{{ exerciseInfo.exerciseTitle }}"
        </h1>
      </div>
      <div class="col-12 col-md-6">
        <div
            v-if="!isEmpty"
            class="d-flex flex-wrap justify-content-md-end gap-2"
        >
          <b-badge :variant="avgColor" class="my-width px-4 py-2"
          >Avg: {{ averageGrade }}
          </b-badge
          >
          <b-badge
              variant="secondary"
              class="my-width px-4 py-2 bg-primary-dark"
          >
            Students: {{ exerciseInfo.studentsWhoAttempted }}
          </b-badge>
          <b-badge
              variant="secondary"
              class="my-width px-4 py-2 bg-primary-dark"
          >
            Tot. attempts: {{ totalAttempts }}
          </b-badge>
        </div>
      </div>
    </div>

    <div v-if="!isEmpty">
      <div class="d-flex gap-2 mb-3 justify-content-center justify-content-sm-end" >
        <b-button class="sort-by-button" id="sort-by-popover">
					<span class="d-flex align-items-center justify-content-center gap-2 fs-5">
						<IconSortGradient class="icon-sort"/>
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
          <template #title>
            <span class="title-popover-sort">Select Sorting</span>
          </template>

          <b-form-radio-group
              v-model="selectedSortKey"
              :options="
							SORTING_OPTIONS.map((opt) => ({
								value: opt.key,
								text: opt.label,
							}))
						"
              name="sorting-options"
              stacked
          />
        </b-popover>
      </div>

      <!-- Question Stats Grid -->
      <b-row>
        <b-col
            v-for="q in exerciseInfo.questions"
            :key="q.id"
            cols="12"
            md="6"
            lg="4"
        >
          <QuestionStat
              :questionText="q.questionTitle"
              :percentage="computeSuccessRate(q)"
          />
        </b-col>
      </b-row>
    </div>
  </section>
</template>

<script setup>
import {ref, computed, onMounted, watch} from 'vue';

import {useRoute} from 'vue-router';
import router from '@/router/index.js';
import { useNavStore } from '@/stores/navigation';
const nav = useNavStore();

import Header from '@/components/Header.vue';
import QuestionStat from '@/components/QuestionStat.vue';
import GoBackPage from '@/components/GoBackPage.vue';
import ErrorMessage from '@/components/ErrorMessage.vue';

import IconSortGradient from '@/components/icons/IconSortGradient.vue';

import {
  BBadge,
  BButton,
  BCol,
  BRow,
  BFormRadioGroup,
  BPopover,
} from 'bootstrap-vue-next';

import {
  sortQuestionsByTitle,
  sortQuestionsByResult,
  sortByTitleReverse,
  sortByQuestionResultReverse,
} from '@/utils/statistics';
import {useUserStore} from '@/stores/user.js';
import {useStatisticsStore} from '@/stores/statistics.js';
import {useExercisesStatisticsFilterStore} from '@/stores/exercisesStatisticsFilter';

const exercisesStatisticsFilterStore = useExercisesStatisticsFilterStore();
const statsStore = useStatisticsStore();
const route = useRoute();
const userStore = useUserStore();
const loading = ref(true);
const error = ref(false);

const selectedSortKey = ref('title');
const SORTING_OPTIONS = [
  {key: 'title', label: 'A-Z', fn: sortQuestionsByTitle},
  {key: 'result', label: 'Best answer', fn: sortQuestionsByResult},
  {key: 'title_reverse', label: 'Z-A', fn: sortByTitleReverse},
  {
    key: 'result_reverse',
    label: 'Worst answer',
    fn: sortByQuestionResultReverse,
  },
];

watch(selectedSortKey, (newKey, _) => {
  applySort(newKey);
});

const previousViewName = computed(() => nav?.previous || 'Exercise');

const exerciseInfo = computed(() => statsStore.exerciseInfo);

const averageGrade = computed(() =>
    exerciseInfo.value.avgUsersGrade.toFixed(2)
);

let isEmpty = computed(
    () => statsStore.exerciseInfo.studentsWhoAttempted === 0
);

const avgColor = computed(() => {
  const avg = parseFloat(averageGrade.value);
  if (avg >= 60) {
    return 'success';
  }
  if (avg >= 40) {
    return 'warning';
  }
  if (avg < 40) {
    return 'danger';
  }
  return 'info';
});

const totalAttempts = computed(
    () => exerciseInfo.value.questions?.[0]?.totalAttempts ?? 0
);

async function loadStatistics() {
  loading.value = true;
  error.value = false;
  try {
    await statsStore.fetchSingleExerciseStatistic(route.params.exerciseDid);
    sortQuestionsByTitle(exerciseInfo.value);
  } catch (err) {
    error.value = true;

    if (String(err).includes('404')) {
      isEmpty.value = true;
    }

    console.error(err);
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  if (!userStore.isInstructor) {
    await router.replace({path: '/'});
    return;
  }

  setTimeout(async () => {
    await loadStatistics()
    exercisesStatisticsFilterStore.loadFromStorage();
    selectedSortKey.value = exercisesStatisticsFilterStore.selectedSortKey;
    applySort(selectedSortKey.value);
  }, 1000)

});

function applySort(key) {
  const opt = SORTING_OPTIONS.find((o) => o.key === key);
  if (!opt) {
    return;
  }

  exercisesStatisticsFilterStore.setSelectedSortKey(opt.key);
  opt.fn(exerciseInfo.value);
}

function computeSuccessRate(q) {
  const raw =
      100 - (q.wrongAnswers / exerciseInfo.value.studentsWhoAttempted) * 100;
  return Math.round(raw * 100) / 100;
}
</script>

<style scoped>
.icon-sort {
  width: 27px;
  height: 27px;
  stroke-width: 2px;
}

.sort-by-button {
  border-radius: 0;
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

:deep(.popover-form .popover-header) {
  background: var(--main-gradient-right) !important;
  color: white;
}

:deep(.popover-form .popover-body) {
  background-color: var(--primary-bg-color-deep-dark) !important;
  color: white;
}

.bg-primary-dark {
  background: var(--primary-bg-color-mid-dark) !important;
}

@media (max-width: 576px) {
  .my-width {
    width: 100%;
  }
}
</style>
