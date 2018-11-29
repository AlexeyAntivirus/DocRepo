<template>
	<el-container style="height: 100%">
		<el-col ref="placeholder"
		        :xs="{offset: 0, span: 24}"
		        :sm="{offset: 0, span: 24}"
		        :md="{offset: 4, span: 16}"
		        :lg="{offset: 4, span: 16}"
		        :xl="{offset: 4, span: 16}"
		        style="overflow-y: scroll;">
			<qualification-work-form v-model="newWork"
			                         :submit-button-name="'Додати'"
			                         :cancel="cancel"
			                         :submit="insert"
			                         :is-update="false"
			                         :is-submitting="isInserting"
			/>
		</el-col>
	</el-container>
</template>

<script lang="ts">
	import QualificationWorkForm from "./QualificationWorkForm.vue"
	import Vue from "vue"
	import {Component} from "vue-property-decorator"
	import {QualificationWorkFormData} from "../../entities/entities"
	import {WindowSizeChangedEvent} from "../../plugins/window"

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

		private mounted() {
			this.$resizeWatcher.addWindowSizeChangedListener(this.onResize)
		}

		private beforeDestroy() {
			this.$resizeWatcher.removeWindowSizeChangedListener(this.onResize)
		}

		private onResize(size: WindowSizeChangedEvent) {
			const placeholder: Vue = this.$refs.placeholder as Vue
			placeholder.$el.style.height = `${size.windowSize.actualHeight - 96}px`
		}

		private cancel() {
			this.$router.push({name: "ShowQualificationWork"})
		}

		private insert(work: QualificationWorkFormData, documentFile: File, presentationFile: File, files: File[]) {
			this.isInserting = true

			const formData: FormData = new FormData()
			formData.append("doc", documentFile)
			formData.append("ppt", presentationFile)
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
					this.$alert(reason.body, "Помилка", {
						type: "error",
						confirmButtonText: "OK"
					})
				})
		}
	}
</script>

<style lang="sass" scoped>

</style>
