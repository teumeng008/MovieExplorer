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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.SplashActivity;
import com.rupp.movieexplorer.helperClass.GoogleSignUpOrLogin;

public class LoginFragment extends Fragment {
    TextView SignInText;
    TextInputLayout emailLayout, passwordLayout;
    TextInputEditText emailEditText;
    TextInputEditText passwordEditText;
    MaterialButton loginBtn;

    FirebaseAuth auth;
    ProgressBar progressBar;
    View overlay;

    private ImageView googleLoginBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        auth = FirebaseAuth.getInstance();

        SignInText = view.findViewById(R.id.SignInText);
        emailLayout = view.findViewById(R.id.loginEmailLayout);
        passwordLayout = view.findViewById(R.id.loginPasswordLayout);
        emailEditText = view.findViewById(R.id.loginEmailEditText);
        passwordEditText = view.findViewById(R.id.loginPasswordEditText);
        loginBtn = view.findViewById(R.id.loginButton);
        googleLoginBtn = view.findViewById(R.id.googleBtn);

        progressBar = view.findViewById(R.id.loading_progress2);
        overlay = view.findViewById(R.id.loading_overlay2);

        loginBtn.setOnClickListener(v ->{
            hideKeyboard(v);
            emailLayout.setError(null);
            passwordLayout.setError(null);

            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            boolean hasError = false;
            if(email.isEmpty()){
                emailLayout.setError("Email is required");
                hasError = true;
            }
            if(password.isEmpty()){
                passwordLayout.setError("Password is required");
                hasError = true;
            }

            if(hasError) return;

            progressBar.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Snackbar.make(view, "Login Success", Snackbar.LENGTH_SHORT).show();

                    requireActivity().getSharedPreferences("user",requireActivity().MODE_PRIVATE).edit().putBoolean("loggedIn",true).apply();

                    Intent intent = new Intent(getActivity(), SplashActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }else {
                    String errorMsg = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                    Snackbar.make(view, errorMsg, Snackbar.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    overlay.setVisibility(View.GONE);
                }
            });
        });

        SignInText.setOnClickListener(v ->{
            Fragment SignInFragment = new SignUpFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.AuthFragmentContainer,SignInFragment).commit();
        });

        googleLoginBtn.setOnClickListener(v->{
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