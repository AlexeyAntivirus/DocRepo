<template>
	<el-container >
		<el-card id="inner" shadow="never">
			<el-form :model="login">
				<el-form-item label="Логін">
					<el-input v-model="login.login" type="text" placeholder="Уведіть логін"></el-input>
				</el-form-item>
				<el-form-item label="Пароль">
					<el-input v-model="login.password" type="password" placeholder="Уведіть пароль"></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="send">Увійти</el-button>
				</el-form-item>
			</el-form>
		</el-card>
	</el-container>
</template>

<script lang="ts">
	import {Component} from "vue-property-decorator"
	import Vue from "vue"
	import auth from "@/auth"

	@Component({
		name: "Login"
	})
	export default class Login extends Vue {
		private login: any = {
			login: "",
			password: ""
		}

		private send() {
			if (process.env.NODE_ENV === "development") {
				auth.isAuthenticated = true
				this.$router.push({path: "/app"})
			}

			this.$axios.post("/login", this.login)
				.then(resp => {
					if (resp.data === true) {
						auth.isAuthenticated = true
						this.$router.push({path: "/app"})
					}
				})
				.catch(reason => {
					this.$alert("Ви ввели неправильні логін або пароль", "Помилка", {
						confirmButtonText: "OK",
						type: "error"
					}).then()
				})

		}
	}
</script>

<style scoped>
	#inner {
		width: 50%;
		margin: 0 auto;
	}
</style>