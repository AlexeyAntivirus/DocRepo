<template>
	<el-container>
		<el-col :span="visible ? 16 : 24">
			<el-card shadow="never">
				<el-form class="form" style="margin-bottom: 20px">
					<el-form-item size="mini" label="Семестр: " prop="workType">
						<el-radio-group v-model="value.semesterType" @change="reset">
							<el-radio label="Весняний">Весняний</el-radio>
							<el-radio label="Осінній">Осінній</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item size="mini" prop="extramural" label="Заочний: ">
						<el-checkbox v-model="value.extramural"></el-checkbox>
					</el-form-item>
					<el-form-item size="mini" prop="shortened" label="Скорочений: ">
						<el-checkbox v-model="value.shortened"></el-checkbox>
					</el-form-item>
					<el-form-item size="mini" label="Навчальний рік: " style="width: 50%">
						<el-col :span="8">
							<el-form-item prop="beginYear">
								<el-input-number size="mini" v-model="value.beginYear" style="width: 100%;"
								                 @change="onBeginYearChanged" :min="1930" :max="2070"/>
							</el-form-item>
						</el-col>
						<el-col class="line" :span="1">-</el-col>
						<el-col :span="8">
							<el-form-item prop="endYear">
								<el-input-number size="mini" v-model="value.endYear" style="width: 100%;"
								                 @change="onEndYearChanged" :min="1931" :max="2071"/>
							</el-form-item>
						</el-col>
					</el-form-item>
					<el-form-item size="mini" prop="courseNumber" label="Курс: ">
						<el-input-number placeholder="Pick a time" size="small"
						                 v-model="value.courseNumber"
						                 :min="1" :max="6"
						                 @change="onCourseNumberChanged"/>
					</el-form-item>
					<el-form-item size="mini" prop="group" label="Група: " style="width: 50%">
						<el-select size="small" v-model="value.group"
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
					<el-form-item>
						<el-button type="primary" @click.native.prevent.stop="loadData">Отримати дані</el-button>
						<el-button type="success" @click="$router.push({name: 'InsertStudentForm'})">
							<el-icon name="plus"></el-icon>
							<span>Додати студента</span>
						</el-button>
					</el-form-item>
				</el-form>
				<el-table :data="tableData" border style="width: 100%" height="55vh">
					<el-table-column prop="fullName" label="ПІБ"/>
					<el-table-column prop="group.groupName" label="Група" width="320"/>
					<el-table-column prop="beginYear" label="Початок року" width="120"/>
					<el-table-column prop="endYear" label="Кінець року" width="120"/>
					<el-table-column prop="semesterType" label="Семестр" width="120"/>
					<el-table-column prop="courseNumber" label="Курс" width="120"/>
					<el-table-column fixed="right" width="150" align="right">
						<template slot-scope="scope">
							<el-button type="text" @click="select(tableData[scope.$index])">
								<el-icon class="el-icon-edit" style="font-size: 25px"></el-icon>
							</el-button>
						</template>
					</el-table-column>
				</el-table>
			</el-card>
		</el-col>
		<el-col v-if="visible" :span="visible ? 8 : 0" style="height: 90vh;">
			<students-form
					ref="studentsForm"
					v-model="selectedStudent"
					:is-update="true"
					:cancel="() => {visible = false}"
					:submit="update"
					:is-submitting="isUpdating"
					:submit-button-text="'Відновити'"/>
		</el-col>
	</el-container>
</template>

<script lang="ts">
	import Vue from "vue"
	import {Component} from "vue-property-decorator"
	import {
		GroupView,
		validateObjectRequired,
		validateString
	} from "../../entities/entities"
	import {Student} from "../../entities/entities"
	import StudentsForm from "./StudentsForm.vue"

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
		components: {StudentsForm}
	})
	export default class StudentsTable extends Vue {
		private tableData: Student[] = []

		private visible: boolean = false
		private isLoading: boolean = false
		private isUpdating: boolean = false

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

		private select(element: Student) {
			this.selectedStudent = JSON.parse(JSON.stringify(element))
			this.visible = true
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
				})
		}

		private update() {
			this.axios.post("/student/update", this.selectedStudent).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Студент успішно додан", "Успіх", {type: "success", confirmButtonText: "OK"})
				} else {
					this.$alert("З'явилася якась помилка", "Помилка", {type: "error", confirmButtonText: "OK"})
				}
			})
		}

	}
</script>

<style scoped lang="scss">
	.form > .el-form-item {
		margin-bottom: 4px;
	}

	.line {
		text-align: center;
	}
</style>