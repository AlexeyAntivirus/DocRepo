<template>
	<el-container style="height: 100%">
		<el-col :span="visible ? 16 : 24">
			<el-card shadow="never" style="height: 100%">
				<el-form :inline="true" :model="model">
					<el-form-item required>
						<el-radio-group size="mini" v-model="model.workType">
							<el-radio label="Дипломна"></el-radio>
							<el-radio label="Курсова"></el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item>
						<el-col :span="11">
							<el-form-item>
								<el-input-number size="mini" v-model="model.beginYear"  style="width: 100%;"
						                 @change="onBeginYearChanged" :min="1930" :max="2070"/>
							</el-form-item>
						</el-col>
						<el-col class="line" :span="2">-</el-col>
						<el-col :span="11">
							<el-form-item>
								<el-input-number size="mini" v-model="model.endYear"  style="width: 100%;"
						                 @change="onEndYearChanged" :min="1931" :max="2071"/>
							</el-form-item>
						</el-col>
					</el-form-item>
					<el-form-item>
						<el-button size="mini" type="primary" @click.native.stop.prevent="loadData" :loading="isLoading">Отримати дані</el-button>
					</el-form-item>
					<el-form-item>
						<el-button size="mini" type="success" @click="$router.push({name: 'InsertQualificationWork'})">
							<el-icon name="plus"></el-icon>
							<span>Додати роботу</span>
						</el-button>
					</el-form-item>
				</el-form>
				<el-table :data="tableData" border style="width: 100%; height: 100%" height="79vh">
					<el-table-column prop="title" label="Тема" width="380" fixed=""/>
					<el-table-column prop="beginYear" label="Початок року" width="120"/>
					<el-table-column prop="endYear" label="Кінець року" width="120"/>
					<el-table-column prop="semesterNumber" label="Семестр" width="120"/>
					<el-table-column prop="courseNumber" label="Курс" width="120"/>
					<el-table-column v-if="loadedDataWorkType !== 'Дипломна'" prop="discipline.shortName"
					                 label="Назва предмету" width="180"/>
					<el-table-column prop="student.fullName" label="Студент" width="120"/>
					<el-table-column prop="group.groupName" label="Група" width="120"/>
					<el-table-column prop="workType" label="Тип роботи" width="120"/>
					<el-table-column prop="faculty" label="Факультет" width="220"/>
					<el-table-column prop="specialty" label="Спеціальність" width="220"/>
					<el-table-column prop="branch" label="Галузь" width="220"/>
					<el-table-column prop="educationLevel" label="ОКР" width="120"/>
					<el-table-column prop="educationProgram" label="ОП" width="120"/>
					<el-table-column prop="grade" label="Оцінка" width="120"/>
					<el-table-column prop="gradeNational" label="Оцінка гос." width="120"/>
					<el-table-column prop="gradeECTS" label="Оцінка ECTS" width="180"/>
					<el-table-column prop="teacherNames" label="Керівники" width="180">
						<template slot-scope="scope" style="white-space: pre-line">
							<span style="white-space: pre-line">{{scope.row.teacherNames}}</span>
						</template>
					</el-table-column>
					<el-table-column prop="extramural" label="Заочний" width="180">
						<template slot-scope="scope" style="white-space: pre-line">
							<el-icon :class="[scope.row.extramural ? 'el-icon-check' : 'el-icon-close']"/>
						</template>
					</el-table-column>
					<el-table-column prop="shortened" label="Скорочений" width="180">
						<template slot-scope="scope" style="white-space: pre-line">
							<el-icon :class="[scope.row.shortened ? 'el-icon-check' : 'el-icon-close']"/>
						</template>
					</el-table-column>
					<el-table-column fixed="right" width="150" align="right">
						<template slot-scope="scope">
							<a :href="$globalConfig.serverUrl + 'works/download-by-id?id=' + scope.row.id" target="_blank">
								<el-icon class="el-icon-download" style="font-size: 25px"></el-icon>
							</a>
							<el-button type="text" @click.native="select(tableData[scope.$index])">
								<el-icon class="el-icon-edit" style="font-size: 25px"></el-icon>
							</el-button>
						</template>
					</el-table-column>
				</el-table>
			</el-card>
		</el-col>
		<el-col v-if="visible" :span="visible ? 8 : 0" style="height: 90vh; overflow-y: scroll;">
			<qualification-work-form
					ref="workForm" 
					v-model="selectedWork" 
					:is-update="true"
					:cancel="() => {visible = false}"
					:submit="update"
					:is-submitting="isUpdating"
					:submit-button-name="'Відновити'"/>
		</el-col>
	</el-container>
