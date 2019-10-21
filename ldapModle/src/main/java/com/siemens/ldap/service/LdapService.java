package com.siemens.ldap.service;

import com.icitic.ldap.UserDAO;
import com.siemens.ldap.config.LDAPConf;
import com.siemens.ldap.dao.UserDbDao;
import com.siemens.ldap.dao.UserGroupDao;
import com.siemens.ldap.dao.UserGroupUserDao;
import com.siemens.ldap.pojo.LdapUsers;
import com.siemens.ldap.pojo.UserGroup;
import com.siemens.ldap.pojo.UsersBo;
import com.siemens.ldap.util.LdapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.*;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.core.support.SingleContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;
import java.util.*;

/**
 * ldap service
 * @author z00403vj
 */
@Service
@Slf4j
public class LdapService {

    @Autowired
    private  LdapTemplate ldapTemplate;

    @Autowired
    private  UserDAO userDAO;

    @Autowired
    private LdapUtils ldapUtils;

    @Autowired
    private UserDbDao userDbDao;

    @Autowired
    private UserGroupDao userGroupDao;


    @Autowired
    private LDAPConf ldapConf;

    @Autowired
    private UserGroupUserDao userGroupUserDao;


    /**
     * 屬性長度限制
     */
    private static final int FIELD_LENGTH_LIMIT = 128;
    private static final String EXIST = "1";
    private static final String LDAP = "LDAP";
    private static final String RDN = "RDN";
    private static final String FDN = "FDN";

    /**
     * 以下为LDAP保留字
     */
    private static final String MEMBER_OF = "memberof";

    /**
     * 以下为map key
     */
    private static final String KEY_GROUPS = "groups";
    private static final String KEY_GROUP_NAME = "groupName";
    private static final String KEY_DN = "dn";
    private static final String KEY_TRUE_NAME = "trueName";
    private static final String OPTIONAL_NAME = "optionalName";
    private static final String GROUP_MEMBER_TYPE = "groupMemberType";
    private static final String LOGIN_NAME = "loginName";
    private static final String REQUIRED_NAME = "requiredName";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String EMPLOYEE_ID = "employeeId";

