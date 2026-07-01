<script setup>
import {
	ref,
	computed,
	onMounted,
	nextTick,
	onBeforeUnmount,
	watchEffect,
} from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useAttemptsStore } from '@/stores/attempts.js';
import { useExercisesStore } from '@/stores/exercises';
import { useTopicsStore } from '@/stores/topics';

import { BButton, BPopover, BModal } from 'bootstrap-vue-next';
import Header from '@/components/Header.vue';
import PrimaryButton from '@/components/buttons/PrimaryButton.vue';
import SecondaryButton from '@/components/buttons/SecondaryButton.vue';
import DeleteMaterialButton from '@/components/DeleteMaterialButton.vue';

import IconBiTrash from '~icons/bi/trash';
import IconBackArrow from './icons/IconBackArrow.vue';
import MaterialSymbolsSave from '~icons/material-symbols/save';
import IconParkOutlinePreviewOpen from '~icons/icon-park-outline/preview-open';
import IcRoundPlus from '~icons/ic/round-plus';
import IcRoundChevronLeft from '~icons/ic/round-chevron-left';
import IcRoundChevronRight from '~icons/ic/round-chevron-right';

import { successPopup, errorPopup } from '@/utils/globalPopup';

const route = useRoute();
const router = useRouter();
const topicsStore = useTopicsStore();
const store = useExercisesStore();
const attemptStore = useAttemptsStore();

const selectedTopic = ref('');
const showTooltip = ref(false);
const questionsContainer = ref(null);

// Add error state variables
const topicError = ref(false);
const titleError = ref(false);
const descriptionError = ref(false);

const isEdit = computed(() => route.path.includes('/edit'));

const windowWidth = ref(window.innerWidth);

const showTypeChangeModal = ref(false);
const pendingTypeChange = ref({
	question: null,
	newType: '',
});

const aiLoading = ref({});

function goToExercisesPage() {
	const topicDid = route.params.topicDid;
	if (!topicDid) return;

	router.push(`/exercises/${topicDid}`);
}

const updateWidth = () => {
	windowWidth.value = window.innerWidth;
};

const isSmallScreen = computed(() => {
	return windowWidth.value <= 575;
});

let originalOrder = [];

onMounted(async () => {
	await topicsStore.fetchTopics();
	store.resetForm();

	if (isEdit.value) {
		const exerciseDid = route.params.exerciseDid;
		await store.populateForm(exerciseDid);
	}

	selectedTopic.value = route.params.topicDid;

	// Store initial order of variantIndexes
	originalOrder = [...new Set(store.questions.map((q) => q.variantIndex))];

	// UI sizing
	window.addEventListener('resize', updateWidth);
});

onBeforeUnmount(() => {
	// UI sizing cleanup
	window.removeEventListener('resize', updateWidth);
});

const scrollToBottom = () => {
	nextTick(() => {
		if (questionsContainer.value) {
			questionsContainer.value.scrollTop =
				questionsContainer.value.scrollHeight;
		}
	});
};

const addQuestionAndScroll = () => {
	store.addQuestion();
	scrollToBottom();
};

const validateForm = () => {
	let isValid = true;

	// Reset previous error states
	topicError.value = false;
	titleError.value = false;
	descriptionError.value = false;

	store.questions.forEach((q) => {
		q.showTypeError = false;
		q.showKeywordError = false;
	});

	// Check topic
	if (!selectedTopic.value && !isEdit) {
		topicError.value = true;
		isValid = false;
	}

	// Check title
	if (!store.formTitle || store.formTitle.trim() === '') {
		titleError.value = true;
		isValid = false;
	}

	// Check description
	if (!store.formDescription || store.formDescription.trim() === '') {
		descriptionError.value = true;
		isValid = false;
	}

	// Check short answer keywords and question type
	for (const question of store.questions) {
		if (!question.type) {
			question.showTypeError = true;
			isValid = false;
		}
		if (!question.text || !question.text.trim()) {
			question.textError = true;
			isValid = false;
		}
		if (question.type === 'short') {
			const hasEmpty = !question.correctAnswer.trim();
			if (hasEmpty) {
				question.showKeywordError = true;
				isValid = false;
			}
		}
		if (question.type === 'multiple') {
			const hasEmptyOption = question.options.some((opt) => !opt.trim());
			if (hasEmptyOption) {
				question.showOptionError = true;
				isValid = false;
			}
		}
	}

	return isValid;
};

