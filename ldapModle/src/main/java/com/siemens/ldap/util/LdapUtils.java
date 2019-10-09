package com.siemens.ldap.util;

import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;

public class LdapUtils {

        private   final String ENCRYPT;
        private   final String LDAPURL;
        private   final String LDAP_PORT;
        private   final String LDAP_PROVIDER;
        private   final String hostname;
        private   final String adminUid;
        private   final String adminPassword;
        private   final String secAuth;
        private static ResourceBundle rb = ResourceBundle.getBundle("com.icitic.ldap.sys.config");

        public LdapUtils() {

            LDAP_PORT = rb.getString("ldapport");
            LDAP_PROVIDER = rb.getString("ldapprovider");
            hostname = rb.getString("ldaphost");
            adminUid = rb.getString("ldapuser");
            adminPassword = rb.getString("ldapuserpass");
            secAuth = rb.getString("secauth");
            ENCRYPT = rb.getString("isEncrypted");
            LDAPURL = "ldap://" + hostname + ":" + LDAP_PORT;
        }

        private InitialLdapContext initLDAPContext() throws NamingException {
            Hashtable env = new Hashtable();
            env.put("java.naming.provider.url", this.LDAPURL);
            env.put("java.naming.security.authentication",secAuth);
            env.put("java.naming.security.principal", adminUid);
            env.put("java.naming.security.credentials", adminPassword);
            env.put("java.naming.factory.initial", LDAP_PROVIDER);
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

        public List<AppRole> queryAllRoles(String appId) {
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search("cn=roles,o=chd,c=cn", "(&(objectClass=chd-role)(appid=" + appId + "))", searchControls);

                ArrayList list;
                AppRole appRole;
                for(list = new ArrayList(); results.hasMore(); list.add(appRole)) {
                    appRole = new AppRole();
                    searchResult = (SearchResult)results.nextElement();
                    attribute = searchResult.getAttributes().get("roleid");
                    if (attribute != null) {
                        appRole.setRoleId((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("rolename");
                    if (attribute != null) {
                        appRole.setRoleName((String)attribute.get());
                    }
                }

                ArrayList var11 = list;
                return var11;
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
                attribute = null;
            }

            return null;
        }

        public List<Organization> queryDeptByDeptCode(String deptID) {
            DirContext ctx = null;
            SearchResult searchResult = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search("cn=orgs,o=chd,c=cn", "(&(objectClass=chd-organization)(o=" + deptID + "))", searchControls);
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

                for(NamingEnumeration results = ctx.search("cn=orgs,o=chd,c=cn", "(&(objectClass=chd-organization)(o=" + parentCode + "))", searchControls); results.hasMore(); organization = this.getOrganization(searchResult)) {
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
                NamingEnumeration results = ctx.search("cn=orgs,o=chd,c=cn", "(&(objectClass=chd-organization)(parentOrgId=" + orgCode + "))", searchControls);
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
                NamingEnumeration results = ctx.search("cn=orgs,o=chd,c=cn", "(&(objectClass=chd-organization)(parentOrgId=" + orgCode + "))", searchControls);
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

        public Map queryUserRole(String appId, String userName) {
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;
            HashMap map = new HashMap();

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search("cn=users,o=chd,c=cn", "(&(objectClass=chd-person)(cn=" + userName + "))", searchControls);

                while(results.hasMore()) {
                    searchResult = (SearchResult)results.nextElement();
                    attribute = searchResult.getAttributes().get("roleids");
                    String roleName;
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            if (i != attribute.size() - 1) {
                                if (attribute.get(i).toString().substring(0, 4).equals(appId)) {
                                    roleName = this.getAppRole(attribute.get(i).toString()).getRoleName();
                                    map.put(attribute.get(i).toString(), roleName);
                                }
                            } else if (attribute.get(i).toString().substring(0, 4).equals(appId)) {
                                roleName = this.getAppRole(attribute.get(i).toString()).getRoleName();
                                map.put(attribute.get(i).toString(), roleName);
                            }
                        }
                    }

                    attribute = searchResult.getAttributes().get("orgIds");
                    String orgids = null;
                    roleName = null;
                    Organization o = null;
                    if (attribute != null) {
                        for(int i = 0; i < attribute.size(); ++i) {
                            orgids = attribute.get(i).toString();
                            if (orgids.substring(0, 4).equals(appId)) {
                                roleName = this.getAppRole(orgids.substring(0, orgids.indexOf("-"))).getRoleName();
                                o = this.queryParentOrganiz(orgids.substring(orgids.indexOf("-") + 1));
                                map.put(orgids.substring(0, orgids.indexOf("-")), "(兼职--" + o.getOrgName() + ")" + roleName);
                            }
                        }
                    }
                }

                HashMap var15 = map;
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
                NamingEnumeration results = ctx.search("cn=users,o=chd,c=cn", "(&(objectClass=chd-person)(cn=" + userID + "))", searchControls);

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

        public List<Organization> queryUserDeptList(String userID) {
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search("cn=users,o=chd,c=cn", "(&(objectClass=chd-person)(cn=" + userID + "))", searchControls);

                do {
                    if (!results.hasMore()) {
                        return null;
                    }

                    searchResult = (SearchResult)results.nextElement();
                    new UserInfo();
                    attribute = searchResult.getAttributes().get("o");
                } while(attribute == null);

                List localList1 = this.queryOrganizeByID((String)attribute.get());
                List var11 = localList1;
                return var11;
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

        public List<Organization> queryPartTimeDeptList(String userID, String appID) {
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;
            boolean flag = false;
            String roleJson = "[";
            String partime = "";
            ArrayList orgList = new ArrayList();

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search("cn=users,o=chd,c=cn", "(&(objectClass=chd-person)(cn=" + userID + ")(orgIds=" + appID + "*))", searchControls);
                ArrayList list = new ArrayList();

                while(true) {
                    do {
                        if (!results.hasMore()) {
                            if (flag && list.size() > 0) {
                                new Organization();

                                for(int j = 0; j < list.size(); ++j) {
                                    partime = (String)list.get(j);
                                    Organization o = this.getOrganize(partime.substring(partime.indexOf("-") + 1));
                                    orgList.add(o);
                                }
                            }

                            return orgList;
                        }

                        flag = true;
                        searchResult = (SearchResult)results.nextElement();
                        attribute = searchResult.getAttributes().get("orgIds");
                    } while(attribute == null);

                    for(int i = 0; i < attribute.size(); ++i) {
                        if (i != attribute.size() - 1) {
                            if (attribute.get(i).toString().indexOf(appID) >= 0) {
                                list.add(attribute.get(i).toString());
                            }
                        } else if (attribute.get(i).toString().indexOf(appID) >= 0) {
                            list.add(attribute.get(i).toString());
                        }
                    }
                }
            } catch (Exception var24) {
                var24.printStackTrace();
            } finally {
                if (ctx != null) {
                    try {
                        ctx.close();
                    } catch (NamingException var23) {
                        ;
                    }
                }

                ctx = null;
                Hashtable env = null;
                searchResult = null;
                attribute = null;
            }

            return orgList;
        }

        public List<UserInfo> queryOrgUser(String orgCode) {
            DirContext ctx = null;
            SearchResult searchResult = null;
            String o = this.queryOs(orgCode);
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search("cn=users,o=chd,c=cn", "(&(objectClass=chd-person)" + o + ")", searchControls);

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

        public List<UserInfo> queryAppUser(String appId) {
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search("cn=users,o=chd,c=cn", "(&(objectClass=chd-person)(|(appIds=" + appId + ")(orgIds=" + appId + "*)))", searchControls);

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

        public List<Organization> queryAllDepartments() {
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search("cn=orgs,o=chd,c=cn", "objectClass=chd-organization", searchControls);
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
                NamingEnumeration results = ctx.search("cn=orgs,o=chd,c=cn", "(&(objectClass=chd-organization)(o=" + orgCode + "))", searchControls);
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
                NamingEnumeration results = ctx.search("cn=users,o=chd,c=cn", "(&(objectClass=chd-person)(cn=" + userId + "))", searchControls);
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

        private AppRole getAppRole(String roleId) {
            AppRole appRole = null;
            DirContext ctx = null;
            new Hashtable();
            SearchResult searchResult = null;
            Attribute attribute = null;

            try {
                ctx = this.initLDAPContext();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                NamingEnumeration results = ctx.search("cn=roles,o=chd,c=cn", "(&(objectClass=chd-role)(roleid=" + roleId + "))", searchControls);
                if (results.hasMoreElements()) {
                    searchResult = (SearchResult)results.nextElement();
                    appRole = new AppRole();
                    attribute = searchResult.getAttributes().get("roleid");
                    if (attribute != null) {
                        appRole.setRoleId((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("rolename");
                    if (attribute != null) {
                        appRole.setRoleName((String)attribute.get());
                    }

                    attribute = searchResult.getAttributes().get("appid");
                    if (attribute != null) {
                        appRole.setAppId((String)attribute.get());
                    }
                }

                AppRole var11 = appRole;
                return var11;
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
                attribute = null;
            }

            return null;
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
                NamingEnumeration results = ctx.search("cn=orgs,o=chd,c=cn", "(&(objectClass=chd-organization)(parentOrgId=" + orgCode + "))", searchControls);
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
                NamingEnumeration results = ctx.search("cn=orgs,o=chd,c=cn", "(&(objectClass=chd-organization)(parentOrgId=" + orgCode + "))", searchControls);
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

                for(NamingEnumeration results = ctx.search("cn=orgs,o=chd,c=cn", "(&(objectClass=chd-organization)(o=" + deptID + "))", searchControls); results.hasMore(); organization = this.getOrganization(searchResult)) {
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

        public class AppRole {
            private String roleId;
            private String roleName;
            private String appId;

            public AppRole() {
            }

            public String getRoleId() {
                return this.roleId;
            }

            public void setRoleId(String roleId) {
                this.roleId = roleId;
            }

            public String getRoleName() {
                return this.roleName;
            }

            public void setRoleName(String roleName) {
                this.roleName = roleName;
            }

            public String getAppId() {
                return this.appId;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }
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
