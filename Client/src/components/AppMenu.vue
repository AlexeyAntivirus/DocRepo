<template>
	<el-menu class="app-menu"
	         :default-active="$route.path"
	         router
	         background-color="#40407a"
			 text-color="#FFFFFF"
		     active-text-color="#f6b93b">
		<el-menu-item index="/works">Дипломні та курсові</el-menu-item>
		<el-menu-item index="/students">Студенти</el-menu-item>
		<el-menu-item index="/prep">Викладачі</el-menu-item>
		<el-menu-item index="/groups">Групи</el-menu-item>
		<el-menu-item index="/disciplines">Предмети</el-menu-item>
	</el-menu>
</template>

<script lang="ts">
	import Vue from "vue"
	import {Component, Prop, Watch} from "vue-property-decorator"

	@Component({
		name: "AppMenu"
	})
	export default class AppMenu extends Vue {

	}
</script>

<style lang="scss" scoped>
	.app-menu {
		height: 100%;
	}
</style>


