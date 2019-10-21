package com.siemens.ldap.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

/**
 * @author fan.bian.ext@siemens.com
 */
@Data
@Entity
@Table(name = "user_group")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class UserGroup {

//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_group_seq")
//    @SequenceGenerator(name = "user_group_seq", sequenceName = "user_group$seq", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户组名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 用户组描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 用户组来源
     */
    @Column(name = "source")
    private String source;

}