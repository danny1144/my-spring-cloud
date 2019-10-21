package com.siemens.ldap.util;

import com.siemens.ldap.config.LDAPConf;
import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.BASE64Encoder;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * ldap工具类
 * @author z00403vj
 */
public class LdapUtils {


        @Autowired
        private LDAPConf ldapConf;

        public LdapUtils() {
        }
        private InitialLdapContext initLDAPContext() throws NamingException {
            Hashtable env = new Hashtable();
            env.put("java.naming.provider.url",  ldapConf.getUrl());
            env.put("java.naming.security.authentication",ldapConf.getSecAuth());
            env.put("java.naming.security.principal", ldapConf.getUserDn());
            env.put("java.naming.security.credentials", ldapConf.getPassword());
            env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
            return new InitialLdapContext(env, (Control[])null);
        }
        public String validateUser(String userId, String password) {
            Object var3 = null;

            try {
                UserInfo userInfo = this.getUserInfo(userId);
                if (userInfo != null) {
                    String sha = "{SHA}" + (new BASE64Encoder()).encode(MessageDigest.getInstance("SHA1").digest(password.getBytes()));
                    System.out.println("sha" + sha);
                    System.out.println("pass" + userInfo.getPassword());
                    return !userInfo.getPassword().equals(password) && !userInfo.getPassword().equals(sha) ? "3" : "1";
                } else {
                    return "2";
                }
            } catch (Exception var6) {
                var6.printStackTrace();
                return "2";
            }
        }

        public boolean login(String cn, String password) {
            boolean flag = false;

            try {
                String shaPassword = "{SHA}" + (new BASE64Encoder()).encode(MessageDigest.getInstance("SHA1").digest(password.getBytes()));
                UserInfo userInfo = this.getUserInfo(cn);
                if (userInfo == null || !userInfo.getPassword().equals(password) && !userInfo.getPassword().equals(shaPassword)) {
                    flag = false;
                } else {
                    flag = true;
                }
            } catch (NoSuchAlgorithmException var6) {
                var6.printStackTrace();
            }

            return flag;
        }
        public List<Organization> queryDeptByDeptCode(String deptID) {
            DirContext ctx = null;
            SearchResult searchResult = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getGroupFilter()+")(o=" + deptID + "))", searchControls);
                ArrayList list = new ArrayList();

                while(results.hasMore()) {
                    searchResult = (SearchResult)results.nextElement();
                    list.add(this.getOrganization(searchResult));
                }

