
<script>
import { FileType, detectFileType } from '@/utils/file';

// Icon Components
import IconCodeFile from '@/components/icons/IconCodeFile.vue';
import IconFile from '@/components/icons/IconFile.vue';
import IconImage from '@/components/icons/IconImage.vue';
import IconAudioFile from '@/components/icons/IconAudioFile.vue';
import IconPdfFile from '@/components/icons/IconPdfFile.vue';
import IconPresentationFile from '@/components/icons/IconPresentationFile.vue';
import IconTextFile from '@/components/icons/IconTextFile.vue';
import IconZipFile from '@/components/icons/IconZipFile.vue';
import IconVideoFile from '@/components/icons/IconVideoFile.vue';

export default {
	name: 'IconFileFactory',

	components: {
		IconCodeFile,
		IconImage,
		IconAudioFile,
		IconPdfFile,
		IconPresentationFile,
		IconTextFile,
		IconZipFile,
		IconFile,
		IconVideoFile,
	},

	props: {
		fileName: {
			type: String,
			required: true,
		},
	},

	computed: {
		currentComponent() {
			const fileType = detectFileType(this.fileName);
			switch (fileType) {
				case FileType.CODE_FILE:
					return 'IconCodeFile';
				case FileType.IMAGE_FILE:
					return 'IconImage';
				case FileType.AUDIO_FILE:
					return 'IconAudioFile';
				case FileType.PDF_FILE:
					return 'IconPdfFile';
				case FileType.PRESENTATION_FILE:
					return 'IconPresentationFile';
				case FileType.TEXT_FILE:
					return 'IconTextFile';
				case FileType.ZIP_FILE:
					return 'IconZipFile';
				case FileType.VIDEO_FILE:
					return 'IconVideoFile';
				default:
					return 'IconFile';
			}
		},
	},
};
</script>

<template>
	<component :is="currentComponent" />
</template>
  