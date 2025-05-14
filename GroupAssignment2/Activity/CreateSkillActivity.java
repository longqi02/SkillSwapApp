package com.example.GroupAssignment2.Activity;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.GroupAssignment2.Domain.SkillDomain;
import com.example.GroupAssignment2.Activity.ChatGPTHelper;
import com.example.GroupAssignment2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateSkillActivity extends AppCompatActivity {

    private static final String TAG = "CreateSkillActivity";

    EditText etSkillName, etDescription;
    Spinner spinnerCategory;
    Button btnSubmitSkill;
    Button btnPickDate;
    TextView tvPickedDate;
    Spinner spinnerMeetingMode;
    ProgressBar progressBar;
    EditText etCustomCategory;

    private FirebaseAuth mAuth;
  //  private FirebaseDatabase database;
    private FirebaseFirestore firestore;
    private DatabaseReference skillsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_skill);

        // Firebase init
        mAuth = FirebaseAuth.getInstance();
        //database = FirebaseDatabase.getInstance();
        //skillsRef = database.getReference("skills");

        firestore = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

        // Setup ChatGPT AI popup button
        setupAiRobot();

        setupMeetingModeSpinner();
        setupCategorySpinner();
        setupDatePicker();
        setupSubmitButton();
    }

    private void initializeViews() {
        etSkillName = findViewById(R.id.etSkillName);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSubmitSkill = findViewById(R.id.btnSubmitSkill);
        btnPickDate = findViewById(R.id.btnPickDate);
        tvPickedDate = findViewById(R.id.tvPickedDate);
        spinnerMeetingMode = findViewById(R.id.spinnerMeetingMode);
        etCustomCategory = findViewById(R.id.etCustomCategory);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupAiRobot() {
        ImageView btnAiRobot = findViewById(R.id.btnAiRobot);
        if (btnAiRobot != null) {
            btnAiRobot.bringToFront();
            btnAiRobot.setOnClickListener(v -> ChatGPTHelper.setupFloatingAI(CreateSkillActivity.this));
        } else {
            Log.w(TAG, "AI Robot button not found in layout");
        }
    }

    private void setupMeetingModeSpinner() {
        ArrayAdapter<String> meetingAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Physical", "Online"}
        );
        meetingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeetingMode.setAdapter(meetingAdapter);
    }

    private void setupCategorySpinner() {
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                etCustomCategory.setVisibility(selected.equals("Others") ? View.VISIBLE : View.GONE);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.skill_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        btnPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateSkillActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        tvPickedDate.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    private void setupSubmitButton() {
        btnSubmitSkill.setOnClickListener(v -> {
            if (validateInputs()) {
                SkillDomain skill = createSkillFromInput();
                saveSkillToSkillsCollection(skill);
                saveSkillToUserProfile(skill);
            }
        });
    }

    private boolean validateInputs() {
        if (etSkillName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a skill name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etDescription.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tvPickedDate.getText().toString().equals("No date selected")) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spinnerCategory.getSelectedItem().toString().equals("Others") &&
                etCustomCategory.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a custom category", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private SkillDomain createSkillFromInput() {
        String name = etSkillName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        if (category.equals("Others")) {
            category = etCustomCategory.getText().toString().trim();
        }
        String meetingMode = spinnerMeetingMode.getSelectedItem().toString();
        String date = tvPickedDate.getText().toString();

        String ownerId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "unknown_user";
        String skillId = ownerId + "_" + System.currentTimeMillis();

        return new SkillDomain(skillId, name, category,
                description + " (Meeting mode: " + meetingMode + ", Available on: " + date + ")",
                ownerId);
    }

    // ✅ Save into 'skills' collection (for listing/viewing)
    private void saveSkillToSkillsCollection(SkillDomain skill) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> skillData = new HashMap<>();
        skillData.put("id", skill.getId());
        skillData.put("title", skill.getTitle());
        skillData.put("type", skill.getType());
        skillData.put("description", skill.getDescription());
        skillData.put("ownerId", skill.getOwnerId());
        skillData.put("timestamp", System.currentTimeMillis());

        FirebaseFirestore.getInstance().collection("skills")
                .document(skill.getId())
                .set(skillData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Skill saved to listing!", Toast.LENGTH_SHORT).show();
                    clearForm();
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save skill: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                });
    }

    // ✅ Save into 'users' collection (for matching)
    private void saveSkillToUserProfile(SkillDomain skill) {
        String uid = mAuth.getCurrentUser().getUid();
        String userName = mAuth.getCurrentUser().getEmail(); // or display name

        List<String> hasSkills = Arrays.asList(skill.getTitle());
        List<String> wantsSkills = Arrays.asList(skill.getType());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", userName);
        userMap.put("hasSkills", hasSkills);
        userMap.put("wantsSkills", wantsSkills);

        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .set(userMap)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "User profile updated for matching!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void clearForm() {
        etSkillName.setText("");
        etDescription.setText("");
        spinnerCategory.setSelection(0);
        etCustomCategory.setText("");
        etCustomCategory.setVisibility(View.GONE);
        tvPickedDate.setText("No date selected");
        spinnerMeetingMode.setSelection(0);
    }
}