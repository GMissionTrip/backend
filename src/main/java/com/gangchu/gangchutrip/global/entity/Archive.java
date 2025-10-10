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
public class Archive {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Long memberId;

    @Column
    private Long travelId;

    @Column
    private Date createdDate;

    @Column
    private Date modifiedDate;
}
