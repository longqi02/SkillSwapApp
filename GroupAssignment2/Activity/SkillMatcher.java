package com.example.GroupAssignment2.Activity;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SkillMatcher {

    // Basic exact matching based on user-selected skill
    public static List<SkillUser> findMatchesBySelectedSkill(List<SkillUser> allUsers, String selectedSkill, String currentUserId) {
        List<SkillUser> matches = new ArrayList<>();

        for (SkillUser user : allUsers) {
            if (user.getId().equals(currentUserId)) continue; // Skip self
            if (user.getOfferedSkills().contains(selectedSkill)) {
                matches.add(user);
            }
        }
        return matches;
    }

    // Prioritized matching: More matching = higher rank
    public static List<SkillUser> prioritizeMatches(List<SkillUser> allUsers, List<String> selectedSkills, String currentUserId) {
        List<ScoredUser> scored = new ArrayList<>();

        for (SkillUser user : allUsers) {
            if (user.getId().equals(currentUserId)) continue;
            int matchCount = 0;
            for (String skill : selectedSkills) {
                if (user.getOfferedSkills().contains(skill)) {
                    matchCount++;
                }
            }
            if (matchCount > 0) {
                scored.add(new ScoredUser(user, matchCount));
            }
        }

        // Sort by match count descending
        Collections.sort(scored, (a, b) -> Integer.compare(b.score, a.score));

        // Extract SkillUser list
        List<SkillUser> result = new ArrayList<>();
        for (ScoredUser su : scored) {
            result.add(su.user);
        }
        return result;
    }

    // Helper class to keep score with user
    private static class ScoredUser {
        SkillUser user;
        int score;

        ScoredUser(SkillUser user, int score) {
            this.user = user;
            this.score = score;
        }
    }
}