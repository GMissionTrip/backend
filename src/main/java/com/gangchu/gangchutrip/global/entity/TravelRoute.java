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
public class TravelRoute {

    //id의 prefix가 travelId와 동일해야함
    @Id
    private String id;

    @Column
    private Integer sequence;

    @Column
    private Long touristId;
}
