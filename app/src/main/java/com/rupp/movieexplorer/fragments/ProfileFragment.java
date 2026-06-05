package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.SplashActivity;

public class ProfileFragment extends Fragment {
    public ProfileFragment(){

    }

    private MaterialButton logoutBtn,logoutBtnFinal,Cancel;
    private LinearLayout logoutLayout;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        auth = FirebaseAuth.getInstance();

        logoutBtn = view.findViewById(R.id.LogOutBtn);
        logoutBtnFinal = view.findViewById(R.id.LogOutBtnFinal);
        Cancel = view.findViewById(R.id.CancelBtn);
        logoutLayout = view.findViewById(R.id.LogOutPromph);

        logoutBtn.setOnClickListener(v ->{
            logoutLayout.setVisibility(View.VISIBLE);
        });
        logoutBtnFinal.setOnClickListener(v ->{
            auth.signOut();
            Intent intent = new Intent(requireActivity(),SplashActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        Cancel.setOnClickListener(v ->{
            logoutLayout.setVisibility(View.GONE);
        });

        return view;
    }
}