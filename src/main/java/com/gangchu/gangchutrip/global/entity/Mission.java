package com.gangchu.gangchutrip.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionType type;

    private String content;

    @Column(name = "is_sudden", nullable = false)
    private Boolean isSudden = false;

    public enum MissionType {
        PHOTO,
        VIDEO,
        VOICE,
        TEXT
    }
}

