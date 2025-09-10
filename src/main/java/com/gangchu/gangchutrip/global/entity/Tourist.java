package com.gangchu.gangchutrip.global.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Tourist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String addr1;

    @Column
    private String addr2;

    @Column
    private String sigungucode;

    @Column
    private String imageUrl;

    @Column
    private Enum<?> contentType;

    @Column
    private String mainTheme;

    @Column
    private String subTheme;

    @Column
    private String mapX; // 경도

    @Column
    private String mapY; // 위도

    @Column
    private String overview;
}
