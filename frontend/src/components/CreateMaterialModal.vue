
<script>
import * as bootstrap from 'bootstrap';
import { useMaterialStore } from '@/stores/material';
import { errorPopup, successPopup } from '@/utils/globalPopup';

export default {
	name: 'CreateMaterialModal',

	props: {
		topicDid: {
			type: String,
			required: true,
		},
	},

	data() {
		return {
			newMaterial: {
				title: '',
				type: '',
				description: '',
				file: null,
			},
		};
	},

	methods: {
		onFileSelected(e) {
			this.newMaterial.file = e.target.files[0];
		},

		async createMaterial() {
			try {
				const materialStore = useMaterialStore();

				if (this.newMaterial.type === 'file') {
					await materialStore.uploadFileMaterial(
						this.topicDid,
						this.newMaterial
					);
				} else {
					await materialStore.uploadTextMaterial(
						this.topicDid,
						this.newMaterial
					);
				}

				await materialStore.fetchMaterials(this.topicDid);

				this.hideModal();
				this.resetForm();
				successPopup(
					'Material Created',
					'Material has been created successfully'
				);
			} catch (error) {
				console.error('Error creating material:', error);
				errorPopup(
					'Error Creating Material',
					'Material has not been created'
				);
			}
		},

		hideModal() {
			const modalEl = document.getElementById('createMaterialModal');
			const modalInstance = bootstrap.Modal.getInstance(modalEl);

			const focused = modalEl.querySelector(':focus');
			if (focused) {
				focused.blur();
			}

			if (modalInstance) {
				modalInstance.hide();
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

		resetForm() {
			this.newMaterial = {
				title: '',
				type: '',
				description: '',
				file: null,
			};
		},
	},
};
</script>

<template>
	<div class="modal fade" id="createMaterialModal">
		<div class="modal-dialog modal-dialog-centered modal-lg">
			<div class="modal-content modal-bg text-white">
				<div class="modal-header border-0">
					<h1 class="modal-title">Create New Material</h1>
					<button
						type="button"
						class="btn-close btn-close-white"
						data-bs-dismiss="modal"
					></button>
				</div>

				<div class="modal-body">
					<form @submit.prevent="createMaterial">
						<div class="mb-3">
							<label class="form-label text-white">Title</label>
							<input
								v-model="newMaterial.title"
								type="text"
								class="form-control input-bg text-white border-secondary"
								placeholder="Enter material title"
								required
							/>
						</div>

						<div class="mb-3">
							<label class="form-label text-white">Type</label>
							<select
								v-model="newMaterial.type"
								class="form-select input-bg text-white border-secondary"
								required
							>
								<option disabled value="">Select type</option>
								<option value="md">Markdown</option>
								<option value="link">Link</option>
								<option value="file">File</option>
							</select>
						</div>

						<div class="mb-3" v-if="newMaterial.type === 'md'">
							<label class="form-label text-white"
								>Markdown Content</label
							>
							<textarea
								v-model="newMaterial.description"
								class="form-control input-bg text-white border-secondary"
								rows="4"
								placeholder="Enter markdown content"
							></textarea>
						</div>

						<div
							class="mb-3"
							v-else-if="newMaterial.type === 'link'"
						>
							<label class="form-label text-white">Link</label>
							<input
								v-model="newMaterial.description"
								type="url"
								class="form-control input-bg text-white border-secondary"
								placeholder="https://example.com"
								required
							/>
						</div>

						<div
							class="mb-3"
							v-else-if="newMaterial.type === 'file'"
						>
							<label class="form-label text-white"
								>Upload File</label
							>
							<input
								type="file"
								class="form-control input-bg text-white border-secondary"
								@change="onFileSelected"
								required
							/>
						</div>

						<div class="text-end">
							<PrimaryButton
								:label="`Create`"
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