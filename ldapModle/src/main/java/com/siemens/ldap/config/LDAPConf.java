package com.siemens.ldap.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 西门子ldap配置
 * @author z00403vj
 */
	@Component
	@ConfigurationProperties(prefix = "ldap")
	@Getter
	@Setter
	@PropertySource("application.yml")
	public class LDAPConf {

		private String host;
		private String port;
		private String base;
		private String url;
		private String userDn;
		private String password;
		private String referral;
		/**
		 * 认证方式simple
		 */
		private String secAuth;
		/**
		 * 用户过滤器
		 */
		private String  userFilter;
		/**
		 * 用户组过滤器
		 */
		private String  groupFilter;
		/**
		 * 用户组同步开关(true打开，false关闭)
		 */
		private Boolean  userGroupSwitch;

		/**
		 * 查询用户根节点
		 */
		private String  userBaseDn;

		/**
		 * 查询用户组根节点
		 */
		private String  userGroupBaseDn;


		private String  	optionalName;
		private String  	employeeId;
		private String  	loginName;
		private String 		email;
		private String  	requiredName;
		private String 		phone;
		private String  	groupObjectClass;
		private String  	groupMemberName;
		private String  	groupMemberType;
		private String      ldapSwitch;




}