                ArrayList var9 = list;
                return var9;
            } catch (Exception var17) {
                var17.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var16) {
                        ;
                    }
                }

                ctx = null;
                searchResult = null;
            }

            return null;
        }

        public Organization queryParentOrganiz(String orgCode) {
            Organization organization = null;
            Organization organiza = this.getOrganize(orgCode);
            String parentCode = null;
            if (organiza != null) {
                parentCode = organiza.getParentOrgId();
            }

            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Object var8 = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);

                for(NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getGroupFilter()+")(o=" + parentCode + "))", searchControls); results.hasMore(); organization = this.getOrganization(searchResult)) {
                    searchResult = (SearchResult)results.nextElement();
                }
            } catch (Exception var19) {
                var19.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var18) {
                        ;
                    }
                }

                ctx = null;
                Hashtable env = null;
                searchResult = null;
                var8 = null;
            }

            return organization;
        }

        public List<Organization> queryChildOrganiz(String orgCode) {
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getGroupFilter()+")(parentOrgId=" + orgCode + "))", searchControls);
                String o = null;
                ArrayList list = new ArrayList();

                while(results.hasMore()) {
                    searchResult = (SearchResult)results.nextElement();
                    attribute = searchResult.getAttributes().get("o");
                    if (attribute != null) {
                        o = (String)attribute.get();
                    }

                    list.add(this.getOrganization(searchResult));
                    if (o != null) {
                        this.getChildOrganiz(o, list);
                    }
                }

                ArrayList var12 = list;
                return var12;
            } catch (Exception var20) {
                var20.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var19) {
                        ;
                    }
                }

                ctx = null;
                Hashtable env = null;
                searchResult = null;
                attribute = null;
            }

            return null;
        }

        private void getChildOrganiz(String orgCode, List<Organization> list) {
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&(" +ldapConf.getGroupFilter()+")(parentOrgId=" + orgCode + "))", searchControls);
                String o = null;

                while(results.hasMore()) {
                    searchResult = (SearchResult)results.nextElement();
                    attribute = searchResult.getAttributes().get("o");
                    if (attribute != null) {
                        o = (String)attribute.get();
                    }

                    list.add(this.getOrganization(searchResult));
                    if (o != null) {
                        this.getChildOrganiz(o, list);
                    }
                }
            } catch (Exception var18) {
                var18.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var17) {
                        ;
                    }
                }

                ctx = null;
                Hashtable env = null;
                searchResult = null;
                attribute = null;
            }

        }

    public List<UserInfo> queryAllUser() {
        DirContext ctx = null;
        new Hashtable();
        SearchResult searchResult = null;
        Attribute attribute = null;

        try {
            ctx = this.initLDAPContext();
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(2);
            NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getUserFilter()+"))", searchControls);

            ArrayList list;
            UserInfo userInfo;
            for(list = new ArrayList(); results.hasMore(); list.add(userInfo)) {
                userInfo = new UserInfo();
                searchResult = (SearchResult)results.nextElement();
                attribute = searchResult.getAttributes().get("cn");
                if (attribute != null) {
                    userInfo.setCn((String)attribute.get());
                }

                attribute = searchResult.getAttributes().get("displayName");
                if (attribute != null) {
                    userInfo.setName((String)attribute.get());
                }

                attribute = searchResult.getAttributes().get("idCardNumber");
                if (attribute != null) {
                    userInfo.setCardNumber((String)attribute.get());
                }

                attribute = searchResult.getAttributes().get("o");
                if (attribute != null) {
                    userInfo.setO((String)attribute.get());
                }

                attribute = searchResult.getAttributes().get("mail");
                if (attribute != null) {
                    userInfo.setMail((String)attribute.get());
                }

                attribute = searchResult.getAttributes().get("mobile");
                if (attribute != null) {
                    userInfo.setMobile((String)attribute.get());
                }

                attribute = searchResult.getAttributes().get("reserve1");
                if (attribute != null) {
                    userInfo.setType((String)attribute.get());
                }

                attribute = searchResult.getAttributes().get("appids");
                String appids = new String();
                if (attribute != null) {
                    for(int i = 0; i < attribute.size(); ++i) {
                        if (i != attribute.size() - 1) {
                            appids = appids + attribute.get(i).toString() + ",";
                        } else {
                            appids = appids + attribute.get(i).toString();
                        }
                    }

                    userInfo.setAppIds(appids);
                }

                attribute = searchResult.getAttributes().get("roleids");
                String roleids = new String();
                if (attribute != null) {
                    for(int i = 0; i < attribute.size(); ++i) {
                        if (i != attribute.size() - 1) {
                            roleids = roleids + attribute.get(i).toString() + ",";
                        } else {
                            roleids = roleids + attribute.get(i).toString();
                        }
                    }

                    userInfo.setRoleIds(roleids);
                }

                attribute = searchResult.getAttributes().get("orgIds");
                String orgids = new String();
                if (attribute != null) {
                    for(int i = 0; i < attribute.size(); ++i) {
                        if (i != attribute.size() - 1) {
                            orgids = orgids + attribute.get(i).toString() + ",";
                        } else {
                            orgids = orgids + attribute.get(i).toString();
                        }
                    }

                    userInfo.setOrgIds(orgids);
                }
            }

            ArrayList var15 = list;
            return var15;
        } catch (Exception var23) {
            var23.printStackTrace();
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException var22) {
                    ;
                }
            }

            ctx = null;
            Hashtable env = null;
            searchResult = null;
            attribute = null;
        }

        return null;
    }

        public List<UserInfo> queryUserByUserID(String userID) {
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getUserFilter()+")(cn=" + userID + "))", searchControls);

                ArrayList list;
                UserInfo userInfo;
                for(list = new ArrayList(); results.hasMore(); list.add(userInfo)) {
                    userInfo = new UserInfo();
                    searchResult = (SearchResult)results.nextElement();
                    attribute = searchResult.getAttributes().get("cn");
                    if (attribute != null) {
                        userInfo.setCn((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("displayName");
                    if (attribute != null) {
                        userInfo.setName((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("idCardNumber");
                    if (attribute != null) {
                        userInfo.setCardNumber((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("o");
                    if (attribute != null) {
                        userInfo.setO((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("mail");
                    if (attribute != null) {
                        userInfo.setMail((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("mobile");
                    if (attribute != null) {
                        userInfo.setMobile((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("reserve1");
                    if (attribute != null) {
                        userInfo.setType((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("appids");
                    String appids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                appids = appids + attribute.get(i).toString() + ",";
                            } else {
                                appids = appids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setAppIds(appids);
                    }

                    attribute = searchResult.getAttributes().get("roleids");
                    String roleids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                roleids = roleids + attribute.get(i).toString() + ",";
                            } else {
                                roleids = roleids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setRoleIds(roleids);
                    }

                    attribute = searchResult.getAttributes().get("orgIds");
                    String orgids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                orgids = orgids + attribute.get(i).toString() + ",";
                            } else {
                                orgids = orgids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setOrgIds(orgids);
                    }
                }

                ArrayList var15 = list;
                return var15;
            } catch (Exception var23) {
                var23.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var22) {
                        ;
                    }
                }

                ctx = null;
                Hashtable env = null;
                searchResult = null;
                attribute = null;
            }

            return null;
        }

    /**
     * 根据用户组编号获取用户集合
     * @param orgCode
     * @return
     */
        public List<UserInfo> queryOrgUser(String orgCode) {
            DirContext ctx = null;
            SearchResult searchResult = null;
            String o = this.queryOs(orgCode);
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getUserFilter()+")" + o + ")", searchControls);

                ArrayList list;
                UserInfo userInfo;
                for(list = new ArrayList(); results.hasMore(); list.add(userInfo)) {
                    userInfo = new UserInfo();
                    searchResult = (SearchResult)results.nextElement();
                    attribute = searchResult.getAttributes().get("cn");
                    if (attribute != null) {
                        userInfo.setCn((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("displayName");
                    if (attribute != null) {
                        userInfo.setName((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("idCardNumber");
                    if (attribute != null) {
                        userInfo.setCardNumber((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("o");
                    if (attribute != null) {
                        userInfo.setO((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("mail");
                    if (attribute != null) {
                        userInfo.setMail((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("mobile");
                    if (attribute != null) {
                        userInfo.setMobile((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("reserve1");
                    if (attribute != null) {
                        userInfo.setType((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("appids");
                    String appids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                appids = appids + attribute.get(i).toString() + ",";
                            } else {
                                appids = appids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setAppIds(appids);
                    }

                    attribute = searchResult.getAttributes().get("roleids");
                    String roleids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                roleids = roleids + attribute.get(i).toString() + ",";
                            } else {
                                roleids = roleids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setRoleIds(roleids);
                    }

                    attribute = searchResult.getAttributes().get("orgIds");
                    String orgids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                orgids = orgids + attribute.get(i).toString() + ",";
                            } else {
                                orgids = orgids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setOrgIds(orgids);
                    }
                }

                ArrayList var15 = list;
                return var15;
            } catch (Exception var23) {
                var23.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var22) {
                        ;
                    }
                }

                ctx = null;
                searchResult = null;
                attribute = null;
            }

            return null;
        }

    /**
     * 获取用户对应的用户组
     * @param userID
     * @return
     */
    public List<LdapUtils.Organization> queryUserDeptList(String userID) {
        List<Organization> result = null;
        boolean finished = false;
        DirContext ctx = null;
        new Hashtable();
        SearchResult searchResult = null;
        Attribute attribute = null;

        try {
            ctx = this.initLDAPContext();
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(2);
            NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getUserFilter()+")(cn=" + userID + "))", searchControls);

            do {
                if (!results.hasMore()) {
                    finished = true;
                    break;
                }

                searchResult = (SearchResult) results.nextElement();
                new UserInfo();
                attribute = searchResult.getAttributes().get("o");
            } while (attribute == null);
            if (!finished) {

                List localList1 = this.queryOrganizeByID((String) attribute.get());
                List var11 = localList1;
                result = var11;
            }
        } catch (Exception var20) {
            var20.printStackTrace();
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException var19) {
                    ;
                }
            }

            ctx = null;
            Hashtable env = null;
            searchResult = null;
            attribute = null;
        }

        return result;
    }

        public List<UserInfo> queryAppUser(String appId) {
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getUserFilter()+")(|(appIds=" + appId + ")(orgIds=" + appId + "*)))", searchControls);

                ArrayList list;
                UserInfo userInfo;
                for(list = new ArrayList(); results.hasMore(); list.add(userInfo)) {
                    userInfo = new UserInfo();
                    searchResult = (SearchResult)results.nextElement();
                    attribute = searchResult.getAttributes().get("cn");
                    if (attribute != null) {
                        userInfo.setCn((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("displayName");
                    if (attribute != null) {
                        userInfo.setName((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("idCardNumber");
                    if (attribute != null) {
                        userInfo.setCardNumber((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("o");
                    if (attribute != null) {
                        userInfo.setO((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("mail");
                    if (attribute != null) {
                        userInfo.setMail((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("mobile");
                    if (attribute != null) {
                        userInfo.setMobile((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("reserve1");
                    if (attribute != null) {
                        userInfo.setType((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("appids");
                    String appids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                appids = appids + attribute.get(i).toString() + ",";
                            } else {
                                appids = appids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setAppIds(appids);
                    }

                    attribute = searchResult.getAttributes().get("roleids");
                    String roleids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                roleids = roleids + attribute.get(i).toString() + ",";
                            } else {
                                roleids = roleids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setRoleIds(roleids);
                    }

                    attribute = searchResult.getAttributes().get("orgIds");
                    String orgids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                orgids = orgids + attribute.get(i).toString() + ",";
                            } else {
                                orgids = orgids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setOrgIds(orgids);
                    }
                }

                ArrayList var15 = list;
                return var15;
            } catch (Exception var23) {
                var23.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var22) {
                        ;
                    }
                }

                ctx = null;
                Hashtable env = null;
                searchResult = null;
                attribute = null;
            }

            return null;
        }

    /**
     * 获取所有用户组
     * @return
     */
        public List<Organization> queryAllDepartments() {
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search(ldapConf.getBase(), ldapConf.getGroupFilter(), searchControls);
                ArrayList list = new ArrayList();

                while(results.hasMore()) {
                    searchResult = (SearchResult)results.nextElement();
                    list.add(this.getOrganization(searchResult));
                }

                ArrayList var9 = list;
                return var9;
            } catch (Exception var17) {
                var17.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var16) {
                        ;
                    }
                }

                ctx = null;
                Hashtable env = null;
                searchResult = null;
            }

            return null;
        }

        public List<Organization> queryOrganizeByID(String orgCode) {
            DirContext ctx = null;
            SearchResult searchResult = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getGroupFilter()+")(o=" + orgCode + "))", searchControls);
                ArrayList list = new ArrayList();

                while(results.hasMore()) {
                    searchResult = (SearchResult)results.nextElement();
                    list.add(this.getOrganization(searchResult));
                }

                ArrayList var9 = list;
                return var9;
            } catch (Exception var17) {
                var17.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var16) {
                        ;
                    }
                }

                ctx = null;
                searchResult = null;
            }

            return null;
        }

        private UserInfo getUserInfo(String userId) {
            UserInfo userInfo = null;
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getUserFilter()+")(cn=" + userId + "))", searchControls);
                if (results.hasMoreElements()) {
                    searchResult = (SearchResult)results.nextElement();
                    userInfo = new UserInfo();
                    attribute = searchResult.getAttributes().get("cn");
                    if (attribute != null) {
                        userInfo.setCn((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("displayName");
                    if (attribute != null) {
                        userInfo.setName((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("idCardNumber");
                    if (attribute != null) {
                        userInfo.setCardNumber((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("o");
                    if (attribute != null) {
                        userInfo.setO((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("mail");
                    if (attribute != null) {
                        userInfo.setMail((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("mobile");
                    if (attribute != null) {
                        userInfo.setMobile((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("appids");
                    String appids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                appids = appids + attribute.get(i).toString() + ",";
                            } else {
                                appids = appids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setAppIds(appids);
                    }

                    attribute = searchResult.getAttributes().get("roleids");
                    String roleids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                roleids = roleids + attribute.get(i).toString() + ",";
                            } else {
                                roleids = roleids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setRoleIds(roleids);
                    }

                    attribute = searchResult.getAttributes().get("orgIds");
                    String orgids = new String();
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                orgids = orgids + attribute.get(i).toString() + ",";
                            } else {
                                orgids = orgids + attribute.get(i).toString();
                            }
                        }

                        userInfo.setOrgIds(orgids);
                    }

                    Attribute roleIdsAttribute = searchResult.getAttributes().get("userPassword");

                    for(int i = 0; i < roleIdsAttribute.size(); ++i) {
                        userInfo.setPassword(new String((byte[])roleIdsAttribute.get(i)));
                    }
                }
            } catch (Exception var22) {
                var22.printStackTrace();
                userInfo = null;
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var21) {
                        ;
                    }
                }

                ctx = null;
                Hashtable env = null;
                searchResult = null;
                attribute = null;
            }

            return userInfo;
        }

        private String queryOs(String orgCode) {
            StringBuffer orgBuffer = new StringBuffer();
            orgBuffer.append("(|(o=");
            orgBuffer.append(orgCode);
            orgBuffer.append(")");
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getGroupFilter()+")(parentOrgId=" + orgCode + "))", searchControls);
                String o = null;
                new ArrayList();

                while(results.hasMore()) {
                    Organization organization = new Organization();
                    searchResult = (SearchResult)results.nextElement();
                    attribute = searchResult.getAttributes().get("o");
                    if (attribute != null) {
                        organization.setOrgId((String)attribute.get());
                        o = (String)attribute.get();
                        orgBuffer.append("(o=");
                        orgBuffer.append(o);
                        orgBuffer.append(")");
                        this.queryO(o, orgBuffer);
                    }
                }

                orgBuffer.append(")");
                String str1 = orgBuffer.toString();
                String var13 = str1;
                return var13;
            } catch (Exception var21) {
                var21.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var20) {
                        ;
                    }
                }

                ctx = null;
                Hashtable env = null;
                searchResult = null;
                attribute = null;
            }

            return null;
        }

        private void queryO(String orgCode, StringBuffer orgs) {
            DirContext ctx = null;
            SearchResult searchResult = null;
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getGroupFilter()+")(parentOrgId=" + orgCode + "))", searchControls);
                String o = null;

                while(results.hasMore()) {
                    Organization organization = new Organization();
                    searchResult = (SearchResult)results.nextElement();
                    attribute = searchResult.getAttributes().get("o");
                    if (attribute != null) {
                        organization.setOrgId((String)attribute.get());
                        o = (String)attribute.get();
                        orgs.append("(o=");
                        orgs.append(o);
                        orgs.append(")");
                        this.queryO(o, orgs);
                    }
                }
            } catch (Exception var18) {
                var18.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var17) {
                        ;
                    }
                }

                ctx = null;
                searchResult = null;
                attribute = null;
            }

        }
        private Organization getOrganize(String deptID) {
            DirContext ctx = null;
            Organization organization = null;
            SearchResult searchResult = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);

                for(NamingEnumeration results = ctx.search(ldapConf.getBase(), "(&("+ldapConf.getGroupFilter()+")(o=" + deptID + "))", searchControls); results.hasMore(); organization = this.getOrganization(searchResult)) {
                    searchResult = (SearchResult)results.nextElement();
                }
            } catch (Exception var15) {
                var15.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var14) {
                        ;
                    }
                }

                ctx = null;
                searchResult = null;
            }

            return organization;
        }

        private Organization getOrganization(SearchResult searchResult) throws NamingException {
            Attribute attribute = null;
            Organization organization = new Organization();
            attribute = searchResult.getAttributes().get("o");
            if (attribute != null) {
                organization.setOrgId((String)attribute.get());
            }

            attribute = searchResult.getAttributes().get("displayName");
            if (attribute != null) {
                organization.setOrgName((String)attribute.get());
            }

            attribute = searchResult.getAttributes().get("parentOrgId");
            if (attribute != null) {
                organization.setParentOrgId((String)attribute.get());
            }

            attribute = searchResult.getAttributes().get("style");
            if (attribute != null) {
                organization.setOrgType((String)attribute.get());
            }

            attribute = searchResult.getAttributes().get("reserve1");
            if (attribute != null) {
                organization.setOrderSeq((String)attribute.get());
            }

            attribute = searchResult.getAttributes().get("reserve2");
            if (attribute != null) {
                organization.setTypeCode((String)attribute.get());
            }

            attribute = searchResult.getAttributes().get("reserve3");
            if (attribute != null) {
                organization.setTypeName((String)attribute.get());
            }

            attribute = searchResult.getAttributes().get("status");
            if (attribute != null) {
                organization.setStatus((String)attribute.get());
            }

            return organization;
        }
        public class Organization {
            private String orgId;
            private String orgName;
            private String parentOrgId;
            private String orgType;
            private String orderSeq;
            private String typeCode;
            private String typeName;
            private String status;

            public Organization() {
            }

            public String getOrgId() {
                return this.orgId;
            }

            public void setOrgId(String orgId) {
                this.orgId = orgId;
            }

            public String getOrgName() {
                return this.orgName;
            }

            public void setOrgName(String orgName) {
                this.orgName = orgName;
            }

            public String getParentOrgId() {
                return this.parentOrgId;
            }

            public void setParentOrgId(String parentOrgId) {
                this.parentOrgId = parentOrgId;
            }

            public String getOrgType() {
                return this.orgType;
            }

            public void setOrgType(String orgType) {
                this.orgType = orgType;
            }

            public String getOrderSeq() {
                return this.orderSeq;
            }

            public void setOrderSeq(String orderSeq) {
                this.orderSeq = orderSeq;
            }

            public String getTypeCode() {
                return this.typeCode;
            }

            public void setTypeCode(String typeCode) {
                this.typeCode = typeCode;
            }

            public String getTypeName() {
                return this.typeName;
            }

            public void setTypeName(String typeName) {
                this.typeName = typeName;
            }

            public String getStatus() {
                return this.status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }

        public class UserInfo {
            private String cn;
            private String name;
            private String cardNumber;
            private String password;
            private String mail;
            private String mobile;
            private String o;
            private String appIds;
            private String orgIds;
            private String roleIds;
            private String type;

            public UserInfo() {
            }

            public String getCn() {
                return this.cn;
            }

            public void setCn(String cn) {
                this.cn = cn;
            }

            public String getName() {
                return this.name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCardNumber() {
                return this.cardNumber;
            }

            public void setCardNumber(String cardNumber) {
                this.cardNumber = cardNumber;
            }

            public String getPassword() {
                return this.password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getMail() {
                return this.mail;
            }

            public void setMail(String mail) {
                this.mail = mail;
            }

            public String getMobile() {
                return this.mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getO() {
                return this.o;
            }

            public void setO(String o) {
                this.o = o;
            }

            public String getAppIds() {
                return this.appIds;
            }

            public void setAppIds(String appIds) {
                this.appIds = appIds;
            }

            public String getOrgIds() {
                return this.orgIds;
            }

            public void setOrgIds(String orgIds) {
                this.orgIds = orgIds;
            }

            public String getRoleIds() {
                return this.roleIds;
            }

            public void setRoleIds(String roleIds) {
                this.roleIds = roleIds;
            }

            public String getType() {
                return this.type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }

}
