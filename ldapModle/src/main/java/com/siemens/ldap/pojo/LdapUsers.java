package com.siemens.ldap.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.*;
import org.springframework.ldap.support.LdapNameBuilder;

import javax.naming.Name;

@Entry(base = "ou=users,dc=siemens,dc=com", objectClasses = "inetOrgPerson")
@JsonIgnoreProperties(value = {"dn"})
@Data
@NoArgsConstructor
public class LdapUsers {

    @Id
    private Name dn;

    @DnAttribute(value = "uid",index = 0)
    private String uid;

    @Attribute(name = "cn")
    private String commonName;

    @Attribute(name = "sn")
    private String suerName;

    @Attribute(name = "userPassword")
    private String passwd;

    @Attribute(name = "employeeNumber")
    private String employeeNum;

    @Attribute(name = "mobile")
    private String phone;

    @Attribute(name = "mail")
    private String email;

    public LdapUsers(String uid) {
        Name dn = LdapNameBuilder.newInstance()
                .add("ou", "users")
                .add("uid", uid)
                .build();
        this.dn = dn;
    }


    public void setUid(String uid) {
        this.uid = uid;
        if (this.dn == null) {
            Name dn = LdapNameBuilder.newInstance()
                    .add("ou", "users")
                    .add("uid", uid)
                    .build();
            this.dn = dn;
        }


    }
}