const saveExercise = async () => {
	if (!validateForm()) {
		return;
	}

	// Capture reordered variantIndex list
	const newOrder = [...new Set(store.questions.map((q) => q.variantIndex))];
	const pairs = [];

	originalOrder.forEach((val, idx) => {
		const newIdx = newOrder.indexOf(val);
		if (idx !== newIdx) {
			pairs.push({ first: idx, second: newIdx });
		}
	});

	// Add `pairs` to the store DTO before sending
	store.formSwapPairs = pairs;

	try {
		if (isEdit.value) {
			await store.updateExercise(route.params.exerciseDid);
			successPopup('Successfull Update', 'The exercise has been updated');
		} else {
			await store.createExercise(selectedTopic.value);
			successPopup('Exercise Created', 'The exercise has been created');
		}

		router.push(`/exercises/${selectedTopic.value}`);
	} catch (error) {
		const action = isEdit.value ? 'updated' : 'created';
		errorPopup(`${action} Failed`, `The exercise has not been ${action}`);
	}
};

const previewExercise = async () => {
	if (!validateForm()) return;

	let exerciseDid;
	if (isEdit.value) {
		await store.updateExercise(route.params.exerciseDid);
		exerciseDid = route.params.exerciseDid;
	} else {
		exerciseDid = await store.createExercise(selectedTopic.value);
	}

	const attemptDid = await attemptStore.createAttemptByExerciseDid(
		exerciseDid
	);

	if (attemptDid) {
		router.push({
			path: `exercises/${exerciseDid}/attempt/${attemptDid}`,
			query: { preview: 'true', topic: selectedTopic.value },
		});
	}
};

// Delete functionality for questions and options
const deleteQuestion = (questionDid) => {
	const questionToDelete = store.questions.find(
		(q) => q.questionDid === questionDid
	);
	if (!questionToDelete) return;

	const variantIndex = questionToDelete.variantIndex;
	const currentDisplayIndex = displayIndex.value[variantIndex];
	const groupLength = groupedQuestions.value[variantIndex].length;
	if (groupLength > 1) {
		if (currentDisplayIndex === groupLength - 1) {
			displayIndex.value[variantIndex] = currentDisplayIndex - 1;
		}
	} else {
		displayIndex.value.splice(variantIndex, 1);
	}
	store.deleteQuestion(questionDid);
	store.reindexVariantIndex();
};

const deleteOption = (questionDid, optionIndex) => {
	store.deleteOption(questionDid, optionIndex);
};

const deleteKeyword = (questionDid, keywordIndex) => {
	store.deleteKeyword(questionDid, keywordIndex);
};

const groupedQuestions = computed(() => {
	const questions = [];
	if (store.questions.length > 0) {
		// First, find the maximum variantIndex
		const maxVariantIndex = Math.max(
			...store.questions.map((q) => q.variantIndex ?? 0)
		);

		// Initialize arrays for all possible variant indices
		for (let i = 0; i <= maxVariantIndex; i++) {
			questions[i] = [];
		}

		// Then populate the arrays
		for (const question of store.questions) {
			const index = question.variantIndex;
			if (index !== undefined) {
				// Add check for undefined
				questions[index].push(question);
			}
		}
	}
	return questions;
});

const displayIndex = ref([]);
watchEffect(() => {
	const groupCount = groupedQuestions.value.length;
	const newDisplayIndex = [];
	for (let i = 0; i < groupCount; i++) {
		newDisplayIndex[i] = displayIndex.value[i] ?? 0;
	}
	displayIndex.value = newDisplayIndex;
});

