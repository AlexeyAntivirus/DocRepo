package com.revex.docrepo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	private final UserLoginConfiguration userLoginConfiguration;

	@Autowired
	public SecurityConfiguration(UserLoginConfiguration userLoginConfiguration) {
		this.userLoginConfiguration = userLoginConfiguration;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic()
				.and()
					.authorizeRequests()
						.antMatchers("/login").permitAll()
					.anyRequest().hasRole("ADMIN")
				.and().csrf().disable();
//					.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				.withUser(userLoginConfiguration.getUsername())
				.password(userLoginConfiguration.getPassword())
				.roles("ADMIN");
	}

}