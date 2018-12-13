package com.revex.docrepo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Component
public class DocRepoAuthenticationProvider implements AuthenticationProvider {

	private final MessageDigest digest;
	private UserLoginConfiguration configuration;

	@Autowired
	public DocRepoAuthenticationProvider(UserLoginConfiguration configuration) throws NoSuchAlgorithmException {
		this.configuration = configuration;
		this.digest = MessageDigest.getInstance("SHA-512");

	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Object login = authentication.getPrincipal();
		String password = authentication.getCredentials().toString();
		byte[] configPassword = DatatypeConverter.parseBase64Binary(configuration.getPassword());

		boolean isAuth = Arrays.equals(digest.digest(password.getBytes()), configPassword)
				&& login.equals(configuration.getUsername());

		if (isAuth) {
			return new UsernamePasswordAuthenticationToken(
					authentication.getPrincipal(), authentication.getCredentials(), authentication.getAuthorities());
		} else {
			throw new AuthenticationException("Cannot authenticate") {};
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(
				UsernamePasswordAuthenticationToken.class);
	}

}