const displayQuestions = computed(() => {
	const questions = [];
	const groups = groupedQuestions.value;
	for (const [index, group] of groups.entries()) {
		const selectedIndex = displayIndex.value[index] ?? 0;
		if (group && group[selectedIndex] !== undefined) {
			questions.push(group[selectedIndex]);
		}
	}
	return questions;
});

const hasMultipleVariants = (variantIndex) => {
	return groupedQuestions.value[variantIndex]?.length > 1;
};

const nextQuestion = (variantIndex) => {
	const len = groupedQuestions.value[variantIndex].length;
	const cur = displayIndex.value[variantIndex];
	displayIndex.value[variantIndex] = (cur + 1) % len;
};

const prevQuestion = (variantIndex) => {
	const len = groupedQuestions.value[variantIndex].length;
	const cur = displayIndex.value[variantIndex];
	displayIndex.value[variantIndex] = (cur + len - 1) % len;
};

const AddVariantAndFocus = (question) => {
	store.addVariant(question);
	const variantIndex = question.variantIndex;
	const len = groupedQuestions.value[variantIndex].length;
	displayIndex.value[variantIndex] = len - 1;
};

const AddVariantWithAIAndFocus = async (question) => {
	aiLoading.value[question.questionDid] = true;

	try {
		await store.addVariantWithAI(question);
		const variantIndex = question.variantIndex;
		const len = groupedQuestions.value[variantIndex].length;
		displayIndex.value[variantIndex] = len - 1;
	} catch (error) {
		errorPopup(
			'AI Generation Failed',
			'Something went wrong while generating the variant. Please try again.'
		);
	} finally {
		aiLoading.value[question.questionDid] = false;
	}
};

const handleTypeChange = (question, newType) => {
	if (!question._cachedType) {
		question._cachedType = newType;
		store.setQuestionType(question, newType);
		return;
	}
	if (question._cachedType === newType) return;

	pendingTypeChange.value = {
		question,
		newType,
	};
	showTypeChangeModal.value = true;
};

const confirmTypeChange = async () => {
	const { question, newType } = pendingTypeChange.value;
	const variantIdx = question.variantIndex;
	const questionTitle = question.text;

	// Delete all variants with the same variantIndex using store function
	await store.deleteQuestionsByVariantIndex(variantIdx);

	store.createNewTypeQuestion(variantIdx, newType, questionTitle);
	// Reindex all questions to ensure consecutive variant indices
	// await store.reindexVariantIndex();

	// Reset display index for this variant group
	displayIndex.value[variantIdx] = 0;

	// Set the new type for the question
	await store.setQuestionType(question, newType);

	showTypeChangeModal.value = false;
	pendingTypeChange.value = { question: null, newType: '' };
};

const cancelTypeChange = () => {
	const { question } = pendingTypeChange.value;
	if (question) {
		question.type = question._cachedType;
	}
	showTypeChangeModal.value = false;
	pendingTypeChange.value = { question: null, newType: '' };
};

const addOption = (questionDid, index) => {
	store.addOption(questionDid);
};

let dragIndex = null;
let dropIndex = null;

function onDragStart(e, index) {
	e.dataTransfer.effectAllowed = 'move';
	dragIndex = index;
}

function onDragOver(e) {
	e.preventDefault();
	e.dataTransfer.dropEffect = 'move';
}

function onDragEnter(e, index) {
	dropIndex = index;
}

