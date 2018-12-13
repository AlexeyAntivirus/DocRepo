package com.revex.docrepo.configs;

import com.revex.docrepo.utils.DocRepoLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	private final DocRepoAuthenticationProvider provider;

	@Autowired
	public SecurityConfiguration(DocRepoAuthenticationProvider provider) {
		this.provider = provider;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/", "/login", "/logout").permitAll()
				.antMatchers(
						"/stat/**",
						"/discipline/**",
						"/group/**",
						"/works/**",
						"/student/**",
						"/teacher/**"
				).authenticated()
			.and().csrf().disable()
			.logout()
				.logoutUrl("/logout")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.logoutSuccessHandler(new DocRepoLogoutSuccessHandler());
//				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
	}


	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(provider);
	}

}