</template>

<script lang="tsx">
	import Vue, { VNode } from "vue"
	import { Component } from "vue-property-decorator"
	import { QualificationWorkFormData } from "../../entities/entities"
	import QualificationWorkForm from "./QualificationWorkForm.vue"
	import { eventbus } from "../../events/eventbus"

	@Component({
		name: "QualificationWorkTable",
		components: {
			QualificationWorkForm
		}
	})
	export default class QualificationWorkTable extends Vue {
		private tableData: QualificationWorkFormData[] = []

		private visible: boolean = false

		private model: {
			workType: string
			beginYear: number
			endYear: number
		} = {
			workType: "Дипломна",
			beginYear: 2017,
			endYear: 2018
		}

		private loadedDataWorkType: string = "Дипломна"
		private isLoading: boolean = false
		private isUpdating: boolean = false

		private selectedWork: QualificationWorkFormData | null = null

		private select(element: QualificationWorkFormData) {
			if (this.visible) {
				this.visible = false
			}

			this.selectedWork = JSON.parse(JSON.stringify(element))
			this.visible = true
		}

		private loadData() {
			this.isLoading = true
			this.axios.post("/works/find/all-by-academic-year-and-work-type", this.model)
				.then((result) => {
					this.loadedDataWorkType = this.model.workType
					this.isLoading = false
					this.tableData = result.data.works
				})
				.catch((reason) => {
					this.$alert(reason.reason, "Помилка", {
						type: "error",
						confirmButtonText: "OK"
					})
					this.isLoading = false
				})
		}

		private onBeginYearChanged(val: number) {
			this.model.endYear = val + 1
		}

		private onEndYearChanged(val: number) {
			this.model.beginYear = val - 1
		}

		private update(value: QualificationWorkFormData, files: File[]) {
			this.isUpdating = true

			const formData: FormData = new FormData()
			for (const file of files) {
				formData.append("files", file)
			}

			formData.append("info", JSON.stringify(this.selectedWork))
			this.axios.post("/works/update", formData)
				.then(resp => {
					if (!resp.data.successful) {	
						this.$alert("З'явилася якась помилка", "Помилка", {
							type: "error",
							confirmButtonText: "OK"
						})
					} else {
						this.$alert("Дані успішно відновлені", "Успіх", {
							type: "success",
							confirmButtonText: "OK"
						})
					}

					this.isUpdating = false
				})
				.catch(reason => {
					this.$alert(reason.reason, "Помилка", {
						type: "error",
						confirmButtonText: "OK"
					})

					this.isUpdating = false
				})
		}

		private delete(element: QualificationWorkFormData) {
			console.log(element)
			this.axios.delete("/works/delete", {
				params: {
					id: this.selectedWork.id
				}
			})
				.then(resp => {
					if (!resp.data.successful) {
						this.$alert("З'явилася якась помилка", "Помилка", {
							type: "error",
							confirmButtonText: "OK"
						})
					} else {
						this.$alert("Дані успішно видалені", "Успіх", {
							type: "success",
							confirmButtonText: "OK"
						})
					}
				})
				.catch(reason => {
					this.$alert(reason.reason, "Помилка", {
						type: "error",
						confirmButtonText: "OK"
					})
				})
		}
	}
</script>

<style scoped>
	.line {
		text-align: center;
	}
</style>