package com.example.GroupAssignment2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import java.util.HashMap;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.GroupAssignment2.R;
import com.example.GroupAssignment2.Domain.SkillDomain;
import com.example.GroupAssignment2.Util.UserMatcher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SkillProfileActivity extends AppCompatActivity {
    private static final String TAG = "SkillProfileActivity";

    private TextView tvTitle, tvType, tvDesc, tvMatch, tvOwnerInfo;
    private Button btnBook;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Skill data
    private String skillId;
    private String skillTitle;
    private String skillType;
    private String skillDescription;
    private String skillOwnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_profile);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Bind UI
        tvTitle = findViewById(R.id.tvTitle);
        tvType = findViewById(R.id.tvType);
        tvDesc = findViewById(R.id.tvDescription);
        tvMatch = findViewById(R.id.tvMatchInfo);
        tvOwnerInfo = findViewById(R.id.tvOwnerInfo);
        btnBook = findViewById(R.id.btnBook);

        // Get skill data from intent
        getSkillDataFromIntent();

        // Display skill data
        displaySkillData();

        // Get owner information
        getOwnerInfo();

        // Show skill match recommendations
        showMatchRecommendation();

        // Setup booking button
        setupBookingButton();
    }

    private void getSkillDataFromIntent() {
        // Get skill ID from intent
        Intent intent = getIntent();
        skillId = intent.getStringExtra("skillId");

        // Check if we received the skill details directly
        if (intent.hasExtra("title") && intent.hasExtra("type") && intent.hasExtra("description")) {
            // Get the skill details from intent extras
            skillTitle = intent.getStringExtra("title");
            skillType = intent.getStringExtra("type");
            skillDescription = intent.getStringExtra("description");
            skillOwnerId = intent.getStringExtra("ownerId");
        } else {
            // Fetch from Firestore if not provided
            fetchSkillFromFirestore();
        }
    }

    private void fetchSkillFromFirestore() {
        if (skillId != null && !skillId.isEmpty()) {
            Log.d(TAG, "Fetching skill with ID: " + skillId);

            FirebaseFirestore.getInstance().collection("skills").document(skillId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "Skill document exists: " + documentSnapshot.getData());

                            // Get skill data
                            skillTitle = documentSnapshot.getString("title");
                            skillType = documentSnapshot.getString("type");
                            skillDescription = documentSnapshot.getString("description");
                            skillOwnerId = documentSnapshot.getString("ownerId");

                            // Update UI
                            displaySkillData();
                            getOwnerInfo();
                        } else {
                            Log.e(TAG, "Skill document does not exist");
                            Toast.makeText(SkillProfileActivity.this, "Skill not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching skill", e);
                        Toast.makeText(SkillProfileActivity.this, "Error loading skill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            Log.e(TAG, "Invalid skill ID");
            Toast.makeText(this, "Invalid skill ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displaySkillData() {
        if (skillTitle != null) {
            tvTitle.setText(skillTitle);
        }

        if (skillType != null) {
            tvType.setText("Type: " + skillType);
        }

        if (skillDescription != null) {
            tvDesc.setText(skillDescription);
        }
    }

    private void getOwnerInfo() {
        if (skillOwnerId != null && !skillOwnerId.isEmpty()) {
            db.collection("users").document(skillOwnerId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String ownerName = documentSnapshot.getString("name");
                            if (ownerName != null && !ownerName.isEmpty()) {
                                tvOwnerInfo.setText("Offered by: " + ownerName);
                                tvOwnerInfo.setVisibility(View.VISIBLE);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error fetching owner info", e));
        }
    }

    private void showMatchRecommendation() {
        if (skillType != null) {
            String suggestion = UserMatcher.suggestMatch(skillType);
            if (suggestion != null) {
                String matchText = getString(R.string.match_recommendation, suggestion);
                tvMatch.setText(matchText);
                tvMatch.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupBookingButton() {
        // Check if current user is the owner
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (currentUserId != null && currentUserId.equals(skillOwnerId)) {
            // User is the owner, change button to "Edit Skill"
            btnBook.setText("EDIT SKILL");
            btnBook.setOnClickListener(v -> {
                // Launch edit mode for this skill
                showEditSkillDialog();
            });
        } else {
            // User is not the owner, check if already joined
            checkIfAlreadyJoined(currentUserId, joined -> {
                if (joined) {
                    // Already joined, show "Joined" with green background
                    setJoinedState();
                } else {
                    // Not joined, show "Join Skill"
                    setJoinSkillState();
                }

                // Setup toggle functionality
                btnBook.setOnClickListener(v -> {
                    if (btnBook.getText().toString().equals("Join Skill")) {
                        // Join the skill
                        joinSkill(currentUserId);
                        setJoinedState();
                    } else if (btnBook.getText().toString().equals("Joined")) {
                        // Leave the skill
                        leaveSkill(currentUserId);
                        setJoinSkillState();
                    }
                });
            });
        }
    }

    private void showEditSkillDialog() {
        // Create a dialog for editing
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Skill");

        // Inflate the custom layout
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_skill, null);
        builder.setView(view);

        // Get references to the edit fields
        EditText etTitle = view.findViewById(R.id.etEditTitle);
        EditText etDescription = view.findViewById(R.id.etEditDescription);
        Spinner spinnerType = view.findViewById(R.id.spinnerEditType);

        // Setup spinner with skill types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.skill_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Set current values
        etTitle.setText(skillTitle);
        etDescription.setText(skillDescription);

        // Find index of current skill type in the spinner
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(skillType)) {
                spinnerType.setSelection(i);
                break;
            }
        }

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Get updated values
            String updatedTitle = etTitle.getText().toString().trim();
            String updatedDescription = etDescription.getText().toString().trim();
            String updatedType = spinnerType.getSelectedItem().toString();

            // Validate input
            if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(this, "Title and description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update Firestore
            updateSkillInFirestore(updatedTitle, updatedDescription, updatedType);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateSkillInFirestore(String title, String description, String type) {
        // Show progress
        Toast.makeText(this, "Updating skill...", Toast.LENGTH_SHORT).show();

        // Create update map
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", title);
        updates.put("description", description);
        updates.put("type", type);

        // Update Firestore document
        FirebaseFirestore.getInstance().collection("skills")
                .document(skillId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Skill updated successfully!", Toast.LENGTH_SHORT).show();

                    // Update the UI with new values
                    skillTitle = title;
                    skillDescription = description;
                    skillType = type;
                    displaySkillData();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update skill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setJoinedState() {
        btnBook.setText("Joined");
        btnBook.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
    }

    private void setJoinSkillState() {
        btnBook.setText("Join Skill");
        // Reset to original color (purple from your theme)
        btnBook.setBackgroundColor(ContextCompat.getColor(this, R.color.purple));
    }

    private void checkIfAlreadyJoined(String userId, JoinCheckCallback callback) {
        if (userId == null || skillId == null) {
            callback.onResult(false);
            return;
        }

        // Check in "skill_enrollments" collection if this user has joined this skill
        FirebaseFirestore.getInstance()
                .collection("skill_enrollments")
                .whereEqualTo("userId", userId)
                .whereEqualTo("skillId", skillId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean joined = !querySnapshot.isEmpty();
                    callback.onResult(joined);
                })
                .addOnFailureListener(e -> {
                    // On error, assume not joined
                    Toast.makeText(this, "Error checking enrollment status", Toast.LENGTH_SHORT).show();
                    callback.onResult(false);
                });
    }

    private void joinSkill(String userId) {
        if (userId == null || skillId == null) return;

        // Create enrollment document
        Map<String, Object> enrollment = new HashMap<>();
        enrollment.put("userId", userId);
        enrollment.put("skillId", skillId);
        enrollment.put("joinedAt", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection("skill_enrollments")
                .document(userId + "_" + skillId)
                .set(enrollment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Successfully joined skill!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to join skill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Reset button state on failure
                    setJoinSkillState();
                });
    }

    private void leaveSkill(String userId) {
        if (userId == null || skillId == null) return;

        FirebaseFirestore.getInstance()
                .collection("skill_enrollments")
                .document(userId + "_" + skillId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Successfully left skill", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to leave skill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Reset button state on failure
                    setJoinedState();
                });
    }

    // Callback interface for async join check
    interface JoinCheckCallback {
        void onResult(boolean joined);
    }
}