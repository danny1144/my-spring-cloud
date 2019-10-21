package com.siemens.ldap.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author fan.bian.ext@siemens.com
 */
@Data
@Entity
@Table(name = "user_group_user")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupUser {

//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_group_user_seq")
//    @SequenceGenerator(name = "user_group_user_seq", sequenceName = "user_group_user$seq", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户组id
     */
    @Column(name = "usergroupid")
    private Long userGroupId;

    /**
     * 用户id
     */
    @Column(name = "userid")
    private Long userId;
}