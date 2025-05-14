package com.example.GroupAssignment2.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.GroupAssignment2.R;
import com.example.GroupAssignment2.databinding.ActivityMainBinding;


import android.util.Log;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.GroupAssignment2.Domain.SkillDomain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Button btnCreateSkill;
    Button btnFindMatch;
    SkillUser currentUser;
    List<SkillUser> mockDatabase;

    // Bottom navigation buttons
    private LinearLayout viewBtn, wishlistBtn, shareBtn, accountBtn;

    // Firebase Auth
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize binding
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        Window window = MainActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {
            // User is not logged in, redirect to login screen
            Intent intent = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        //initRecyclerView();
        //initViews();
        setupBottomNavigation();

        btnCreateSkill = findViewById(R.id.btnCreateSkill); // Add a button in your MainActivity layout if not present
        btnCreateSkill.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateSkillActivity.class);
            startActivity(intent);
        });

        // Optional AI match entry point
        btnFindMatch = findViewById(R.id.btnFindMatch);
        if (btnFindMatch != null) {

            btnFindMatch.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SkillMatchingActivity.class);
                startActivity(intent);
            });

        }

        LinearLayout viewLayout = findViewById(R.id.viewLayout);

        viewLayout.setOnClickListener(v -> {

                Toast.makeText(MainActivity.this, "View button clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SkillListActivity.class);
                startActivity(intent);

        });

        fetchUsersFromFirestore();


    }

    /*
    private void initViews() {
        // Create Skill button
        btnCreateSkill = findViewById(R.id.btnCreateSkill);
        if (btnCreateSkill != null) {
            btnCreateSkill.setOnClickListener(v ->
                    startActivity(new Intent(MainActivity.this, CreateSkillActivity.class)));
        }

        // Optional AI match entry point
        btnFindMatch = findViewById(R.id.btnFindMatch);
        }
*/



    private void setupBottomNavigation() {
        // Find bottom navigation buttons
        viewBtn = findViewById(R.id.viewLayout);
        wishlistBtn = findViewById(R.id.wishlistLayout);
        shareBtn = findViewById(R.id.shareLayout);
        accountBtn = findViewById(R.id.accountLayout);

        // Set click listeners for each button
        if (viewBtn != null) {
            viewBtn.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "View button clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SkillListActivity.class);
                startActivity(intent);
            });
        }

        if (wishlistBtn != null) {
            wishlistBtn.setOnClickListener(v -> {
                Toast.makeText(MainActivity.this, "Wishlist button clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, WishListActivity.class);
                startActivity(intent);
            });
        }

        if (shareBtn != null) {
            shareBtn.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out SkillSwap App");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Join SkillSwap and start exchanging skills with others!");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            });
        }

        if (accountBtn != null) {
            accountBtn.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
            });
        }
    }
    private void fetchUsersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mockDatabase = new ArrayList<>();

        db.collection("users")  // Change to "skillUsers" if needed
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("name");
                        List<String> hasSkills = (List<String>) doc.get("hasSkills");
                        List<String> wantsSkills = (List<String>) doc.get("wantsSkills");

                        SkillUser user = new SkillUser(name, hasSkills, wantsSkills);
                        mockDatabase.add(user);
                    }

                    if (!mockDatabase.isEmpty()) {
                        currentUser = mockDatabase.get(0);  // You can replace this with actual logged-in user
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
