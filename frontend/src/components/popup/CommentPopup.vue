<script>
import { BPopover } from 'bootstrap-vue-next';
import PrimaryButtonVue from '@/components/buttons/PrimaryButton.vue';
import IconChatRoundVue from '@/components/icons/IconChatRound.vue';
import CommentBarVue from '@/components/inputs/CommentBar.vue';
import DeleteMaterialButton from '../DeleteMaterialButton.vue';

import { nextTick } from 'vue';

import { useCommentStore } from '@/stores/comment';
import { useUserStore } from '@/stores/user';
import { errorPopup, successPopup } from '@/utils/globalPopup';

export default {
	name: 'CommentPopup',

	components: {
		PrimaryButtonVue,
		IconChatRoundVue,
		BPopover,
		CommentBarVue,
		DeleteMaterialButton,
	},

	props: {
		aiResponseDid: {
			type: String,
			required: true,
		},
	},

	computed: {
		getComments() {
			return this.commentStore().getComments(this.aiResponseDid);
		},

		isInstructor() {
			return this.userStore().isInstructor;
		},
	},

	methods: {
		commentStore() {
			return useCommentStore();
		},

		userStore() {
			return useUserStore();
		},

		async deleteComment(did) {
			const success = await this.commentStore().deleteComment(did);

			if (success) {
				successPopup(
					'Comment Posted',
					'Your comment is now visible to all students'
				);
				// Refresh UI
				this.commentStore().loadComments(this.aiResponseDid);
				return;
			}
			errorPopup(
				'Error Posting Comment',
				'Your comment has not been posted correctly'
			);
		},

		async makeCommentPost(did, comment) {
			const success = await this.commentStore().postComment(did, comment);

			if (!success) {
				errorPopup(
					'Error Posting Comment',
					'Your comment has not been posted correctly'
				);
				return;
			}

			successPopup(
				'Comment Posted',
				'Your comment is now visible to all students'
			);

			// Refresh UI
			this.commentStore().loadComments(this.aiResponseDid);

			await nextTick();
			setTimeout(async () => {
				await nextTick();

				const el = document.getElementById(
					`comments-${this.aiResponseDid}`
				);
				if (el) {
					el.scrollTo({
						top: el.scrollHeight,
						behavior: 'smooth',
					});
				}
			}, 300);
		},

		formatDate(date) {
			const commentDate = new Date(date);
			const now = new Date();

			const diffInMs = now - commentDate;
			const oneDayInMs = 24 * 60 * 60 * 1000;

			if (diffInMs < oneDayInMs) {
				return commentDate.toLocaleTimeString([], {
					hour: '2-digit',
					minute: '2-digit',
				});
			} else {
				const d = String(commentDate.getDate()).padStart(2, '0');
				const m = String(commentDate.getMonth() + 1).padStart(2, '0');
				const y = String(commentDate.getFullYear()).slice(-2);
				return commentDate
					.toLocaleTimeString([], {
						hour: '2-digit',
						minute: '2-digit',
					})
					.concat(` ${d}.${m}.${y}`);
			}
		},

		loadComments() {
			this.commentStore().loadComments(this.aiResponseDid);
		},
	},
};
</script>

