<template>
	<el-card shadow="never">
		<el-form :model="value" ref="workForm" label-position="top">
			<el-form-item size="small" label="Тип роботи: " prop="workType">
				<el-radio-group v-model="value.workType">
					<el-radio label="Дипломна"></el-radio>
					<el-radio label="Курсова"></el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item size="small" prop="extramural" label="Заочний: ">
				<el-checkbox v-model="value.extramural" @change="reset"></el-checkbox>
			</el-form-item>
			<el-form-item size="small" prop="shortened" label="Скорочений: ">
				<el-checkbox v-model="value.shortened" @change="reset"></el-checkbox>
			</el-form-item>
			<el-form-item size="small" label="Навчальний рік: ">
				<el-col :span="11">
					<el-form-item prop="beginYear">
						<el-input-number size="mini" v-model="value.beginYear" style="width: 100%;"
						                 @change="onBeginYearChanged" :min="1930" :max="2070"/>
					</el-form-item>
				</el-col>
				<el-col class="line" :span="2">-</el-col>
				<el-col :span="11">
					<el-form-item prop="endYear">
						<el-input-number size="mini" v-model="value.endYear" style="width: 100%;"
						                 @change="onEndYearChanged" :min="1931" :max="2071"/>
					</el-form-item>
				</el-col>
			</el-form-item>
			<el-form-item size="small" prop="semesterNumber" label="Семестр: ">
				<el-input-number placeholder="Pick a time" size="small"
				                 v-model="value.semesterNumber"
				                 :min="1" :max="11"
				                 @change="onSemesterNumberChanged"/>
			</el-form-item>
			<el-form-item size="small" prop="courseNumber" label="Курс: ">
				<el-input-number placeholder="Pick a time" size="small"
				                 v-model="value.courseNumber"
				                 :min="1" :max="6"
				                 @change="onCourseNumberChanged"/>
			</el-form-item>
			<el-form-item size="small" prop="title" label="Тема: ">
				<el-input size="small" v-model="value.title"/>
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
			<el-form-item v-if="value.workType === 'Курсова'" size="small" prop="discipline" label="Дисципліна: ">
				<el-select size="small" v-model="value.discipline"
				           remote filterable
				           :loading="isLoading"
				           loading-text="Зачекайте доки їде запит."
				           :remote-method="findDisciplinesByParam"
				           @change="onDisciplineChange"
				           value-key="id" style="width: 100%">
					<el-option v-for="discipline in disciplineSuggestions" :key="discipline.id"
					           :value="discipline" :label="discipline.shortName"/>
				</el-select>
			</el-form-item>
			<el-form-item size="small" prop="student" label="Студент: ">
				<el-select size="small" v-model="value.student"
				           remote filterable
				           :loading="isLoading"
				           loading-text="Зачекайте доки їде запит."
				           :remote-method="findStudentsByParam"
				           @change="onStudentChange"
				           value-key="id" style="width: 100%">
					<el-option v-for="student in studentSuggestions"
					           :key="student.id"
					           :value="student" :label="student.fullName"/>
				</el-select>
			</el-form-item>
			<el-form-item size="mini" label="Викладачі: " prop="teachers">
				<el-transfer
						v-model="value.teachers"
						:data="teacherSuggestions"
						:props="{key: 'id', label: 'fullName'}"
						filterable
						@change="onTeachersChanged">
				</el-transfer>
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
			<el-form-item size="small" prop="grade" label="Оцінка: ">
				<el-input-number size="small" v-model="value.grade"
				                 @change="onGradeChanged" :min="0" :max="100"/>
			</el-form-item>
			<el-form-item size="small" prop="gradeECTS" label="Оцінка ECTS: ">
				<el-select size="small" v-model="value.gradeECTS"
				           @change="onGradeECTSChanged" placeholder="Select">
					<el-option value="A" label="A"/>
					<el-option value="B" label="B"/>
					<el-option value="C" label="C"/>
					<el-option value="D" label="D"/>
					<el-option value="E" label="E"/>
					<el-option value="FX" label="FX"/>
					<el-option value="F" label="F"/>
				</el-select>
			</el-form-item>
			<el-form-item size="small" prop="gradeECTS" label="Оцінка гос.: ">
				<el-select size="small" v-model="value.gradeNational"
				           @change="onGradeNationalChanged"
				           placeholder="Select">
					<el-option key="відмінно" value="відмінно" label="відмінно"/>
					<el-option key="добре" value="добре" label="добре"/>
					<el-option key="задовільно" value="задовільно" label="задовільно"/>
					<el-option key="не задовільно" value="не задовільно" label="не задовільно"/>
				</el-select>
			</el-form-item>
			<el-form-item prop="documentFile" label="Файл пояснювальної записки">
				<input ref="documentFileInput" type="file"
				       accept="application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/msword"
				       @change="addDocumentFile"/>
			</el-form-item>
			<el-form-item v-if="value.workType !== 'Курсова'"  prop="presentationFile" label="Файл презентації">
				<input ref="presentationFileInput" type="file" @change="addPresentationFile"
						accept="application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation"/>
			</el-form-item>
			<el-form-item prop="additionFiles" label="Додаткові файли">
				<input ref="additionFilesInput" type="file" multiple @change="addAdditionFiles"/>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" :loading="isSubmitting" @click.prevent.stop="submitFinally">{{ submitButtonName }}</el-button>
				<el-button @click.prevent.stop="cancel">Назад</el-button>
			</el-form-item> 
		</el-form>
	</el-card>
