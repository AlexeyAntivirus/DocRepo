<template>
	<el-card shadow="never">
		<el-form :model="value">
			<el-form-item size="small" label="Семестр: " prop="semesterType">
				<el-radio-group v-model="value.semesterType">
					<el-radio label="Весняний">Весняний</el-radio>
					<el-radio label="Осінній">Осінній</el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item size="small" prop="extramural" label="Заочний: ">
				<el-checkbox v-model="value.extramural"></el-checkbox>
			</el-form-item>
			<el-form-item size="small" prop="shortened" label="Скорочений: ">
				<el-checkbox v-model="value.shortened"></el-checkbox>
			</el-form-item>
			<el-form-item size="small" label="Навчальний рік: ">
				<el-col :span="8">
					<el-form-item prop="beginYear">
						<el-input-number size="mini" v-model="value.beginYear" style="width: 100%;"
						                 @change="onBeginYearChanged" :min="1930" :max="2070"/>
					</el-form-item>
				</el-col>
				<el-col class="line" :span="2">-</el-col>
				<el-col :span="8">
					<el-form-item prop="endYear">
						<el-input-number size="mini" v-model="value.endYear" style="width: 100%;"
						                 @change="onEndYearChanged" :min="1931" :max="2071"/>
					</el-form-item>
				</el-col>
			</el-form-item>
			<el-form-item size="small" prop="courseNumber" label="Курс: ">
				<el-input-number placeholder="Pick a time" size="small"
				                 v-model="value.courseNumber"
				                 :min="1" :max="6"/>
			</el-form-item>
			<el-form-item size="small" prop="faculty" label="Факультет: ">
				<el-input size="small" v-model="value.faculty"/>
			</el-form-item>
			<el-form-item size="small" prop="branch" label="Галузь: ">
				<el-input size="small" v-model="value.branch"/>
			</el-form-item>
			<el-form-item size="small" prop="specialty" label="Спеціальність: ">
				<el-input size="small" v-model="value.specialty"/>
			</el-form-item>
			<el-form-item size="small" prop="educationLevel" label="ОКР: ">
				<el-input size="small" v-model="value.educationLevel"/>
			</el-form-item>
			<el-form-item size="small" prop="educationProgram" label="ОП: ">
				<el-input size="small" v-model="value.educationProgram"/>
			</el-form-item>
			<el-form-item size="small" prop="educationProgram" label="Назва: ">
				<el-input size="small" v-model="value.name"/>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" @click.native.stop.prevent="submitFinally">{{submitButtonText}}</el-button>
				<el-button @click.native.stop.prevent="cancel">Назад</el-button>
			</el-form-item>
		</el-form>
	</el-card>

</template>

<script lang="tsx">
	import Vue from "vue"
	import {Component, Prop, Watch} from "vue-property-decorator"
	import {Group, validateObjectRequired, validateString} from "../../entities/entities"

	@Component({
		name: "GroupsForm"
	})
	export default class GroupsForm extends Vue {

		private isLoading: boolean = false

		@Prop({required: true})
		private value: Group

		@Prop()
		private isUpdate: boolean

		@Prop()
		private submit: (group: Group) => void

		@Prop()
		private cancel: () => void

		@Prop()
		private submitButtonText: string

		@Prop()
		private isSubmitting: boolean

		@Watch("value", {deep: true})
		private onDataChanged(val: Group) {
			this.$emit("input", val)
		}

		private onBeginYearChanged(val: number) {
			this.value.endYear = val + 1
		}

		private onEndYearChanged(val: number) {
			this.value.beginYear = val - 1
		}

		private submitFinally() {
			const errors = this.validate()

			if (errors.length > 0) {
				this.$msgbox({
					message: <span style="white-space: pre-line;">
						{errors}
					</span>,
					type: "error",
					confirmButtonText: "OK",
					title: "Помилка"
				})

				return
			}

			this.submit(this.value)
		}

		private validate(): string {
			let errors = ""

			if (validateString(this.value.semesterType) !== "fine") {
				errors += "Ви не вибрали тип семестра\n"
			}

			if (validateString(this.value.faculty) !== "fine") {
				errors += "Ви не ввели факультет\n"
			}

			if (validateString(this.value.branch) !== "fine") {
				errors += "Ви не ввели галузь\n"
			}

			if (validateString(this.value.specialty) !== "fine") {
				errors += "Ви не ввели спеціальність\n"
			}

			if (validateString(this.value.educationLevel) !== "fine") {
				errors += "Ви не ввели ОКР\n"
			}

			if (validateString(this.value.educationProgram) !== "fine") {
				errors += "Ви не ввели ОП\n"
			}

			return errors
		}
	}
</script>

<style scoped lang="scss">
	.line {
		text-align: center;
	}
</style>