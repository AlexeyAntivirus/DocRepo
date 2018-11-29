<template>
	<el-container style="height: 100%">
		<el-col ref="placeholder"
		        :xs="{offset: 0, span: 24}"
		        :sm="{offset: 0, span: 24}"
		        :md="{offset: 4, span: 16}"
		        :lg="{offset: 4, span: 16}"
		        :xl="{offset: 4, span: 16}"
		        style="overflow-y: auto;">
			<groups-form
					ref="groupsForm"
					v-model="value"
					:is-update="false"
					:cancel="cancel"
					:submit="insert"
					:is-submitting="isInserting"
					:submit-button-text="'Додати'"/>
		</el-col>
	</el-container>
</template>

<script lang="tsx">
	import Vue from "vue"
	import { Component } from "vue-property-decorator"
	import { Group } from "@/entities/entities"
	import GroupsForm from "./GroupsForm.vue"
	import { WindowSizeChangedEvent } from "@/plugins/window"

	@Component({
		name: "InsertGroupForm",
		components: {GroupsForm}
	})
	export default class GroupsTable extends Vue {
		private isInserting: boolean = false

		private value: Group = {
			id: 0,
			name: "",
			courseNumber: 1,
			faculty: "",
			specialty: "",
			branch: "",
			educationLevel: "",
			educationProgram: "",
			beginYear: 2018,
			endYear: 2019,
			semesterType: "Весняний",
			extramural: false,
			shortened: false
		}

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


		private insert(group: Group) {
			this.isInserting = true
			this.axios.put("/group/insert", group).then((resp) => {
				if (resp.data.successful) {
					this.$alert("Група успішно додана", "Успіх", {
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

		private cancel() {
			this.$router.push({name: "ShowGroup"})
		}
	}
</script>

<style scoped>

</style>