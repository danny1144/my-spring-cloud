package com.siemens.ldap.controller;

import com.siemens.ldap.dao.UserRepository;
import com.siemens.ldap.pojo.LdapUsers;
import com.siemens.ldap.service.LdapService;
import com.siemens.ldap.util.LdapUtils;
import com.siemens.ldap.util.ResponseMessage;
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
     * 测试本地连接
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
        return ResponseMessage.okWithoutRes("添加用户组成功");
    }


    /**
     * 添加用户组并添加用户至用户组
     * @return
     * @throws Exception
     */
    @RequestMapping("findAll")
    public  Object findAll() throws Exception {
        Iterable<LdapUsers> all = userRepository.findAll();
        return ResponseMessage.okWithoutMsg(all);
    }


    /**
     * 測試工具获取所有用户
     * @return
     * @throws Exception
     */
    @RequestMapping("testAll")
    public  Object testAll() {
        List all = ldapService.testAll();
        return ResponseMessage.okWithoutMsg(all);
    }


    /**
     * 測試工具用户是否存在
     * @return
     * @throws Exception
     */
    @RequestMapping("testUserLogin")
    public  Object testUserLogin(String cn,String password) {
        Boolean all = ldapService.login(cn,password);
        return ResponseMessage.okWithoutMsg(all);
    }

    /**
     * ldap測試根据用户id获取用户
     * @return
     */
    @RequestMapping("queryUserByUserID")
    public  Object queryUserByUserID(String userId ) {
        Iterable<LdapUtils.UserInfo> all = ldapService.queryUserByUserID(userId);
        return ResponseMessage.okWithoutMsg(all);
    }
}
