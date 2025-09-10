package com.gangchu.gangchutrip.auth.entity;

import jakarta.persistence.*;
import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String profile_image_url;

    @Column
    private Date created_at;

    @Column
    private Integer point;

}
