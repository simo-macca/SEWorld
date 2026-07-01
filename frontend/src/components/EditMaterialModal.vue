<script>
import * as bootstrap from 'bootstrap';
import { useMaterialStore } from '@/stores/material';

import PrimaryButton from './buttons/PrimaryButton.vue';
import { errorPopup, successPopup } from '@/utils/globalPopup';

export default {
	name: 'EditMaterialModal',

	components: {
		PrimaryButton,
	},

	props: {
		topicDid: {
			type: String,
			required: true,
		},
		material: {
			type: Object,
			required: true,
		},
	},

	data() {
		return {
			updatedMaterial: {
				title: '',
				type: '',
				description: '',
				materialFileName: '',
				// useful?
				file: null,
			},
		};
	},

	watch: {
		material: {
			immediate: true,
			handler(newVal) {
				if (newVal) {
					this.updatedMaterial.title = newVal.title;
					this.updatedMaterial.type = newVal.materialType;
					this.updatedMaterial.description = newVal.description;
					this.updatedMaterial.materialFileName =
						newVal.materialFileName;
					this.updatedMaterial.file = null;
				}
			},
		},
	},

	methods: {
		onFileSelected(e) {
			this.updatedMaterial.file = e.target.files[0];
		},

		async editMaterial() {
			try {
				const materialStore = useMaterialStore();
				const upMaterial = await materialStore.editMaterial(
					// did
					this.material.materialDid,
					// material
					this.updatedMaterial,
					// oldName
					this.updatedMaterial.file
						? undefined
						: this.updatedMaterial.materialFileName
				);

				// reload materials
				await materialStore.fetchMaterials(this.topicDid);

				this.hideModal();
				this.resetForm(upMaterial);
				successPopup(
					'Update Successful',
					`The update for the material was successful`
				);
			} catch (error) {
				console.error('Error editing material:', error);
				errorPopup(
					'Failed Update',
					`The update for the material failed.`
				);
			}
		},

		hideModal() {
			const modalEl = document.getElementById(`editMaterialModal`);
			const modalElDid = document.getElementById(
				`editMaterialModal-${this.material.materialDid}`
			);
			const modalInstance = bootstrap.Modal.getInstance(modalEl);
			const modalInstanceDid = bootstrap.Modal.getInstance(modalElDid);
			if (modalInstance) {
				modalInstance.hide();
			}
			if (modalInstanceDid) {
				modalInstanceDid.hide();
			}

			// I know this isn't the best way to do it but the conventional method wasn't working
			document
				.querySelectorAll('.modal-backdrop')
				.forEach((bd) => bd.remove());
			document.body.classList.remove('modal-open');
			document.body.removeAttribute('style');
			document.body.removeAttribute('data-bs-overflow');
			document.body.removeAttribute('data-bs-padding-right');
			document.body.classList.remove('modal-open');
		},

		resetForm(upMaterial) {
			this.updatedMaterial = upMaterial
				? {
						title: upMaterial.data.title,
						type: upMaterial.data.materialType,
						description: upMaterial.data.description,
						materialFileName: upMaterial.data.materialFileName,
				  }
				: {
						title: '',
						type: '',
						description: '',
						materialFileName: '',
						file: null,
				  };

			if (this.$refs.editMaterialFileInput) {
				this.$refs.editMaterialFileInput.value = '';
			}
		},
	},
};
</script>

<template>
	<div
		class="modal fade"
		id="editMaterialModal"
		:ref="`editModal-${this.material.materialDid}`"
	>
		<div class="modal-dialog modal-dialog-centered modal-lg">
			<div class="modal-content modal-bg text-white">
				<div class="modal-header border-0">
					<h5 class="modal-title">Edit Material</h5>
					<button
						type="button"
						class="btn-close btn-close-white"
						data-bs-dismiss="modal"
					></button>
				</div>

				<div class="modal-body">
					<form @submit.prevent="editMaterial">
						<div class="mb-3">
							<label
								class="form-label text-white"
								for="edit-material-modal-title-input"
								>Title</label
							>
							<input
								id="edit-material-modal-title-input"
								v-model="updatedMaterial.title"
								type="text"
								class="form-control input-bg text-white border-secondary"
								placeholder="Enter material title"
								required
							/>
						</div>

						<div class="mb-3">
							<label
								class="form-label text-white"
								for="edit-material-modal-type-input"
								>Type</label
							>
							<select
								id="edit-material-modal-type-input"
								v-model="updatedMaterial.type"
								class="form-select input-bg text-white border-secondary"
								required
							>
								<option disabled value="">Select type</option>
								<option value="md">Markdown</option>
								<option value="link">Link</option>
								<option value="file">File</option>
							</select>
						</div>

						<div class="mb-3" v-if="updatedMaterial.type === 'md'">
							<label
								class="form-label text-white"
								for="edit-material-modal-markdown-input"
								>Markdown Content</label
							>
							<textarea
								id="edit-material-modal-markdown-input"
								v-model="updatedMaterial.description"
								class="form-control input-bg text-white border-secondary"
								rows="4"
								placeholder="Enter markdown content"
							/>
						</div>

						<div
							class="mb-3"
							v-else-if="updatedMaterial.type === 'link'"
						>
							<label
								class="form-label text-white"
								for="edit-material-modal-link-input"
								>Link</label
							>
							<input
								id="edit-material-modal-link-input"
								v-model="updatedMaterial.description"
								type="url"
								class="form-control input-bg text-white border-secondary"
								placeholder="https://example.com"
								required
							/>
						</div>

						<div
							class="mb-3"
							v-else-if="updatedMaterial.type === 'file'"
						>
							<label
								class="form-label text-white"
								for="edit-material-modal-file-input"
								>Upload File</label
							>
							<input
								id="edit-material-modal-file-input"
								type="file"
								class="form-control input-bg text-white border-secondary"
								ref="editMaterialFileInput"
								@change="onFileSelected"
							/>
							<div v-if="updatedMaterial.materialFileName">
								<p class="fs-6 mt-3 mb-0">
									Previous file:
									{{ updatedMaterial.materialFileName }}
								</p>
								<p class="fs-6 mt-1">
									Submit without file to maintain the old one.
								</p>
							</div>
						</div>

						<div class="text-end">
							<PrimaryButton
								:label="`Save`"
								:size="`sm`"
								type="submit"
							/>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
.form-control::placeholder {
	color: white;
	opacity: 0.6;
}

.modal-bg {
	background: var(--primary-bg-color-dark);
}

.input-bg {
	background: var(--primary-bg-color-deep-dark);
}
</style>