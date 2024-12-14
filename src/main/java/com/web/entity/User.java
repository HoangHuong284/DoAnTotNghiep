package com.web.entity;

import com.nimbusds.openid.connect.sdk.claims.Gender;
import com.web.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String email;

    private String password;

    private String fullName;

    private String phone;

    private Boolean actived;

    private Date createdDate;

    private String avatar;

    private UserType userType;

    private String activation_key;

    private String rememberKey;

    @ManyToOne
    @JoinColumn(name = "authority_name")
    private Authority authorities;
}