function onDrop(e) {
	e.preventDefault();
	if (dragIndex === null || dropIndex === null || dragIndex === dropIndex)
		return;

	// --- 1. Build your grouped‐by‐variantIndex arrays as before ---
	const questionsCopy = [...store.questions];
	const variantIndexes = [
		...new Set(questionsCopy.map((q) => q.variantIndex)),
	];
	const groups = variantIndexes.map((v) =>
		questionsCopy.filter((q) => q.variantIndex === v)
	);

	// --- 2. Detach and reinsert the dragged group ---
	const [draggedGroup] = groups.splice(dragIndex, 1);
	groups.splice(dropIndex, 0, draggedGroup);

	// --- 3. Reassign new variantIndex to all questions in their new order ---
	let newVariantIndex = 0;
	const reordered = [];
	groups.forEach((group) => {
		group.forEach((q) => {
			q.variantIndex = newVariantIndex;
			reordered.push(q);
		});
		newVariantIndex++;
	});
	store.questions = reordered;

	// --- 4. Only record the swap if at least one swapped group has persisted questions ---
	const oldVariant = variantIndexes[dragIndex];
	const newVariant = variantIndexes[dropIndex];

	const wasExisting = (idx) => {
		return groups[idx].some(
			(q) => !String(q.questionDid).startsWith('temp-questionDid-')
		);
	};

	// if either the dragged group or the drop target group contained at least one non-temp question, record it
	if (wasExisting(dragIndex) || wasExisting(dropIndex)) {
		store.pairs.push({ first: dragIndex, second: dropIndex });
	}

	// reset drag/drop
	dragIndex = null;
	dropIndex = null;
}

function onDragEnd() {
	dragIndex = null;
	dropIndex = null;
}
</script>

