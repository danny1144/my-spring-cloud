package com.siemens.ldap.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author z00403vj
 *
 */
@Data
@Entity
@Table(name = "users")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class UsersBo implements Serializable {

//    @id
//    @generatedvalue(strategy = generationtype.sequence, generator = "users_seq")
//    @sequencegenerator(name = "users_seq", sequencename = "users$seq", allocationsize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "userlevelid")
    private Integer userlevelid = 1;

    @Column(name = "employeenum")
    private String employeenum;

    @Column(name = "passwd")
    private String passwd;

    @Column(name = "truename")
    private String truename;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "usersource")
    private String usersource;

    @Column(name = "reserve03")
    private String reserve03;

}