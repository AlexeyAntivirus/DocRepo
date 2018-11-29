<template>
	<div class="qualification-work-table">
		<div class="qualification-work-table__table" :style="isNeedToShowLeftForm ? {'grid-column-end': 2} : {'grid-column-end': 3}">
			<el-card shadow="never" style="height: 100%" :body-style="{height: '100%'}">
				<el-form v-resize:throttle="onElementResize" :inline="true" :model="model" ref="workSearchForm">
					<el-form-item size="mini" required>
						<el-radio-group size="mini" v-model="model.workType">
							<el-radio label="Дипломна"></el-radio>
							<el-radio label="Курсова"></el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item size="mini" style="width: 300px">
						<el-col :span="11" :sm="11">
							<el-form-item>
								<el-input-number size="mini" v-model="model.beginYear" style="width: 100%"
								                 @change="onBeginYearChanged" :min="1930" :max="2070"/>
							</el-form-item>
						</el-col>
						<el-col class="line" :span="2" :sm="2">-</el-col>
						<el-col :span="11" :sm="11">
							<el-form-item>
								<el-input-number size="mini" v-model="model.endYear" style="width: 100%"
								                 @change="onEndYearChanged" :min="1931" :max="2071"/>
							</el-form-item>
						</el-col>
					</el-form-item>
					<el-form-item size="mini">
						<el-button size="mini" type="primary" @click.native.stop.prevent="loadData"
						           :loading="isLoading">
							<el-icon name="search"></el-icon>
							Отримати дані
						</el-button>
						<el-button size="mini" type="success" @click="$router.push({name: 'InsertQualificationWork'})">
							<el-icon name="plus"></el-icon>
							Додати роботу
						</el-button>
					</el-form-item>
				</el-form>
				<el-table ref="table" :data="tableData" border width="100%" :height="tableHeight"
						  :row-class-name="markInvalid">
					<el-table-column prop="title" label="Тема" width="150" fixed=""/>
					<el-table-column prop="beginYear" label="Початок року" width="120"/>
					<el-table-column prop="endYear" label="Кінець року" width="120"/>
					<el-table-column prop="semesterNumber" label="Семестр" width="120"/>
					<el-table-column prop="courseNumber" label="Курс" width="120"/>
					<el-table-column v-if="loadedDataWorkType !== 'Дипломна'" prop="discipline.shortName"
					                 label="Назва предмету" width="180"/>
					<el-table-column prop="student.fullName" label="Студент" width="120"/>
					<el-table-column prop="group.groupName" label="Група" width="120"/>
					<el-table-column prop="workType" label="Тип роботи" width="120"/>
					<el-table-column prop="faculty" label="Факультет" width="120"/>
					<el-table-column prop="specialty" label="Спеціальність" width="120"/>
					<el-table-column prop="branch" label="Галузь" width="120"/>
					<el-table-column prop="educationLevel" label="ОКР" width="120"/>
					<el-table-column prop="educationProgram" label="ОП" width="120"/>
					<el-table-column prop="grade" label="Оцінка" width="120"/>
					<el-table-column prop="gradeNational" label="Оцінка гос." width="120"/>
					<el-table-column prop="gradeECTS" label="Оцінка ECTS" width="120"/>
					<el-table-column prop="teacherNames" label="Керівники" width="120">
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
					<el-table-column fixed="right" width="110" align="right">
						<template slot-scope="scope">
							<a :href="$globalConfig.serverUrl + 'works/download-by-id?id=' + scope.row.id"
							   target="_blank">
								<el-icon class="el-icon-download" style="font-size: 25px"></el-icon>
							</a>
							<el-button type="text" @click.native="select(tableData[scope.$index])">
								<el-icon class="el-icon-edit" style="font-size: 25px"></el-icon>
							</el-button>
							<el-button type="text" @click="deleteData(tableData[scope.$index])" style="margin-left: 0 !important; ">
								<el-icon class="el-icon-delete" style="color: #ff5252; font-size: 25px"></el-icon>
							</el-button>
						</template>
					</el-table-column>
				</el-table>
			</el-card>
		</div>
		<div class="qualification-work-table__update-form">
			<qualification-work-form
					v-if="isNeedToShowLeftForm"
					ref="workForm"
					v-model="selectedWork"
					:is-update="true"
					:cancel="() => {visible = false}"
					:submit="update"
					:is-submitting="isUpdating"
					:submit-button-name="'Відновити'"/>
		</div>
		<el-dialog :visible="isNeedToShowDialog" width="85%" :before-close="() => {visible = false}">
			<qualification-work-form
					v-if="isNeedToShowDialog"
					ref="workForm"
					v-model="selectedWork"
					:is-update="true"
					:cancel="() => {visible = false}"
					:submit="update"
					:is-submitting="isUpdating"
					:submit-button-name="'Відновити'"/>
		</el-dialog>
	</div>
</template>

