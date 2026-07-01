<script>
import IconBiSend from '~icons/bi/send';

import { errorPopup } from '@/utils/globalPopup';

export default {
	name: 'CommentBar',

	components: {
		IconBiSend,
	},

	props: {
		aiResponseDid: {
			type: String,
			required: true,
		},
	},

	data() {
		return { query: '' };
	},

	methods: {
		submit() {
			if (typeof this.query !== 'string') {
				errorPopup(
					'Comment Error',
					"You can't post a comment that is not a string"
				);
			}

			if (typeof this.query === 'string' && this.query.length > 5000) {
				errorPopup(
					'Comment is too long',
					'The comment exceeds 5000 characters, write a short one'
				);
				return;
			}

			this.$emit('submit', this.aiResponseDid, this.query);
		},
	},
};
</script>

<template>
	<div class="input-bar-container flex-center p-3">
		<div class="input-bar-wrapper flex-center">
			<div class="input-bar flex-center gap-2">
				<input
					v-model="query"
					:type="'text'"
					placeholder="Write a comment"
					class="input shadow-lg"
					@keyup.enter="submit"
				/>

				<div v-if="query.length > 5000">
					<p class="m-0" style="color: red !important">
						-{{ query.length - 5000 }}
					</p>
				</div>

				<button class="flex-center click send-button" @click="submit">
					<IconBiSend class="send-icon" />
				</button>
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

.input-bar-container {
	--padding-size: 2.5px;

	width: 100%;
	height: 100%;
}

.input-icon {
	width: 24px;
	cursor: pointer;
}

.input-bar-wrapper {
	flex-grow: 1;
	width: 100%;

	border: 1px solid #ccc;
	border-radius: 8px;

	padding: 0px;

	background: var(--main-gradient-bottom);

	transition: all 0.125s ease;
}

.input-bar-wrapper:focus-within {
	padding: var(--padding-size);
}

.input-bar {
	flex-grow: 1;

	border-radius: 8px;
	background: var(--primary-bg-color);

	padding: calc(var(--padding-size) + 0.5rem) calc(var(--padding-size) + 1rem);

	transition: all 0.125s ease;
}

.input-bar:focus-within {
	padding: calc(0px + 0.5rem) calc(0px + 1rem);
}

.input {
	outline: none;

	width: 100%;

	padding: 0.25rem 0.5rem;

	border-radius: 8px;
	border: transparent;

	background: var(--primary-bg-color);
	color: var(--primary-text-color);

	font-size: 1rem;
}

.send-button {
	background: var(--main-gradient-bottom);
	padding: 10px;
	border: none;
	border-radius: 100%;

	display: flex;
	align-items: center;
	justify-content: center;
}

.send-icon {
	transform: translate(-0.5px, 0.5px);
	color: white !important;
}

@media screen and (max-width: 500px) {
	.send-icon {
		width: 18px;
	}
}
</style>