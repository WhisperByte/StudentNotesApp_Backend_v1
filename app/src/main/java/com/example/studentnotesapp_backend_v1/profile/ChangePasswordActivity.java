package com.example.studentnotesapp_backend_v1.profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentnotesapp_backend_v1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import android.os.Build;
import androidx.core.content.ContextCompat;


import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.view.Window;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPassword, newPassword, confirmNewPassword;
    private Button  btnChangePassword;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);




        // Add top padding equal to status bar height so the app bar is not hidden
        int statusBarHeight = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (statusBarHeight > 0) {
            int height = getResources().getDimensionPixelSize(statusBarHeight);
            findViewById(R.id.back_button).getRootView().setPadding(0, height, 0, 0);
        }

        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        confirmNewPassword = findViewById(R.id.confirm_new_password);
        btnChangePassword = findViewById(R.id.btn_change_password);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        btnChangePassword.setOnClickListener(v -> changePassword());
        findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed());

        Window window = getWindow();

        // Make sure layout starts below status bar
        WindowCompat.setDecorFitsSystemWindows(window, false);

        // Set the status bar to the same blue as app bar
        window.setStatusBarColor(getResources().getColor(android.R.color.holo_blue_light));

    }

    private void changePassword() {
        String currentPass = currentPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmNewPassword.getText().toString().trim();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reauthenticate user
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);
        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    user.updatePassword(newPass)
                            .addOnSuccessListener(aVoid1 -> Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Current password incorrect", Toast.LENGTH_SHORT).show());
    }
}

