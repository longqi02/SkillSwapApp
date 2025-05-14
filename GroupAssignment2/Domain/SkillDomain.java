package com.example.GroupAssignment2.Domain;


public class SkillDomain {
    private String id;
    private String title;
    private String type;
    private String description;
    private String ownerId;

    /** 1) Required no-arg constructor for Firestore */
    public SkillDomain() { }


    public SkillDomain(String id, String title, String type, String description, String ownerId) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.description = description;
        this.ownerId = ownerId;
    }

    // getters & setters omitted for brevity
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

}
