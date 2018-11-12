<template>
	<el-container>
		<el-col :span="16" :offset="4">
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
					<el-form-item>
						<el-button size="mini" type="primary" @click.native.stop.prevent="stat">Отримати дані</el-button>
					</el-form-item>
				</el-form>
				<line-chart ref="chart"></line-chart>
			</el-card>
		</el-col>
	</el-container>
</template>

<script>
	import Vue from "vue"
	import Component from "vue-class-component"
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
		model = {
			beginYear: 2017,
			endYear: 2018,
			workType: "Дипломна"
		}
		onBeginYearChanged(val) {
			this.model.endYear = val + 1
		}

		onEndYearChanged(val) {
			this.model.beginYear = val - 1
		}

		stat() {
			this.$axios.post("/stat/get", this.model)
				.then((resp) => {
					this.$refs.chart.renderChart({
						labels: ["Загальна кількість", "Відмінно", "Добре", "Задовільно", "Не задовільно"],
						datasets: [
							{
								label: "Кількість робіт",
								backgroundColor: "#f87979",
								data: [
									resp.data.wholeCount,
									resp.data.agradeWorksCount,
									resp.data.bgradeWorksCount,
									resp.data.cgradeWorksCount,
									resp.data.fgradeWorksCount
								]
							}
						]
					}, {responsive: true, maintainAspectRatio: false})
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