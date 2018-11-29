package com.revex.docrepo.controllers;

import com.revex.docrepo.exchange.teacher.DeleteTeacherByIdResponsePayload;
import com.revex.docrepo.exchange.teacher.DeleteTeacherByIdRequestPayload;
import com.revex.docrepo.exchange.teacher.FindTeacherViewsByParamRequestPayload;
import com.revex.docrepo.exchange.teacher.FindTeacherViewsByParamResponsePayload;
import com.revex.docrepo.exchange.teacher.FindTeacherViewsByParamsRequestPayload;
import com.revex.docrepo.exchange.teacher.FindTeacherViewsByParamsResponsePayload;
import com.revex.docrepo.exchange.teacher.FindTeachersByParamRequestPayload;
import com.revex.docrepo.exchange.teacher.FindTeachersByParamResponsePayload;
import com.revex.docrepo.exchange.teacher.GetAllTeacherViewsResponsePayload;
import com.revex.docrepo.exchange.teacher.GetAllTeachersResponsePayload;
import com.revex.docrepo.exchange.teacher.InsertNewTeacherRequestPayload;
import com.revex.docrepo.exchange.teacher.InsertNewTeacherResponsePayload;
import com.revex.docrepo.exchange.teacher.UpdateTeacherByParamRequestPayload;
import com.revex.docrepo.exchange.teacher.UpdateTeacherByParamResponsePayload;
import com.revex.docrepo.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

	private final TeacherService service;

	@Autowired
	public TeacherController(TeacherService service) {
		this.service = service;
	}

	@GetMapping("/all")
	@ResponseBody
	public GetAllTeachersResponsePayload getAllTeachers() {
		return this.service.getAllTeachers();
	}

	@GetMapping("/view/all")
	@ResponseBody
	public GetAllTeacherViewsResponsePayload getAllTeacherViews() {
		return this.service.getAllTeacherViews();
	}

	@PostMapping("/find")
	@ResponseBody
	public FindTeachersByParamResponsePayload findTeachersByParam(
			@RequestBody FindTeachersByParamRequestPayload payload) {
		return this.service.findTeachersByParam(payload);
	}

	@PostMapping("/view/find")
	@ResponseBody
	public FindTeacherViewsByParamsResponsePayload findTeachersByParam(
			@RequestBody FindTeacherViewsByParamsRequestPayload payload) {
		return this.service.findTeacherViewsByParams(payload);
	}

	@PostMapping("/view/find-by-name")
	@ResponseBody
	public FindTeacherViewsByParamResponsePayload findTeachersByParam(
			@RequestBody FindTeacherViewsByParamRequestPayload payload) {
		return this.service.findTeacherViewsByParam(payload);
	}

	@PutMapping("/insert")
	@ResponseBody
	public InsertNewTeacherResponsePayload insertNewTeacher(
			@RequestBody InsertNewTeacherRequestPayload payload) {
		return this.service.insertNewTeacher(payload);
	}

	@DeleteMapping("/delete")
	@ResponseBody
	public DeleteTeacherByIdResponsePayload deleteTeacherById(
			DeleteTeacherByIdRequestPayload payload) {
		return this.service.deleteTeacherById(payload);
	}

	@PostMapping("/update")
	@ResponseBody
	public UpdateTeacherByParamResponsePayload updateTeacherByParam(
			@RequestBody UpdateTeacherByParamRequestPayload payload) {
		return this.service.updateTeacherByParam(payload);
	}
}
