package com.gangchu.gangchutrip.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Mission {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private Enum<?> type; // 사진, 음성, 글

    @Column
    private String content;

    @Column
    private Boolean isSudden; // 즉흥 미션 여부
}
