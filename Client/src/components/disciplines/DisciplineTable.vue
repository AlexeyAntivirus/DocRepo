<template>
	<el-container>
		<el-col :span="isVisible ? 16 : 24">
			<el-card shadow="never">
				<el-form :inline="true" label-width="100px">
					<el-form-item>
						<el-button type="primary" :loading="isLoading" @click="loadData">Отримати дані</el-button>
						<el-button type="success" @click="$router.push({name: 'InsertDisciplineForm'})">
							<el-icon name="plus"></el-icon>
							<span>Додати предмет</span>
						</el-button>
					</el-form-item>
				</el-form>
				<el-table :data="tableData" border style="width: 100%" height="79vh">
					<el-table-column prop="name" label="Назва" width="520"/>
					<el-table-column prop="shortName" label="Скорочена назва" width="380"/>
					<el-table-column prop="semesterNumber" label="Семестр" width="180"/>
					<el-table-column prop="workType" label="Тип роботи" width="280"/>
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
			<discipline-form ref="teacherForm"
			              v-model="selectedDiscipline"
			              :is-update="true"
			              :cancel="() => {isVisible = false}"
			              :submit="update"
			              :is-submitting="isUpdating"
			              :submit-button-text="'Відновити'"></discipline-form>
		</el-col>
	</el-container>
</template>

<script lang="tsx">
	import Vue from "vue"
	import {Component, Prop, Watch} from "vue-property-decorator"
	import DisciplineForm from "./DisciplineForm.vue"
	import {Discipline} from "../../entities/entities"

	@Component({
		name: "DisciplineTable",
		components: {DisciplineForm}
	})
	export default class DisciplineTable extends Vue {
		private isVisible: boolean = false
		private isLoading: boolean = false
		private tableData: Discipline[] = []
		private selectedDiscipline: Discipline | null = null

		private select(discipline: Discipline) {
			this.selectedDiscipline = JSON.parse(JSON.stringify(discipline))
			this.isVisible = true
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

		private update(discipline: Discipline) {
			this.axios.post("/discipline/update", discipline).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Інформація про предмет успішно відредаговано", "Успіх", {type: "success", confirmButtonText: "OK"})
				} else {
					this.$alert("З'явилася якась помилка", "Помилка", {type: "error", confirmButtonText: "OK"})
				}
			})
		}
	}
</script>

<style scoped>

</style>