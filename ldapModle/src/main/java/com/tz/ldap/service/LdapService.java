package com.tz.ldap.service;

import com.icitic.ldap.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.directory.BasicAttribute;
import javax.naming.ldap.LdapName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LdapService {

    @Autowired
    private  LdapTemplate ldapTemplate;



    /**
     * 测试连接
     * @throws Exception
     */
    public boolean connection()   {

        try {
            return null != ldapTemplate.lookup("");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 添加用户
     * @throws Exception
     */
    public void addUser() throws Exception {

        DirContextAdapter ctx;
        for (int i=20000; i<=25000; i++) {
            ctx = new DirContextAdapter();
            ctx.setDn(new LdapName("cn=testUser"+i));
            ctx.setAttribute(new BasicAttribute("sn", "test"));
            ctx.setAttribute(new BasicAttribute("mail","testUser"+i+"@test.net"));
            ctx.setAttribute(new BasicAttribute("telephoneNumber", "13123456789"));
            ctx.setAttribute(new BasicAttribute("userPassword", "testUser"+i));
            ctx.setAttribute(new BasicAttribute("uid", "testUser"+i));
            ctx.setAttribute(new BasicAttribute("givenName", "User "+i));
            ctx.setAttribute(new BasicAttribute("objectClass", "inetOrgPerson"));
            ldapTemplate.bind(ctx);
        }
    }

    /**
     * 添加用户组
     * @throws Exception
     */
    public void addUserGroup() throws Exception {

        DirContextAdapter ctx;
        ctx = new DirContextAdapter();
        ctx.setDn(new LdapName("cn=ddd"));
        ctx.setAttribute(new BasicAttribute("sn", "Doe"));
        ctx.setAttribute(new BasicAttribute("mail", "john@doe.net"));
        ctx.setAttribute(new BasicAttribute("telephoneNumber", "13123456789"));
        ctx.setAttribute(new BasicAttribute("userPassword", "password"));
        ctx.setAttribute(new BasicAttribute("uid", "ddddsdf"));
        ctx.setAttribute(new BasicAttribute("givenName", "John"));
        ctx.setAttribute(new BasicAttribute("objectClass", "inetOrgPerson"));
        ldapTemplate.bind(ctx);
        for (int i=1; i<=50; i++) {
            ctx = new DirContextAdapter();
            ctx.setDn(new LdapName("cn=testGroup"+i));
            BasicAttribute member = new BasicAttribute("member");
            int random = 1+(int)(50*Math.random());
            Set<Integer> set = new HashSet<>();
            while (set.size()<random) {
                set.add(1+(int)(200*Math.random()));
            }
            List<Integer> list = new ArrayList<>(set);
            for (int j=0; j<list.size(); j++) {
                member.add("cn=testUser"+list.get(j));
            }
            ctx.setAttribute(member);
            ctx.setAttribute(new BasicAttribute("objectClass", "groupOfNames"));
            ldapTemplate.bind(ctx);
        }
        ctx = new DirContextAdapter();
        ctx.setDn(new LdapName("cn=dfdfsdfsdfsdfsdfsdf"));
        BasicAttribute member = new BasicAttribute("member");
        int random = 1+(int)(50*Math.random());
        Set<Integer> set = new HashSet<>();
        while (set.size()<random) {
            set.add(1+(int)(5000*Math.random()));
        }
        List<Integer> list = new ArrayList<>(set);
        for (int j=0; j<list.size(); j++) {
            member.add("cn=testUser"+list.get(j));
        }
        ctx.setAttribute(member);
        ctx.setAttribute(new BasicAttribute("objectClass", "groupOfNames"));
        ldapTemplate.bind(ctx);
    }


    public boolean authentic(String userName, String password) {

        String filter = "(&(objectClass=inetOrgPerson)(uid="+userName+"))";
        //用于认证的LdapTemplate不能用SingleContext生成，否则会认证失败
        return ldapTemplate.authenticate("", filter, password );

    }



    /**
     * ldap測試
     * @throws Exception
     */
    public List testAll() {

        UserDAO userDao=new UserDAO();
        List allUsers = userDao.getAllUsers();
        return  allUsers;
    }
}
