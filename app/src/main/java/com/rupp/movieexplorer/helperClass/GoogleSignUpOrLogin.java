package com.rupp.movieexplorer.helperClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.rupp.movieexplorer.Constants;
import com.rupp.movieexplorer.MainActivity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class GoogleSignUpOrLogin {

    private final Activity activity;
    private final CredentialManager credentialManager;


    public GoogleSignUpOrLogin(Activity activity) {
        this.activity = activity;
        this.credentialManager = CredentialManager.create(activity);
    }

    public void startSignIn(View rootView) {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setServerClientId(Constants.SERVER_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false) // Prevents auto-select crashes on initial attempt
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                activity,
                request,
                null,
                ContextCompat.getMainExecutor(activity),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        Credential credential = result.getCredential();

                        if (credential instanceof CustomCredential &&
                                credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                            try {
                                GoogleIdTokenCredential tokenCredential =
                                        GoogleIdTokenCredential.createFrom(credential.getData());
                                signInToFirebase(tokenCredential.getIdToken(), rootView);
                            } catch (Exception e) {
                                if (rootView != null) {
                                    Snackbar.make(rootView, "Failed to parse Google credentials", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        if (rootView != null) {
                            Snackbar.make(rootView, "Sign in error: " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void signInToFirebase(String idToken, View rootView) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storeGoogleUserData();
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        if (rootView != null) {
                            Snackbar.make(rootView, "Authentication failed. Try again.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void storeGoogleUserData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) return; // Prevent NullPointerException if user isn't logged in

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(user.getUid());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> userData = new HashMap<>();

            if (!documentSnapshot.exists()) {
                // Document doesn't exist -> Write all initial Google data
                userData.put("email", user.getEmail());
                userData.put("profileImage", String.valueOf(user.getPhotoUrl()));
                userData.put("username", user.getDisplayName());

                userRef.set(userData)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "User profile created"))
                        .addOnFailureListener(e -> Log.e("Firestore", "Error creating user", e));

            } else {
                // Document exists -> Only supply missing fields
                if (!documentSnapshot.contains("email") || documentSnapshot.get("email") == null) {
                    userData.put("email", user.getEmail());
                }
                if (!documentSnapshot.contains("profileImage") || documentSnapshot.get("profileImage") == null) {
                    userData.put("profileImage", String.valueOf(user.getPhotoUrl()));
                }
                if (!documentSnapshot.contains("username") || documentSnapshot.get("username") == null) {
                    userData.put("username", user.getDisplayName());
                }

                // Only issue a network update if there are missing fields to fill
                if (!userData.isEmpty()) {
                    userRef.set(userData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Missing fields updated"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating missing fields", e));
                }
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch user document", e));
    }
}


//                         YOUR APP
//                            │
//                            │
//              ┌─────────────┴─────────────┐
//              │                           │
//              ▼                           ▼
//       Android OAuth ID             Web OAuth ID
//       799141...35tp                799141...t2sa
//              │                           │
//              │                           │
//       identifies app              used in
//       package + SHA-1             setServerClientId()
//                                          │
//                                          ▼
//                                  Google ID Token
//                                          │
//                                          ▼
//                                      Firebase