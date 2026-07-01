<script>
import Header from '@/components/Header.vue';

import MyAiVue from '@/components/user/myai/MyAi.vue';

export default {
	name: 'MyAiView',
	components: {
		Header,
		MyAiVue,
	},
};
</script>

<template>
	<Header />

	<MyAiVue />
</template>
