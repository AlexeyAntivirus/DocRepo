<template>
	<el-container style="height: 100%">
		<el-col :span="16" :offset="4" style="height: 90vh; overflow-y: scroll;">
			<qualification-work-form v-model="newWork" 
					:submit-button-name="'Додати'"
					:cancel="cancel"
					:submit="insert"
					:is-update="false"
					:is-submitting="isInserting"/>
		</el-col>
	</el-container>
</template>

<script lang="ts">
	import QualificationWorkForm from "./QualificationWorkForm.vue"
	import Vue from "vue"
	import { Component } from "vue-property-decorator"
	import { QualificationWorkFormData } from "../../entities/entities"

	@Component({
		name: "QualificationWorkTable",
		components: {
			QualificationWorkForm
		}
	})
	export default class InsertQualificationWork extends Vue {
		private newWork: QualificationWorkFormData = {
			id: 0,
			title: "",
			beginYear: 2018,
			endYear: 2019,
			semesterNumber: 1,
			discipline: {
				id: 0,
				shortName: ""
			},
			group: {
				id: 0,
				groupName: ""
			},
			groupName: "",
			student: {
				id: 0,
				fullName: ""
			},
			workType: "Дипломна",
			studentFullName: "",
			faculty: "",
			specialty: "",
			branch: "",
			educationLevel: "",
			educationProgram: "",
			gradeECTS: "A",
			gradeNational: "Відмінно",
			grade: 100,
			courseNumber: 1,
			teachers: [],
			teacherNames: "",
			extramural: false,
			shortened: false
		}

		private isInserting: boolean = false

		private cancel() {
			this.$router.push({name: "ShowQualificationWork"})
		}

		private insert(work: QualificationWorkFormData, files: File[]) {
			this.isInserting = true
			const formData: FormData = new FormData()
			for (const file of files) {
				formData.append("files", file)
			}

			formData.append("info", JSON.stringify(work))
			this.axios.put("/works/insert", formData)
				.then(value => {
					this.isInserting = false
					if (!value.data.successful) {
						this.$alert("З'явилася якась помилка", "Помилка", {
							type: "error",
							confirmButtonText: "OK"
						})
					} else {
						this.$alert("Робота успішно додана", "Успіх", {
							type: "success",
							confirmButtonText: "OK"
						})
					}
				})
				.catch(reason => {
					this.isInserting = false
					this.$alert(reason.reason, "Помилка", {
						type: "error"
					})
				})
		}
	}
</script>

<style lang="sass" scoped>

</style>
