package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.method.Touch;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rupp.movieexplorer.MainActivity;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.SplashActivity;

import java.util.HashMap;
import java.util.Map;


public class SignInFragment extends Fragment {

    private FirebaseAuth  auth;
    private FirebaseFirestore db;
    private TextView LoginText;
    private TextInputEditText EmailEditText, PasswordEditText, C_PasswordEditText, NameEditText;
    private MaterialButton SignInBtn;
    private ProgressBar loading_bar;
    private View loading_overlay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

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



        SignInBtn.setOnClickListener(v ->{
            String name = NameEditText.getText().toString().trim();
            String email = EmailEditText.getText().toString().trim();
            String password = PasswordEditText.getText().toString().trim();
            String confirm_password = C_PasswordEditText.getText().toString().trim();

            if(name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm_password.isEmpty()){
                Toast.makeText(getContext(), "Fill all Fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if(password.length() < 6){
                Toast.makeText(getContext(),"Password too short (min 6)", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!confirm_password.equals(password)){
                Toast.makeText(getContext(),"Password not matching", Toast.LENGTH_SHORT).show();
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
                      Toast.makeText(getContext(),"Account Created",Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(getActivity(), SplashActivity.class);
                       startActivity(intent);
                       requireActivity().finish();

                   }).addOnFailureListener(e -> {
                     Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                     SignInBtn.setEnabled(true);
                   });
               } else {
                   Toast.makeText(getContext(),
                           task.getException().getMessage(),
                           Toast.LENGTH_SHORT).show();
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

        return view;
    }
}