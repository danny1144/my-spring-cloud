package com.siemens.ldap.dao;

import com.siemens.ldap.pojo.UsersBo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author z00403vj
 *
 */
@Repository
public interface UserDbDao extends JpaRepository<UsersBo,Long> {


    List<UsersBo> findAllByUsersource(String usersource);

    UsersBo findAllByUsername(String uName);
}
