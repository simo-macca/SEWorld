
<script>
import IconBiEyeSlash from '~icons/bi/eye-slash';
import IconBiEye from '~icons/bi/eye';

import { BPopover } from 'bootstrap-vue-next';

export default {
	components: {
		IconBiEyeSlash,
		IconBiEye,
		BPopover,
	},

	props: {
		isPublic: {
			type: Boolean,
			required: false,
			default: false,
		},

		aiResponseDid: {
			type: String,
			required: true,
		},
	},
};
</script>

<template>
	<div class="d-flex flex-column align-items-center justify-content-center">
		<div
			:class="`d-flex flex-column align-items-center justify-content-center placeholder-style`"
			style="width: 45px; height: 40px"
			:id="`public-private-placeholder-${aiResponseDid}`"
		>
			<IconBiEye v-if="isPublic" />
			<IconBiEyeSlash v-else />
		</div>
		<BPopover
			:target="`public-private-placeholder-${aiResponseDid}`"
			placement="bottom"
			triggers="hover"
			:delay="{ show: 0, hide: 0 }"
		>
			<h5 v-if="isPublic" class="fs-6 m-0">
				This question is public, anyone can see it
			</h5>
			<h5 v-else class="fs-6 m-0">
				This question is private, you are the only one who can see it
			</h5>
		</BPopover>
	</div>
</template>


<style scoped>
.placeholder-style {
	background: #d63384;
	border-radius: 8px;
}
</style>
