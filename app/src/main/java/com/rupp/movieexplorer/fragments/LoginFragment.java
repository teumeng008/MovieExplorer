package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.SplashActivity;

import java.time.Instant;

public class LoginFragment extends Fragment {
    TextView SignInText;
    TextInputLayout passwordLayout;
    TextInputEditText emailEditText;
    TextInputEditText passwordEditText;
    MaterialButton loginBtn;

    FirebaseAuth auth;
    ProgressBar progressBar;
    View overlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        auth = FirebaseAuth.getInstance();

        SignInText = view.findViewById(R.id.SignInText);
        passwordLayout = view.findViewById(R.id.loginPasswordLayout);
        emailEditText = view.findViewById(R.id.loginEmailEditText);
        passwordEditText = view.findViewById(R.id.loginPasswordEditText);
        loginBtn = view.findViewById(R.id.loginButton);

        progressBar = view.findViewById(R.id.loading_progress2);
        overlay = view.findViewById(R.id.loading_overlay2);

        loginBtn.setOnClickListener(v ->{
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(getContext(),"Fill all fields",Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(),"Login Success",Toast.LENGTH_SHORT).show();

                    requireActivity().getSharedPreferences("user",requireActivity().MODE_PRIVATE).edit().putBoolean("loggedIn",true).apply();

                    Intent intent = new Intent(getActivity(), SplashActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }else {
                    Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    overlay.setVisibility(View.GONE);
                }
            });
        });

        SignInText.setOnClickListener(v ->{
            Fragment SignInFragment = new SignInFragment();
            requireActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.AuthFragmentContainer,SignInFragment).commit();
        });
        return view;
    }
}