package com.example.studentnotesapp_backend_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentnotesapp_backend_v1.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileName, profileEmail;
    private Button logoutButton;
    private FirebaseAuth mAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        logoutButton = view.findViewById(R.id.logout_button);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            profileEmail.setText(currentUser.getEmail() != null ?
                    currentUser.getEmail() : "student@email.com");

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String name = document.getString("name");
                            profileName.setText(name != null ? name : "Student Name");
                        } else {
                            profileName.setText("Student Name");
                        }
                    })
                    .addOnFailureListener(e -> {
                        profileName.setText("Error loading name");
                    });

            Uri photoUri = currentUser.getPhotoUrl();
            if (photoUri != null) {
                Picasso.get().load(photoUri).into(profileImage);
            }
    }

        logoutButton.setOnClickListener(v -> {
            try {
                // 1️⃣ Sign out from Firebase
                FirebaseAuth.getInstance().signOut();

                // 2️⃣ Clear local login state
                if (getActivity() != null) {
                    SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    // 3️⃣ Redirect to LoginActivity
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(getActivity(), "You have been logged out", Toast.LENGTH_SHORT).show();
                    startActivity(intent);

                    // 4️⃣ Finish MainActivity safely
                    getActivity().finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Logout failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Optional: click listener for Edit Profile (future)
        view.findViewById(R.id.edit_profile).setOnClickListener(v -> {
            // TODO: Open EditProfileActivity to change name/email/photo
        });
    }
}
