
<script>
import { nextTick } from 'vue';
import { useUserStore } from '@/stores/user';
import { useMaterialStore } from '@/stores/material';

import { BButton, BCollapse, vBToggle, BPopover } from 'bootstrap-vue-next';

import IconBiBoxArrowInRight from '~icons/bi/box-arrow-in-right';
import IconBichevroncompactup from '~icons/bi/chevron-compact-up';
import IconBichevroncompactdown from '~icons/bi/chevron-compact-down';
import IconBichatlefttext from '~icons/bi/chat-left-text';
import IconBiQuestionCircle from '~icons/bi/question-circle';
import BxsEdit from '~icons/bxs/edit';

import DeleteMaterialButton from './DeleteMaterialButton.vue';

import IconFileFactory from '@/components/icons/IconFileFactory.vue';

import PrimaryButton from './buttons/PrimaryButton.vue';

import { DOWNLOAD_TOPIC_MATERIAL } from '@/utils/constants.js';

import AskAIPopup from '@/components/popup/AskAIPopup.vue';
import FAQ from './FAQ.vue';

import { MdPreview } from 'md-editor-v3';
import { errorPopup, successPopup } from '@/utils/globalPopup';

export default {
	name: 'MaterialCard',

	directives: {
		'b-toggle': vBToggle,
	},

	components: {
		BButton,
		BCollapse,
		BPopover,
		MdPreview,
		IconBiBoxArrowInRight,
		IconBichevroncompactup,
		IconBichevroncompactdown,
		IconBichatlefttext,
		IconBiQuestionCircle,
		DeleteMaterialButton,
		IconFileFactory,
		PrimaryButton,
		BxsEdit,
		AskAIPopup,
		FAQ,
	},

	props: {
		materialData: {
			type: Object,
			required: true,
		},
	},

	data() {
		return {
			user: useUserStore(),
			isArrowUp: true,
			renderedMarkdown: '',
			showAskButton: false,
			askButtonStyle: { top: '0px', left: '0px' },
			highlighted: '',
		};
	},

	watch: {
		materialData: {
			immediate: true,
			handler(newVal) {
				if (newVal?.materialType === 'md') {
					this.renderedMarkdown = newVal.description;
				}
			},
		},
	},

	computed: {
		isInstructor() {
			return this.userStore().isInstructor;
		},
	},

	methods: {
		toggleArrow() {
			this.isArrowUp = !this.isArrowUp;
		},

		async downloadMaterial() {
			let did = this.materialData.materialDid;
			try {
				const response = await fetch(
					`${DOWNLOAD_TOPIC_MATERIAL}/${did}/download`,
					{
						method: 'GET',
						credentials: 'include',
					}
				);

				if (!response.ok) {
					throw new Error('Something went wrong!');
				}

				const blob = await response.blob();
				const url = window.URL.createObjectURL(blob);
				const a = document.createElement('a');
				a.href = url;
				a.download = this.materialData.materialFileName;
				document.body.appendChild(a);
				a.click();
				a.remove();
				window.URL.revokeObjectURL(url);
			} catch (error) {
				console.error('Download failed:', error);
				errorPopup(
					'Download Failed',
					'Failed to download the material file'
				);
			}
		},

		userStore() {
			return useUserStore();
		},

		materialStore() {
			return useMaterialStore();
		},

		async deleteMaterial(did) {
			try {
				await this.materialStore().deleteMaterialByDid(did);
				successPopup(
					'Deletetion Success!',
					'The material has been successfully deleted'
				);
			} catch (error) {
				console.error('Error deleting material: ', error);
				errorPopup(
					'Deletetion Failed',
					'Failed to delete the material'
				);
			}
		},

		hideFaqPopover(did) {
			this.$root.$emit('bv::hide::popover', `faq-button-${did}`);
		},

		onTextSelection() {
			const sel = window.getSelection();
			if (
				event.target.closest('.ask-button-container') ||
				event.target.closest('.ask-ai-popover')
			) {
				return;
			}

			const txt = sel.toString().trim();

			if (!sel || sel.isCollapsed || sel.toString().trim() === '') {
				this.highlighted = '';
				this.showAskButton = false;
				this.hideAskPopover(this.materialData.materialDid);
				return;
			}
			this.highlighted = sel.toString().trim();

			const range = sel.getRangeAt(0);
			const rect = range.getBoundingClientRect();
			const containerRect =
				this.$refs.markdownContainer.getBoundingClientRect();

			this.askButtonStyle = {
				position: 'absolute',
				//top: `${rect.top - containerRect.top + rect.height / 2}px`,
				//left: `${(rect.left - containerRect.left + rect.width / 2) + 70}px`,
				//transform: 'translate(-50%, -50%)',
				top: `${rect.bottom - containerRect.top - 5}px`,
				left: `${rect.right - containerRect.left + 3}px`,
			};
			this.showAskButton = true;
		},

		hideAskPopover(did) {
			this.$root.$emit('bv::hide::popover', `ask-button-${did}`);
		},
	},

	async mounted() {
		await this.userStore().refreshUser();

		await nextTick();

		document.addEventListener('mousedown', this.handleGlobalClick);
		if (this.$refs.markdownContainer) {
			this.$refs.markdownContainer.addEventListener('mouseup', (e) =>
				this.onTextSelection(e)
			);
		}
	},
	unmounted() {
		document.removeEventListener('mousedown', this.handleGlobalClick);
		if (this.$refs.markdownContainer) {
			this.$refs.markdownContainer.removeEventListener(
				'mouseup',
				this.onTextSelection
			);
		}
	},

	handleGlobalClick(event) {
		const askButton = document.querySelector('.ask-button-container');
		const markdownContainer = this.$refs.markdownContainer;

		if (!askButton || !markdownContainer) return;

		// If clicking outside the button OR on the highlighted text
		if (
			!askButton.contains(event.target) &&
			markdownContainer.contains(event.target)
		) {
			const sel = window.getSelection();
			if (!sel || sel.isCollapsed || sel.toString().trim() === '') {
				this.showAskButton = false;
				this.highlighted = '';
				this.hideAskPopover(this.materialData.materialDid);
			}
		}
	},
};
</script>

