<template>
	<div class="students-table">
		<div class="students-table__table" :style="isNeedToShowLeftForm ? {'grid-column-end': 2} : {'grid-column-end': 3}">
			<el-card shadow="never" style="height: 100%" :body-style="{height: '100%'}">
				<el-form v-resize:throttle="onElementResize" ref="studentSearchForm" class="form" :inline="true">
					<el-form-item size="mini" label="Семестр: " prop="workType">
						<el-radio-group v-model="value.semesterType" @change="reset" :disabled="isDisabled">
							<el-radio label="Весняний">Весняний</el-radio>
							<el-radio label="Осінній">Осінній</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item size="mini" prop="extramural" label="Заочний: ">
						<el-checkbox v-model="value.extramural" @change="reset" :disabled="isDisabled"></el-checkbox>
					</el-form-item>
					<el-form-item size="mini" prop="shortened" label="Скорочений: ">
						<el-checkbox v-model="value.shortened" @change="reset" :disabled="isDisabled"></el-checkbox>
					</el-form-item>
					<br>
					<el-form-item size="mini" label="Навчальний рік: " label-width="20" style="width: 100%;">
						<el-col :span="8">
							<el-form-item prop="beginYear">
								<el-input-number size="mini" v-model="value.beginYear" style="width: 100%;"
								                 @change="onBeginYearChanged" :min="1930" :max="2070" :disabled="isDisabled"/>
							</el-form-item>
						</el-col>
						<el-col class="line" :span="2">-</el-col>
						<el-col :span="8">
							<el-form-item prop="endYear">
								<el-input-number size="mini" v-model="value.endYear" style="width: 100%;"
								                 @change="onEndYearChanged" :min="1931" :max="2071" :disabled="isDisabled"/>
							</el-form-item>
						</el-col>
					</el-form-item>
					<el-form-item size="mini" prop="courseNumber" label="Курс: ">
						<el-input-number placeholder="Pick a time" size="mini" :disabled="isDisabled"
						                 v-model="value.courseNumber"
						                 :min="1" :max="6"
						                 @change="onCourseNumberChanged"/>
					</el-form-item>
					<br>
					<el-form-item size="mini" prop="group" label="Група: " style="width: 100%">
						<el-select size="small" v-model="value.group" :disabled="isDisabled"
						           remote filterable
						           :loading="isLoading"
						           loading-text="Зачекайте доки їде запит."
						           :remote-method="findGroupsByParam"
						           @change="onGroupChange"
						           value-key="id" style="width: 100%">
							<el-option v-for="group in groupSuggestions" :key="group.id"
							           :value="group" :label="group.groupName"/>
						</el-select>
					</el-form-item>
					<el-form-item size="mini">
						<el-button size="mini" type="primary" @click.native.prevent.stop="loadData">
							<el-icon name="search"></el-icon>
							Отримати дані
						</el-button>
						<el-button size="mini" type="success" @click="$router.push({name: 'InsertStudentForm'})">
							<el-icon name="plus"></el-icon>
							Додати студента
						</el-button>
						<el-checkbox style="margin-left: 20px" v-model="isDisabled">Студенти без групи</el-checkbox>
					</el-form-item>
				</el-form>
				<el-table :data="tableData" border width="100%" :height="tableHeight">
					<el-table-column prop="fullName" label="ПІБ" width="520"/>
					<el-table-column prop="group.groupName" label="Група" width="320"/>
					<el-table-column prop="beginYear" label="Початок року" width="120">
						<template slot-scope="scope" style="white-space: pre-line">
							{{scope.row.beginYear === 0 ? '' : scope.row.beginYear}}
						</template>
					</el-table-column>
					<el-table-column prop="endYear" label="Кінець року" width="120">
						<template slot-scope="scope" style="white-space: pre-line">
							{{scope.row.endYear === 0 ? '' : scope.row.endYear}}
						</template>
					</el-table-column>
					<el-table-column prop="courseNumber" label="Курс" width="120">
						<template slot-scope="scope" style="white-space: pre-line">
							{{scope.row.courseNumber === 0 ? '' : scope.row.courseNumber}}
						</template>
					</el-table-column>
					<el-table-column prop="semesterType" label="Семестр" width="120"/>
					<el-table-column prop="extramural" label="Заочний" width="180">
						<template slot-scope="scope" style="white-space: pre-line">
							<el-icon v-if="scope.row.group.id !== 0" :class="[scope.row.extramural ? 'el-icon-check' : 'el-icon-close']"/>
						</template>
					</el-table-column>
					<el-table-column prop="shortened" label="Скорочений" width="180">
						<template slot-scope="scope" style="white-space: pre-line">
							<el-icon v-if="scope.row.group.id !== 0" :class="[scope.row.shortened ? 'el-icon-check' : 'el-icon-close']"/>
						</template>
					</el-table-column>
					<el-table-column fixed="right" width="100" align="right">
						<template slot-scope="scope">
							<el-button type="text" @click="select(tableData[scope.$index])">
								<el-icon class="el-icon-edit" style="font-size: 25px"></el-icon>
							</el-button>
							<el-button type="text" @click="deleteData(tableData[scope.$index])">
								<el-icon class="el-icon-delete" style="font-size: 25px; color: #ff5252"></el-icon>
							</el-button>
						</template>
					</el-table-column>
				</el-table>
			</el-card>
		</div>
		<div class="students-table__update-form">
			<students-form
					v-if="isNeedToShowLeftForm"
					ref="studentsForm"
					v-model="selectedStudent"
					:is-update="true"
					:cancel="() => {visible = false}"
					:submit="update"
					:is-submitting="isUpdating"
					:submit-button-text="'Відновити'"/>
		</div>
		<el-dialog :visible="isNeedToShowDialog" width="85%" :before-close="() => {visible = false}">
			<students-form
					v-if="isNeedToShowDialog"
					ref="studentsForm"
					v-model="selectedStudent"
					:is-update="true"
					:cancel="() => {visible = false}"
					:submit="update"
					:is-submitting="isUpdating"
					:submit-button-text="'Відновити'"/>
		</el-dialog>
	</div>
