<template>
	<div class="teacher-table">
		<div class="teacher-table__table" :style="isNeedToShowLeftForm ? {'grid-column-end': 2} : {'grid-column-end': 3}">
			<el-card shadow="never" style="height: 100%" :body-style="{height: '100%'}">
				<el-form v-resize:throttle="onElementResize" ref="teacherSearchForm" :model="value" :inline="true" label-width="100px">
					<el-form-item size="mini">
						<el-radio-group size="mini" v-model="value.status">
							<el-radio label="Діючі">Діючі</el-radio>
							<el-radio label="Всі">Всі</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item size="mini" label="Кафедра: " style="min-width: 100%">
						<el-input size="mini" v-model="value.cathedra" style="width: 40vw"></el-input>
					</el-form-item>
					<br>
					<el-form-item size="mini">
						<el-button size="mini" type="primary" :loading="isLoading" @click="loadData">
							<el-icon name="search"></el-icon>
							<span>Отримати дані</span>
						</el-button>
						<el-button size="mini" type="success" @click="$router.push({name: 'InsertTeacherForm'})">
							<el-icon name="plus"></el-icon>
							<span>Додати викладача</span>
						</el-button>
					</el-form-item>
				</el-form>
				<el-table :data="tableData" border style="width: 100%" :height="tableHeight">
					<el-table-column prop="fullName" label="ПІБ" width="220"/>
					<el-table-column prop="position" label="Посада"/>
					<el-table-column prop="rank" label="Звання"/>
					<el-table-column prop="degree" label="Ступінь"/>
					<el-table-column prop="working" label="Діючий">
						<template slot-scope="scope" style="white-space: pre-line">
							<el-icon :class="[scope.row.working ? 'el-icon-check' : 'el-icon-close']"/>
						</template>
					</el-table-column>
					<el-table-column fixed="right" width="150" align="right">
						<template slot-scope="scope">
							<el-button type="text" @click="select(tableData[scope.$index])">
								<el-icon class="el-icon-edit" style="font-size: 25px"></el-icon>
							</el-button>
							<el-button type="text" @click="deleteData(tableData[scope.$index])">
								<el-icon class="el-icon-delete" style="color: #ff5252; font-size: 25px"></el-icon>
							</el-button>
						</template>
					</el-table-column>
				</el-table>
			</el-card>
		</div>
		<div class="teacher-table__update-form">
			<teacher-form
					v-if="isNeedToShowLeftForm"
					ref="teacherForm"
					v-model="selectedTeacher"
					:is-update="true"
					:cancel="() => {isVisible = false}"
					:submit="update"
					:is-submitting="isUpdating"
					:submit-button-text="'Відновити'"></teacher-form>
		</div>
		<el-dialog :visible="isNeedToShowDialog" width="85%" :before-close="() => {isVisible = false}">
			<teacher-form
					v-if="isNeedToShowDialog"
					ref="teacherForm"
					v-model="selectedTeacher"
					:is-update="true"
					:cancel="() => {isVisible = false}"
					:submit="update"
					:is-submitting="isUpdating"
					:submit-button-text="'Відновити'"></teacher-form>
		</el-dialog>
	</div>
</template>

<script lang="tsx">
	import Vue from "vue"
	import {Component, Prop, Watch} from "vue-property-decorator"
	import {Teacher, TeacherModel, validateObjectRequired, validateString} from "../../entities/entities"
	import TeacherForm from "./TeacherForm.vue"
	import {WindowSize, WindowSizeChangedEvent} from "../../plugins/window"
	import resize from "vue-resize-directive"

	@Component({
		name: "TeacherTable",
		components: {TeacherForm},
		directives: {
			resize
		}
	})
	export default class TeacherTable extends Vue {
		private tableData: Teacher[] = []
		private isVisible: boolean = false
		private isUpdating: boolean = false
		private isLoading: boolean = false
		private selectedTeacher: Teacher | null = null
		private value: TeacherModel = {
			cathedra: "",
			status: "Діючі"
		}

		private isXs: boolean
		private searchForm: Vue | null = null
		private tableHeight: number = this.$resizeWatcher.height - 192

		private mounted() {
			this.searchForm = (this.$refs["teacherSearchForm"] as Vue)
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

		private loadData() {
			this.isLoading = true
			this.axios.post("/teacher/find", {
				cathedra: this.value.cathedra,
				status: this.value.status,
			})
			.then((result) => {
				this.isLoading = false
				this.tableData = result.data.teachers
			})
			.catch((reason) => {
				this.$alert(reason.reason, "Помилка", {
					type: "error",
					confirmButtonText: "OK",
				})
				this.isLoading = false
			})
		}

		private deleteData(teacher: Teacher) {
			const promise = this.$msgbox({
				message: <span style="white-space: pre-line;">
							Ви намагаєтесь видалити викладача <p style="font-weight: 700">"{teacher.fullName}"</p>.<br/>
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
				return this.axios.delete("/teacher/delete", {
					params: {
						id: teacher.id
					},
				})
			}, value => {
				return Promise.reject("Aborted")
			})

			promise.then((result) => {
				if ((result.data as any).successful) {
					this.$alert("Інформація про викладача успішно видалена", "Успіх", {type: "success", confirmButtonText: "OK"})
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

		private select(teacher: Teacher) {
			if (this.isVisible) {
				this.isVisible = false
			}
			this.selectedTeacher = JSON.parse(JSON.stringify(teacher))
			this.isVisible = true
		}

		private get isNeedToShowDialog(): boolean {
			return this.isVisible && (this.isXs || this.$globalConfig.isInternetExplorer)
		}

		private get isNeedToShowLeftForm(): boolean {
			return this.isVisible && !this.isXs && !this.$globalConfig.isInternetExplorer
		}


		private update(teacher: Teacher) {
			this.isUpdating = false
			this.axios.post("/teacher/update", teacher).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Інформація про викладача успішно відредагована", "Успіх", {type: "success", confirmButtonText: "OK"})
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

	.teacher-table {
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