package com.proaula.fitnesslife.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

@Document(collection = "functionalTrainings")
public class FunctionalTraining {

    @Id
    private String id;

    @Indexed(unique = true)
    private int idFunctionalTraining;

    private String nameTraining;
    private String instructor;
    private String description;
    private int maximumCapacity;
    private String duration;
    private String status;

    @CreatedDate
    private Date createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date datetime;

    private String room;

    private List<Long> userIds = new ArrayList<>();

    public FunctionalTraining() {
    }

    public FunctionalTraining(String id, int idFunctionalTraining, String nameTraining, String instructor,
            String description,
            int maximumCapacity, String duration, String status, Date createdAt, Date datetime, String room,
            List<Long> userIds) {
        this.id = id;
        this.idFunctionalTraining = idFunctionalTraining;
        this.nameTraining = nameTraining;
        this.instructor = instructor;
        this.description = description;
        this.maximumCapacity = maximumCapacity;
        this.duration = duration;
        this.status = "Active";
        this.createdAt = createdAt;
        this.datetime = new Date();
        this.room = room;
        this.userIds = userIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdFunctionalTraining() {
        return idFunctionalTraining;
    }

    public void setIdFunctionalTraining(int idFunctionalTraining) {
        this.idFunctionalTraining = idFunctionalTraining;
    }

    public String getNameTraining() {
        return nameTraining;
    }

    public void setNameTraining(String nameTraining) {
        this.nameTraining = nameTraining;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaximumCapacity() {
        return maximumCapacity;
    }

    public void setMaximumCapacity(int maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
