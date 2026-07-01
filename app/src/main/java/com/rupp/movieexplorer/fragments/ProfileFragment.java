package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.SplashActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private TextView textName, textUsername;
    private ImageView imageProfile, imageCameraEditBtn;
    private ImageButton editProfileBtn, changePwBtn, logOutBtn, deleteAccBtn;
    private MaterialButton logoutBtnFinal, cancelBtn, deleteBtnFinal, cancelDeleteBtn;
    private LinearLayout logoutLayout, deleteLayout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ActivityResultLauncher<PickVisualMediaRequest> imagePickerLauncher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        uploadImageToFirebase(uri);
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        initFirebase();
        setupClickListeners();
        loadUserProfile();

        return view;
    }

    private void initViews(View view) {
        textName = view.findViewById(R.id.textName);
        textUsername = view.findViewById(R.id.textUsername);
        imageProfile = view.findViewById(R.id.imageProfile);
        imageCameraEditBtn = view.findViewById(R.id.imageCameraEditBtn);
        editProfileBtn = view.findViewById(R.id.EditProfileBtn);
        changePwBtn = view.findViewById(R.id.ChangePwBtn);
        logOutBtn = view.findViewById(R.id.LogOutBtn);
        deleteAccBtn = view.findViewById(R.id.DeleteAccBtn);
        logoutBtnFinal = view.findViewById(R.id.LogOutBtnFinal);
        cancelBtn = view.findViewById(R.id.CancelBtn);
        logoutLayout = view.findViewById(R.id.LogOutPromph);
        deleteBtnFinal = view.findViewById(R.id.DeleteAccBtnFinal);
        cancelDeleteBtn = view.findViewById(R.id.CancelDeleteBtn);
        deleteLayout = view.findViewById(R.id.DeleteAccPromph);
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    private void setupClickListeners() {
        editProfileBtn.setOnClickListener(v -> showEditNameDialog());

        imageCameraEditBtn.setOnClickListener(v -> {
            imagePickerLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        changePwBtn.setOnClickListener(v -> showChangePasswordDialog());

        logOutBtn.setOnClickListener(v -> logoutLayout.setVisibility(View.VISIBLE));

        logoutBtnFinal.setOnClickListener(v -> {
            auth.signOut();
            navigateToSplash();
        });

        cancelBtn.setOnClickListener(v -> logoutLayout.setVisibility(View.GONE));

        deleteAccBtn.setOnClickListener(v -> deleteLayout.setVisibility(View.VISIBLE));

        cancelDeleteBtn.setOnClickListener(v -> deleteLayout.setVisibility(View.GONE));

        deleteBtnFinal.setOnClickListener(v -> {
            showReAuthForDeleteDialog();
        });
    }

    private void showReAuthForDeleteDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Please enter your password to permanently delete your account.");

        final EditText passwordInput = new EditText(getContext());
        passwordInput.setHint("Password");
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setPadding(50, 20, 50, 20);
        builder.setView(passwordInput);

        builder.setPositiveButton("Delete Forever", (dialog, which) -> {
            String password = passwordInput.getText().toString().trim();
            if (password.isEmpty()) {
                Toast.makeText(getContext(), "Password required", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = auth.getCurrentUser();
            if (user != null && user.getEmail() != null) {
                user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), password))
                        .addOnCompleteListener(reAuthTask -> {
                            if (reAuthTask.isSuccessful()) {
                                deleteUserAccount(user);
                            } else {
                                Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteUserAccount(FirebaseUser user) {
        String uid = user.getUid();
        // Delete from Firestore first
        db.collection("Users").document(uid).delete()
                .addOnSuccessListener(aVoid -> {
                    // Then delete from Auth
                    user.delete().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                            navigateToSplash();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(getContext(), "Auth deletion failed: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete user data", Toast.LENGTH_SHORT).show());
    }

    private void loadUserProfile() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            db.collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String name = username != null ? username : documentSnapshot.getString("name");

                            String profileUrl = documentSnapshot.getString("profileImage");

                            textName.setText(name != null ? name : "No Name");
                            textUsername.setText("@" + (name != null ? name.toLowerCase().replace(" ", "_") : "user"));

                            if (profileUrl != null && !profileUrl.isEmpty()) {
                                Picasso.get().load(profileUrl).placeholder(R.drawable.pf).into(imageProfile);
                            }
                        } else {
                            // Handle case where Firestore document doesn't exist yet
                            FirebaseUser currentUser = auth.getCurrentUser();
                            if (currentUser != null) {
                                textName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Guest User");
                                String email = currentUser.getEmail();
                                if (email != null) {
                                    textUsername.setText("@" + email.split("@")[0]);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show());
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        Toast.makeText(getContext(), "Uploading...", Toast.LENGTH_SHORT).show();

        StorageReference profileRef = storage.getReference().child("profile_images/" + user.getUid() + ".jpg");
        profileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    db.collection("Users").document(user.getUid())
                            .update("profileImage", downloadUrl)
                            .addOnSuccessListener(aVoid -> {
                                Picasso.get().load(downloadUrl).into(imageProfile);
                                Toast.makeText(getContext(), "Profile Image Updated", Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showEditNameDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Edit Name");

        final EditText input = new EditText(getContext());
        input.setPadding(50, 20, 50, 20);
        input.setText(textName.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateProfileName(newName);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateProfileName(String newName) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            db.collection("Users").document(user.getUid())
                    .update("username", newName)
                    .addOnSuccessListener(aVoid -> {
                        textName.setText(newName);
                        textUsername.setText("@" + newName.toLowerCase().replace(" ", "_"));
                        Toast.makeText(getContext(), "Name Updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show());
        }
    }

    private void showChangePasswordDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Change Password");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText currentPwInput = new EditText(getContext());
        currentPwInput.setHint("Current Password");
        currentPwInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(currentPwInput);

        final EditText newPwInput = new EditText(getContext());
        newPwInput.setHint("New Password");
        newPwInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(newPwInput);

        builder.setView(layout);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String currentPassword = currentPwInput.getText().toString().trim();
            String newPassword = newPwInput.getText().toString().trim();

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(getContext(), "New password too short", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = auth.getCurrentUser();
            if (user != null && user.getEmail() != null) {
                // Re-authenticate user
                user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), currentPassword))
                        .addOnCompleteListener(reAuthTask -> {
                            if (reAuthTask.isSuccessful()) {
                                user.updatePassword(newPassword).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Current password incorrect", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void navigateToSplash() {
        Intent intent = new Intent(requireActivity(), SplashActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
