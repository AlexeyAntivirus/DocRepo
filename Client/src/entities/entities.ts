import isEqual from "lodash.isequal"

export interface QualificationWorkFormData {
	id: number
	title: string
	beginYear: number
	endYear: number
	semesterNumber: number
	discipline: DisciplineView | null
	group: GroupView | null
	groupName: string
	student: StudentView | null
	workType: string
	studentFullName: string
	faculty: string
	specialty: string
	branch: string
	educationLevel: string
	educationProgram: string
	gradeECTS: string
	gradeNational: string
	grade: number
	courseNumber: number
	teachers: number[]
	teacherNames: string
	extramural: boolean
	shortened: boolean
}

export interface Student {
	id: number
	fullName: string
	semesterType: string
	group: GroupView
	beginYear: number
	endYear: number
	courseNumber: number
	extramural: boolean
	shortened: boolean
}

export interface Group {
    id: number
    name: string
    courseNumber: number
    faculty: string
    specialty: string
    branch: string
    educationLevel: string
    educationProgram: string
    beginYear: number
    endYear: number
    semesterType: string
    extramural: boolean
    shortened: boolean
}

export interface GroupView {
	id: number
	groupName: string
}

export interface StudentView {
	id: number
	fullName: string
}

export interface DisciplineView {
	id: number
	shortName: string
}

export interface TeacherView {
	id: number
	fullName: string
}

export interface AcademicYearView {
	beginYear: number
	endYear: number
}

export function validateRequired(val: any) {
	if (val === undefined) {
		return "undefined"
	} 

	if (val === null) {
		return "null"
	}
	
	return "fine"
}

export function validateArrayRequired(val: any[]) {
	const result = validateRequired(val)

	if (result !== "fine") {
		return result
	}

	if (val.length === 0) {
		return "empty"
	}

	return "fine"
}

export function validateObjectRequired(val: any, notPreferredValue: any) {
	const result = validateRequired(val)

	if (result !== "fine") {
		return result
	}

	console.log(isEqual(val, notPreferredValue))
	
	if (isEqual(val, notPreferredValue)) {
		return "not-preferred"
	}

	return "fine"
}

export function validateString(val: string) {
	const result = validateRequired(val)

	if (result !== "fine") {
		return result
	}

	if (val.length === 0) {
		return "empty"
	}

	return "fine"
}