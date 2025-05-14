package com.example.GroupAssignment2.Activity;


import java.util.ArrayList;
import java.util.List;

public class OfflineTagSuggestor {

    public static List<String> availableTags() {
        List<String> tags = new ArrayList<>();
        tags.add("Programming");
        tags.add("Music");
        tags.add("Cooking");
        tags.add("Photography");
        tags.add("Art");
        return tags;
    }

    public static List<SkillUser> suggestByTag(String tag, List<SkillUser> database) {
        List<SkillUser> result = new ArrayList<>();

        for (SkillUser user : database) {
            for (String skill : user.getOfferedSkills()) {
                if (skill.toLowerCase().contains(tag.toLowerCase())) {
                    result.add(user);
                    break;
                }
            }
        }
        return result;
    }
}