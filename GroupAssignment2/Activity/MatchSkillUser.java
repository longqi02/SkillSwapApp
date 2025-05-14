package com.example.GroupAssignment2.Activity;

// File: MatchedSkillUser.java
import java.io.Serializable;
import java.util.List;

public class MatchSkillUser implements Serializable {
    private String userName;
    private List<String> offeredSkills;
    private List<String> wantedSkills;
    private double matchScore;

    public MatchSkillUser(String userName, List<String> offeredSkills, List<String> wantedSkills, double matchScore) {
        this.userName = userName;
        this.offeredSkills = offeredSkills;
        this.wantedSkills = wantedSkills;
        this.matchScore = matchScore;
    }

    public String getUserName() { return userName; }
    public List<String> getOfferedSkills() { return offeredSkills; }
    public List<String> getWantedSkills() { return wantedSkills; }
    public double getMatchScore() { return matchScore; }
}