<template>
	<div v-if="materialData" class="collapse-wrapper">
		<div class="flash-card p-2 row align-items-center">
			<div class="title-wrapper">
				<h2 class="name m-0">{{ materialData.title }}</h2>
			</div>

			<div class="actions-wrapper">
				<div
					class="d-flex align-items-center justify-content-center gap-3"
				>
					<div
						v-if="materialData.materialType === 'link'"
						class="d-flex align-items-center justify-content-center"
					>
						<BButton
							:href="materialData.description"
							target="_blank"
							class="click bg-button"
						>
							<div
								class="d-flex align-items-center justify-content-center flex-wrap gap-2"
							>
								Link
								<IconBiBoxArrowInRight class="icon" />
							</div>
						</BButton>
					</div>
					<div
						v-if="materialData.materialType === 'file'"
						class="d-flex align-items-center justify-content-center"
					>
						<BButton
							@click="downloadMaterial"
							class="click bg-button"
						>
							<div
								class="d-flex align-items-center justify-content-center flex-wrap gap-2"
							>
								Download

								<IconFileFactory
									class="icon"
									:fileName="materialData.materialFileName"
								/>
							</div>
						</BButton>
					</div>
					<div
						v-if="materialData.materialType === 'md'"
						class="d-flex align-items-center justify-content-center"
					>
						<div
							@click="toggleArrow"
							class="markdown-toggle"
							style="cursor: pointer"
						>
							<div v-if="isArrowUp" class="arrow-up">
								<BButton
									v-b-toggle="
										`collapse-${materialData.materialDid}`
									"
									class="bg-transparent d-flex align-items-center justify-items-center"
									style="width: 40px; height: 40px"
								>
									<IconBichevroncompactup class="icon" />
								</BButton>
							</div>
							<div v-else class="markdown-content">
								<BButton
									v-b-toggle="
										`collapse-${materialData.materialDid}`
									"
									class="bg-transparent d-flex align-items-center justify-items-center"
									style="width: 40px; height: 40px"
								>
									<IconBichevroncompactdown class="icon" />
								</BButton>
							</div>
						</div>
					</div>

					<div
						class="d-flex align-items-center justify-content-center"
					>
						<PrimaryButton
							v-if="user.getUserRole === 'INSTRUCTOR'"
							:size="`xs`"
							class="d-flex align-items-center justify-content-center"
							style="width: 38px; height: 38px"
							@click="$emit('edit', materialData)"
						>
							<BxsEdit />
						</PrimaryButton>
					</div>

					<div
						v-if="userStore().getUserRole === 'INSTRUCTOR'"
						class="d-flex align-items-center justify-content-center"
					>
						<DeleteMaterialButton
							:elementId="materialData.materialDid"
							deletionSpecText="a material"
							@delete="deleteMaterial"
						/>
					</div>
				</div>
			</div>
		</div>

		<BCollapse
			v-if="materialData.materialType === 'md'"
			:id="`collapse-${materialData.materialDid}`"
			class="w-100 collapse-md"
		>
			<!-- FAQ placeholder button -->
			<div v-if="!isInstructor" class="faq-button-container">
				<b-button
					:id="`faq-button-${materialData.materialDid}`"
					class="btn-faq"
					size="sm"
				>
					FAQ
					<IconBichatlefttext class="faq-icon" />
				</b-button>
				<b-popover
					:target="`faq-button-${materialData.materialDid}`"
					triggers="click"
					placement="bottom"
					custom-class="faq-popover"
				>
					<template #default>
						<div class="popover-close-wrapper">
							<button
								type="button"
								class="btn-close btn-close-white btn-sm"
								@click="
									hideFaqPopover(materialData.materialDid)
								"
							></button>
						</div>
						<FAQ :materialDid="materialData.materialDid" />
					</template>
				</b-popover>
			</div>

			<div ref="markdownContainer" class="markdown-container">
				<MdPreview
					id="markdown"
					class="bg-transparent"
					:language="`en-US`"
					:previewTheme="`github`"
					:theme="'dark'"
					:modelValue="renderedMarkdown"
				/>

				<!-- Ask-AI ? button, shown only when text is selected -->
				<div
					v-if="showAskButton && !isInstructor"
					class="ask-button-container"
					:style="askButtonStyle"
				>
					<b-button
						id="ask-ai-button"
						size="sm"
						class="btn-ask"
						tabindex="0"
						@mouseup.stop
					>
						<IconBiQuestionCircle class="ask-icon" />
					</b-button>

					<AskAIPopup
						:materialDid="materialData.materialDid"
						:highlighted="highlighted"
					/>
				</div>
			</div>
		</BCollapse>
	</div>
