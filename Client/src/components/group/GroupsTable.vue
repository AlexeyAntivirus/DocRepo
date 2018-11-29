<template>
	<div class="groups-table">
		<div class="groups-table__table" :style="isNeedToShowLeftForm ? {'grid-column-end': 2} : {'grid-column-end': 3}">
			<el-card shadow="never" style="height: 100%" :body-style="{height: '100%'}">
				<el-form v-resize:throttle="onElementResize" ref="groupsSearchForm" :inline="true" label-width="100px">
					<el-form-item size="mini">
						<el-button size="mini" type="primary" :loading="isLoading" @click="loadData">
							<el-icon name="search"></el-icon>
							<span>Отримати дані</span>
						</el-button>
						<el-button size="mini" type="success" @click="$router.push({name: 'InsertGroupForm'})">
							<el-icon name="plus"></el-icon>
							<span>Додати групу</span>
						</el-button>
					</el-form-item>
				</el-form>
				<el-table :data="tableData" border style="width: 100%" :height="tableHeight">
					<el-table-column prop="name" label="Назва" width="220" fixed/>
					<el-table-column prop="semesterType" label="Семестр" width="120"/>
					<el-table-column prop="courseNumber" label="Курс" width="120"/>
					<el-table-column prop="beginYear" label="Початок року" width="120"/>
					<el-table-column prop="endYear" label="Кінець року" width="120"/>
					<el-table-column prop="faculty" label="Факультет" width="320"/>
					<el-table-column prop="branch" label="Галузь" width="320"/>
					<el-table-column prop="specialty" label="Спеціальність" width="320"/>
					<el-table-column prop="educationLevel" label="ОКР" width="220"/>
					<el-table-column prop="educationProgram" label="ОП" width="220"/>
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
					<el-table-column fixed="right" width="100" align="right">
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
		<div v-if="isNeedToShowLeftForm" class="groups-table__update-form" :style="{'height': tableHeight + 146 + 'px', 'overflow-y': 'auto'}">
			<groups-form
					v-if="isNeedToShowLeftForm"
					ref="groupsForm"
					v-model="selectedGroup"
					:is-update="true"
					:cancel="() => {isVisible = false}"
					:submit="update"
					:is-submitting="isUpdating"
					:submit-button-text="'Відновити'"/>
		</div>
		<el-dialog :visible="isNeedToShowDialog" width="85%" :before-close="() => {isVisible = false}">
			<groups-form
					v-if="isNeedToShowDialog"
					ref="groupsForm"
					v-model="selectedGroup"
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
	import { Component } from "vue-property-decorator"
	import { Group } from "../../entities/entities"
	import GroupsForm from "./GroupsForm.vue"
	import {WindowSize, WindowSizeChangedEvent} from "../../plugins/window"
	import resize from "vue-resize-directive"

	@Component({
		name: "GroupsTable",
		components: {GroupsForm},
		directives: {
			resize
		}
	})
	export default class GroupsTable extends Vue {
		private tableData: Group[] = []
		private isVisible: boolean = false
		private isLoading: boolean = false
		private selectedGroup: Group | null = null
		private isUpdating: boolean = false

		private isXs: boolean
		private searchForm: Vue | null = null
		private tableHeight: number = this.$resizeWatcher.height - 192

		private mounted() {
			this.searchForm = (this.$refs["groupsSearchForm"] as Vue)
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

		private get isNeedToShowDialog(): boolean {
			return this.isVisible && (this.isXs || this.$globalConfig.isInternetExplorer)
		}

		private get isNeedToShowLeftForm(): boolean {
			return this.isVisible && !this.isXs && !this.$globalConfig.isInternetExplorer
		}

		private calculateTableSize(size: WindowSize, searchForm: HTMLElement): number {
			return size.actualHeight - searchForm.offsetHeight - 143
		}

		private loadData() {
			this.isLoading = true
			this.axios.get("/group/all")
				.then((result) => {
					this.isLoading = false
					this.tableData = result.data.groups
				})
				.catch((reason) => {
					this.$alert(reason.reason, "Помилка", {
						type: "error",
						confirmButtonText: "OK",
					})
					this.isLoading = false
				})
		}

		private deleteData(group: Group) {
			const promise = this.$msgbox({
				message: <span style="white-space: pre-line;">
					Ви намагаєтесь видалити групу <p style="font-weight: 700">"{group.name}"</p>.<br/>
					Деякі дані груп можуть бути прив'язані до студентів та кваліфікаційних робіт.
					При видаленні, дані про студентів та кваліфікаційні роботи будуть мати недійсну інформацію.
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
				return this.axios.delete("/group/delete", {
					params: {
						id: group.id
					},
				})
			}, value => {
				return Promise.reject("Aborted")
			})

			promise.then((result) => {
				if ((result.data as any).successful) {
					this.$alert("Інформація про групу успішно видалена", "Успіх", {type: "success", confirmButtonText: "OK"})
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

		private select(group: Group) {
			if (this.isVisible) {
				this.isVisible = false
			}
			this.isVisible = true
			this.selectedGroup = JSON.parse(JSON.stringify(group))
		}

		private update(group: Group) {
			this.isUpdating = true

			this.axios.post("/group/update", group).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Інформація про групу успішно відредагована", "Успіх", {type: "success", confirmButtonText: "OK"})
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
	.groups-table {
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