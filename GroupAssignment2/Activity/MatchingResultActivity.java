package com.example.GroupAssignment2.Activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.GroupAssignment2.R;

import java.util.List;

public class MatchingResultActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MatchAdapter adapter;
    List<SkillUser> matchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_matching);

        recyclerView = findViewById(R.id.recyclerViewMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        matchList = (List<SkillUser>) getIntent().getSerializableExtra("matches");

        if (matchList == null || matchList.isEmpty()) {
            Toast.makeText(this, "No matching skills found!", Toast.LENGTH_SHORT).show();
            finish(); // Close the page if nothing to show
        } else {
            adapter = new MatchAdapter(matchList);
            recyclerView.setAdapter(adapter);
        }
    }
}