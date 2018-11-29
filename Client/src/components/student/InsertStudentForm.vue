<template>
	<el-container style="height: 100%">
		<el-col ref="placeholder"
		        :xs="{offset: 0, span: 24}"
		        :sm="{offset: 0, span: 24}"
		        :md="{offset: 4, span: 16}"
		        :lg="{offset: 4, span: 16}"
		        :xl="{offset: 4, span: 16}">
			<students-form v-model="value"
			     :submit-button-text="'Додати'"
			     :cancel="cancel"
			     :submit="insert"
			     :is-update="false"
			     :is-submitting="isInserting"/>
		</el-col>
	</el-container>
</template>

<script lang="ts">
	import Vue from "vue"
	import {Component, Prop, Watch} from "vue-property-decorator"
	import {
		GroupView,
		validateObjectRequired,
		validateString
	} from "../../entities/entities"
	import {Student} from "../../entities/entities"
	import StudentsForm from "./StudentsForm.vue"
	import {WindowSizeChangedEvent} from "../../plugins/window"

	@Component({
		name: "InsertStudentForm",
		components: {StudentsForm}
	})
	export default class InsertStudentForm extends Vue {

		private value: Student = {
			id: 0,
			fullName: "",
			beginYear: 2018,
			endYear: 2019,
			semesterType: "Весняний",
			group: {
				id: 0,
				groupName: ""
			},
			courseNumber: 1,
			extramural: false,
			shortened: false
		}

		private isInserting: boolean = false

		private mounted() {
			this.$resizeWatcher.addWindowSizeChangedListener(this.onResize)
		}

		private beforeDestroy() {
			this.$resizeWatcher.removeWindowSizeChangedListener(this.onResize)
		}

		private onResize(size: WindowSizeChangedEvent) {
			const placeholder: Vue = this.$refs.placeholder as Vue
			placeholder.$el.style.height = `${size.windowSize.actualHeight - 96}px`
		}

		private cancel() {
			this.$router.push({name: "ShowStudent"})
		}

		private insert(student: Student) {
			this.isInserting = true
			this.axios.put("/student/insert", student).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Студент успішно додан", "Успіх", {
						type: "success",
						confirmButtonText: "OK"
					})
					this.isInserting = false
				} else {
					this.$alert("З'явилася якась помилка", "Помилка", {
						type: "error",
						confirmButtonText: "OK",
					})
					this.isInserting = false
				}
			})
		}

	}
</script>

<style scoped>

</style>