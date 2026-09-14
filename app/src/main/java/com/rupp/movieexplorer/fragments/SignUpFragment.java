package com.rupp.movieexplorer.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.SplashActivity;
import com.rupp.movieexplorer.helperClass.GoogleSignUpOrLogin;

import java.util.HashMap;
import java.util.Map;


public class SignUpFragment extends Fragment {

    private FirebaseAuth  auth;
    private FirebaseFirestore db;
    private TextView LoginText;
    private TextInputEditText EmailEditText, PasswordEditText, C_PasswordEditText, NameEditText;
    private TextInputLayout NameLayout, EmailLayout, PasswordLayout, C_PasswordLayout;
    private MaterialButton SignInBtn;
    private ProgressBar loading_bar;
    private View loading_overlay;
    private ImageView googleSignUpBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        loading_bar = view.findViewById(R.id.loading_progress);
        loading_overlay = view.findViewById(R.id.loading_overlay);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        LoginText = view.findViewById(R.id.LoginText);
        NameEditText = view.findViewById(R.id.signInUserNameEditText);
        EmailEditText = view.findViewById(R.id.signInEmailEditText);
        PasswordEditText = view.findViewById(R.id.signInPasswordEditText);
        C_PasswordEditText = view.findViewById(R.id.signInConfirmPasswordEditText);
        SignInBtn = view.findViewById(R.id.signInButton);

        NameLayout = view.findViewById(R.id.signInUserNameLayout);
        EmailLayout = view.findViewById(R.id.signInEmailLayout);
        PasswordLayout = view.findViewById(R.id.signInPasswordLayout);
        C_PasswordLayout = view.findViewById(R.id.signInConfirmPasswordLayout);

        googleSignUpBtn = view.findViewById(R.id.googleBtn);


        SignInBtn.setOnClickListener(v ->{
            hideKeyboard(v);
            NameLayout.setError(null);
            EmailLayout.setError(null);
            PasswordLayout.setError(null);
            C_PasswordLayout.setError(null);

            String name = NameEditText.getText().toString().trim();
            String email = EmailEditText.getText().toString().trim();
            String password = PasswordEditText.getText().toString().trim();
            String confirm_password = C_PasswordEditText.getText().toString().trim();

            boolean hasError = false;
            if(name.isEmpty()){
                NameLayout.setError("Name is required");
                hasError = true;
            }
            if(email.isEmpty()){
                EmailLayout.setError("Email is required");
                hasError = true;
            }
            if(password.isEmpty()){
                PasswordLayout.setError("Password is required");
                hasError = true;
            }
            if(confirm_password.isEmpty()){
                C_PasswordLayout.setError("Confirm password is required");
                hasError = true;
            }

            if(hasError) return;

            if(password.length() < 6){
                PasswordLayout.setError("Password too short (min 6)");
                return;
            }
            if(!confirm_password.equals(password)){
                C_PasswordLayout.setError("Passwords do not match");
                return;
            }
            SignInBtn.setEnabled(false);
            loading_bar.setVisibility(View.VISIBLE);
            loading_overlay.setVisibility(View.VISIBLE);

            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {

               if(task.isSuccessful()){
                   String userId = auth.getCurrentUser().getUid();
                   Map<String,Object> user = new HashMap<>();
                   user.put("username",name);
                   user.put("email",email);

                   db.collection("Users").document(userId).set(user).addOnSuccessListener(unused -> {
                       Snackbar.make(view, "Account Created", Snackbar.LENGTH_SHORT).show();
                       Intent intent = new Intent(getActivity(), SplashActivity.class);
                       startActivity(intent);
                       requireActivity().finish();

                   }).addOnFailureListener(e -> {
                     Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
                     SignInBtn.setEnabled(true);
                     loading_bar.setVisibility(View.GONE);
                     loading_overlay.setVisibility(View.GONE);
                   });
               } else {
                   Snackbar.make(view, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                   SignInBtn.setEnabled(true);
                   loading_bar.setVisibility(View.GONE);
                   loading_overlay.setVisibility(View.GONE);
               }
            });

        });

        LoginText.setOnClickListener(v ->{
            Fragment LoginFragment = new LoginFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.AuthFragmentContainer,LoginFragment).commit();

        });

        googleSignUpBtn.setOnClickListener( v ->{
            GoogleSignUpOrLogin googleSignUpOrLogin = new GoogleSignUpOrLogin(requireActivity());
            googleSignUpOrLogin.startSignIn(v);
        });

        return view;
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}