package com.example.GroupAssignment2.Activity;

import java.io.Serializable;

public class SessionRequest implements Serializable {
    private String requesterName;
    private String requestedSkill;
    private long timestampCreated;

    // ✅ Required empty constructor for Firebase deserialization
    public SessionRequest() {
        // Firebase will automatically populate fields using setters
    }

    // ✅ Optional constructor for manual creation (used by app logic)
    public SessionRequest(String requesterName, String requestedSkill) {
        this.requesterName = requesterName;
        this.requestedSkill = requestedSkill;
        this.timestampCreated = System.currentTimeMillis(); // record current time
    }

    // ✅ Getter for requester name
    public String getRequesterName() {
        return requesterName;
    }

    // ✅ Setter for Firebase to assign requesterName
    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    // ✅ Getter for requested skill
    public String getRequestedSkill() {
        return requestedSkill;
    }

    // ✅ Setter for Firebase to assign requestedSkill
    public void setRequestedSkill(String requestedSkill) {
        this.requestedSkill = requestedSkill;
    }

    // ✅ Getter for timestamp
    public long getTimestampCreated() {
        return timestampCreated;
    }

    // ✅ Setter for Firebase to assign timestamp
    public void setTimestampCreated(long timestampCreated) {
        this.timestampCreated = timestampCreated;
    }
}