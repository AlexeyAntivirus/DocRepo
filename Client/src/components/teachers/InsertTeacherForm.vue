<template>
	<el-container style="height: 100%">
		<el-col :span="16" :offset="4" style="height: 90vh;">
			<teacher-form
					ref="teacherForm"
					v-model="value"
					:is-update="false"
					:cancel="cancel"
					:submit="insert"
					:is-submitting="isInserting"
					:submit-button-text="'Додати'"/>
		</el-col>
	</el-container>
</template>

<script lang="tsx">
	import Vue from "vue"
	import {Component, Prop, Watch} from "vue-property-decorator"
	import {Teacher, validateObjectRequired, validateString} from "../../entities/entities"
	import TeacherForm from "./TeacherForm.vue"

	@Component({
		name: "InsertTeacherForm",
		components: {TeacherForm}
	})
	export default class InsertTeacher extends Vue {
		private isInserting: boolean = false

		private value: Teacher = {
			id: 0,
			degree: "",
			position: "",
			rank: "",
			fullName: "",
			cathedra: "",
			working: false
		}

		private insert(teacher: Teacher) {
			this.isInserting = true
			this.axios.put("/teacher/insert", teacher).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Викладач успішно додан", "Успіх", {
						type: "success",
						confirmButtonText: "OK"
					})
					this.isInserting = false
				} else {
					this.$alert("З'явилася якась помилка", "Помилка", {
						type: "error",
						confirmButtonText: "OK",
					})
					this.isInserting = false
				}
			})
		}

		private cancel() {
			this.$router.push({name: "ShowTeacher"})
		}
	}
</script>

<style scoped>

</style>