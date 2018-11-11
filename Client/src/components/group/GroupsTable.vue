<template>
	<el-card shadow="never">
		<el-form>
			<el-form-item>
				<el-button type="primary" :loading="isLoading" @click="loadData">Отримати дані</el-button>
				<el-button type="success" @click="$router.push({name: 'InsertGroupForm'})">
					<el-icon name="plus"></el-icon>
					<span>Додати групу</span>
				</el-button>
			</el-form-item>
		</el-form>
		<el-table :data="tableData" border style="width: 100%" height="79vh">
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
			<el-table-column fixed="right" width="150" align="right">
				<template slot-scope="scope">
					<el-button type="text" @click="select(tableData[scope.$index])">
						<el-icon class="el-icon-edit" style="font-size: 25px"></el-icon>
					</el-button>
				</template>
			</el-table-column>
		</el-table>
	</el-card>
</template>

<script lang="ts">
	import Vue from "vue"
	import { Component } from "vue-property-decorator"
	import { Group } from "../../entities/entities"

	@Component({
		name: "GroupsTable"
	})
	export default class GroupsTable extends Vue {
		private tableData: Group[] = []
		private isLoading: boolean = false

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
				})
		}

	}

</script>

<style scoped>

</style>