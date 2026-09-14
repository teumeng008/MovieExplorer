package com.rupp.movieexplorer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.rupp.movieexplorer.fragments.LoginFragment;

public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstantState){
        super.onCreate(savedInstantState);
        setContentView(R.layout.activity_auth);

        Fragment fragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.AuthFragmentContainer,fragment).commit();
    }
}
