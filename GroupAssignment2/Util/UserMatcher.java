package com.example.GroupAssignment2.Util;

import java.util.HashMap;
import java.util.Map;

public class UserMatcher {
    private static Map<String, String> suggestions;
    static {
        suggestions = new HashMap<>();
        suggestions.put("Music", "Alice (5★ guitar)");
        suggestions.put("Programming", "Bob (4.8★ Java)");
        // can be adding more mapping
    }

    public static String suggestMatch(String skillType) {
        return suggestions.getOrDefault(skillType, "No match found");
    }
}