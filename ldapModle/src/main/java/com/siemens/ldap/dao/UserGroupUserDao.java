package com.siemens.ldap.dao;

import com.siemens.ldap.pojo.UserGroup;
import com.siemens.ldap.pojo.UserGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author z00403vj
 *
 */
@Repository
public interface UserGroupUserDao extends JpaRepository<UserGroupUser,Long> {

  int deleteAllByUserId(Long uid);
 }
