<template>
	<el-container style="height: 100%">
		<el-header class="header" height="56px">
			<div class="header__logo">
				<img :src="logoImg" width="48" height="48">
			</div>
			<div class="header__text">
				<span>DocRepo</span>
			</div>
		</el-header>
		<el-container>
			<app-menu/>
			<el-main>
				<router-view/>
			</el-main>
		</el-container>
	</el-container>
</template>

<script lang="ts">
	import {Component} from "vue-property-decorator"
	import Vue from "vue"
	import AppMenu from "@/components/AppMenu.vue"
	import {WindowSize} from "./plugins/window"
	const logoImg = require("@/assets/logo.png")

	@Component({
		name: "App",
		components: {
			AppMenu
		}
	})
	export default class App extends Vue {
		private readonly logoImg = logoImg

	}
</script>

<style lang="scss" scoped>
	.header {
		background-color: #2c2c54;

		&__logo {
			line-height: 88px;
			font-size: 28px;
			color: white;
			float: left;
		}
		&__text {
			line-height: 56px;
			float: left;
			margin-left: 5px;

			> span {
				font-size: 28px;
				color: white;
			}
		}
	}
</style>