</template>

<script lang="tsx">
	import Vue from "vue"
	import {Component} from "vue-property-decorator"
	import {
		GroupView
	} from "../../entities/entities"
	import {Student} from "../../entities/entities"
	import StudentsForm from "./StudentsForm.vue"
	import {WindowSize, WindowSizeChangedEvent} from "../../plugins/window"
	import resize from "vue-resize-directive"
	import {AxiosPromise, AxiosResponse} from "axios"

	interface Model {
		beginYear: number
		endYear: number
		semesterType: string,
		group: GroupView
		courseNumber: number
		extramural: boolean
		shortened: boolean
	}

	@Component({
		name: "StudentsTable",
		components: {StudentsForm},
		directives: {
			resize
		}
	})
	export default class StudentsTable extends Vue {
		private tableData: Student[] = []

		private visible: boolean = false
		private isLoading: boolean = false
		private isUpdating: boolean = false

		private isDisabled: boolean = false

		private value: Model = {
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

		private groupSuggestions: GroupView[] = []

		private selectedStudent: Student | null = null

		private isXs: boolean
		private searchForm: Vue | null = null
		private tableHeight: number = this.$resizeWatcher.height - 192

		private mounted() {
			this.searchForm = (this.$refs["studentSearchForm"] as Vue)
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

		private calculateTableSize(size: WindowSize, searchForm: HTMLElement): number {
			return size.actualHeight - searchForm.offsetHeight - 143
		}

		private onElementResize(element: HTMLElement) {
			this.isXs = this.$resizeWatcher.size.actualWidth < 992
			this.tableHeight = this.$resizeWatcher.size.actualHeight - element.offsetHeight - 143
		}

		private select(element: Student) {
			if (this.visible) {
				this.selectedStudent = null
				this.visible = false
			}
			this.selectedStudent = JSON.parse(JSON.stringify(element))
			if (element.group.id === 0) {
				this.selectedStudent.semesterType = "Весняний"
				this.selectedStudent.beginYear = 2018
				this.selectedStudent.endYear = 2019
			}
			this.visible = true
		}

		private deleteData(element: Student) {
			const promise = this.$msgbox({
				message: <span style="white-space: pre-line;">
					Ви намагаєтесь видалити студента <p style="font-weight: 700">"{element.fullName}"</p>.<br/>
					Деякі дані студентів можуть бути прив'язані до кваліфікаційних робіт та груп.
					При видаленні, дані про групи кваліфікаційні роботи будуть мати недійсну інформацію.
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
				return this.axios.delete("/student/delete", {
					params: {
						id: element.id
					},
				})
			}, value => {
				return Promise.reject("Aborted")
			})

			promise.then((result) => {
				if ((result.data as any).successful) {
					this.$alert("Інформація про дисципліну успішно видалена", "Успіх", {type: "success", confirmButtonText: "OK"})
				} else {
					this.$alert("З'явилася якась помилка", "Помилка", {type: "error", confirmButtonText: "OK"})
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

		private onBeginYearChanged(val: number) {
			this.value.endYear = val + 1
			this.reset()
		}

		private onEndYearChanged(val: number) {
			this.value.beginYear = val - 1
			this.reset()
		}

		private onCourseNumberChanged(val: number, oldVal: number) {
			this.reset()
		}

		private onGroupChange(groupView: GroupView) {
			this.groupSuggestions = [groupView]
		}

		private reset() {
			this.value.group = {
				id: 0,
				groupName: ""
			}
		}

		private get isNeedToShowDialog(): boolean {
			return this.visible && (this.isXs || this.$globalConfig.isInternetExplorer)
		}

		private get isNeedToShowLeftForm(): boolean {
			return this.visible && !this.isXs && !this.$globalConfig.isInternetExplorer
		}

		private findGroupsByParam(groupNamePart: string) {
			if (groupNamePart === "") {
				return
			}
			this.isLoading = true
			this.axios.post("/group/get-by-course-number-and-academic-year", {
				beginYear: this.value.beginYear,
				endYear: this.value.endYear,
				courseNumber: this.value.courseNumber,
				semester: this.value.semesterType === "Весняний" ? 2 : 1,
				extramural: this.value.extramural,
				shortened: this.value.shortened,
				groupNamePart
			}).then((value) => {
				this.isLoading = false
				this.groupSuggestions = value.data.groups
			}).catch((reason) => {
				console.log(reason)
			})
		}

		private loadData() {
			this.isLoading = true
			if (this.isDisabled) {
				this.axios.get("/student/all/not-assigned")
				.then((result) => {
					this.isLoading = false
					this.tableData = result.data.students
				})
				.catch((reason) => {
					this.$alert(reason.reason, "Помилка", {
						type: "error",
						confirmButtonText: "OK",
					})
					this.isLoading = false
				})
			} else {
				this.axios.post("/student/find-students-by-fullname-and-group", {
					group: this.value.group,
					beginYear: this.value.beginYear,
					endYear: this.value.endYear,
					semesterType: this.value.semesterType
				})
				.then((result) => {
					this.isLoading = false
					this.tableData = result.data.students
				})
				.catch((reason) => {
					this.$alert(reason.reason, "Помилка", {
						type: "error",
						confirmButtonText: "OK",
					})
					this.isLoading = false
				})
			}
		}

		private update(student: Student) {
			this.isUpdating = true
			this.axios.post("/student/update", student).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Інформація про студента успішно відредагована", "Успіх", {type: "success", confirmButtonText: "OK"})
				} else {
					this.$alert("З'явилася якась помилка", "Помилка", {type: "error", confirmButtonText: "OK"})
				}
				this.isUpdating = false
			}).catch(reason => {
				this.$alert(reason.reason, "Помилка", {
					type: "error",
					confirmButtonText: "OK"
				})

				this.isUpdating = false
			})
		}

	}
</script>

<style scoped lang="scss">
	.form .el-form-item {
		margin-right: 6px;
	}

	.form > .el-form-item {
		margin-right: 40px !important;
		margin-bottom: 6px !important;
	}

	.line {
		text-align: center;
	}

	.students-table {
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
			box-sizing: content-box;
			max-height: 100% !important;
		}
	}
</style>