package com.gangchu.gangchutrip.mission.entity;

import com.gangchu.gangchutrip.global.entity.Travel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "missions")
@Getter
@Setter
@NoArgsConstructor
public class Mission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MissionType type;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MissionStatus status = MissionStatus.UNLOCKED;
    
    @Column(nullable = false)
    private Integer points = 0;
    
    @Column
    private String icon;
    
    @Column(name = "mission_order")
    private Integer order;
    
    @Column(length = 2000)
    private String requirements; // JSON 형태로 저장
    
    @Column(length = 2000)
    private String submittedData; // JSON 형태로 저장
    
    @Column
    private String location;
    
    @Column
    private String category;
    
    public enum MissionType {
        PHOTO_UPLOAD,
        TEMPERATURE,
        TEXT_INPUT,
        POSE_CHALLENGE,
        RANDOM_CHALLENGE,
        FILM_PHOTOS,
        ROULETTE
    }
    
    public enum MissionStatus {
        LOCKED,
        UNLOCKED,
        IN_PROGRESS,
        COMPLETED
    }
}

