package club.szuai.signin.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public class Location {
    private Integer id;

    private Integer campus;

    private String building;

    private BigDecimal lat;

    private BigDecimal lng;

    @JsonIgnore
    private String classroom;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCampus() {
        return campus;
    }

    public void setCampus(Integer campus) {
        this.campus = campus;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building == null ? null : building.trim();
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom == null ? null : classroom.trim();
    }
}