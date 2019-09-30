package com.tz.ldap.dao;

import com.tz.ldap.pojo.LdapUsers;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.naming.Name;
@Repository
public interface UserRepository extends CrudRepository<LdapUsers, Name> {
}
