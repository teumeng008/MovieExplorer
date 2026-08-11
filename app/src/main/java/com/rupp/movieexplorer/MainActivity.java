package com.rupp.movieexplorer;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rupp.movieexplorer.fragments.DiscoverFragment;
import com.rupp.movieexplorer.fragments.FavoritesFragment;
import com.rupp.movieexplorer.fragments.HomeFragment;
import com.rupp.movieexplorer.fragments.MovieListFragment;
import com.rupp.movieexplorer.fragments.ProfileFragment;
import com.rupp.movieexplorer.fragments.SearchFragment;
import com.rupp.movieexplorer.fragments.WatchlistFragment;
import com.rupp.movieexplorer.viewModel.NavViewModel;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends AppCompatActivity {
    private Fragment homeFragment = new HomeFragment();
//    private Fragment favoritesFragment = new FavoritesFragment();
    private Fragment watchlistFragment = new WatchlistFragment() ;
    private Fragment profileFragment = new ProfileFragment();

    private Fragment discoverFragment = new DiscoverFragment();
    private Fragment searchFragment = new SearchFragment();

    private NavViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. Initialize ViewModel to preserve navigation state across rotations
        viewModel = new ViewModelProvider(this).get(NavViewModel.class);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Handle window insets for Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.FullScreen), (v, insets) -> {
            boolean isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
            // Hide navbar when keyboard is open to save space
            bottomNavigationView.setVisibility(isKeyboardVisible ? View.GONE : View.VISIBLE);
            return insets;
        });

        // 2. Setup Navigation Listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Save the selection in ViewModel so it survives rotation
            viewModel.setSelectedNavId(item.getItemId());

            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.menu_home) {
                selectedFragment = homeFragment;
            } else if (item.getItemId() == R.id.menu_watchlist) {
                selectedFragment = watchlistFragment;
            } else if (item.getItemId() == R.id.menu_profile) {
                selectedFragment = profileFragment;
            } else if (item.getItemId() == R.id.menu_movie) {
                selectedFragment = discoverFragment;
            } else if (item.getItemId() == R.id.menu_search) {
                selectedFragment = searchFragment;
            }

            // Perform transaction with a simple fade animation for dynamic feel
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // 3. Restore selection after rotation OR set default on first launch
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_home);
        } else {
            // This will trigger the listener above and restore the correct fragment
            bottomNavigationView.setSelectedItemId(viewModel.getSelectedNavId());
        }
    }
    /**
     * Helper to apply a dynamic scale effect when a view is touched.
     */
    private void applyTouchScale(View view) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
            }
            return false;
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        View view = getCurrentFocus();

        if (view instanceof EditText) {

            Rect outRect = new Rect();
            view.getGlobalVisibleRect(outRect);

            if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {

                view.clearFocus();

                InputMethodManager imm =
                        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        return super.dispatchTouchEvent(ev);
    }
}
