<script>
import Header from '@/components/Header.vue';

import UserProfile from '@/components/user/profile/UserProfile.vue';

export default {
	components: { Header, UserProfile },
};
</script>

<!-- change alignment emojii profile page -->
<!-- change min / avg / max user/instructor view -->
<!-- look at feedback decimals: tofixed1 -->

<template>
	<Header />

	<UserProfile />
</template>

<style scoped>
</style>
