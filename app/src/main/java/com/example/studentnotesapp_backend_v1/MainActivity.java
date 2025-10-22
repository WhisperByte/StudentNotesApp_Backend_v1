package com.example.studentnotesapp_backend_v1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.studentnotesapp_backend_v1.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "NotesTest";
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }
        FirebaseApp.initializeApp(this);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 🔹 If user is not logged in, go to LoginActivity
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // 🔹 Otherwise continue normal flow
        onSignedIn();

        // 🔹 Setup Bottom Navigation
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_calendar) {
                selectedFragment = new CalendarFragment();
            } else if (id == R.id.nav_tasks) {
                selectedFragment = new TasksFragment();
            } else if (id == R.id.nav_reminder) {
                selectedFragment = new ReminderFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    private void onSignedIn() {
        String uid = auth.getCurrentUser().getUid();
        Log.d(TAG, "Signed in as " + uid);

        Map<String, Object> data = new HashMap<>();
        data.put("msg", "Hello Firestore");
        data.put("ownerId", uid);

        db.collection("notes").add(data)
                .addOnSuccessListener(this::readBack)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Write failed", e);
                    Toast.makeText(this, "Write failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void readBack(DocumentReference ref) {
        Log.d(TAG, "Wrote doc: " + ref.getPath());
        ref.get().addOnSuccessListener(snap -> {
            String msg = snap.getString("msg");
            Log.d(TAG, "Read back: " + msg);
            Toast.makeText(this, "Read: " + msg, Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Read failed", e);
            Toast.makeText(this, "Read failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