<template>
	<PrimaryButtonVue
		style="width: 40px; height: 40px"
		class="d-flex align-items-center justify-content-center"
		:id="`comment-button-${aiResponseDid}`"
		@mouseenter="loadComments"
	>
		<IconChatRoundVue style="width: 36px; height: 36px" />
	</PrimaryButtonVue>

	<b-popover
		:target="`comment-button-${aiResponseDid}`"
		triggers="click"
		placement="bottom"
		custom-class="comment-popover"
		container="body"
	>
		<template #default>
			<div
				v-if="getComments.length != 0"
				:id="`comments-${aiResponseDid}`"
				class="d-flex flex-column align-items-center justify-content-start gap-3 w-100 comment-container-wrapper custom-scrollbar px-3"
				:class="{
					'comment-container-wrapper-bar': isInstructor,
				}"
			>
				<div
					:class="`w-100 comment-container d-flex gap-2 ${
						isInstructor
							? comment.isCallerTheOwner
								? 'align-items-end flex-column'
								: 'align-items-center justify-content-start flex-row'
							: 'align-items-center justify-content-start flex-row'
					}`"
					v-for="comment in getComments"
					:key="comment.commentDid"
				>
					<div
						class="d-flex flex-column align-items-center justify-content-center gap-2 comment comment-bg px-3"
						:class="{
							'my-comment-bg':
								isInstructor && comment.isCallerTheOwner,
							'comment-bg':
								isInstructor && !comment.isCallerTheOwner,
						}"
					>
						<div
							class="w-100 d-flex align-items-center"
							:class="{
								'justify-content-end':
									isInstructor && comment.isCallerTheOwner,
								'justify-content-start':
									isInstructor && !comment.isCallerTheOwner,
							}"
						>
							<h6 class="name-date-color m-0">
								{{ comment.instructorName }}
							</h6>
						</div>
						<div
							class="w-100 d-flex align-items-center align-content-start"
						>
							<div>
								{{ comment.commentContent }}
							</div>
						</div>
						<div
							class="w-100 d-flex align-items-center name-date-color"
							:class="{
								'justify-content-end':
									isInstructor && comment.isCallerTheOwner,
								'justify-content-start':
									isInstructor && !comment.isCallerTheOwner,
							}"
						>
							{{ formatDate(comment.timeStamp) }}
						</div>
					</div>

					<DeleteMaterialButton
						v-if="isInstructor && comment.isCallerTheOwner"
						@delete="deleteComment"
						:deletionSpecText="`the comment`"
						:elementId="comment.commentDid"
					/>
				</div>
			</div>
			<div
				v-else
				class="d-flex flex-column align-items-center justify-content-start gap-3 w-100 custom-scrollbar px-3"
				:class="{
					'comment-container-wrapper-bar': isInstructor,
				}"
			>
				<h3 style="color: white">
					There are no comments for this response
				</h3>
			</div>

			<CommentBarVue
				v-if="isInstructor"
				@submit="makeCommentPost"
				:aiResponseDid="aiResponseDid"
			/>
		</template>
	</b-popover>
</template>

<style>
:root {
	--min-width: 500px;
	--padding-popover: 16px;
}

.popover.comment-popover {
	background: var(--primary-bg-color-mid-dark) !important;
	border: 1px solid white;
	padding: 1px;
}

.popover.comment-popover,
.popover.comment-popover .my-comment-bg,
.popover.comment-popover .comment-bg {
	color: white !important;
}

.comment-popover,
.comment-popover > .overflow-auto,
.comment-popover > .popover-body {
	border-radius: 8px;
}

@media screen and (min-width: 600px) {
	.comment-popover,
	.comment-popover > div,
	.comment-popover > .popover-body {
		min-width: calc(
			var(--min-width) + var(--padding-popover) * 2
		) !important;
	}

	.comment-popover > .popover-arrow {
		display: flex;
		align-items: center;
		justify-content: center;
	}
}
</style>

<style scoped>
.comment {
	width: 400px;
	padding: 10px;
	border-radius: 12px;
}

.comment-bg {
	background: var(--accent-color-1-opacity-2);
}

.my-comment-bg {
	background: var(--accent-color-1-opacity-8);
}

.comment-container-wrapper-bar {
	border-bottom: 1px solid gray;
}

.comment-container-wrapper {
	padding-bottom: 10px;
}

.comment-container {
	text-align: justify;
}

.name-date-color {
	color: rgba(255, 255, 255, 0.8) !important;
}

@media screen and (min-width: 600px) {
	.comment-container-wrapper {
		min-width: 500px;
		max-height: 450px;
	}
}
</style>