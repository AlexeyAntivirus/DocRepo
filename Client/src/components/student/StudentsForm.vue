<template>
	<el-card shadow="never">
		<el-form :model="value">
			<el-form-item size="small" label="Семестр: " prop="workType">
				<el-radio-group v-model="value.semesterType" @change="onSemesterTypeChanged">
					<el-radio label="Весняний">Весняний</el-radio>
					<el-radio label="Осінній">Осінній</el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item size="small" prop="extramural" label="Заочний: ">
				<el-checkbox v-model="value.extramural" @change="reset"></el-checkbox>
			</el-form-item>
			<el-form-item size="small" prop="shortened" label="Скорочений: ">
				<el-checkbox v-model="value.shortened" @change="reset"></el-checkbox>
			</el-form-item>
			<el-form-item size="small" label="Навчальний рік: ">
				<el-col :span="7">
					<el-form-item prop="beginYear">
						<el-input-number size="mini" v-model="value.beginYear" style="width: 100%;"
						                 @change="onBeginYearChanged" :min="1930" :max="2070"/>
					</el-form-item>
				</el-col>
				<el-col class="line" :span="2">-</el-col>
				<el-col :span="7">
					<el-form-item prop="endYear">
						<el-input-number size="mini" v-model="value.endYear" style="width: 100%;"
						                 @change="onEndYearChanged" :min="1931" :max="2071"/>
					</el-form-item>
				</el-col>
			</el-form-item>
			<el-form-item size="small" prop="courseNumber" label="Курс: ">
				<el-input-number placeholder="Pick a time" size="small"
				                 v-model="value.courseNumber"
				                 :min="1" :max="6"
				                 @change="onCourseNumberChanged"/>
			</el-form-item>
			<el-form-item size="small" prop="group" label="Група: ">
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
			<el-form-item size="small" prop="fullName" label="ПІБ: ">
				<el-input size="small" v-model="value.fullName"/>
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
	import {
		GroupView,
		validateObjectRequired,
		validateString
	} from "../../entities/entities"
	import {Student} from "../../entities/entities"

	@Component({
		name: "StudentsForm"
	})
	export default class StudentsForm extends Vue {

		private isLoading: boolean = false

		@Prop()
		private isUpdate: boolean

		@Prop()
		private submit: (student: Student) => void

		@Prop()
		private cancel: () => void

		@Prop()
		private submitButtonText: string

		@Prop()
		private isSubmitting: boolean

		@Prop({required: true})
		private value: Student

		private groupSuggestions: GroupView[] = []

		private mounted() {
			this.groupSuggestions = [this.value.group]
		}

		@Watch("value", {deep: true})
		private onDataChanged(val: Student) {
			this.groupSuggestions = [val.group]

			this.$emit("input", val)
		}

		private onSemesterTypeChanged(val: string) {
			this.reset()
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

			if (validateObjectRequired(this.value.group, {id: 0, groupName: ""}) !== "fine") {
				errors += "Ви не вибрали групу\n"
			}

			if (validateString(this.value.fullName) !== "fine") {
				errors += "Ви не ввели ПІБ студента\n"
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