<template>
	<Header />

	<div
		class="w-100 min-h-screen-header d-flex flex-column align-items-center justify-content-center p-2"
	>
		<div
			class="d-flex w-100 flex-column flex-md-row align-items-center justify-content-between gap-3"
		>
			<div
				class="d-flex flex-column flex-sm-row align-items-center justify-content-center gap-2 gap-sm-5"
			>
				<h1 class="text-nowrap py-2 m-0" style="font-size: 50px">
					<span class="highlight">{{ isEdit ? 'Edit' : 'New' }}</span>
					Exercise
				</h1>
			</div>

			<div
				class="d-flex align-items-center justify-content-center flex-row gap-3"
			>
				<SecondaryButton
					style="min-height: 50px; min-width: 50px"
					:size="'sm'"
					@click="previewExercise"
					title="preview exercise"
				>
					<IconParkOutlinePreviewOpen></IconParkOutlinePreviewOpen>
				</SecondaryButton>

				<SecondaryButton
					style="min-height: 50px; min-width: 50px"
					id="add-question-button"
					@click="addQuestionAndScroll"
					title="add new question"
				>
					<IcRoundPlus></IcRoundPlus>
				</SecondaryButton>
				<PrimaryButton
					style="min-height: 50px; min-width: 50px"
					@click="saveExercise"
					:size="'sm'"
					title="save modifications"
				>
					<MaterialSymbolsSave></MaterialSymbolsSave>
				</PrimaryButton>
			</div>
		</div>

		<main
			class="flex-grow-1 w-100 mt-4 d-flex flex-sm-row flex-column gap-3"
		>
			<div
				class="d-flex align-items-center justify-content-start flex-column gap-2 description-wrapper"
			>
				<select
					id="select-exercise-topic"
					v-model="selectedTopic"
					class="title-input"
					:disabled="isEdit"
					name="topic-title"
					:class="`border-input ${topicError ? 'error-border' : ''}`"
				>
					<option disabled value="">
						Select the topic for the exercise
					</option>
					<option
						v-for="t in topicsStore.topics"
						:key="t.did"
						:value="t.did"
					>
						{{ t.title }}
					</option>
				</select>
				<BPopover
					target="select-exercise-topic"
					:placement="isSmallScreen ? 'bottom' : 'right'"
					:visible="true"
					v-if="topicError"
				>
					<p class="error-message">Please select a topic</p>
				</BPopover>

				<input
					id="add-exercise-title"
					v-model="store.formTitle"
					@input="titleError = false"
					placeholder="Exercise title"
					:class="`border-input title-input p-1 ${
						titleError ? 'error-border' : ''
					}`"
				/>
				<BPopover
					target="add-exercise-title"
					:placement="isSmallScreen ? 'bottom' : 'right'"
					:visible="true"
					v-if="titleError"
				>
					<p class="error-message">Title is mandatory</p>
				</BPopover>

				<textarea
					id="add-exercise-description"
					v-model="store.formDescription"
					@input="descriptionError = false"
					placeholder="Exercise description"
					:class="`description-input p-1 border-input ${
						descriptionError ? 'error-border' : ''
					}`"
					rows="8"
				/>
				<BPopover
					target="add-exercise-description"
					:placement="isSmallScreen ? 'bottom' : 'right'"
					:visible="true"
					v-if="descriptionError"
				>
					<p class="error-message">Description is mandatory</p>
				</BPopover>
			</div>

			<TransitionGroup
				name="list"
				tag="div"
				ref="questionsContainer"
				class="d-flex flex-column align-items-end justify-content-start pl-0 pl-lg-5 questions-wrapper questions-scroll-container position-relative"
			>
				<div
					v-for="(question, index) in displayQuestions"
					:key="`${question.questionDid}`"
					class="question-block ..."
					draggable="true"
					@dragstart="onDragStart($event, index)"
					@dragover="onDragOver"
					@dragenter="onDragEnter($event, index)"
					@drop="onDrop"
					@dragend="onDragEnd"
				>
					<!-- AI Loading Overlay -->
					<div
						v-if="aiLoading[question.questionDid]"
						class="ai-loading-overlay"
					>
						<div class="ai-loading-text">
							Waiting for AI response...
						</div>
					</div>
					<div
						class="question-nav-area question-nav-left"
						@click="prevQuestion(question.variantIndex)"
						:class="{
							disabled: !hasMultipleVariants(
								question.variantIndex
							),
						}"
					>
						<IcRoundChevronLeft class="nav-icon" />
					</div>
					<div class="question-content d-flex flex-column gap-4">
						<div
							class="w-100 d-flex align-items-center justify-content-center gap-2"
						>
							<div
								class="d-flex align-items-center gap-2 flex-shrink-0"
							>
								{{ question.variantIndex + 1 }}.
							</div>
							<select
								:id="`select-question-type-${index}`"
								v-model="question.type"
								:class="`select-type border-input ${
									question.showTypeError ? 'error-border' : ''
								}`"
								@change="
									handleTypeChange(question, question.type)
								"
								name="question-type"
							>
								<option disabled value="">
									Select which type of question
								</option>
								<option value="multiple">
									Multiple choice
								</option>
								<option value="truefalse">True/False</option>
								<option value="short">Short answer</option>
							</select>
							<BPopover
								:target="`select-question-type-${index}`"
								:placement="isSmallScreen ? 'bottom' : 'right'"
								:visible="true"
								v-if="question.showTypeError"
							>
								<p class="error-message">
									Please select a question type
								</p>
							</BPopover>

							<div
								class="d-flex align-items-center gap-2 flex-shrink-0"
							>
								<PrimaryButton
									v-if="question._cachedType"
									:size="'xs'"
									@click="AddVariantAndFocus(question)"
									:label="'Add variant'"
								/>
								<PrimaryButton
									v-if="question._cachedType"
									:size="'xs'"
									@click="AddVariantWithAIAndFocus(question)"
									:label="'Add variant with AI'"
								/>
								<DeleteMaterialButton
									@delete="deleteQuestion"
									:element-id="`${question.questionDid}`"
									:deletion-spec-text="'the question'"
								/>
							</div>
						</div>

						<div
							v-if="question._cachedType === 'truefalse'"
							class="d-flex align-items-start justify-content-center flex-column gap-2 w-100"
						>
							<label class="w-100">
								<input
									:id="`select-tf-title-${index}`"
									v-model="question.text"
									@input="question.textError = false"
									placeholder="Question Title"
									:class="`question-input border-input p-1 ${
										question.textError ? 'error-border' : ''
									}`"
								/>
								<BPopover
									:target="`select-tf-title-${index}`"
									:placement="
										isSmallScreen ? 'bottom' : 'right'
									"
									:visible="true"
									v-if="question.textError"
								>
									<p class="error-message">
										Please add a question title
									</p>
								</BPopover>
							</label>

							<label
								class="d-flex align-items-start justify-content-center flex-column gap-1"
							>
								<label
									class="d-flex align-items-center justify-content-center gap-2 flex-row"
								>
									<input
										type="radio"
										v-model="question.correctAnswer"
										:value="true"
										class="input-radius-true"
									/>
									True
								</label>

								<label
									class="d-flex align-items-center justify-content-center gap-2 flex-row"
								>
									<input
										type="radio"
										v-model="question.correctAnswer"
										:value="false"
										class="input-radius-false"
									/>
									False
								</label>
							</label>
							<div
								class="w-100 position-relative"
								style="min-height: 30px"
							>
								<div
									class="position-absolute start-50 translate-middle-x"
									style="
										top: 50%;
										transform: translateX(-50%)
											translateY(-50%);
									"
								>
									{{ displayIndex[index] + 1 }} /
									{{ groupedQuestions[index].length }}
								</div>
							</div>
						</div>

						<div
							class="d-flex align-items-start justify-content-center flex-column gap-3 w-100"
							v-if="question._cachedType === 'multiple'"
							:id="`question-multiple-${index}`"
						>
							<BPopover
								:target="`question-multiple-${index}`"
								placement="top"
								:visible="true"
								v-if="question.showOptionError"
							>
								<p class="error-message">
									Multichoice options must all have a
									description
								</p>
							</BPopover>

							<input
								:id="`select-multi-title-${index}`"
								v-model="question.text"
								@input="question.textError = false"
								placeholder="Question Title"
								:class="`question-input border-input p-1 ${
									question.textError ? 'error-border' : ''
								}`"
							/>
							<BPopover
								:target="`select-multi-title-${index}`"
								:placement="isSmallScreen ? 'bottom' : 'right'"
								:visible="true"
								v-if="question.textError"
							>
								<p class="error-message">
									Please add a question title
								</p>
							</BPopover>

							<div
								class="d-flex align-items-start justify-content-center gap-3 flex-column w-100"
							>
								<div
									v-for="(option, i) in question.options"
									:key="i"
									class="d-flex align-items-center justify-content-center gap-2 w-100"
								>
									<input
										type="radio"
										:name="'correct-' + index"
										v-model="question.correctAnswer"
										:value="i"
										class="multi-choice-radio"
									/>

									<div class="option-input-wrapper">
										<input
											v-model="question.options[i]"
											@input="
												question.showOptionError =
													question.options.some(
														(opt) => !opt.trim()
													)
											"
											placeholder="Option Description"
											:class="`option-input border-input p-1 ${
												question.showOptionError &&
												!option.trim()
													? 'error-border'
													: ''
											}`"
										/>

										<button
											@click="
												deleteOption(
													displayQuestions[index]
														.questionDid,
													i
												)
											"
											class="x-delete-button"
											title="Delete option"
										>
											✕
										</button>
									</div>
								</div>
							</div>

							<div
								class="w-100 position-relative"
								style="min-height: 30px"
							>
								<div
									class="position-absolute start-50 translate-middle-x"
									style="
										top: 50%;
										transform: translateX(-50%)
											translateY(-50%);
									"
								>
									{{ displayIndex[index] + 1 }} /
									{{ groupedQuestions[index].length }}
								</div>

								<div
									class="d-flex align-items-center justify-content-end"
								>
									<PrimaryButton
										:size="'xs'"
										@click="
											addOption(
												displayQuestions[index]
													.questionDid,
												index
											)
										"
										:label="'Add Option'"
									/>
								</div>
							</div>
						</div>

						<div
							v-if="question._cachedType === 'short'"
							:id="`question-short-${index}`"
							class="d-flex align-items-start justify-content-center gap-3 flex-column w-100"
						>
							<input
								:id="`select-short-title-${index}`"
								v-model="question.text"
								@input="question.textError = false"
								placeholder="Question Title"
								:class="`question-input border-input p-1 ${
									question.textError ? 'error-border' : ''
								}`"
							/>
							<BPopover
								:target="`select-short-title-${index}`"
								:placement="isSmallScreen ? 'bottom' : 'right'"
								:visible="true"
								v-if="question.textError"
							>
								<p class="error-message">
									Please add a question title
								</p>
							</BPopover>

							<div class="option-input-wrapper">
								<input
									v-model="question.correctAnswer"
									@input="
										question.showKeywordError =
											!question.correctAnswer ||
											!question.correctAnswer.trim()
									"
									:class="`short-input border-input p-1 ${
										question.showKeywordError &&
										(!question.correctAnswer ||
											!question.correctAnswer.trim())
											? 'error-border'
											: ''
									}`"
									placeholder="Correct Answer"
								/>
							</div>

							<BPopover
								:target="`question-short-${index}`"
								:placement="isSmallScreen ? 'bottom' : 'right'"
								:visible="true"
								v-if="question.showKeywordError"
							>
								<p class="error-message">
									Keyword must not be empty
								</p>
							</BPopover>

							<div
								class="w-100 position-relative"
								style="min-height: 30px"
							>
								<div
									class="position-absolute start-50 translate-middle-x"
									style="
										top: 50%;
										transform: translateX(-50%)
											translateY(-50%);
									"
								>
									{{ displayIndex[index] + 1 }} /
									{{ groupedQuestions[index].length }}
								</div>
							</div>
						</div>
					</div>
					<div
						class="question-nav-area question-nav-right"
						@click="nextQuestion(question.variantIndex)"
						:class="{
							disabled: !hasMultipleVariants(
								question.variantIndex
							),
						}"
					>
						<IcRoundChevronRight class="nav-icon" />
					</div>
				</div>
			</TransitionGroup>
		</main>
	</div>

	<BModal
		v-model="showTypeChangeModal"
		title="Change Question Type"
		@hide="cancelTypeChange"
		class="custom-modal"
		header-class="custom-modal-header"
		body-class="custom-modal-body"
		footer-class="custom-modal-footer"
		centered
		size="md"
	>
		<template #modal-title>
			<div class="modal-title-container">
				<h4 class="mb-0">Change Question Type</h4>
			</div>
		</template>

		<div class="modal-content-container">
			<p class="modal-message">
				Changing the question type will delete all variants of this
				question. Are you sure you want to proceed?
			</p>
		</div>

		<template #footer>
			<div class="modal-footer-container d-flex gap-2">
				<SecondaryButton
					@click="cancelTypeChange"
					:size="'xs'"
					variant="danger"
					style="font-weight: 400"
					>Cancel
				</SecondaryButton>
				<PrimaryButton
					@click="confirmTypeChange"
					:size="'xs'"
					variant="success"
					style="font-weight: 400"
					>Yes, Change Type
				</PrimaryButton>
			</div>
		</template>
	</BModal>
</template>

<style scoped>
.txt-inline {
	display: inline !important;
}

.description-wrapper {
	width: 40%;
	height: 700px;
}

.questions-wrapper {
	width: 60%;
	max-height: 700px;
	overflow: scroll;
	scroll-behavior: smooth;
	top: 0;
	gap: 20px;
	padding-right: 10px;
	margin-top: 0;
	position: relative;
}

/* Scrollbar styles */
.questions-scroll-container::-webkit-scrollbar {
	width: 8px;
}

.questions-scroll-container::-webkit-scrollbar-track {
	margin-right: 5px;
}

.questions-scroll-container::-webkit-scrollbar-thumb {
	background: #b0adad6e;
	border-radius: 4px;
}

.question-block {
	width: 100%;
	padding: 1.5rem;
	background-color: var(--primary-bg-color-mid-deep-dark);
	border-radius: 12px;
	text-align: left;
	position: relative;
}

.question-content {
	width: calc(100% - 100px);
	margin: 0 50px;
}

.question-nav-area {
	position: absolute;
	top: 0;
	bottom: 0;
	width: 50px;
	z-index: 10;
	cursor: pointer;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s ease;
}

.question-nav-area:hover {
	background-color: rgba(255, 255, 255, 0.1);
}

.nav-icon {
	font-size: 24px;
	color: rgba(255, 255, 255, 0.7);
	transition: all 0.3s ease;
}

.question-nav-area:hover .nav-icon {
	color: rgba(255, 255, 255, 1);
	transform: scale(1.1);
}

.question-nav-left {
	left: 0;
	border-top-left-radius: 12px;
	border-bottom-left-radius: 12px;
}

.question-nav-right {
	right: 0;
	border-top-right-radius: 12px;
	border-bottom-right-radius: 12px;
}

.border-input {
	border: 4px solid white !important;
}

.error-border {
	border-color: #ff6b6b !important;
}

.error-message {
	color: #ff6b6b;
	font-size: 1.1rem;

	margin: 0;

	display: flex;
	align-items: center;
	justify-content: center;

	font-weight: bolder;
}

.question-input,
.option-input,
.short-input,
.select-type {
	display: block;
	width: 100%;
	border-radius: 6px;
	border: none;
	outline: none;
}

.input-radius-true {
	transform: scale(1.2);
	accent-color: var(--accent-color-1);
}

.input-radius-false {
	transform: scale(1.2);
	accent-color: var(--accent-color-2);
}

.multi-choice-radio {
	transform: scale(1.2);
	accent-color: var(--accent-color-3);
}

h1 {
	font-weight: 700;
	color: #ffffff;
	margin: 1.5rem 0;
	text-align: left;
}

.questions {
	width: 45%;
	max-height: 30rem;
	padding-right: 1rem;
	scroll-behavior: smooth;

	scrollbar-width: thin;
	scrollbar-color: #888 #0a192f;
}

.question {
	display: flex;
	flex-direction: column;
}

.questions::-webkit-scrollbar {
	-webkit-appearance: none;
	width: 8px;
}

.questions::-webkit-scrollbar-track {
	background: #0a192f;
}

.questions::-webkit-scrollbar-thumb {
	background-color: #888;
	border-radius: 4px;
}

.title-input,
.description-input {
	display: block;
	padding: 0.75rem;
	font-size: 1.1rem;
	width: 100%;
	max-width: 600px;
	border-radius: 8px;
	border: none;
	outline: none;
}

.description-input {
	min-height: 300px;
	resize: vertical;
}

.option-input,
.short-input {
	margin-bottom: 0;
	padding-right: 2rem;
}

.option-input-wrapper {
	position: relative;
	width: 100%;
}

.x-delete-button {
	position: absolute;
	right: 8px;
	top: 50%;
	transform: translateY(-50%);
	background: transparent;
	border: none;
	color: #ff6b6b;
	font-size: 14px;
	cursor: pointer;
	display: flex;
	align-items: center;
	justify-content: center;
	width: 20px;
	height: 20px;
	border-radius: 50%;
	transition: all 0.2s ease;
}

.x-delete-button:hover {
	background-color: rgba(255, 107, 107, 0.2);
}

.truefalse-block label {
	display: block;
	margin-bottom: 0.5rem;
}

@media (max-width: 768px) {
	.question-block,
	.title-input,
	.description-input {
		width: 90%;
	}

	.description-wrapper {
		width: 100%;
		height: 500px;
	}

	.questions-wrapper {
		width: 100%;
	}
}

.question-nav-area.disabled {
	cursor: not-allowed;
	opacity: 0.5;
}

.question-nav-area.disabled:hover {
	background-color: transparent;
}

.question-nav-area.disabled:hover .nav-icon {
	color: rgba(255, 255, 255, 0.7);
	transform: none;
}

/* Modal Styles */
:deep(.custom-modal) {
	.modal-title-container {
		h4 {
			color: #ffffff;
			font-weight: 600;
			font-size: 1.5rem;
		}
	}

	.modal-content-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		text-align: center;
		gap: 1rem;
	}

	.modal-message {
		color: #ffffff;
		font-size: 1.1rem;
		line-height: 1.5;
		margin: 0;
	}

	.modal-footer-container {
		display: flex;
		justify-content: flex-end;
		gap: 1rem;
	}
}

.moveing {
	opacity: 0;
}

.ai-loading-overlay {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(0, 0, 0, 0.5);
	z-index: 100;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 12px;
}
.ai-loading-text {
	color: #fff;
	font-size: 1.5rem;
	font-weight: bold;
	letter-spacing: 2px;
}
</style>