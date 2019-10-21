package com.siemens.ldap.dao;

import com.siemens.ldap.pojo.LdapUsers;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.naming.Name;

/**
 * @author z00403vj
 */
@Repository
public interface UserRepository extends CrudRepository<LdapUsers, Name> {
}