<script lang="tsx">
	import Vue from "vue"
	import {Component, Watch} from "vue-property-decorator"
	import {QualificationWorkFormData} from "../../entities/entities"
	import QualificationWorkForm from "./QualificationWorkForm.vue"
	import {WindowSize, WindowSizeChangedEvent} from "../../plugins/window"
	import resize from "vue-resize-directive"

	@Component({
		name: "QualificationWorkTable",
		components: {
			QualificationWorkForm
		},
		directives: {
			resize
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
		private searchForm: Vue | null = null
		private tableHeight: number = this.$resizeWatcher.height - 192

		private isXs: boolean

		private mounted() {
			this.searchForm = (this.$refs["workSearchForm"] as Vue)
			this.$resizeWatcher.addWindowSizeChangedListener(
				this.changeTableSizeAfterWindowSizeChanged)
			this.$resizeWatcher.dispatchWindowResizeChangedEvent()
			this.searchForm.$el.style.height = (this.searchForm.$el.offsetHeight + 1) + "px"
			setTimeout(() => this.searchForm.$el.style.height = "auto", 150)
		}

		private beforeDestroy() {
			this.$resizeWatcher.removeWindowSizeChangedListener(
				this.changeTableSizeAfterWindowSizeChanged)
		}

		private changeTableSizeAfterWindowSizeChanged(event: WindowSizeChangedEvent) {
			this.isXs = event.windowSize.actualWidth < 992
			this.tableHeight = this.calculateTableSize(event.windowSize, this.searchForm.$el)
		}

		private onElementResize(element: HTMLElement) {
			this.isXs = this.$resizeWatcher.size.actualWidth < 992

			this.tableHeight = this.$resizeWatcher.size.actualHeight - element.offsetHeight - 143
		}

		private calculateTableSize(size: WindowSize, searchForm: HTMLElement): number {
			return size.actualHeight - searchForm.offsetHeight - 143
		}

		private select(element: QualificationWorkFormData) {
			if (this.visible) {
				this.selectedWork = null
				this.visible = false
			}
			this.selectedWork = JSON.parse(JSON.stringify(element))
			this.visible = true
		}

		private deleteData(qualificationWorkFormData: QualificationWorkFormData) {
			const promise = this.$msgbox({
				message: <span style="white-space: pre-line;">
							Ви намагаєтесь видалити викладача <p style="font-weight: 700">"{qualificationWorkFormData.title}"</p>.<br/>
							Деякі дані викладачів можуть бути прив'язані до кваліфікаційних робіт.
							При видаленні кваліфікаційні роботи будуть мати недійсну інформацію.
							Ви впевнені що хочете видалити цього викладача?
						</span>,
				type: "warning",
				confirmButtonText: "Так",
				confirmButtonClass: "el-button--danger",
				cancelButtonText: "Ні",
				cancelButtonClass: "el-button--primary",
				showCancelButton: true,
				title: "Обережно! Видалення даних"
			}).then(value => {
				return this.axios.delete("/works/delete", {
					params: {
						id: qualificationWorkFormData.id
					},
				})
			}, value => {
				return Promise.reject("Aborted")
			})

			promise.then((result) => {
				if ((result.data as any).successful) {
					this.$alert("Інформація про кваліфікаційну роботу успішно видалена", "Успіх",
						{type: "success", confirmButtonText: "OK"})
				} else {
					this.$alert("З'явилася якась помилка", "Помилка",
						{type: "error", confirmButtonText: "OK"})
				}
			}, (reason) => {
				if (reason !== "Aborted") {
					this.$alert(reason.reason, "Помилка", {
						type: "error",
						confirmButtonText: "OK",
					})
				}
				this.isLoading = false
			})
		}

		private get isNeedToShowDialog(): boolean {
			return this.visible && (this.isXs || this.$globalConfig.isInternetExplorer)
		}

		private get isNeedToShowLeftForm(): boolean {
			return this.visible && !this.isXs && !this.$globalConfig.isInternetExplorer
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

		private markInvalid(rowData: {row: QualificationWorkFormData, rowIndex: number}): string {
			console.log(rowData)
			if (rowData.row.student === null
				|| (rowData.row.discipline === null && rowData.row.workType === "Курсова")
				|| rowData.row.group === null
				|| rowData.row.teachers === null
				|| rowData.row.teachers.length === 0) {
				return "warn-table-row"
			} else {
				return ""
			}
		}

		private update(value: QualificationWorkFormData, documentFile: File, presentationFile: File, files: File[]) {
			this.isUpdating = true

			const formData: FormData = new FormData()
			formData.append("doc", documentFile)
			formData.append("ppt", presentationFile)
			for (const file of files) {
				formData.append("files", file)
			}

			formData.append("info", JSON.stringify(value))
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

<style scoped lang="scss">
	.line {
		text-align: center;
	}

	.qualification-work-table {
		height: 100%;
		max-height: 100%;
		display: grid;
		grid-template-columns: 70% 30%;
		grid-template-rows: 100%;

		@media screen and (max-width: 1200px) {
			grid-template-columns: 65% 35%;
		}

		&__table {
			grid-column-start: 1;
		}

		&__update-form {
			grid-column-start: 2;
			grid-column-end: 3;
			overflow-y: scroll;
			box-sizing: content-box;
			max-height: 100% !important;
		}
	}
</style>