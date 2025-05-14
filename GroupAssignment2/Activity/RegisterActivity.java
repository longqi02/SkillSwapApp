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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity"; // For logging
    private EditText emailInput, passwordInput;  // Changed TextView to EditText
    private TextView loginText;
    private Button signupBtn;
    private FirebaseAuth mAuth;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Log Firebase initialization
        Log.d(TAG, "Firebase Auth initialized: " + (mAuth != null));

        // Initialize UI elements
        emailInput = findViewById(R.id.userEdt);
        passwordInput = findViewById(R.id.passEdt);
        signupBtn = findViewById(R.id.signupBtn);
        loginText = findViewById(R.id.textView);

        // Set status bar color
        Window window = RegisterActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(RegisterActivity.this, R.color.purple));

        // Set up signup button click listener
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Signup button clicked");
                registerUser();
            }
        });

        // Set up login text click listener
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login text clicked");
                Intent intent = new Intent(RegisterActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUser() {
        // Get input values
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        Log.d(TAG, "Attempting to register with email: " + email);

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            Toast.makeText(RegisterActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Email field is empty");
            return;
        }

        if (!email.matches(emailPattern)) {
            emailInput.setError("Enter a valid email address");
            Toast.makeText(RegisterActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Email format is invalid");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            Toast.makeText(RegisterActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Password field is empty");
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Password is too short");
            return;
        }

        // Show progress (you can add a progress dialog here)
        Toast.makeText(RegisterActivity.this, "Creating account...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Creating account with Firebase Auth");

        try {
            // Create user with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign up success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(RegisterActivity.this, "Account created successfully!",
                                        Toast.LENGTH_SHORT).show();

                                // Send email verification
                                sendEmailVerification(user);

                                // Navigate to main activity or profile setup
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign up fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                String errorMessage = "Registration failed";
                                if (task.getException() != null) {
                                    errorMessage += ": " + task.getException().getMessage();

                                    // Get error code for Firebase Auth exceptions
                                    if (task.getException() instanceof FirebaseAuthException) {
                                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                        Log.w(TAG, "Firebase Auth Error Code: " + errorCode);
                                    }
                                }

                                Toast.makeText(RegisterActivity.this, errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception during Firebase Auth operation", e);
            Toast.makeText(RegisterActivity.this, "Authentication error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void sendEmailVerification(FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email verification sent to " + user.getEmail());
                                Toast.makeText(RegisterActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Log.w(TAG, "Failed to send verification email", task.getException());
                                Toast.makeText(RegisterActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}