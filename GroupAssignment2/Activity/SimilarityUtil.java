package com.example.GroupAssignment2.Activity;

import java.util.List;

public class SimilarityUtil {
    public static float cosineSimilarity(List<Float> vec1, List<Float> vec2) {
        float dotProduct = 0.0f;
        float normVec1 = 0.0f;
        float normVec2 = 0.0f;

        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            normVec1 += vec1.get(i) * vec1.get(i);
            normVec2 += vec2.get(i) * vec2.get(i);
        }

        return (float) (dotProduct / (Math.sqrt(normVec1) * Math.sqrt(normVec2)));
    }
}