</template>

<style>
.popover.faq-popover .popover-close-wrapper {
	display: none !important;
}

/* Global styles to override bootstrap-vue popover */
.popover.faq-popover {
	max-width: 450px !important;
	width: 450px !important;
}
.popover.faq-popover h3 {
	color: white;
}

.popover.faq-popover > .overflow-auto > .popover-body {
	padding: 0;
}
</style>

<style scoped>
.collapse-wrapper {
	width: 100%;

	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;

	border: 2px solid black;
	border-radius: 5px;
	padding: 5px 15px 5px 15px;
	background-color: #05050d23;

	min-height: 70px;

	transition: all 0.3s ease;
}

.flash-card {
	width: 100%;

	display: grid;
	grid-template-columns: 65% 35%;
	gap: 1rem;
	align-items: center;

	transition: all 0.3s ease;
}

.title-wrapper {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: start;

	width: 100%;
}

.actions-wrapper {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: end;

	flex-wrap: wrap;

	width: 100%;
}

.bg-button {
	background: var(--primary-bg-color-mid-dark) !important;
	border-color: var(--primary-bg-color-deep-dark) !important;
}

.icon {
	width: 24px;
	height: 24px;
	stroke-width: 5px;
}

@media screen and (max-width: 789px) {
	.flash-card {
		flex-direction: column;
	}
}

@media screen and (max-width: 1200px) {
	.flash-card {
		display: flex;
	}
}

@media screen and (min-width: 1500px) {
	.flash-card {
		grid-template-columns: 70% 30%;
	}
}

.btn-faq {
	background-color: #c82375 !important;
	border-color: #c82375 !important;
	color: #ffffff !important;
}
.faq-icon {
	margin-left: 4px;
	vertical-align: middle;
}

/* 2) Position container so the button sits top-right */
.faq-button-container {
	position: relative;
	display: flex;
	justify-content: flex-end;
	width: 100%;
	margin-bottom: 8px;
}

/* allow absolute positioning inside the markdown area */
.markdown-container {
	position: relative;
}

/* style & position the “?” button */
.ask-button-container {
	position: absolute;
	z-index: 10;
}

.btn-ask {
	background-color: #c82375 !important;
	border-color: #c82375 !important;
	color: #ffffff !important;
	padding: 0px;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.ask-icon {
	width: 20px;
	height: 20px;
}

.popover {
	background-color: #05050d23 !important;
	color: #de0a0a !important;
	border-radius: 5px;
	padding: 10px;
}
</style>
