import Vue from "vue"
import Router from "vue-router"
import QualificationWorkTable from "@/components/works/QualificationWorkTable.vue"
import InsertQualificationWork from "@/components/works/InsertQualificationWork.vue"

import StudentsTable from "@/components/student/StudentsTable.vue"
import InsertStudentForm from "@/components/student/InsertStudentForm.vue"

import InsertGroupForm from "@/components/group/InsertGroupForm.vue"
import GroupsTable from "@/components/group/GroupsTable.vue"

import InsertTeacherForm from "@/components/teachers/InsertTeacherForm.vue"
import TeacherTable from "@/components/teachers/TeacherTable.vue"

import InsertDisciplineForm from "@/components/disciplines/InsertDisciplineForm.vue"
import DisciplineTable from "@/components/disciplines/DisciplineTable.vue"

import Stat from "@/components/stat/Stat.vue"

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
		},
		{
			name: "ShowTeacher",
			path: "/prep",
			component: TeacherTable
		},
		{
			name: "InsertTeacherForm",
			path: "/prep/insert",
			component: InsertTeacherForm
		},
		{
			name: "ShowDiscipline",
			path: "/disciplines",
			component: DisciplineTable
		},
		{
			name: "InsertDisciplineForm",
			path: "/disciplines/insert",
			component: InsertDisciplineForm
		},
		{
			name: "Stat",
			path: "/stat",
			component: Stat
		},
	],
})
