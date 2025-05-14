package com.example.GroupAssignment2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GroupAssignment2.Adapter.SkillAdapter;
import com.example.GroupAssignment2.Domain.SkillDomain;
import com.example.GroupAssignment2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SkillListActivity extends AppCompatActivity {

    private static final String TAG = "SkillListActivity";
    private RecyclerView recyclerSkills;
    private SkillAdapter adapter;
    private FloatingActionButton fabAddSkill;
    private ProgressBar progressBar;
    private TextView tvEmptyMessage;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<SkillDomain> skillsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_list);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        initViews();

        // Setup RecyclerView
        recyclerSkills.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SkillAdapter(skillsList,this,false);
        recyclerSkills.setAdapter(adapter);

        // Setup click listeners
        fabAddSkill.setOnClickListener(v -> {
            Intent intent = new Intent(SkillListActivity.this, CreateSkillActivity.class);
            startActivity(intent);
        });

        // Load skills from Firestore
        loadSkills();
    }

    private void initViews() {
        recyclerSkills = findViewById(R.id.recyclerSkills);
        fabAddSkill = findViewById(R.id.fabAddSkill);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadSkills();
    }

    private void loadSkills() {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Clear previous data
        skillsList.clear();

        // Query Firestore for all skills
        FirebaseFirestore.getInstance().collection("skills")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, "Skill found: " + document.getId());

                            // Convert Firestore document to SkillDomain object
                            String id = document.getId();
                            String title = document.getString("title");
                            String type = document.getString("type");
                            String description = document.getString("description");
                            String ownerId = document.getString("ownerId");

                            SkillDomain skill = new SkillDomain(id, title, type, description, ownerId);
                            skillsList.add(skill);
                        }

                        // Update UI with skills
                        if (skillsList.isEmpty()) {
                            // Show empty state
                            tvEmptyMessage.setVisibility(View.VISIBLE);
                            recyclerSkills.setVisibility(View.GONE);
                        } else {
                            // Show skills list
                            tvEmptyMessage.setVisibility(View.GONE);
                            recyclerSkills.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        // Show error message
                        Toast.makeText(SkillListActivity.this,
                                "Error loading skills: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}