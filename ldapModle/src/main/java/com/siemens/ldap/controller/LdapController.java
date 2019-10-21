package com.siemens.ldap.controller;

import com.siemens.ldap.dao.UserRepository;
import com.siemens.ldap.pojo.LdapUsers;
import com.siemens.ldap.pojo.UsersBo;
import com.siemens.ldap.service.LdapService;
import com.siemens.ldap.util.LdapUtils;
import com.siemens.ldap.util.ResponseMessage;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * ldap测试类
 * @author z00403vj
 */
@RestController
@EnableAsync
public class LdapController {

    @Resource
    private LdapService ldapService;

    @Resource
    private UserRepository userRepository;

    /**
     * 测试连接是否正常
     * @return
     * @throws Exception
     */
    @RequestMapping("connection")
    public  Object connection() throws Exception {
        boolean connection = ldapService.connection();
        return ResponseMessage.okWithoutMsg(connection);
    }

    /**
     * 測試工具认证用户
     * @return
     * @throws Exception
     */
    @RequestMapping("authentic")
    public  Object authentic(String cn,String password) {
        Boolean all = ldapService.login(cn,password);
        return ResponseMessage.okWithoutMsg(all);
    }

    /**
     * ldap同步用户和用户组到数据库
     * @return
     */
    @RequestMapping("sync")
    public  Object sync( ) {
        ldapService.syncZengcheng();
        return ResponseMessage.okWithoutRes("同步成功！");
    }
}
