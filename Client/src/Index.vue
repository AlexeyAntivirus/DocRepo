<template>
	<router-view></router-view>
</template>

<script>
	import {Component} from "vue-property-decorator"
	import Vue from "vue"

	@Component({
		name: "Index"
	})
	export default class Login extends Vue {
	}
</script>

<style scoped>

</style>