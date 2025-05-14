package com.example.GroupAssignment2.Activity;

public class SimpleSimilarity {

    public static boolean isSimilar(String skillA, String skillB) {
        if (skillA == null || skillB == null) return false;

        skillA = skillA.trim().toLowerCase();
        skillB = skillB.trim().toLowerCase();

        // Direct contains
        if (skillA.contains(skillB) || skillB.contains(skillA)) {
            return true;
        }

        // Jaccard similarity
        String[] wordsA = skillA.split("\\s+");
        String[] wordsB = skillB.split("\\s+");

        int common = 0;
        for (String word : wordsA) {
            for (String word2 : wordsB) {
                if (word.equals(word2)) {
                    common++;
                }
            }
        }
        int total = wordsA.length + wordsB.length - common;
        if (total == 0) return false;

        float jaccard = (float) common / total;
        return jaccard >= 0.5f; // Threshold: 50% common words
    }
}