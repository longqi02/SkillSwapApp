package com.example.GroupAssignment2.Domain;

public class UserDomain {
    private String id;
    private String name;
    private int age;
    private String phone;
    private String learnedSkills;

    // Empty constructor required for Firebase
    public UserDomain() {
    }

    public UserDomain(String id, String name, int age, String phone, String learnedSkills) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.learnedSkills = learnedSkills;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLearnedSkills() {
        return learnedSkills;
    }

    public void setLearnedSkills(String learnedSkills) {
        this.learnedSkills = learnedSkills;
    }
}