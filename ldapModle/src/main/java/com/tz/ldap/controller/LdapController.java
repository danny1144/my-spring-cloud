package com.tz.ldap.controller;

import com.tz.ldap.dao.UserRepository;
import com.tz.ldap.pojo.LdapUsers;
import com.tz.ldap.service.LdapService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class LdapController {

    @Resource
    private LdapService ldapService;


    @Resource
    private UserRepository userRepository;



    /**
     * 添加用户
     * @return
     * @throws Exception
     */
    @RequestMapping("connection")
    public  Object connection(){

        return  ldapService.connection();
    }

    /**
     * 认证用户名和密码
     * @return
     * @throws Exception
     */
    @RequestMapping("authentic")
    public  Object authentic(String userName,String password){

        return  ldapService.authentic(userName,password);
    }
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


    /**
     * 添加用户组并添加用户至用户组
     * @return
     * @throws Exception
     */
    @RequestMapping("findAll")
    public  Object findAll() throws Exception {
        Iterable<LdapUsers> all = userRepository.findAll();
        return all;
    }


    /**
     * 測試工具
     * @return
     * @throws Exception
     */
    @RequestMapping("testAll")
    public  Object testAll() {
        List all = ldapService.testAll();
        return all;
    }
}
