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
public class Travel {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long memberId;

    @Column
    private Integer accompanyNum;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @Column
    private String mainTheme;

    @Column
    private String subTheme;

    @Column
    private Date createdDate;

    @Column
    private Date modifiedDate;

    @Column
    private String title;

    @Column
    private Enum<?> travelWith; // 혼자, 친구, 가족, 연인, 기타
}
