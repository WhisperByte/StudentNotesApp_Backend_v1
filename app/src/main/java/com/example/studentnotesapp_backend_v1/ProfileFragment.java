package com.example.studentnotesapp_backend_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studentnotesapp_backend_v1.auth.LoginActivity;
import com.example.studentnotesapp_backend_v1.profile.ChangePasswordActivity;
import com.example.studentnotesapp_backend_v1.profile.EditProfileActivity;
import com.example.studentnotesapp_backend_v1.profile.NotificationSettingsActivity;
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

            // 🔹 Fetch Firestore data for name and profile image
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String photoUrl = documentSnapshot.getString("photoUrl");

                            profileName.setText(name != null ? name : "Student Name");

                            if (photoUrl != null && !photoUrl.isEmpty()) {
                                Picasso.get().load(photoUrl)
                                        .placeholder(R.drawable.ic_profile_placeholder)
                                        .error(R.drawable.ic_profile_placeholder)
                                        .into(profileImage);
                            } else {
                                profileImage.setImageResource(R.drawable.ic_profile_placeholder);
                            }
                        } else {
                            profileName.setText("Student Name");
                            profileImage.setImageResource(R.drawable.ic_profile_placeholder);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        // 🔹 Logout button
        logoutButton.setOnClickListener(v -> {
            try {
                FirebaseAuth.getInstance().signOut();

                if (getActivity() != null) {
                    SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(getActivity(), "You have been logged out", Toast.LENGTH_SHORT).show();
                    startActivity(intent);

                    getActivity().finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Logout failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // 🔹 Edit Profile
        view.findViewById(R.id.edit_profile).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        // 🔹 Change Password
        view.findViewById(R.id.change_password).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        // 🔹 Notification Settings
        view.findViewById(R.id.notifications).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationSettingsActivity.class);
            startActivity(intent);
        });
    }
}
