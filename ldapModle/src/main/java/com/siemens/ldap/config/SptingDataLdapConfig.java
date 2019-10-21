package com.siemens.ldap.config;

import com.icitic.ldap.UserDAO;
import com.siemens.ldap.util.LdapUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Component;

/**
 * 数据源配置
 *@author z00403vj
 */
@Configuration
@EnableLdapRepositories
public class SptingDataLdapConfig {

	private LdapTemplate ldapTemplate;
	@Autowired
	private LDAPConf ldapConf;

	@Bean
	ContextSource contextSource() {
 
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setBase(ldapConf.getBase());
		ldapContextSource.setUrl(ldapConf.getUrl());
		//rootDn用户
		ldapContextSource.setUserDn(ldapConf.getUserDn());
		ldapContextSource.setPassword(ldapConf.getPassword());
		ldapContextSource.setReferral(ldapConf.getReferral());
		return ldapContextSource;
	}
 
	@Bean
	LdapTemplate ldapTemplate(ContextSource contextSource) {
		if (null == ldapTemplate) {
			ldapTemplate = new LdapTemplate(contextSource);
		}
		return ldapTemplate;
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