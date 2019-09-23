package com.tz.ldap.controller;

import com.tz.ldap.service.LdapService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class LdapController {

    @Resource
    private LdapService ldapService;

    /**
     * 添加用户
     * @return
     * @throws Exception
     */
    @RequestMapping("addUser")
    public  Object addUser() throws Exception {
        ldapService.addUser();
        return "success";
    }

    /**
     * 添加用户组并添加用户至用户组
     * @return
     * @throws Exception
     */
    @RequestMapping("addUserGroup")
    public  Object addUserGroup() throws Exception {
        ldapService.addUserGroup();
        return "success";
    }
}
