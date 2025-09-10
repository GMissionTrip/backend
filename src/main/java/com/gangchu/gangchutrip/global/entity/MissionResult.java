package com.gangchu.gangchutrip.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class MissionResult {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long missionId;

    @Column
    private Long memberId;

    @Column
    private Long touristId;

    @Column
    private String travelId;

    @Column
    private String context; // 감상문 같은 것

    @Column
    private Boolean isCompleted; // 미션 완료 여부

    @Column
    private Date createdDate;

    @Column
    private Date modifiedDate;
}
