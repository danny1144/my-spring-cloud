package com.siemens.ldap.config;

import com.icitic.ldap.UserDAO;
import com.siemens.ldap.util.LdapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@EnableLdapRepositories
public class SptingDataLdapConfig {

	@Value("${ldap.base}")
	private String base;
	@Value("${ldap.url}")
	private String url;
	@Value("${ldap.userDn}")
	private String userDn;
	@Value("${ldap.password}")
	private String password;
	@Value("${ldap.referral}")
	private String referral;

	@Bean
	ContextSource contextSource() {
 
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setBase(base);
		ldapContextSource.setUrl(url);
		//rootDn用户
		ldapContextSource.setUserDn(userDn);
		ldapContextSource.setPassword(password);
		ldapContextSource.setReferral(referral);
		return ldapContextSource;
	}
 
	@Bean
	LdapTemplate ldapTemplate(ContextSource contextSource) {
		return new LdapTemplate(contextSource);
	}


	@Bean
	UserDAO userDAO( ) {
		return new UserDAO( );
	}

	@Bean
	LdapUtils ldapUtils( ) {
		return new LdapUtils( );
	}

}