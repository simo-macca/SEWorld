<script>
import SearchBarVue from '@/components/inputs/SearchBar.vue';
import PrimaryButton from '@/components/buttons/PrimaryButton.vue';
import SecondaryButton from '@/components/buttons/SecondaryButton.vue';
import PrimaryCard from '@/components/cards/PrimaryCard.vue';
import SecondaryCard from '@/components/cards/SecondaryCard.vue';
import DeleteButton from '@/components/buttons/DeleteButton.vue';
import Rating from '@/components/buttons/Rating.vue';

import Header from '@/components/Header.vue';

import { showPopup, successPopup } from '@/utils/globalPopup';
import QACard from '@/components/cards/QACard.vue';

export default {
	name: 'ComponentsView',

	data() {
		return { deleteRoutineParameter: { message: 'An example parameter' } };
	},

	components: {
		QACard,
		Header,
		//
		SearchBarVue,
		PrimaryButton,
		SecondaryButton,
		PrimaryCard,
		SecondaryCard,
		DeleteButton,
		Rating,
	},

	methods: {
		sp() {
			const popups = ['info', 'success', `warning`, `error`];
			const idx = Math.floor(Math.random() * 4);

			showPopup(
				`${popups[idx]} Popup`,
				'This is the popup message youa re reading',
				popups[idx]
			);
		},

		rd(param) {
			successPopup(`Delete Success!`, `The parameter: ${param.message}`);
		},
	},
};
</script>

<template>
	<Header />
	<div
		class="d-flex align-items-center justify-content-center w-100 min-h-screen-header flex-column p-5"
		style="gap: 100px"
	>
		<div
			class="d-flex align-items-center justify-content-center flex-column gap-3 w-100"
		>
			<p>search bar</p>
			<SearchBarVue :placeholder="`Search for a user`" />
		</div>

		<div
			class="d-flex align-items-center justify-content-center w-100 flex-column gap-3"
		>
			<p>primary button</p>
			<PrimaryButton @click="sp" :label="'Show popup'" size="lg" />
		</div>

		<div
			class="d-flex align-items-center justify-content-center w-100 flex-column gap-3"
		>
			<p>secondary button</p>
			<SecondaryButton size="md" />
		</div>

		<div
			class="d-flex align-items-center justify-content-center w-100 flex-column gap-3"
		>
			<p>primary card</p>
			<PrimaryCard style="max-width: 80%">
				<div>this is the primary card with no border</div>
			</PrimaryCard>

			<PrimaryCard style="max-width: 80%" :withBorder="true">
				<div>this is the primary card with border</div>
			</PrimaryCard>

			<PrimaryCard style="max-width: 80%" :withHover="true">
				<div>this is the primary card with hover effect</div>
			</PrimaryCard>
		</div>

		<div
			class="d-flex align-items-center justify-content-center w-100 flex-column gap-3"
		>
			<p>secondary card</p>
			<SecondaryCard style="max-width: 80%">
				<div>this is the secondary card with no border</div>
			</SecondaryCard>

			<SecondaryCard style="max-width: 80%" :withBorder="true">
				<div>this is the secondary card with border</div>
			</SecondaryCard>

			<SecondaryCard style="max-width: 80%" :withHover="true">
				<div>this is the secondary card with hover effect</div>
			</SecondaryCard>
		</div>

		<div
			class="d-flex align-items-center justify-content-center w-100 flex-column gap-3"
		>
			<p>Q&A Card</p>
			<QACard
				:question-title="'Title exercise'"
				:correct-answer="'Description exercise'"
				:short-answer-list="[
					{
						shortAnswerDid: `ajajajjajaa`,
						username: `Spana`,
						user_answer: `my answer`,
						generated_AI_feedback: `Ai feedback`,
					},
					{
						shortAnswerDid: `aixndtt`,
						username: `Spana`,
						user_answer: `my answer`,
						generated_AI_feedback: `Ai feedback`,
					},
					{
						shortAnswerDid: `toyjc`,
						username: `Spana`,
						user_answer: `my answer`,
						generated_AI_feedback: `Ai feedback`,
					},
					{
						shortAnswerDid: `ggggjsj`,
						username: `Spana`,
						user_answer: `my answer`,
						generated_AI_feedback: `Ai feedback`,
					},
					{
						shortAnswerDid: `aooccue`,
						username: `Spana`,
						user_answer: `my answer`,
						generated_AI_feedback: `Ai feedback`,
					},
					{
						shortAnswerDid: `eututr`,
						username: `Spana`,
						user_answer: `my answer`,
						generated_AI_feedback: `Ai feedback`,
					},
					{
						shortAnswerDid: `mgmgis`,
						username: `Spana`,
						user_answer: `my answer`,
						generated_AI_feedback: `Ai feedback`,
					},
					{
						shortAnswerDid: `sdjufurr`,
						username: `Spana`,
						user_answer: `my answer`,
						generated_AI_feedback: `Ai feedback`,
					},
					{
						shortAnswerDid: `ajajffssajjajaa`,
						username: `Spana`,
						user_answer: `my answer`,
						generated_AI_feedback: `Ai feedback`,
					},
				]"
			/>
		</div>

		<div
			class="d-flex align-items-center justify-content-center w-100 flex-column gap-3"
		>
			<p>delete button</p>
			<DeleteButton
				:deleteRoutine="rd"
				:routineParameter="deleteRoutineParameter"
				:size="'xs'"
			/>
			<DeleteButton
				:deleteRoutine="rd"
				:routineParameter="deleteRoutineParameter"
				:size="'sm'"
			/>
			<DeleteButton
				:deleteRoutine="rd"
				:routineParameter="deleteRoutineParameter"
			/>
			<DeleteButton
				:deleteRoutine="rd"
				:routineParameter="deleteRoutineParameter"
				:size="'lg'"
			/>
		</div>
	</div>

	<div
		class="d-flex align-items-center justify-content-center w-100 flex-column gap-3"
	>
		<p>Rating button</p>
		<Rating :up-votes="170" :down-votes="3" />
	</div>

	<br /><br />
</template>

