<template>
	<el-container>
		<el-col :span="16">
			<el-card shadow="never">
				<line-chart ref="chart"></line-chart>
			</el-card>
		</el-col>
		<el-col :span="8">
			<el-card shadow="never">
				<el-form :model="model">
					<el-form-item size="small" label="Тип роботи: " prop="workType">
						<el-radio-group v-model="model.workType">
							<el-radio label="Дипломна"></el-radio>
							<el-radio label="Курсова"></el-radio>
						</el-radio-group>
					</el-form-item>
					<el-form-item size="small" label="Навчальний рік: ">
						<el-col :span="8">
							<el-form-item prop="beginYear">
								<el-input-number size="mini" v-model="model.beginYear" style="width: 100%;"
								                 @change="onBeginYearChanged" :min="1930" :max="2070"/>
							</el-form-item>
						</el-col>
						<el-col class="line" :span="2">-</el-col>
						<el-col :span="8">
							<el-form-item prop="endYear">
								<el-input-number size="mini" v-model="model.endYear" style="width: 100%;"
								                 @change="onEndYearChanged" :min="1931" :max="2071"/>
							</el-form-item>
						</el-col>
					</el-form-item>
					<el-form-item size="small" prop="cathedra" label="Кафедра: ">
						<el-input size="small" v-model="model.cathedra"/>
					</el-form-item>
					<el-form-item size="small" prop="faculty" label="Факультет: ">
						<el-input size="small" v-model="model.faculty"/>
					</el-form-item>
					<el-form-item size="small" prop="specialty" label="Спеціальність: ">
						<el-input size="small" v-model="model.specialty"/>
					</el-form-item>
					<el-form-item size="small" prop="teacher" label="Викладач: ">
						<el-select size="small" v-model="model.teacherId"
						           remote filterable clearable
						           :loading="isLoading"
						           loading-text="Зачекайте доки їде запит."
						           :remote-method="loadTeachers"
						           @change="teacherSuggestions = []"
						           value-key="id" style="width: 100%">
							<el-option v-for="teacher in teacherSuggestions"
							           :key="teacher.id"
							           :value="teacher.id" :label="teacher.fullName"/>
						</el-select>
					</el-form-item>
					<el-form-item>
						<el-button size="mini" type="primary" @click.native.stop.prevent="stat">Отримати дані</el-button>
					</el-form-item>
				</el-form>
			</el-card>
		</el-col>
	</el-container>
</template>

<script lang="ts">
	import Vue from "vue"
	import Component from "vue-class-component"
	import {TeacherView} from "@/entities/entities"

	const VueChartJs = require("vue-chartjs")

	const lineChart = Vue.component("line-chart", {
		extends: VueChartJs.Bar,
	})

	@Component({
		name: "Stat",
		components: {
			lineChart
		}
	})
	export default class Stat extends Vue {
		private model: any = {
			beginYear: 2017,
			endYear: 2018,
			workType: "Дипломна",
			cathedra: "",
			specialty: "",
			faculty: "",
			teacherId: null
		}
		private isLoading: boolean = false

		private teacherSuggestions: TeacherView[] = []

		private loadTeachers(part: string) {
			if (part.length === 0) {
				return
			}
			this.isLoading = true
			if (this.model.cathedra.length > 0) {
				this.axios.post("/teacher/view/find", {
					fullName: part,
					cathedra: this.model.cathedra
				}).then((value) => {
					console.log(value.data)
					this.isLoading = false
					this.teacherSuggestions = value.data.teachers
				}).catch((reason) => {
					this.isLoading = false
				})
			} else {
				this.axios.post("/teacher/view/find-by-name", {
					fullName: part
				}).then((value) => {
					this.teacherSuggestions = value.data.teacherViews
					this.isLoading = false
				}).catch((reason) => {
					this.isLoading = false
				})
			}
		}

		private onBeginYearChanged(val: number) {
			if (val === this.model.endYear) {
				this.model.endYear = val + 1
			}
		}

		private onEndYearChanged(val: number) {
			if (val === this.model.beginYear) {
				this.model.beginYear = val - 1
			}
		}

		private stat() {
			this.$axios.post("/stat/get", this.model)
				.then((resp) => {
					console.log(resp.data)
					const chart: any = this.$refs.chart
					chart.renderChart({
						labels: [
							"Кількість робіт"
						],
						datasets: [
							{
								label: "Загальна кількість робіт",
								data: [
									resp.data.wholeCount
								],
								backgroundColor: "#34ace0"
							},
							{
								label: "Якість",
								data: [
									resp.data.quality
								],
								backgroundColor: "#227093"
							},
							{
								label: "Успішність",
								data: [
									resp.data.success
								],
								backgroundColor: "#218c74"
							},
							{
								label: "Відмінно",
								data: [
									resp.data.agradeWorksCount
								],
								backgroundColor: "#33d9b2"
							},
							{
								label: "Добре",
								data: [
									resp.data.bgradeWorksCount
								],
								backgroundColor: "#ffb142"
							},
							{
								label: "Задовільно",
								data: [
									resp.data.cgradeWorksCount
								],
								backgroundColor: "#ff793f"
							},
							{
								label: "Не задовільно",
								data: [
									resp.data.fgradeWorksCount
								],
								backgroundColor: "#ff5252"
							}
						]
					}, {
						responsive: true,
						maintainAspectRatio: false,
						scales: {
							yAxes: [{
								ticks: {
									beginAtZero: true
								}
							}]
						}
					})
				})
				.catch((reason) => {
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