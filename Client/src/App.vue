<template>
	<el-container style="height: 100%">
		<el-header class="header" height="56px">
			<div class="header__logo">
				<img :src="logoImg" width="48" height="48">
			</div>
			<div class="header__text">
				<span>DocRepo</span>
			</div>
			<div class="header__logout">
				<el-button type="danger" @click="logout">Вихід</el-button>
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
	import auth from "@/auth"
	const logoImg = require("@/assets/logo.png")

	@Component({
		name: "App",
		components: {
			AppMenu
		}
	})
	export default class App extends Vue {
		private readonly logoImg = logoImg

		private logout() {
			this.$confirm("Ви впевнені, що бажаєте вийти з системи?", "Помилка", {
				confirmButtonText: "Так",
				cancelButtonText: "Ні",
				type: "warning"
			}).then(value => {
				const result = value as any
				if (result === "confirm" || result.action === "confirm") {
					this.$axios.post("/logout").then(value1 => {
						auth.isAuthenticated = false
						this.$router.push("/login")
					}).catch(reason => {
						this.$alert("З'явилася якась помилка.\n" + reason, "Помилка", {
							type: "error",
							confirmButtonText: "OK",
						})
					})
				}
			})
		}
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
		&__logout {

			line-height: 56px;
			float: right;
		}
	}
</style>
