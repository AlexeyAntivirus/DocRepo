package com.revex.docrepo.controllers;

import com.revex.docrepo.exchange.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginController {

	private AuthenticationManager manager;

	@Autowired
	public LoginController(AuthenticationManager manager) {
		this.manager = manager;
	}

	@PostMapping("/login")
	public boolean login(@RequestBody LoginForm loginForm, HttpServletRequest request) {
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				loginForm.getLogin(), loginForm.getPassword());
		Authentication newAuth = manager.authenticate(authentication);
		SecurityContext context = SecurityContextHolder.getContext();

		context.setAuthentication(newAuth);
		request.setAttribute("SPRING_SECURITY_CONTEXT", newAuth);
		return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
	}

	@PostMapping("/logout")
	public boolean logout() {
		return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
	}
}
