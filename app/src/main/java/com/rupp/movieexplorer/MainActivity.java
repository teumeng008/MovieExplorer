package com.rupp.movieexplorer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rupp.movieexplorer.fragments.FavoritesFragment;
import com.rupp.movieexplorer.fragments.HomeFragment;
import com.rupp.movieexplorer.fragments.MovieListFragment;
import com.rupp.movieexplorer.fragments.ProfileFragment;
import com.rupp.movieexplorer.fragments.WatchlistFragment;

public class MainActivity extends AppCompatActivity {
    private Fragment homeFragment = new HomeFragment();
    private Fragment favoritesFragment = new FavoritesFragment();
    private Fragment watchlistFragment = new WatchlistFragment() ;
    private Fragment profileFragment = new ProfileFragment();

    private Fragment movieListFragment = new MovieListFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Fragment fragment = homeFragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
        Fragment selectedFragment = null;
        if(item.getItemId() == R.id.menu_home) {
            selectedFragment = homeFragment;
        }else if(item.getItemId() == R.id.menu_favorites){
            selectedFragment = favoritesFragment;
        }else if(item.getItemId() == R.id.menu_watchlist) {
            selectedFragment = watchlistFragment;
        }else if(item.getItemId() == R.id.menu_profile) {
            selectedFragment = profileFragment;
        }else if(item.getItemId() == R.id.menu_movie) {
            selectedFragment = movieListFragment;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
        });
        }
    }