    /**
     * 测试连接
     * @throws Exception
     */
    public boolean connection()   {

        try {
            //lookup的dn为空则查询basedn
            return null != ldapTemplate.lookup("");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean authentic(String userName, String password) {
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectClass", "inetOrgPerson"));
        filter.and(new EqualsFilter("uid", userName));
       // String filter = "(&(objectClass=inetOrgPerson)(uid="+userName+"))";
        return ldapTemplate.authenticate("o=test", filter.encode(), password );

    }
    /**
     * ldap測試获取所有用户
     */
    public List testAll() {
        List allUsers = userDAO.getAllUsers(true);
        return  allUsers;
    }



    /**
     * ldap測試登陆用户是否存在
      */
    public Boolean login(String cn, String password) {

        log.info("loginName:{},password:{}",cn,password);
        boolean login = ldapUtils.login(cn, password);
        log.info("login success.");
        return  login;
    }


    /**
     * ldap測試根据用户id获取用户
     */
    public  List<LdapUtils.UserInfo> queryUserByUserID(String userId ) {
        List<LdapUtils.UserInfo> userInfos = ldapUtils.queryUserByUserID(userId);
        return  userInfos;
    }

    public  List getUserByUid(String uid){
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectClass", "inetOrgPerson"));
        filter.and(new EqualsFilter("uid", uid));

        //search是根据过滤条件进行查询，第一个参数是父节点的dn，可以为空，不为空时查询效率更高
        List users = ldapTemplate.search("o=test", filter.encode(), new AttributesMapper() {
            @Override
            public Object mapFromAttributes(Attributes attrs) throws NamingException {
                LdapUsers user=new LdapUsers();
                if(attrs.get("uid") != null){
                    user.setUid( attrs.get("uid").get().toString());
                }
                if(attrs.get("cn") != null){
                    user.setCommonName( attrs.get("cn").get().toString());
                }
                if(attrs.get("phone") != null){
                    user.setPhone( attrs.get("phone").get().toString());
                }
                if(attrs.get("mail") != null){
                    user.setEmail( attrs.get("mail").get().toString());
                }
                if(attrs.get("userPassword") != null){
                    user.setPasswd( attrs.get("userPassword").get().toString());
                }
                if(attrs.get("employeeNumber") != null){
                    user.setEmployeeNum( attrs.get("employeeNumber").get().toString());
                }
                return user;
            }
        });
    return users;
    }

    /**
     * 修改密码
     */

    public Boolean modifyPassword(String uid,String password,String newPassword){
        String s = userDAO.setUserPassword(uid, password, newPassword);
            if(StringUtils.equals(s,"0")){
                return true;
            }
        return false;
    }

    public UsersBo getUserById(Long id){
        UsersBo one = userDbDao.getOne(id);
        return one;
    }


    /**
     * 同步用户
     */
    @Async
    public Boolean sync() {
        // user 不支持memberof属性
        // userattr=uid,cn,sn,givenname,displayname,description,userpassword,order,ou,mou,isparticipant,mobile,mail,facsimiletelephonenumber,internationalisdnnumber,popnewwin,accountstatus,birthday,college,education,employeetype,gender,homepage,homephone,homepostaladdress,nation,polity,postalcode,professional,registeredaddress,roomnumber,telephonenumber,teletexterminalidentifier,title
        // orgattr=pou,ou,cn,ouid,order,description,uniquemember,manager,member,depttype,departmentnumber
        Set<String> attrList = new HashSet<>();
        attrList.add(ldapConf.getRequiredName());
        attrList.add(ldapConf.getEmail());
        attrList.add(ldapConf.getEmployeeId());
        attrList.add(ldapConf.getLoginName());
        attrList.add(ldapConf.getPhone());
        attrList.add(ldapConf.getOptionalName());
        if (ldapConf.getUserGroupSwitch()) {
            attrList.add(MEMBER_OF);
            if (!RDN.equals(ldapConf.getGroupMemberType())&&!FDN.equals(ldapConf.getGroupMemberType())) {
                attrList.add(ldapConf.getGroupMemberType() );
            }
        } else {
            log.warn("LDAP user group switch is off.");
        }
        Map<String, String> params = new HashMap<>(7);
        params.put(LOGIN_NAME, ldapConf.getLoginName());
        params.put(REQUIRED_NAME, ldapConf.getRequiredName());
        params.put(OPTIONAL_NAME, ldapConf.getOptionalName());
        params.put(EMAIL, ldapConf.getEmail());
        params.put(PHONE, ldapConf.getPhone());
        params.put(EMPLOYEE_ID, ldapConf.getEmployeeId());
        params.put(GROUP_MEMBER_TYPE, ldapConf.getGroupMemberType());
        log.info("Start fetching LDAP data.");
        List<Map<String, Object>> userData;
        List<String> groupList;
        //获取user信息列表
        try {
            long  time = System.currentTimeMillis();
            LdapTemplate    ldapTemplate =  this.init(true);
            userData = pagedSearch(ldapConf.getUserBaseDn(),attrList.toArray(new String[0]), ldapConf.getUserFilter(), new UserContextMapper(true, ldapConf.getUserGroupSwitch(), params), ldapTemplate);
        if (userData.size()>0) {
            Iterator<Map<String, Object>> userIterator = userData.iterator();
            while (userIterator.hasNext()) {
                Map<String, Object> map = userIterator.next();
                if (map.containsValue(null)) {
                    userIterator.remove();
                }
            }
        }
            //在获取用户组前去重，否则会引入多余用户组
            List<Map<String, Object>> tempUserData = new ArrayList<>();
            List<String> uniqueNameList = new ArrayList<>();
            for (int i=0; i<userData.size(); i++) {
                if (!uniqueNameList.contains(userData.get(i).get(LOGIN_NAME))) {
                    tempUserData.add(userData.get(i));
                    uniqueNameList.add(userData.get(i).get(LOGIN_NAME).toString());
                }
            }
            userData = tempUserData;
            log.info("user");
            if (ldapConf.getUserGroupSwitch()) {

                boolean supportsMemberOf =false;
                Set<String> groups = new HashSet<>();
                /*
                 * 判断是否支持memberof，如果支持则直接生成group列表
                 * 此方式最大支持1500用户组每用户，如果需要支持多于1500用户组每用户
                 * 需要用DefaultIncrementalAttributesMapper进行查询
                 * 或者通过user dn搜索user所属group
                 */
                for (int i=0; i<userData.size(); i++) {
                    if (((List)userData.get(i).get(KEY_GROUPS)).size()>0) {
                        if (!supportsMemberOf) {
                            log.info("Target LDAP server supports using \"member of\".");
                            supportsMemberOf = true;
                        }
                        List list = (List)userData.get(i).get(KEY_GROUPS);
                        for (int j=0; j<list.size(); j++) {
                            if (null!=list.get(j)) {
                                groups.add(list.get(j).toString());
                            }
                        }
                    }
                }

                //如果不支持则通过指定dn搜索user所属group，并填入user信息，然后生成group列表
                if(!supportsMemberOf) {
                    log.warn("Target LDAP server does not support using \"member of\".");
                    log.warn("Fetching group data explicitly, could be slow...");
                    log.warn("And nested groups might be ignored.");
                    List<Map<String, Object>> groupData;
                    for (int i=0; i<userData.size(); i++) {
                        String groupFilter = "(&("+ldapConf.getGroupFilter()+")("+ldapConf.getGroupMemberName()+"="+userData.get(i).get(KEY_DN)+"))";
                        groupData = pagedSearch(ldapConf.getUserGroupBaseDn() ,new String[0], groupFilter, new GroupContextMapper(), ldapTemplate);
                        if (groupData.size()>0) {
                            Iterator<Map<String, Object>> groupIterator = groupData.iterator();
                            while (groupIterator.hasNext()) {
                                Map<String, Object> map = groupIterator.next();
                                if (map.containsValue(null)) {
                                    groupIterator.remove();
                                }
                            }
                        }
                        List<String> memberOf = new ArrayList<>();
                        for (int j=0; j<groupData.size(); j++) {
                            if (null!=groupData.get(j).get(KEY_GROUP_NAME)) {
                                memberOf.add(groupData.get(j).get(KEY_GROUP_NAME).toString());
                            }
                        }
                        userData.get(i).put(KEY_GROUPS, memberOf);
                        groups.addAll(memberOf);
                    }
                }

                //移除中间数据
                for (int i=0; i<userData.size(); i++) {
                    userData.get(i).remove(KEY_DN);
                }

                //生成group信息
                groupList = Arrays.asList(groups.toArray(new String[0]));

            } else {
                groupList = new ArrayList<>();
            }
            log.info("LDAP data fetched successfully.");
            if(!CollectionUtils.isEmpty(userData)){
                updateDataBase(userData);
            }
            if (ldapConf.getUserGroupSwitch()) {
                if(!CollectionUtils.isEmpty(groupList)){
                    mapUser2Group(userData, groupList);
                }
            }
            log.info("LDAP synchronized successfully in "+(System.currentTimeMillis()-time)/60000+" min.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  false;
    }
    private LdapTemplate init(boolean useSingleContext) throws Exception{
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setBase(ldapConf.getBase());
        ldapContextSource.setUrl(ldapConf.getUrl());
        //rootDn用户
        ldapContextSource.setUserDn(ldapConf.getUserDn());
        ldapContextSource.setPassword(ldapConf.getPassword());
        ldapContextSource.setReferral(ldapConf.getReferral());
        ldapContextSource.afterPropertiesSet();
        SingleContextSource singleContextSource = new SingleContextSource(ldapContextSource.getReadOnlyContext());
        //可以选择用SingleContext生成LdapTemplate
        return useSingleContext?new LdapTemplate(singleContextSource):new LdapTemplate(ldapContextSource);
    }

    /**
     * 分页搜索。LDAP服务器可能会限制每次请求的最大返回数据量，需要多次查询
     * @param attributes
     * @param filter
     * @param mapper
     * @param <T>
     * @return
     */
    private <T>List<T> pagedSearch(String searchBase,String[] attributes, String filter, ContextMapper<T> mapper, LdapTemplate ldapTemplate) throws Exception{

        SearchControls searchControls = new SearchControls();
        //查询sub tree
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(attributes);

        //第一次查询不带cookie；设置的page size会被服务器端参数覆盖
        PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor(Integer.MAX_VALUE);

        //为确保分页请求成功，需要使用SingleContext生成LdapTemplate
        List<T> res = new ArrayList<>();

        PagedResultsCookie cookie;
        List<T> temp;
        while(true) {
            //执行查询
            temp = ldapTemplate.search(searchBase, filter, searchControls, mapper, processor);
            //获取cookie
            cookie = processor.getCookie();
            res.addAll(temp);
            //第一次查询cookie为空
            if(cookie == null){
                break;
            }
            //cookie为null说明已返回所有数据
            if (null==cookie.getCookie()) {
                break;
            } else {
                log.info("Paged search in process... current count is: "+res.size()+".");
            }
            //下一次请求时带上cookie
            processor = new PagedResultsDirContextProcessor(Integer.MAX_VALUE, cookie);
        }
        return res;
    }


    private class UserContextMapper extends AbstractContextMapper<Map<String, Object>> {

        /**
         * 是否显示optional name，一般为given name
         */
        private boolean useOptionalName;

        /**
         * 用户组开关
         */
        private boolean useGroup;

        /**
         * LDAP配置
         */
        private Map<String, String> params;

        public UserContextMapper(boolean useOptionalName, boolean useGroup, Map<String, String> params) {
            super();
            this.useOptionalName = useOptionalName;
            this.useGroup = useGroup;
            this.params = params;
        }

        @Override
        public Map<String, Object> doMapFromContext(DirContextOperations operations) {
            Map<String, Object> res = new HashMap<>(7);
            try {
                res.put(LOGIN_NAME, object2StringWithLengthCap(operations.getStringAttribute(params.get(LOGIN_NAME)), true));
                if (useOptionalName){
                    //用"姓, 名"的方式显示
                    res.put(KEY_TRUE_NAME, object2StringWithLengthCap(operations.getStringAttribute(params.get(REQUIRED_NAME))+", "+operations.getStringAttribute(params.get(OPTIONAL_NAME)), true));
                } else {
                    res.put(KEY_TRUE_NAME, object2StringWithLengthCap(operations.getStringAttribute(params.get(REQUIRED_NAME)), true));
                }
                res.put(EMPLOYEE_ID, object2StringWithLengthCap(operations.getStringAttribute(params.get(EMPLOYEE_ID)), false));
                res.put(EMAIL, object2StringWithLengthCap(operations.getStringAttribute(params.get(EMAIL)), false));
                res.put(PHONE, object2StringWithLengthCap(operations.getStringAttribute(params.get(PHONE)), false));
                if (useGroup) {
                    if (FDN.equals(params.get(GROUP_MEMBER_TYPE))) {
                        //获取full dn
                        res.put(KEY_DN, operations.getNameInNamespace());
                    } else if (RDN.equals(params.get(GROUP_MEMBER_TYPE))) {
                        res.put(KEY_DN, operations.getDn().toString());
                    } else {
                        res.put(KEY_DN, operations.getStringAttribute(params.get(GROUP_MEMBER_TYPE)));
                    }
                    //memberof非空；但是其中可能有空项，取决于dn是否合法
                    List<String> memberOf = Arrays.asList(null==operations.getStringAttributes(MEMBER_OF)?new String[0]:operations.getStringAttributes(MEMBER_OF));
                    for (int i=0; i<memberOf.size(); i++) {
                        memberOf.set(i, object2StringWithLengthCap(getCnFromDn(memberOf.get(i)), true));
                    }
                    res.put(KEY_GROUPS, memberOf);
                }
                if(res.containsValue(null)) {
                    log.warn("User \""+getCnFromDn(operations.getNameInNamespace())+"\" contains overlong field(s) therefore not synchronized.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return res;
        }

    }

    private class GroupContextMapper extends AbstractContextMapper<Map<String, Object>> {
        @Override
        public Map<String, Object> doMapFromContext(DirContextOperations operations) {
            Map<String, Object> res = new HashMap<>(1);
            try {
                //用dn第一项作为group name
                res.put(KEY_GROUP_NAME, object2StringWithLengthCap(getCnFromDn(operations.getNameInNamespace()), true));
                if(res.containsValue(null)) {
                    log.warn("Group \""+getCnFromDn(operations.getNameInNamespace())+"\" contains overlong field(s) therefore not synchronized.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return res;
        }
    }
    /**
     * 获取dn中的第一项，一般为cn
     * @param dn
     * @return
     */
    private String getCnFromDn(String dn) {
        try {
            LdapName ldapName = new LdapName(dn);
            String cn = ldapName.get(ldapName.size()-1);
            return cn.substring(cn.indexOf("=")+1).trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String object2StringWithLengthCap(Object obj, boolean flag) {
        if (null==obj) {
            return "";
        }
        String string = obj.toString();
        int stringLength = 0;
        String x3 = "[\u0391-\uFFE5]";
        for (int i=0; i<string.length(); i++) {
            String temp = string.substring(i, i + 1);
            if (temp.matches(x3)) {
                stringLength += 3;
            } else {
                stringLength += 1;
            }
            if (stringLength>FIELD_LENGTH_LIMIT) {
                return flag?null:"";
            }
        }
        return string;
    }
    private void updateDataBase(List<Map<String, Object>> userData) {
        log.info("Start updating database for users");
        List<UsersBo> oldUserList = userDbDao.findAllByUsersource("LDAP");
        Map<String, UsersBo> oldUserListWithKey = new HashMap<>(16);
        Map<String, UsersBo> newUserListWithKey = new HashMap<>(16);
        UsersBo newUser;
        for (int i = 0; i < userData.size(); i++) {
            newUser = new UsersBo();
            newUser.setUsername(userData.get(i).get(LOGIN_NAME).toString());
            newUser.setTruename(userData.get(i).get(KEY_TRUE_NAME).toString());
            newUser.setEmployeenum(userData.get(i).get(EMPLOYEE_ID).toString());
            newUser.setEmail(userData.get(i).get(EMAIL).toString());
            newUser.setPhone(userData.get(i).get(PHONE).toString());
            newUser.setUserlevelid(1);
            newUser.setReserve03(EXIST);
            newUser.setUsersource(LDAP);
            newUserListWithKey.put(newUser.getUsername(), newUser);
        }
        log.info("Total users count: " + newUserListWithKey.size() + ".");
        for (int i = 0; i < oldUserList.size(); i++) {
            oldUserListWithKey.put(oldUserList.get(i).getUsername(), oldUserList.get(i));
        }
        for (Map.Entry<String, UsersBo> entry : oldUserListWithKey.entrySet()) {
            if (null == newUserListWithKey.get(entry.getKey())) {
                try {
                    userDbDao.delete(entry.getValue().getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                newUserListWithKey.get(entry.getKey()).setId(entry.getValue().getId());
                newUserListWithKey.get(entry.getKey()).setUserlevelid(entry.getValue().getUserlevelid());
            }
        }
        for (Map.Entry<String, UsersBo> entry : newUserListWithKey.entrySet()) {
            if (null == entry.getValue().getId()) {
                UsersBo value = entry.getValue();
                userDbDao.save(value);

            }
        }
    }
        private void mapUser2Group(List<Map<String, Object>> userData, List<String> groupList) {


        log.info("Start updating database for user groups");
        List<UserGroup> oldUserGroupList = userGroupDao.findAllBySource("LDAP");
        Map<String, UserGroup> oldUserGroupListWithKey = new HashMap<>(16);
        Map<String, UserGroup> newUserGroupListWithKey = new HashMap<>(16);
        UserGroup newGroup;
        for (int i=0; i<groupList.size(); i++) {
            newGroup = new UserGroup();
            newGroup.setName(groupList.get(i));
            newGroup.setSource(LDAP);
            newUserGroupListWithKey.put(newGroup.getName(), newGroup);
        }
        log.info("Total user groups count: "+newUserGroupListWithKey.size()+".");
        for (int i=0; i<oldUserGroupList.size(); i++) {
            oldUserGroupListWithKey.put(oldUserGroupList.get(i).getName(), oldUserGroupList.get(i));
        }
        for (Map.Entry<String, UserGroup> entry : oldUserGroupListWithKey.entrySet()) {
            if (null==newUserGroupListWithKey.get(entry.getKey())) {
                userGroupDao.delete(entry.getValue().getId());
            } else {
                newUserGroupListWithKey.get(entry.getKey()).setId(entry.getValue().getId());
                newUserGroupListWithKey.get(entry.getKey()).setDescription(entry.getValue().getDescription());
            }
        }
        for (Map.Entry<String, UserGroup> entry : newUserGroupListWithKey.entrySet()) {
                userGroupDao.save(entry.getValue());
        }
        log.info("Start mapping user to user groups");
        Map<String, Long> groupMap = new HashMap<>(16);
        for (int i=0; i<groupList.size(); i++) {
            groupMap.put(groupList.get(i), userGroupDao.selectLdapGroupIdByName(groupList.get(i)));
        }
        for (int i=0; i<userData.size(); i++) {
            Long userId = userDbDao.findAllByUsername(userData.get(i).get(LOGIN_NAME).toString()).getId();
            userGroupUserDao.deleteAllByUserId(userId);
            List<Long> groupIdList = new ArrayList<>();
            List data = (List)userData.get(i).get(KEY_GROUPS);
            for (int j=0; j<data.size(); j++) {
                groupIdList.add(groupMap.get(data.get(j).toString()));
            }
            log.info("groupIdList:>>>{}",groupIdList.toString());
        }
    }

    /**
     * 同步用户,仅仅能同步最多1千条用户
     */
    @Async
    public Boolean syncZengcheng() {
        // user 不支持memberof属性
        // userattr=uid,cn,sn,givenname,displayname,description,userpassword,order,ou,mou,isparticipant,mobile,mail,facsimiletelephonenumber,internationalisdnnumber,popnewwin,accountstatus,birthday,college,education,employeetype,gender,homepage,homephone,homepostaladdress,nation,polity,postalcode,professional,registeredaddress,roomnumber,telephonenumber,teletexterminalidentifier,title
        // orgattr=pou,ou,cn,ouid,order,description,uniquemember,manager,member,depttype,departmentnumber
        log.info("Start fetching LDAP data.");
        List<LdapUtils.UserInfo> userData;
         //获取user信息列表
        try {
            long  time = System.currentTimeMillis();
            userData =  ldapUtils.queryAllUser();
            log.info("user");
            log.info("LDAP data fetched successfully.");
            if(!CollectionUtils.isEmpty(userData)){
                log.info("Start updating database for users");
                List<UsersBo> oldUserList = userDbDao.findAllByUsersource("LDAP");
                Map<String, UsersBo> oldUserListWithKey = new HashMap<>(16);
                Map<String, UsersBo> newUserListWithKey = new HashMap<>(16);
                UsersBo newUser;
                for (LdapUtils.UserInfo  userInfo:  userData ) {
                    newUser = new UsersBo();
                    newUser.setUsername(userInfo.getCn( ) );
                    newUser.setTruename(userInfo.getName() );
                    newUser.setEmployeenum(userInfo.getCardNumber());
                    newUser.setEmail(userInfo.getMail());
                    newUser.setPhone(userInfo.getMobile());
                    newUser.setUserlevelid(1);
                    newUser.setReserve03(EXIST);
                    newUser.setUsersource(LDAP);
                    newUserListWithKey.put(newUser.getUsername(), newUser);
                }
                log.info("Total users count: " + newUserListWithKey.size() + ".");
                for (int i = 0; i < oldUserList.size(); i++) {
                    oldUserListWithKey.put(oldUserList.get(i).getUsername(), oldUserList.get(i));
                }
                for (Map.Entry<String, UsersBo> entry : oldUserListWithKey.entrySet()) {
                    if (null == newUserListWithKey.get(entry.getKey())) {
                        try {
                            userDbDao.delete(entry.getValue().getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        newUserListWithKey.get(entry.getKey()).setId(entry.getValue().getId());
                        newUserListWithKey.get(entry.getKey()).setUserlevelid(entry.getValue().getUserlevelid());
                    }
                }
                for (Map.Entry<String, UsersBo> entry : newUserListWithKey.entrySet()) {
                    if (null == entry.getValue().getId()) {
                        UsersBo value = entry.getValue();
                        userDbDao.save(value);

                    }
                }
            }
            log.info("LDAP synchronized successfully in "+(System.currentTimeMillis()-time)/60000+" min.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  false;
    }

}
