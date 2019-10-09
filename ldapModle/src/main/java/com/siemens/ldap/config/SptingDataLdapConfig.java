package com.siemens.ldap.config;

import com.icitic.ldap.UserDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@EnableLdapRepositories
public class SptingDataLdapConfig {
 
	@Bean
	ContextSource contextSource() {
 
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setBase("ou=users,dc=siemens,dc=com");
		ldapContextSource.setUrl("ldap://localhost:389");
		//rootDn用户
		ldapContextSource.setUserDn("cn=Manager,dc=siemens,dc=com");
		ldapContextSource.setPassword("123456");
		ldapContextSource.setReferral("follow");
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
}