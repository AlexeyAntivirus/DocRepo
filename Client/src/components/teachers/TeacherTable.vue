<template>
	<el-container>
		<el-col :span="isVisible ? 16 : 24">
			<el-card shadow="never">
				<el-form :model="value" :inline="true" label-width="100px">
					<el-form-item>
						<el-radio-group v-model="value.status">
							<el-radio label="Діючі">Діючі</el-radio>
							<el-radio label="Всі">Всі</el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item label="Кафедра: ">
						<el-input v-model="value.cathedra" style="width: 20vw"></el-input>
					</el-form-item>
					<el-form-item>
						<el-button type="primary" :loading="isLoading" @click="loadData">Отримати дані</el-button>
						<el-button type="success" @click="$router.push({name: 'InsertTeacherForm'})">
							<el-icon name="plus"></el-icon>
							<span>Додати викладача</span>
						</el-button>
					</el-form-item>
				</el-form>
				<el-table :data="tableData" border style="width: 100%" height="79vh">
					<el-table-column prop="fullName" label="ПІБ" width="420"/>
					<el-table-column prop="position" label="Посада" width="280"/>
					<el-table-column prop="rank" label="Звання" width="280"/>
					<el-table-column prop="degree" label="Ступінь" width="280"/>
					<el-table-column prop="working" label="Діючий" width="120">
						<template slot-scope="scope" style="white-space: pre-line">
							<el-icon :class="[scope.row.working ? 'el-icon-check' : 'el-icon-close']"/>
						</template>
					</el-table-column>
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
		<el-col v-if="isVisible" :span="isVisible ? 8 : 0">
			<teacher-form ref="teacherForm"
					v-model="selectedTeacher"
					:is-update="true"
					:cancel="() => {isVisible = false}"
					:submit="update"
					:is-submitting="isUpdating"
					:submit-button-text="'Відновити'"></teacher-form>
		</el-col>
	</el-container>
</template>

<script lang="tsx">
	import Vue from "vue"
	import {Component, Prop, Watch} from "vue-property-decorator"
	import {Teacher, TeacherModel, validateObjectRequired, validateString} from "../../entities/entities"
	import TeacherForm from "./TeacherForm.vue"

	@Component({
		name: "TeacherTable",
		components: {TeacherForm}
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

		private select(teacher: Teacher) {
			this.selectedTeacher = JSON.parse(JSON.stringify(teacher))
			this.isVisible = true
		}

		private update(teacher: Teacher) {
			this.axios.post("/teacher/update", teacher).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Інформація про викладача успішно відредагована", "Успіх", {type: "success", confirmButtonText: "OK"})
				} else {
					this.$alert("З'явилася якась помилка", "Помилка", {type: "error", confirmButtonText: "OK"})
				}
			})
		}

	}
</script>

<style scoped>

</style>