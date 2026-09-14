package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import android.net.Uri;
import android.os.Bundle;

import android.os.CancellationSignal;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.Fragment;
import androidx.credentials.Credential;
import androidx.credentials.CustomCredential;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.rupp.movieexplorer.Constants;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.SplashActivity;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView textName, textUsername;
    private ImageView imageProfile, imageCameraEditBtn;
    private ImageButton editProfileBtn, changePwBtn, logOutBtn, deleteAccBtn;
    private MaterialButton logoutBtnFinal, cancelBtn, deleteBtnFinal, cancelDeleteBtn;
    private LinearLayout logoutLayout, deleteLayout;
    private View loadingLayout, mainContent;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ActivityResultLauncher<PickVisualMediaRequest> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cropLauncher;
    private LinearLayout changePWCtn;

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
                        startCrop(uri);
                    }
                }
        );

        cropLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Uri resultUri = UCrop.getOutput(result.getData());
                        if (resultUri != null) {
                            uploadImageToCloudinary(resultUri);
                        }
                    } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                        Intent data = result.getData();
                        if (data != null) {
                            Throwable cropError = UCrop.getError(data);
                            if (cropError != null) {
                                View view = getView();
                                if (view != null) {
                                    Snackbar.make(view, "Crop error: " + cropError.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }
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
        setUpUI();
        setupClickListeners();
        loadUserProfile();

        return view;
    }

    private void initViews(View view) {
        textName = view.findViewById(R.id.textName);
//        textUsername = view.findViewById(R.id.textUsername);
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
        loadingLayout = view.findViewById(R.id.loadingLayout);
        mainContent = view.findViewById(R.id.mainContent);
        changePWCtn = view.findViewById(R.id.ChangePwCtn);
    }

    private void showLoading(boolean isLoading) {
        if (loadingLayout != null && mainContent != null) {
            loadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            mainContent.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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
            if (isGoogleUser()){
                showReAuthGoogleUser();
            }else {
                showReAuthForDeleteDialog();
            }
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
                passwordInput.setError("Password required");
                return;
            }

            FirebaseUser user = auth.getCurrentUser();
            if (user != null && user.getEmail() != null) {
                user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), password))
                        .addOnCompleteListener(reAuthTask -> {
                            if (reAuthTask.isSuccessful()) {
                                deleteUserAccount(user);
                            } else {
                                View view = getView();
                                if (view != null) {
                                    Snackbar.make(view, "Authentication failed", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showReAuthGoogleUser() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            return;
        }

        GetGoogleIdOption googleIdOption =
                new GetGoogleIdOption.Builder()
                        .setServerClientId(Constants.SERVER_CLIENT_ID)
                        .setFilterByAuthorizedAccounts(false)
                        .build();

        GetCredentialRequest request =
                new GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build();

        CredentialManager credentialManager =
                CredentialManager.create(requireActivity());

        credentialManager.getCredentialAsync(
                requireActivity(),
                request,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),

                new CredentialManagerCallback<
                        GetCredentialResponse,
                        GetCredentialException>() {

                    @Override
                    public void onResult(GetCredentialResponse result) {

                        Credential credential =
                                result.getCredential();

                        if (credential instanceof CustomCredential) {

                            CustomCredential customCredential =
                                    (CustomCredential) credential;

                            if (GoogleIdTokenCredential
                                    .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                    .equals(customCredential.getType())) {

                                GoogleIdTokenCredential googleCredential =
                                        GoogleIdTokenCredential.createFrom(
                                                customCredential.getData()
                                        );

                                String idToken =
                                        googleCredential.getIdToken();

                                // Now re-authenticate Firebase
                                reAuthenticateFirebase(idToken);
                            }
                        }
                    }

                    @Override
                    public void onError(GetCredentialException e) {

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(
                                        requireContext(),
                                        "Google authentication failed",
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                    }
                }
        );
    }

    private void reAuthenticateFirebase(String idToken) {

        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }

        AuthCredential credential =
                GoogleAuthProvider.getCredential(idToken, null);

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> {

                    View view = getView();

                    Snackbar.make( view
                            ,
                            "Re-authentication successful",
                            Snackbar.LENGTH_SHORT
                    ).show();

                    // NOW it is safe to delete
                    deleteUserAccount(user);
                    deleteAuthenticationAccount(user);
                })
                .addOnFailureListener(e -> {
                    View view = getView();

                    Snackbar.make( view
                            ,
                            "Re-authentication failed",
                            Snackbar.LENGTH_SHORT
                    ).show();
                });
    }

    private void deleteUserAccount(FirebaseUser user) {
        String uid = user.getUid();
        DocumentReference userDocRef = db.collection("Users").document(uid);

        Task<QuerySnapshot> favoritesTask = userDocRef.collection("favorites").get();
        Task<QuerySnapshot> watchlistTask = userDocRef.collection("watchlist").get();

        Tasks.whenAll(favoritesTask, watchlistTask).addOnSuccessListener(aVoid -> {
            WriteBatch batch = db.batch();

            if (favoritesTask.isSuccessful() && favoritesTask.getResult() != null) {
                for (QueryDocumentSnapshot doc : favoritesTask.getResult()){
                    batch.delete(doc.getReference());
                }
            }

            if (watchlistTask.isSuccessful() && watchlistTask.getResult() != null) {
                for(QueryDocumentSnapshot doc : watchlistTask.getResult()) {
                    batch.delete(doc.getReference());
                }
            }

            batch.delete(userDocRef);
            batch.commit().addOnSuccessListener(aVoid2 -> {
                deleteAuthenticationAccount(user);
            }).addOnFailureListener(e -> {
                View view = getView();
                if (view != null) {
                    Snackbar.make(view, "Failed to clear user data", Snackbar.LENGTH_LONG).show();
                }
            });
        }).addOnFailureListener(e -> {
            View view = getView();
            if (view != null) {
                Snackbar.make(view, "Failed to fetch user data for deletion", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // Helper method to keep the code clean and readable
    private void deleteAuthenticationAccount(FirebaseUser user) {
        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                View view = getView();
                if (view != null) {
                    Snackbar.make(view, "Account Completely Deleted", Snackbar.LENGTH_SHORT).show();
                }
                navigateToSplash();
            } else {
                String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                View view = getView();
                if (view != null) {
                    Snackbar.make(view, "Auth deletion failed: " + error, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadUserProfile() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            showLoading(true);
            db.collection("Users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        showLoading(false);
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String name = username != null ? username : documentSnapshot.getString("name");

                            String profileUrl = documentSnapshot.getString("profileImage");

                            textName.setText(name != null ? name : "No Name");
//                            textUsername.setText("@" + (name != null ? name.toLowerCase().replace(" ", "_") : "user"));

                            if (profileUrl != null && !profileUrl.isEmpty()) {
                                Picasso.get().load(profileUrl).placeholder(R.drawable.pf).into(imageProfile);
                            }
                        } else {
                            // Handle case where Firestore document doesn't exist yet
                            FirebaseUser currentUser = auth.getCurrentUser();
                            if (currentUser != null) {
                                textName.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Guest User");
                                String email = currentUser.getEmail();
                                if (email != null && textUsername != null) {
                                    textUsername.setText("@" + email.split("@")[0]);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        showLoading(false);
                        View view = getView();
                        if (view != null) {
                            Snackbar.make(view, "Failed to load profile", Snackbar.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = "cropped_profile_" + System.currentTimeMillis() + ".jpg";
        UCrop.Options options = new UCrop.Options();

        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setToolbarColor(android.graphics.Color.BLACK);
        options.setStatusBarColor(android.graphics.Color.BLACK);
        options.setToolbarWidgetColor(android.graphics.Color.WHITE);
        options.setActiveControlsWidgetColor(android.graphics.Color.WHITE);

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(requireContext().getCacheDir(), destinationFileName)))
                .withAspectRatio(1, 1)
                .withMaxResultSize(1000, 1000)
                .withOptions(options);

        cropLauncher.launch(uCrop.getIntent(requireContext()));
    }

    private void uploadImageToCloudinary(Uri imageUri) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        View view = getView();
        if (view != null) {
            Snackbar.make(view, "Uploading...", Snackbar.LENGTH_SHORT).show();
        }

        MediaManager.get().upload(imageUri)
                .unsigned("User_profile") // Replace with your Cloudinary upload preset
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String downloadUrl = (String) resultData.get("secure_url");
                        db.collection("Users").document(user.getUid())
                                .update("profileImage", downloadUrl)
                                .addOnSuccessListener(aVoid -> {
                                    Picasso.get().load(downloadUrl).into(imageProfile);
                                    View v = getView();
                                    if (v != null) {
                                        Snackbar.make(v, "Profile Image Updated", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        View v = getView();
                        if (v != null) {
                            Snackbar.make(v, "Upload failed: " + error.getDescription(), Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
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
                        View view = getView();
                        if (view != null) {
                            Snackbar.make(view, "Name Updated", Snackbar.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        View view = getView();
                        if (view != null) {
                            Snackbar.make(view, "Update failed", Snackbar.LENGTH_SHORT).show();
                        }
                    });
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
                if (currentPassword.isEmpty()) currentPwInput.setError("Required");
                if (newPassword.isEmpty()) newPwInput.setError("Required");
                return;
            }

            if (newPassword.length() < 6) {
                newPwInput.setError("New password too short");
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
                                        View view = getView();
                                        if (view != null) {
                                            Snackbar.make(view, "Password Changed", Snackbar.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        String msg = task.getException() != null ? task.getException().getMessage() : "Error changing password";
                                        View view = getView();
                                        if (view != null) {
                                            Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                currentPwInput.setError("Current password incorrect");
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void setUpUI(){
        if (isGoogleUser()){
            changePWCtn.setVisibility(View.GONE);
        }else {
            changePWCtn.setVisibility(View.VISIBLE);
        }
    }

    private boolean isGoogleUser(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        boolean isGUser= false;
        for (UserInfo profile : user.getProviderData()){
            if(profile.getProviderId().equals("google.com")){
                isGUser = true;
            }
        }

        return isGUser;
    }

    private void navigateToSplash() {
        Intent intent = new Intent(requireActivity(), SplashActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
