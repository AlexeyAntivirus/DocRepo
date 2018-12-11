package com.revex.docrepo.controllers;

import com.revex.docrepo.exchange.stat.StatRequestPayload;
import com.revex.docrepo.exchange.stat.StatResponsePayload;
import com.revex.docrepo.services.StatService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stat")
public class StatController {
	private Logger logger = LogManager.getLogger(StatController.class);
	private StatService statService;

	@Autowired
	public StatController(StatService statService) {
		this.statService = statService;
	}

	@ResponseBody
	@PostMapping("/get")
	public StatResponsePayload getStat(Authentication authentication, @RequestBody StatRequestPayload payload) {
		logger.error(authentication.getAuthorities());
		logger.error(authentication.getPrincipal());
		logger.error(authentication.getCredentials());
		logger.error(authentication.getDetails());
		logger.error(authentication.isAuthenticated());

		return this.statService.getStat(payload);
	}
}
