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
public class PointHistory {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column
    private Date historyDate;

    @Column
    private Long memberId;

    @Column
    private Integer amount; // 포인트 증감 (예: +100, -50)

    @Column
    private String reason; // 포인트 증감 사유 (예: "미션 완료", "게시글 작성" 등)

}
