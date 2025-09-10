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
public class ArchivePhotos {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long archiveId;

    @Column
    private String photoUrl;

    @Column
    private Date picturedDate;

    @Column
    private Integer routeIn;

    @Column
    private String beforeInAfter; // 전/중/후
}
