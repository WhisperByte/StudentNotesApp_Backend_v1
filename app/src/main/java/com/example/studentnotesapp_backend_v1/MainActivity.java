package com.example.studentnotesapp_backend_v1;



import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "NotesTest";
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db   = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously()
                    .addOnSuccessListener(this::onSignedIn)
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Auth failed", e);
                        Toast.makeText(this, "Auth failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            onSignedIn(null);
        }
    }

    private void onSignedIn(AuthResult res) {
        String uid = auth.getCurrentUser().getUid();
        Log.d(TAG, "Signed in as " + uid);

        Map<String,Object> data = new HashMap<>();
        data.put("msg", "Hello Firestore");
        data.put("ownerId", uid);

        db.collection("notes").add(data)
                .addOnSuccessListener(this::readBack)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Write failed", e);
                    Toast.makeText(this, "Write failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void readBack(@NonNull DocumentReference ref) {
        Log.d(TAG, "Wrote doc: " + ref.getPath()); // should be notes/<id>
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
