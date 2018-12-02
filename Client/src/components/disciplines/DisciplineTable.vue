<template>
	<div class="discipline-table">
		<div class="discipline-table__table" :style="isNeedToShowLeftForm ? {'grid-column-end': 2} : {'grid-column-end': 3}">
			<el-card shadow="never" style="height: 100%" :body-style="{height: '100%'}">
				<el-form :inline="true" label-width="100px" ref="disciplineSearchForm">
					<el-form-item>
						<el-button size="mini" type="primary" :loading="isLoading" @click="loadData">
							<el-icon name="search"></el-icon>
							<span>Отримати дані</span>
						</el-button>
						<el-button size="mini" type="success" @click="$router.push({name: 'InsertDisciplineForm'})">
							<el-icon name="plus"></el-icon>
							<span>Додати предмет</span>
						</el-button>
					</el-form-item>
				</el-form>
				<el-table :data="tableData" border style="width: 100%" :height="tableHeight">
					<el-table-column prop="name" label="Назва" width="220"/>
					<el-table-column prop="shortName" label="Скорочена назва"/>
					<el-table-column prop="semesterNumber" label="Семестр"/>
					<el-table-column prop="workType" label="Тип роботи"/>
					<el-table-column fixed="right" align="right" width="75">
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
		<div class="discipline-table__update-form">
			<discipline-form ref="disciplineForm"
			                 v-if="isNeedToShowLeftForm"
			              v-model="selectedDiscipline"
			              :is-update="true"
			              :cancel="() => {isVisible = false}"
			              :submit="update"
			              :is-submitting="isUpdating"
			              :submit-button-text="'Відновити'"></discipline-form>
		</div>
		<el-dialog :visible="isNeedToShowDialog">
			<discipline-form ref="disciplineForm"
			                 v-if="isNeedToShowDialog"
			                 v-model="selectedDiscipline"
			                 :is-update="true"
			                 :cancel="() => {isVisible = false}"
			                 :submit="update"
			                 :is-submitting="isUpdating"
			                 :submit-button-text="'Відновити'"></discipline-form>
		</el-dialog>
	</div>
</template>

<script lang="tsx">
	import Vue from "vue"
	import {Component, Prop, Watch} from "vue-property-decorator"
	import DisciplineForm from "./DisciplineForm.vue"
	import {Discipline, QualificationWorkFormData} from "../../entities/entities"
	import {WindowSize, WindowSizeChangedEvent} from "../../plugins/window"

	@Component({
		name: "DisciplineTable",
		components: {DisciplineForm}
	})
	export default class DisciplineTable extends Vue {
		private isVisible: boolean = false
		private isLoading: boolean = false
		private isUpdating: boolean = false
		private tableData: Discipline[] = []
		private selectedDiscipline: Discipline | null = null

		private searchForm: Vue | null = null
		private tableHeight: number = this.$resizeWatcher.height - 212

		private isXs: boolean

		private mounted() {
			this.searchForm = (this.$refs["disciplineSearchForm"] as Vue)
			this.$resizeWatcher.addWindowSizeChangedListener(
				this.changeTableSizeAfterWindowSizeChanged)
			this.$resizeWatcher.dispatchWindowResizeChangedEvent()
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
			console.log("WIn: " + size.actualHeight)
			console.log("WIn: " + searchForm.offsetHeight)
			return size.actualHeight - searchForm.offsetHeight - 192
		}

		private select(discipline: Discipline) {
			this.selectedDiscipline = JSON.parse(JSON.stringify(discipline))
			this.isVisible = true
		}

		private get isNeedToShowDialog(): boolean {
			return this.isVisible && (this.isXs || this.$globalConfig.isInternetExplorer)
		}

		private get isNeedToShowLeftForm(): boolean {
			return this.isVisible && !this.isXs && !this.$globalConfig.isInternetExplorer
		}

		private loadData() {
			this.isLoading = true
			this.axios.get("/discipline/all")
				.then((result) => {
					this.isLoading = false
					this.tableData = result.data.disciplines
				})
				.catch((reason) => {
					this.$alert(reason.reason, "Помилка", {
						type: "error",
						confirmButtonText: "OK",
					})
					this.isLoading = false
				})
		}

		private deleteData(discipline: Discipline) {
			const promise = this.$msgbox({
				message: <span style="white-space: pre-line;">
							Ви намагаєтесь видалити викладача <p style="font-weight: 700">"{discipline.name}"</p>.<br/>
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
				return this.axios.delete("/discipline/delete", {
					params: {
						id: discipline.id
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

		private update(discipline: Discipline) {
			this.isUpdating = true

			this.axios.post("/discipline/update", discipline).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Інформація про предмет успішно відредаговано", "Успіх", {type: "success", confirmButtonText: "OK"})
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
	.discipline-table {
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