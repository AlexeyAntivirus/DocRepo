import Vue from "vue"
import Router from "vue-router"
import QualificationWorkTable from "@/components/works/QualificationWorkTable.vue"
import InsertQualificationWork from "@/components/works/InsertQualificationWork.vue"
import StudentsTable from "@/components/student/StudentsTable.vue"
import InsertGroupForm from "@/components/group/InsertGroupForm.vue"
import GroupsTable from "@/components/group/GroupsTable.vue"
import InsertStudentForm from "@/components/student/InsertStudentForm.vue"

Vue.use(Router)

export default new Router({
	routes: [
		{
			name: "ShowQualificationWork",
			path: "/works",
			component: QualificationWorkTable,
		},
		{
			name: "InsertQualificationWork",
			path: "/works/insert",
			component: InsertQualificationWork
		},
		{
			name: "ShowStudent",
			path: "/students",
			component: StudentsTable
		},
		{
			name: "InsertStudentForm",
			path: "/students/insert",
			component: InsertStudentForm
		},
		{
			name: "ShowGroup",
			path: "/groups",
			component: GroupsTable
		},
		{
			name: "InsertGroupForm",
			path: "/groups/insert",
			component: InsertGroupForm
		}
	],
})
