package com.example.GroupAssignment2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.GroupAssignment2.Domain.BookingDomain;
import com.example.GroupAssignment2.Domain.SkillDomain;
import com.example.GroupAssignment2.Domain.UserDomain;
import com.example.GroupAssignment2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    // UI elements
    private EditText etName, etAge, etPhone, etLearnedSkills;
    private Button btnSave, btnBack, btnLogout;
    private TextView tvBookingsHeader;
    private RecyclerView recyclerBookings;
    private ImageView ivProfilePicture;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private DatabaseReference bookingsRef;
    private DatabaseReference skillsRef;

    // User ID
    private String currentUserId;

    // Adapter for bookings list
    private BookingAdapter bookingAdapter;
    private List<BookingDomain> bookingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get current user ID
        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        } else {
            currentUserId = "user123"; // Default user ID if not logged in
        }

        // Initialize UI elements
        initViews();

        // Initialize Firebase database
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users").child(currentUserId);
        bookingsRef = database.getReference("bookings");
        skillsRef = database.getReference("skills");

        // Set up bookings recycler view
        bookingsList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(bookingsList);
        recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
        recyclerBookings.setAdapter(bookingAdapter);

        // Load user data
        loadUserData();

        // Load user's bookings
        loadBookings();

        // Set up listeners
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookingsAndJoinedSkills();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etPhone = findViewById(R.id.etPhone);
        etLearnedSkills = findViewById(R.id.etLearnedSkills);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnlogout);
        tvBookingsHeader = findViewById(R.id.tvBookingsHeader);
        recyclerBookings = findViewById(R.id.recyclerBookings);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
    }

    private void setupListeners() {
        // Save button
        btnSave.setOnClickListener(v -> saveUserData());

        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Logout button
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> signOut());
        }

        // Profile picture
        if (ivProfilePicture != null) {
            ivProfilePicture.setOnClickListener(v -> {
                Toast.makeText(this, "Change profile picture feature coming soon!", Toast.LENGTH_SHORT).show();
                // Could launch image picker here
            });
        }
    }

    private void loadUserData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserDomain user = dataSnapshot.getValue(UserDomain.class);
                    if (user != null) {
                        // Set UI elements with user data
                        etName.setText(user.getName());
                        etAge.setText(String.valueOf(user.getAge()));
                        etPhone.setText(user.getPhone());
                        etLearnedSkills.setText(user.getLearnedSkills());
                    }
                } else {
                    // Create default user if not exists
                    String name = mAuth.getCurrentUser() != null ?
                            mAuth.getCurrentUser().getDisplayName() : "Your Name";
                    if (name == null || name.isEmpty()) {
                        name = mAuth.getCurrentUser() != null ?
                                mAuth.getCurrentUser().getEmail() : "Your Name";
                    }
                    UserDomain newUser = new UserDomain(currentUserId, name, 25,
                            "Your Phone Number", "Your Skills");
                    userRef.setValue(newUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserProfileActivity.this,
                        "Failed to load user data: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData() {
        // Validate inputs
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String learnedSkills = etLearnedSkills.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            etAge.setError("Valid age is required");
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            return;
        }

        // Create UserDomain object
        UserDomain user = new UserDomain(currentUserId, name, age, phone, learnedSkills);

        // Save to Firebase
        userRef.setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserProfileActivity.this,
                            "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(UserProfileActivity.this,
                        "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadBookings() {
        bookingsRef.orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookingsList.clear();
                        if (!dataSnapshot.exists() || !dataSnapshot.hasChildren()) {
                            tvBookingsHeader.setText("No bookings found");
                            bookingAdapter.notifyDataSetChanged();
                            return;
                        }

                        tvBookingsHeader.setText("Your Bookings");

                        for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                            BookingDomain booking = bookingSnapshot.getValue(BookingDomain.class);
                            if (booking != null) {
                                bookingsList.add(booking);
                            }
                        }

                        bookingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(UserProfileActivity.this,
                                "Failed to load bookings: " + databaseError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to sign out user
    private void signOut() {
        // Sign out from Firebase Auth
        mAuth.signOut();

        // Navigate back to IntroActivity
        Intent intent = new Intent(UserProfileActivity.this, IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void loadBookingsAndJoinedSkills() {
        // Show loading indicator if you have one

        // Clear previous data
        bookingsList.clear();

        // Get current user ID
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (currentUserId == null) return;

        // Load joined skills
        FirebaseFirestore.getInstance().collection("skill_enrollments")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String skillId = document.getString("skillId");
                        if (skillId != null) {
                            // For each joined skill, get the skill details
                            FirebaseFirestore.getInstance().collection("skills")
                                    .document(skillId)
                                    .get()
                                    .addOnSuccessListener(skillDoc -> {
                                        if (skillDoc.exists()) {
                                            // Create a booking object for the joined skill
                                            BookingDomain booking = new BookingDomain(
                                                    skillId,
                                                    currentUserId,
                                                    document.getLong("joinedAt") != null ? document.getLong("joinedAt") : System.currentTimeMillis()
                                            );

                                            // Add to list and update UI
                                            bookingsList.add(booking);
                                            bookingAdapter.notifyDataSetChanged();

                                            // Update header text if needed
                                            updateBookingsHeader();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserProfileActivity.this, "Error loading joined skills: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Load traditional bookings (your existing code)
        FirebaseDatabase.getInstance().getReference("bookings").orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                            for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                                BookingDomain booking = bookingSnapshot.getValue(BookingDomain.class);
                                if (booking != null) {
                                    bookingsList.add(booking);
                                }
                            }

                            bookingAdapter.notifyDataSetChanged();
                            updateBookingsHeader();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(UserProfileActivity.this,
                                "Failed to load bookings: " + databaseError.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateBookingsHeader() {
        if (bookingsList.isEmpty()) {
            tvBookingsHeader.setText("No bookings found");
        } else {
            tvBookingsHeader.setText("Your Bookings");
        }
    }

    // Method to navigate back to main activity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}