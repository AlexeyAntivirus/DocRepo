package com.revex.docrepo.configs;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("file:user-login.properties")
public class UserLoginConfiguration {
	@Value("${docrepo.login.username}")
	private String username;

	@Value("${docrepo.login.password}")
	private String password;
}
