<template>
	<el-container style="height: 100%">
		<el-col :span="16" :offset="4" style="height: 90vh;">
			<discipline-form
					ref="groupsForm"
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
	// import {Discipline, DisciplineModel, validateObjectRequired, validateString} from "../../entities/entities"
	import DisciplineForm from "./DisciplineForm.vue"
	import {Discipline} from "../../entities/entities"

	@Component({
		name: "InsertDisciplineForm",
		components: {DisciplineForm}
	})
	export default class InsertDisciplineTable extends Vue {
		private isInserting: boolean = false

		private value: Discipline = {
			id: 0,
			shortName: "",
			workType: "курсова робота",
			name: "",
			semesterNumber: 1
		}

		private insert(discipline: Discipline) {
			this.isInserting = true
			this.axios.put("/discipline/insert", discipline).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Предмет успішно додан", "Успіх", {
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
			this.$router.push({name: "ShowDiscipline"})
		}
	}
</script>

<style scoped>

</style>