</template>

<script lang="tsx">
	import {Component, Prop, Watch} from "vue-property-decorator"
	import Vue from "vue"
	import {
		DisciplineView, GroupView, QualificationWorkFormData, StudentView, TeacherView,
		validateArrayRequired, validateObjectRequired, validateRequired, validateString
	} from "@/entities/entities"

	@Component({
		name: "QualificationWorkForm"
	})
	export default class QualificationWorkForm extends Vue {

		@Prop()
		private isSubmitting: boolean

		@Prop()
		private isUpdate: boolean

		@Prop()
		private submitButtonName: string

		@Prop()
		private submit: (work: QualificationWorkFormData, documentFile: File, presentationFile: File, files: File[]) => void

		@Prop()
		private cancel: () => void

		@Prop({required: true})
		private value: QualificationWorkFormData

		private additionFiles: File[] = []
		private presentationFile: File | null = null
		private documentFile: File | null = null
		private selectedTeachers: number[] = []

		private groupSuggestions: GroupView[] = []
		private disciplineSuggestions: DisciplineView[] = []
		private teacherSuggestions: TeacherView[] = []
		private studentSuggestions: StudentView[] = []

		private isLoading: boolean = false

		private mounted() {
			this.groupSuggestions = this.value.group === null ? [] : [this.value.group]
			this.disciplineSuggestions = this.value.discipline === null ? [] : [this.value.discipline]
			this.studentSuggestions = this.value.student === null ? [] : [this.value.student]
			this.loadTeachers()
		}

		@Watch("value", {deep: true})
		private onDataChanged(val: QualificationWorkFormData) {
			this.groupSuggestions = [val.group]
			this.disciplineSuggestions = val.discipline === null ? [] : [val.discipline]
			this.studentSuggestions = [val.student]

			this.$emit("input", val)
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
				semester: this.value.semesterNumber,
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

		private findDisciplinesByParam(disciplineNamePart: string) {
			if (disciplineNamePart === "") {
				return
			}
			this.isLoading = true
			this.axios.post("/discipline/find-by-semester-number-and-part", {
				semesterNumber: this.value.semesterNumber,
				part: disciplineNamePart
			}).then((value) => {
				this.isLoading = false
				this.disciplineSuggestions = value.data.disciplines
			}).catch((reason) => {
				console.log(reason)
			})
		}

		private findStudentsByParam(studentFullNamePart: string) {
			if (studentFullNamePart === "") {
				return
			}
			if (this.value.group === null) {
				return
			}
			this.isLoading = true
			this.axios.post("/student/view/find-by-fullname-and-group", {
				groupId: this.value.group.id,
				beginYear: this.value.beginYear,
				endYear: this.value.endYear,
				semesterNumber: this.value.semesterNumber,
				extramural: this.value.extramural,
				shortened: this.value.shortened,
				studentFullNamePart
			}).then((value) => {
				this.isLoading = false
				this.studentSuggestions = value.data.students
			}).catch((reason) => {
				console.log(reason)
			})
		}

		private onDisciplineChange(value: DisciplineView) {
			this.disciplineSuggestions = [value]
		}

		private onGroupChange(groupView: GroupView) {
			this.groupSuggestions = [groupView]
			this.axios.post("/group/get-by-id", {
				id: groupView.id
			}).then((value) => {
				this.value.faculty = value.data.group.faculty
				this.value.branch = value.data.group.branch
				this.value.educationLevel = value.data.group.educationLevel
				this.value.specialty = value.data.group.specialty
				this.value.educationProgram = value.data.group.educationProgram
			}).catch((reason) => {
				console.log(reason)
			})
			this.value.groupName = groupView.groupName
		}

		private onStudentChange(value: StudentView) {
			this.value.studentFullName = value.fullName
			this.studentSuggestions = [value]
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
			if (val > oldVal) {
				this.value.semesterNumber = val + oldVal
			} else {
				this.value.semesterNumber = (val - 1) + (oldVal - 1)
			}
			this.value.discipline = {
				id: 0,
				shortName: ""
			}

			this.reset()
		}

		private onSemesterNumberChanged(val: number) {
			this.value.courseNumber = Math.round(val / 2)
			this.value.discipline = {
				id: 0,
				shortName: ""
			}
			this.reset()
		}

		private onGradeChanged(val: number) {
			if (val >= 88) {
				this.value.gradeECTS = "A"
				this.value.gradeNational = "відмінно"
			} else if (val >= 81) {
				this.value.gradeECTS = "B"
				this.value.gradeNational = "добре"
			} else if (val >= 74) {
				this.value.gradeECTS = "C"
				this.value.gradeNational = "добре"
			} else if (val >= 68) {
				this.value.gradeECTS = "D"
				this.value.gradeNational = "задовільно"
			} else if (val >= 60) {
				this.value.gradeECTS = "E"
				this.value.gradeNational = "задовільно"
			} else if (val >= 40) {
				this.value.gradeECTS = "FX"
				this.value.gradeNational = "не задовільно"
			} else {
				this.value.gradeECTS = "F"
				this.value.gradeNational = "не задовільно"
			}
		}

		private onGradeECTSChanged(val: string) {
			if (val === "A") {
				this.value.grade = 88
				this.value.gradeNational = "відмінно"
			} else if (val === "B") {
				this.value.grade = 81
				this.value.gradeNational = "добре"
			} else if (val === "C") {
				this.value.grade = 74
				this.value.gradeNational = "добре"
			} else if (val === "D") {
				this.value.grade = 68
				this.value.gradeNational = "задовільно"
			} else if (val === "E") {
				this.value.grade = 60
				this.value.gradeNational = "задовільно"
			} else if (val === "FX") {
				this.value.grade = 40
				this.value.gradeNational = "не задовільно"
			} else {
				this.value.grade = 39
				this.value.gradeNational = "не задовільно"
			}
		}

		private onGradeNationalChanged(val: string) {
			if (val === "відмінно") {
				this.value.grade = 88
				this.value.gradeECTS = "A"
			} else if (val === "добре") {
				this.value.grade = 74
				this.value.gradeECTS = "C"
			} else if (val === "задовільно") {
				this.value.grade = 60
				this.value.gradeECTS = "E"
			} else {
				this.value.grade = 40
				this.value.gradeECTS = "FX"
			}
		}

		private reset() {
			this.value.group = {
				id: 0,
				groupName: ""
			}
			this.value.student = {
				id: 0,
				fullName: ""
			}

			this.value.faculty = ""
			this.value.branch = ""
			this.value.educationLevel = ""
			this.value.specialty = ""
			this.value.educationProgram = ""

			let input = this.$refs.documentFileInput as HTMLInputElement

			input.files = null
			input.value = ""

			input = this.$refs.presentationFileInput as HTMLInputElement

			input.files = null
			input.value = ""

			input = this.$refs.additionFilesInput as HTMLInputElement

			input.files = null
			input.value = ""
		}

		private addAdditionFiles(a: Event) {
			const target = a.target as HTMLInputElement

			this.additionFiles = Array.from(target.files!)
		}

		private addDocumentFile(a: Event) {
			const target = a.target as HTMLInputElement

			for (const file of target.files) {
				if (file.type !== "application/msword" &&
					file.type !== "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {

					this.$msgbox({
						message: <span style="white-space: pre-line;">
							{
								"Ви намагаєтесь завантажити файли з неприпустимим форматом.\n"
							}
							{
								"Доступні формати: '.doc', '.docx'.\n"
							}
						</span>,
						type: "error",
						confirmButtonText: "OK",
						title: "Помилка"
					})

					this.resetFileInput(this.$refs.documentFileInput as HTMLInputElement)
					return
				}
			}
			this.documentFile = Array.from(target.files!)[0]
		}

		private addPresentationFile(a: Event) {
			const target = a.target as HTMLInputElement

			for (const file of target.files) {
				console.log(file.type)
				if (file.type !== "application/vnd.ms-powerpoint" &&
					file.type !== "application/vnd.openxmlformats-officedocument.presentationml.presentation") {

					this.$msgbox({
						message: <span style="white-space: pre-line;">
							{
								"Ви намагаєтесь завантажити файли з неприпустимим форматом.\n"
							}
							{
								"Доступні формати: '.ppt', '.pptx'.\n"
							}
						</span>,
						type: "error",
						confirmButtonText: "OK",
						title: "Помилка"
					})

					this.resetFileInput(this.$refs.presentationFileInput as HTMLInputElement)
					return
				}
			}
			this.presentationFile = Array.from(target.files!)[0]
		}

		private onTeachersChanged(val: number[]) {
			let str: string = ""

			for (const teacher of this.teacherSuggestions) {
				if (val.includes(teacher.id)) {
					str += teacher.fullName + "\t\n"
				}
			}

			this.value.teacherNames = str
		}

		private loadTeachers() {
			this.isLoading = true
			this.axios.get("/teacher/view/all").then((value) => {
				this.isLoading = false
				this.teacherSuggestions = value.data.teacherViews
			}).catch((reason) => {
				console.log(reason)
			})
		}

		private resetFileInput(input: HTMLInputElement) {
			console.log(input)
			input.files = null
			input.value = ""
		}

		private submitFinally() {
			const form = this.$refs.workForm as any

			form.validate((value, object) => {
				let errors = ""

				if (validateRequired(this.presentationFile) !== "fine" && !this.isUpdate) {
					errors += "Ви не додали презентацію\n"
				}

				if (validateRequired(this.documentFile) !== "fine" && !this.isUpdate) {
					errors += "Ви не додали пояснювальну записку\n"
				}

				if (validateObjectRequired(this.value.group, {id: 0, groupName: ""}) !== "fine") {
					errors += "Ви не вибрали групу\n"
				}
				
				if (validateObjectRequired(this.value.student, {id: 0, fullName: ""}) !== "fine") {
					errors += "Ви не вибрали студента\n"
				}	

				if (validateObjectRequired(this.value.discipline, {id: 0, groupName: ""}) !== "fine" 
					&& this.value.workType === "Курсова") {
					errors += "Ви не вибрали дисципліну\n"
				}

				if (validateString(this.value.title) !== "fine") {
					errors += "Ви не ввели назву роботи\n"
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

				if (validateArrayRequired(this.value.teachers) !== "fine") {
					errors += "Ви не вибрали керівників роботи\n"
				}
				
				if (errors.length !== 0) {
					this.$msgbox({
						message: <span style="white-space: pre-line;">
							{ errors }
						</span>,
						type: "error",
						confirmButtonText: "OK",
						title: "Помилка"
					})
				} else {
					this.submit(this.value, this.documentFile, this.presentationFile, this.additionFiles)

					this.resetFileInput(this.$refs.additionFilesInput as HTMLInputElement)


					this.additionFiles = []
				}
			})
		}
	}
</script>

<style scoped lang="scss">
	.line {
		text-align: center;
	}
</style>