package com.revex.docrepo.controllers;

import com.revex.docrepo.exchange.stat.StatRequestPayload;
import com.revex.docrepo.exchange.stat.StatResponsePayload;
import com.revex.docrepo.services.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stat")
public class StatController {
	private StatService statService;

	@Autowired
	public StatController(StatService statService) {
		this.statService = statService;
	}

	@ResponseBody
	@PostMapping("/get")
	public StatResponsePayload getStat(@RequestBody StatRequestPayload payload) {
		return this.statService.getStat(payload);
	}
}
