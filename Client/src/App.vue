<template>
	<div>
		<el-container>
			<el-aside width="300px">
				<app-menu/>
			</el-aside>
			<el-main style="height: 100vh">
				<router-view/>
			</el-main>
		</el-container>
	</div>
</template>

<script lang="ts">
	import {Component} from "vue-property-decorator"
	import Vue from "vue"
	import AppMenu from "@/components/AppMenu.vue"

	@Component({
		name: "App",
		components: {
			AppMenu
		}
	})
	export default class App extends Vue {
		
	}
</script>
