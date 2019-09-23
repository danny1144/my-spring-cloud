package com.tz.ldap.controller;

import com.tz.ldap.service.LdapService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class LdapController {

    @Resource
    private LdapService ldapService;

    @RequestMapping("addUser")
    public  Object addUser() throws Exception {
        ldapService.addUser();
        return "success";
    }

    @RequestMapping("addUserGroup")
    public  Object addUserGroup() throws Exception {
        ldapService.addUserGroup();
        return "success";
    }
}
