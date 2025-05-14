package com.example.GroupAssignment2.Activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SkillUser implements Serializable {
    private String userName;
    private List<String> offeredSkills;
    private List<String> wantedSkills;
    private String id;
    private double matchScore;

    private List<SessionRequest> incomingRequests = new ArrayList<>();

    // 🔥 Required for Firebase
    public SkillUser() {
        this.offeredSkills = new ArrayList<>();
        this.wantedSkills = new ArrayList<>();
        this.incomingRequests = new ArrayList<>();
    }

    // Optional constructor for manual creation
    public SkillUser(String userName, List<String> offeredSkills, List<String> wantedSkills) {
        this.userName = userName;
        this.offeredSkills = offeredSkills;
        this.wantedSkills = wantedSkills;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { // 🔥 Add setter for Firebase
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getOfferedSkills() {
        return offeredSkills;
    }

    public void setOfferedSkills(List<String> offeredSkills) {
        this.offeredSkills = offeredSkills;
    }

    public List<String> getWantedSkills() {
        return wantedSkills;
    }

    public void setWantedSkills(List<String> wantedSkills) {
        this.wantedSkills = wantedSkills;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public List<SessionRequest> getIncomingRequests() {
        return incomingRequests;
    }

    public void setIncomingRequests(List<SessionRequest> incomingRequests) { // 🔥 Add setter
        this.incomingRequests = incomingRequests;
    }

    public void addIncomingRequest(SessionRequest request) {
        incomingRequests.add(request);
    }

    @Override
    public String toString() {
        return "SkillUser{" +
                "userName='" + userName + '\'' +
                ", offeredSkills=" + offeredSkills +
                ", wantedSkills=" + wantedSkills +
                ", matchScore=" + matchScore +
                '}';
    }
}