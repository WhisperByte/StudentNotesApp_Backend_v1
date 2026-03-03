package com.example.studentnotesapp_backend_v1.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentnotesapp_backend_v1.MainActivity;
import com.example.studentnotesapp_backend_v1.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText name, email, password, confirmPassword;
    private Button btnRegister;
    private TextView goToLogin;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase explicitly
        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_register);

        name = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        confirmPassword = findViewById(R.id.registerConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        goToLogin = findViewById(R.id.goToLogin);
        progressBar = findViewById(R.id.progressBar); // Add a ProgressBar in your layout

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
        goToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if (userName.isEmpty() || userEmail.isEmpty() || userPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userPass.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPass.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        auth.createUserWithEmailAndPassword(userEmail, userPass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveUserData(user.getUid(), userName, userEmail);
                        } else {
                            showError("User creation failed");
                        }
                    } else {
                        showError("Registration failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    private void saveUserData(String uid, String name, String email) {
        Log.d(TAG, "Saving user data for UID: " + uid);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("createdAt", System.currentTimeMillis());

        firestore.collection("users").document(uid)
                .set(userMap)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Firestore write success");
                    progressBar.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);

                    Toast.makeText(this, "Account created successfully 🎉", Toast.LENGTH_SHORT).show();

                    // Keep user logged in
                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("isLoggedIn", true).apply();

                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore write failed", e);
                    showError("Error saving user data: " + e.getMessage());
                });
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        btnRegister.setEnabled(true);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
