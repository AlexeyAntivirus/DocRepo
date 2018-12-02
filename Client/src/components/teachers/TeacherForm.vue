<template>
	<el-card shadow="never">
		<el-form>
			<el-form-item size="small" prop="fullName" label="ПІБ: ">
				<el-input size="small" v-model="value.fullName"/>
			</el-form-item>
			<el-form-item size="small" prop="position" label="Посада: ">
				<el-select size="small" v-model="value.position">
					<el-option :key="'асистент'" :label="'Асистент'" :value="'асистент'"/>
					<el-option :key="'старший викладач'" :label="'Старший викладач'" :value="'старший викладач'"/>
					<el-option :key="'доцент'" :label="'Доцент'" :value="'доцент'"/>
					<el-option :key="'професор'" :label="'Професор'" :value="'професор'"/>
					<el-option :key="'завідувач кафедри'" :label="'Завідувач кафедри'" :value="'завідувач кафедри'"/>
				</el-select>
			</el-form-item>
			<el-form-item size="small" prop="cathedra" label="Кафедра: ">
				<el-input size="small" v-model="value.cathedra"/>
			</el-form-item>
			<el-form-item size="small" prop="rank" label="Звання: ">
				<el-input size="small" v-model="value.rank"/>
			</el-form-item>
			<el-form-item size="small" prop="degree" label="Ступінь: ">
				<el-input size="small" v-model="value.degree"/>
			</el-form-item>
			<el-form-item size="small" prop="shortened" label="Діючий: ">
				<el-checkbox v-model="value.working"></el-checkbox>
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
	import {Teacher, validateObjectRequired, validateString} from "@/entities/entities"

	@Component({
		name: "TeacherForm"
	})
	export default class TeacherForm extends Vue {

		private isLoading: boolean = false

		@Prop()
		private isUpdate: boolean

		@Prop()
		private submit: (teacher: Teacher) => void

		@Prop()
		private cancel: () => void

		@Prop()
		private submitButtonText: string

		@Prop()
		private isSubmitting: boolean

		@Prop({required: true})
		private value: Teacher

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

			if (validateString(this.value.fullName) !== "fine") {
				errors += "Ви не ввели ПІБ викладача\n"
			}

			if (validateString(this.value.position) !== "fine") {
				errors += "Ви не ввели посаду\n"
			}

			if (validateString(this.value.cathedra) !== "fine") {
				errors += "Ви не ввели кафедру\n"
			}

			return errors
		}
	}
</script>

<style scoped>

</style>