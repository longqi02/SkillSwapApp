package com.example.GroupAssignment2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.GroupAssignment2.R;
import com.example.GroupAssignment2.databinding.ActivityIntroBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class IntroActivity extends AppCompatActivity {
    private static final String TAG = "IntroActivity"; // For logging
    ActivityIntroBinding binding;
    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;
    private TextView registerText;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Log Firebase initialization
        Log.d(TAG, "Firebase Auth initialized: " + (mAuth != null));

        // Initialize UI elements
        emailInput = findViewById(R.id.userEdt);
        passwordInput = findViewById(R.id.passEdt);
        registerText = findViewById(R.id.textView);
        loginBtn = findViewById(R.id.loginBtn);

        // Set status bar color
        Window window = IntroActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(IntroActivity.this, R.color.purple));

        // Set click listener for login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login button clicked");
                loginUser();
            }
        });

        // Set click listener for register text
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Register text clicked");
                startActivity(new Intent(IntroActivity.this, RegisterActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "Current user: " + (currentUser != null ? currentUser.getEmail() : "null"));

        if (currentUser != null && currentUser.isEmailVerified()) {
            // User is already signed in and verified, redirect to MainActivity
            Log.d(TAG, "User already signed in and verified. Redirecting to MainActivity");
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
        } else if (currentUser != null) {
            Log.d(TAG, "User signed in but email not verified");
        }
    }

    private void loginUser() {
        if (emailInput == null || passwordInput == null) {
            Log.e(TAG, "Email or password input fields are null. UI elements not found.");
            Toast.makeText(IntroActivity.this, "UI Error. Please restart the app.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get email and password from input fields
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        Log.d(TAG, "Attempting to login with email: " + email);

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            Toast.makeText(IntroActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Email field is empty");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            Toast.makeText(IntroActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Password field is empty");
            return;
        }

        // Show progress message
        Toast.makeText(IntroActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Authenticating with Firebase");

        try {
            // Authenticate with Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, check if email is verified
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    if (user.isEmailVerified()) {
                                        // Email is verified, proceed to MainActivity
                                        Log.d(TAG, "Email is verified. Proceeding to MainActivity");
                                        Toast.makeText(IntroActivity.this, "Login successful!",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(IntroActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        // Email is not verified, show message and send verification again
                                        Log.d(TAG, "Email is not verified. Sending verification email again");
                                        Toast.makeText(IntroActivity.this, "Please verify your email first.",
                                                Toast.LENGTH_LONG).show();
                                        user.sendEmailVerification();
                                        mAuth.signOut();
                                    }
                                }
                            } else {
                                // If sign in fails, display a message to the user
                                Log.w(TAG, "signInWithEmail:failure", task.getException());

                                String errorMessage = "Authentication failed";
                                if (task.getException() != null) {
                                    errorMessage += ": " + task.getException().getMessage();

                                    // Get error code for Firebase Auth exceptions
                                    if (task.getException() instanceof FirebaseAuthException) {
                                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                        Log.w(TAG, "Firebase Auth Error Code: " + errorCode);
                                    }
                                }

                                Toast.makeText(IntroActivity.this, errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception during Firebase Auth operation", e);
            Toast.makeText(IntroActivity.this, "Authentication error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}