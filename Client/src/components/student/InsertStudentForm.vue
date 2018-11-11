<template>
	<el-container style="height: 100%">
		<el-col :span="16" :offset="4" style="height: 90vh;">
			<students-form v-model="value"
			     :submit-button-text="'Додати'"
			     :cancel="cancel"
			     :submit="insert"
			     :is-update="false"
			     :is-submitting="isInserting"/>
		</el-col>
	</el-container>
</template>

<script lang="ts">
	import Vue from "vue"
	import {Component, Prop, Watch} from "vue-property-decorator"
	import {
		GroupView,
		validateObjectRequired,
		validateString
	} from "../../entities/entities"
	import {Student} from "../../entities/entities"
	import StudentsForm from "./StudentsForm.vue"

	@Component({
		name: "InsertStudentForm",
		components: {StudentsForm}
	})
	export default class InsertStudentForm extends Vue {

		private value: Student = {
			id: 0,
			fullName: "",
			beginYear: 2018,
			endYear: 2019,
			semesterType: "Весняний",
			group: {
				id: 0,
				groupName: ""
			},
			courseNumber: 1,
			extramural: false,
			shortened: false
		}

		private isInserting: boolean = false

		private cancel() {
			this.$router.push({name: "ShowStudent"})
		}

		private insert() {
			this.isInserting = true
			this.axios.put("/student/insert", this.value).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Студент успішно додан", "Успіх", {
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

	}
</script>

<style scoped>

</style>