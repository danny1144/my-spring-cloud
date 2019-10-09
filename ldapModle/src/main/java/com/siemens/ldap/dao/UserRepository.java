package com.siemens.ldap.dao;

import com.siemens.ldap.pojo.LdapUsers;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.naming.Name;
@Repository
public interface UserRepository extends CrudRepository<LdapUsers, Name> {
}
