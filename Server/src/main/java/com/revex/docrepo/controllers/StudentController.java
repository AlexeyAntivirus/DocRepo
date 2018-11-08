package com.revex.docrepo.controllers;

import com.revex.docrepo.exchange.student.FindStudentViewsByFullNameAndGroupRequestPayload;
import com.revex.docrepo.exchange.student.FindStudentViewsByFullNameAndGroupResponsePayload;
import com.revex.docrepo.exchange.student.FindStudentViewsByParamRequestParameterRequestPayload;
import com.revex.docrepo.exchange.student.FindStudentViewsByParamRequestParameterResponsePayload;
import com.revex.docrepo.exchange.student.FindStudentsByFullNameAndGroupRequestPayload;
import com.revex.docrepo.exchange.student.FindStudentsByFullNameAndGroupResponsePayload;
import com.revex.docrepo.exchange.student.GetAllStudentsResponsePayload;
import com.revex.docrepo.exchange.student.InsertNewStudentRequestPayload;
import com.revex.docrepo.exchange.student.InsertNewStudentResponsePayload;
import com.revex.docrepo.exchange.student.UpdateStudentByIdRequestPayload;
import com.revex.docrepo.exchange.student.UpdateStudentByIdResponsePayload;
import com.revex.docrepo.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentController {
	private final StudentService service;

	@Autowired
	public StudentController(StudentService service) {
		this.service = service;
	}

	@ResponseBody
	@GetMapping("/all")
	public GetAllStudentsResponsePayload getAllStudents() {
		return this.service.getAllStudents();
	}

	@ResponseBody
	@PostMapping("/view/find")
	public FindStudentViewsByParamRequestParameterResponsePayload findStudentViewsByParameter(
			@RequestBody FindStudentViewsByParamRequestParameterRequestPayload payload) {
		return this.service.findStudentViewsByParameter(payload);
	}

	@ResponseBody
	@PutMapping("/insert")
	public InsertNewStudentResponsePayload insertNewStudent(@RequestBody InsertNewStudentRequestPayload payload) {
		return this.service.insertNewStudent(payload);
	}

	@ResponseBody
	@PostMapping("/view/find-by-fullname-and-group")
	public FindStudentViewsByFullNameAndGroupResponsePayload findStudentViewsByFullNameAndGroup(
			@RequestBody FindStudentViewsByFullNameAndGroupRequestPayload payload) {
		return this.service.findStudentViewsByFullNameAndGroup(payload);
	}

	@ResponseBody
	@PostMapping("/find-students-by-fullname-and-group")
	public FindStudentsByFullNameAndGroupResponsePayload findStudentsByFullNameAndGroup(
			@RequestBody FindStudentsByFullNameAndGroupRequestPayload payload) {
		return this.service.findStudentsByFullNameAndGroup(payload);
	}

	@ResponseBody
	@PostMapping("/update")
	public UpdateStudentByIdResponsePayload updateStudentById(@RequestBody UpdateStudentByIdRequestPayload payload) {
		return this.service.updateStudentById(payload);
	}
}
