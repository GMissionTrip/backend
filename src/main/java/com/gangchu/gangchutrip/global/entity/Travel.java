package com.gangchu.gangchutrip.global.entity;


import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Enumerated(EnumType.STRING)
    private TravelWith travelWith; // 혼자, 친구, 가족, 연인, 기타
    
    @Column
    @Enumerated(EnumType.STRING)
    private TravelStatus status = TravelStatus.PLANNING; // 여행 상태
    
    @Column
    private String region; // 여행 지역
    
    @Column(length = 1000)
    private String coverImage; // 커버 이미지 URL

    public enum TravelWith {
        ALONE, FRIENDS, FAMILY, COUPLE, OTHER
    }
    
    public enum TravelStatus {
        PLANNING,    // 계획 중
        IN_PROGRESS, // 진행 중
        COMPLETED    // 완료
    }
}
