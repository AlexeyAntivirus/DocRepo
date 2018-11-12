<template>
	<el-card shadow="never">
		<el-form>
			<el-form-item size="small" prop="name" label="Назва: ">
				<el-input size="small" v-model="value.name"/>
			</el-form-item>

			<el-form-item size="small" prop="position" label="Скорочена назва: ">
				<el-input size="small" v-model="value.shortName"/>
			</el-form-item>
			<el-form-item size="small" prop="semesterNumber" label="Семестр: ">
				<el-input-number placeholder="Pick a time" size="small"
				                 v-model="value.semesterNumber"
				                 :min="1" :max="11"/>
			</el-form-item>
			<el-form-item size="small" label="Тип роботи: " prop="workType">
				<el-radio-group v-model="value.workType">
					<el-radio label="курсова робота"></el-radio>
					<el-radio label="курсовий проект"></el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" @click.native.stop.prevent="submitFinally">{{submitButtonText}}</el-button>
				<el-button @click.native.stop.prevent="cancel">Відмінити</el-button>
			</el-form-item>
		</el-form>
	</el-card>
</template>

<script lang="tsx">
	import Vue from "vue"
	import {Component, Prop, Watch} from "vue-property-decorator"
	import {Discipline, validateString} from "../../entities/entities"
	// import {Discipline, DisciplineModel, validateObjectRequired, validateString} from "../../entities/entities"

	@Component({
		name: "DisciplineForm"
	})
	export default class DisciplineForm extends Vue {

		private isLoading: boolean = false

		@Prop()
		private isUpdate: boolean

		@Prop()
		private submit: (teacher: Discipline) => void

		@Prop()
		private cancel: () => void

		@Prop()
		private submitButtonText: string

		@Prop()
		private isSubmitting: boolean

		@Prop({required: true})
		private value: Discipline

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

			if (validateString(this.value.name) !== "fine") {
				errors += "Ви не ввели назву предмета\n"
			}

			if (validateString(this.value.shortName) !== "fine") {
				errors += "Ви не ввели скорочену назву предмета\n"
			}

			return errors
		}
	}
</script>

<style scoped>

</style>