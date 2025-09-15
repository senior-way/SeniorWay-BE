package com.seniorway.seniorway.entity.touristSpotDetail;

import jakarta.persistence.*;
import com.seniorway.seniorway.entity.common.BaseTimeEntity;

@Entity
@Table(name = "wheelchair_access_info", uniqueConstraints = @UniqueConstraint(name = "uk_content", columnNames = "content_id"))
public class WheelchairAccessEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_id", nullable = false, length = 50)
    private String contentId;

    @Column(name = "parking", length = 100)
    private String parking;

    @Column(name = "route", length = 100)
    private String route;

    @Column(name = "exit_info", length = 100)
    private String exitInfo;

    @Column(name = "elevator", length = 100)
    private String elevator;

    @Column(name = "restroom", length = 100)
    private String restroom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getExitInfo() {
        return exitInfo;
    }

    public void setExitInfo(String exitInfo) {
        this.exitInfo = exitInfo;
    }

    public String getElevator() {
        return elevator;
    }

    public void setElevator(String elevator) {
        this.elevator = elevator;
    }

    public String getRestroom() {
        return restroom;
    }

    public void setRestroom(String restroom) {
        this.restroom = restroom;
    }
}
