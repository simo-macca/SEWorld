<script>
import GlobalPopup from '@/components/popup/GlobalPopup.vue';
import { registerGlobalPopup } from '@/utils/globalPopup';

export default {
	components: { GlobalPopup },

	mounted() {
		registerGlobalPopup(this.$refs.globalPopup);
	},
};
</script>

<template>
	<GlobalPopup ref="globalPopup" />
	<RouterView />
</template>

<!-- We should remove scoped here as it imports main css for the whole app.. -->
<!-- Still big style refactor needed from not on -->
<style>
</style>
