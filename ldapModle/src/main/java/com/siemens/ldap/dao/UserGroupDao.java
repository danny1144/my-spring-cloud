package com.siemens.ldap.dao;

import com.siemens.ldap.pojo.UserGroup;
import com.siemens.ldap.pojo.UsersBo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author z00403vj
 *
 */
@Repository
public interface UserGroupDao extends JpaRepository<UserGroup,Long> {

    List<UserGroup> findAllBySource(String  source);
     @Query(nativeQuery = true,value = "select id from user_group where name =?1")
     Long  selectLdapGroupIdByName(String name);

 }
