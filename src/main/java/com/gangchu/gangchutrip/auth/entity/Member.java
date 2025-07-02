package com.gangchu.gangchutrip.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;    //유저 닉네임

    private String email;       //유저 이메일

    @Column(length = 500)
    private String profile_image_url;
}
