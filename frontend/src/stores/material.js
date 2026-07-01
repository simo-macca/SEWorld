import { defineStore } from 'pinia';
import {
	AUTH_GET_MATERIAL,
	DOWNLOAD_TOPIC_MATERIAL,
} from '@/utils/constants.js';
import { FileType, NOT_A_FILE, detectFileType } from '@/utils/file';

export const useMaterialStore = defineStore('materials', {
	state: () => ({
		materials: [],
	}),

	actions: {
		async fetchMaterials(did) {
			try {
				const response = await fetch(
					`${AUTH_GET_MATERIAL}/${did}/all_materials`,
					{
						method: 'GET',
						credentials: 'include',
					},
				);

				if (!response.ok) {
					this.materials = [];
					throw new Error(`HTTP error! Status: ${response.status}`);
				}

				const data = await response.json();
				this.materials = data.data;
			} catch (error) {
				this.materials = [];
				console.error('Error fetching materials:', error);
			}
		},

		async deleteMaterialByDid(did) {
			try {
				const response = await fetch(
					`/api/auth/material/delete/${did}`,
					{
						method: 'DELETE',
						credentials: 'include',
					},
				);

				if (!response.ok) {
					throw new Error(
						`Failed to delete material: ${response.status}`,
					);
				}

				this.materials = this.materials.filter(
					(mat) => mat.materialDid !== did,
				);
			} catch (error) {
				console.error('Error deleting material:', error);
				throw error;
			}
		},

		async uploadTextMaterial(topicDid, newMaterial) {
			try {
				const dto = {
					title: newMaterial.title,
					materialType: newMaterial.type,
					description: newMaterial.description,
				};

				const response = await fetch(
					`/api/auth/material/${topicDid}/upload`,
					{
						method: 'POST',
						credentials: 'include',
						headers: {
							'Content-Type': 'application/json',
						},
						body: JSON.stringify(dto),
					},
				);

				if (!response.ok)
					throw new Error(
						'Failed to create new material (markdown/link)',
					);
				return await response.json();
			} catch (error) {
				console.error('Error creating new material:', error);
				throw error;
			}
		},

		async uploadFileMaterial(topicDid, newMaterial) {
			const formData = new FormData();

			formData.append('file', newMaterial.file);

			const dto = {
				title: newMaterial.title,
				materialType: newMaterial.type,
				description: newMaterial.description,
			};
			formData.append(
				'materialDTO',
				new Blob([JSON.stringify(dto)], { type: 'application/json' }),
			);

			const response = await fetch(
				`/api/auth/material/${topicDid}/upload_file`,
				{
					method: 'POST',
					body: formData,
					credentials: 'include',
				},
			);

			if (!response.ok)
				throw new Error('Failed to create new material (file)');
			return await response.json();
		},

		async editMaterial(materialDid, updatedMaterial, oldName) {
			try {
				const formData = new FormData();
				const dto = {
					title: updatedMaterial.title,
					materialType: updatedMaterial.type,
					description: updatedMaterial.description,
				};

				formData.append(
					'materialDTO',
					new Blob([JSON.stringify(dto)], {
						type: 'application/json',
					}),
				);

				if (updatedMaterial.type === 'file' && updatedMaterial.file) {
					formData.append('file', updatedMaterial.file);
				} else {
					// fetch previous file
					const blob = await this.downloadMaterial(materialDid);
					const file = blob
						? blob
						: new Blob(['placeholder'], { type: 'text/plain' });
					const name = blob ? oldName : 'placeholder.txt';
					formData.append('file', file, name);
				}

				const response = await fetch(
					`/api/auth/material/update/${materialDid}`,
					{
						method: 'PATCH',
						credentials: 'include',
						body: formData,
					},
				);

				if (!response.ok) throw new Error('Failed to edit material');
				return await response.json();
			} catch (error) {
				console.error('Error editing material:', error);
				throw error;
			}
		},

		async downloadMaterial(did) {
			try {
				const response = await fetch(
					`${DOWNLOAD_TOPIC_MATERIAL}/${did}/download`,
					{
						method: 'GET',
						credentials: 'include',
					},
				);

				if (!response.ok) {
					throw new Error('Something went wrong!');
				}

				const blob = await response.blob();
				return blob;
			} catch (error) {
				console.error('error downloading material: ', error);
				return null;
			}
		},
	},

	getters: {
		getMaterialByDid: (state) => (did) =>
			state.materials.find((m) => m.materialDid == did),

		// Title
		sortedByTitleAsc:
			(state) =>
			(materials = undefined) => {
				return [...state.fillMaterials(materials)].sort((a, b) =>
					a.title.localeCompare(b.title),
				);
			},
		sortedByTitleDesc:
			(state) =>
			(materials = undefined) =>
				[...state.fillMaterials(materials)].sort((a, b) =>
					b.title.localeCompare(a.title),
				),

		// Filter By Type
		filterByMaterialsType:
			(state) =>
			(types = ['link', 'md', 'file'], materials = undefined) =>
				[...state.fillMaterials(materials)].filter((m) =>
					types.includes(m.materialType),
				),

		// Filter File Type
		filterByMaterialFilesType:
			(state) =>
			(fTypes = Object.values(FileType), materials = undefined) =>
				// Not a strict file filter
				[...state.fillMaterials(materials)].filter(
					(m) =>
						fTypes.includes(detectFileType(m.materialFileName)) ||
						detectFileType(m.materialFileName) === NOT_A_FILE,
				),

		// Fill the materials variable with the state material array if it is not valid
		fillMaterials: (state) => (materials) => {
			if (
				materials == undefined ||
				materials == null ||
				!Array.isArray(materials)
			) {
				materials = state.materials;
			}

			return materials;
		},
	},
});
