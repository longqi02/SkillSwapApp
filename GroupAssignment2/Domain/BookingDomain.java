package com.example.GroupAssignment2.Domain;

public class BookingDomain {
    private String skillId;
    private String userId;
    private long timestamp;

    public BookingDomain(String skillId, String userId, long timestamp) {
        this.skillId = skillId;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    // getters & setters omitted
    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}