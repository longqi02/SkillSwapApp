package com.example.GroupAssignment2.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GroupAssignment2.Adapter.SkillMatchAdapter;
import com.example.GroupAssignment2.R;
import com.example.GroupAssignment2.Domain.SkillDomain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkillMatchingActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth     auth;

    private LinearLayout   checkboxContainer;
    private RecyclerView   rvResults;
    private ProgressBar    progressBar;
    private Button         btnFindMatches;
    private SkillMatchAdapter adapter;

    // holds the skill‐types the user has checked
    private final Set<String> selectedSkills = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_matching);

        // 1) bind all views + Firebase
        firestore         = FirebaseFirestore.getInstance();
        auth              = FirebaseAuth.getInstance();
        checkboxContainer = findViewById(R.id.checkboxContainer);
        rvResults         = findViewById(R.id.rvMatchResults);
        progressBar       = findViewById(R.id.progressBarMatching);
        btnFindMatches    = findViewById(R.id.btnFindMatches);

        // 2) set up RecyclerView + adapter
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SkillMatchAdapter(this::saveToWishlist);
        rvResults.setAdapter(adapter);

        // 3) dynamically load all distinct skill-types into checkboxes
        setupSkillCheckboxes();

        // 4) wire the FIND MATCHES button
        btnFindMatches.setOnClickListener(v -> {
            if (selectedSkills.isEmpty()) {
                Toast.makeText(this,
                        "Select at least one skill type",
                        Toast.LENGTH_SHORT).show();
            } else {
                findMatches();   // <-- keep this!
            }
        });
    }

    /**
     *  Loads every document in "skills", reads its "type" field,
     *  and creates one CheckBox per type.  When checked/unchecked,
     *  we add/remove that type from selectedSkills.
     */
    private void setupSkillCheckboxes() {
        firestore.collection("skills")
                .get()
                .addOnSuccessListener(qsnap -> {
                    Set<String> seen = new HashSet<>();
                    for (DocumentSnapshot doc : qsnap) {
                        String type = doc.getString("type");
                        if (type == null || seen.contains(type)) continue;
                        seen.add(type);

                        CheckBox cb = new CheckBox(this);
                        cb.setText(type);
                        cb.setOnCheckedChangeListener((button, isChecked) -> {
                            if (isChecked) selectedSkills.add(type);
                            else           selectedSkills.remove(type);
                        });
                        checkboxContainer.addView(cb);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load skills", Toast.LENGTH_SHORT).show()
                );
    }

    /**
     *  1) Clears old results & shows a spinner
     *  2) Queries Firestore for all skills whose "type" is in selectedSkills
     *  3) Converts each doc → SkillDomain and pushes them into the adapter
     *  4) Toggles visibility and shows toasts on empty / error
     */
    private void findMatches() {
        progressBar.setVisibility(View.VISIBLE);
        rvResults.setVisibility(View.GONE);
        adapter.setItems(new ArrayList<>());  // clear old list

        firestore.collection("skills")
                .whereIn("type", new ArrayList<>(selectedSkills))
                .get()
                .addOnSuccessListener(qsnap -> {
                    List<SkillDomain> hits = new ArrayList<>();
                    for (DocumentSnapshot doc : qsnap) {
                        hits.add(doc.toObject(SkillDomain.class));
                    }

                    // hand off to the RecyclerView
                    adapter.setItems(hits);
                    rvResults.setVisibility(hits.isEmpty()
                            ? View.GONE
                            : View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    if (hits.isEmpty()) {
                        Toast.makeText(this,
                                "No matches found",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this,
                            "Error loading matches",
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     *  Saves one skill into /users/{uid}/wishlist
     *  and toasts success or failure.
     *  You need this to persist the user’s selections!
     */
    private void saveToWishlist(SkillDomain skill) {
        String uid = auth.getCurrentUser().getUid();
        firestore.collection("users")
                .document(uid)
                .collection("wishlist")
                .add(skill)  // <-- keep this!
                .addOnSuccessListener(r ->
                        Toast.makeText(this,
                                skill.getTitle() + " saved",
                                Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to save",
                                Toast.LENGTH_SHORT).show()
                );
    }
}
