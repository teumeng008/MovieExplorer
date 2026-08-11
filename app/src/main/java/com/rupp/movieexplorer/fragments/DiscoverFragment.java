package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.MovieOrTvAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.FilterData;
import com.rupp.movieexplorer.viewModel.FilterViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DiscoverFragment extends Fragment {

    private FilterViewModel filterViewModel;

    private MovieOrTvAdapter movieOrTvAdapter;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private MaterialButton filterBtn;
    private DrawerLayout drawerLayout;
    private EditText yearValueText;
    private Slider slider;
    private MaterialSwitch genreSwitch, yearSwitch, ratingSwitch;
    private ChipGroup chipGroup, ratingChipGroup;
    private TabLayoutMediator tabLayoutMediator;
    private MaterialButton resetBtn, applyBtn;
    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        initViews(view);
        setupViewPagerAndTabs();
        setupFilterListeners();
        dynamicYearText();
        setupSwitches(view);
        filter();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        String currentType = (viewPager2.getCurrentItem() == 1) ? "tv" : "movie";
        setUpDynamicChipItem(currentType);
    }

    private void initViews(View view) {
        filterBtn = view.findViewById(R.id.filterBtn);
        chipGroup = view.findViewById(R.id.genresGroup);
        ratingChipGroup = view.findViewById(R.id.ratingChipGroup);
        slider = view.findViewById(R.id.yearSlider);
        yearValueText = view.findViewById(R.id.yearValueText);

        genreSwitch = view.findViewById(R.id.genreSwitch);
        yearSwitch = view.findViewById(R.id.yearSwitch);
        ratingSwitch = view.findViewById(R.id.ratingSwitch);

        drawerLayout = view.findViewById(R.id.drawerLayout);
        tabLayout = view.findViewById(R.id.discoverTabLayout);
        viewPager2 = view.findViewById(R.id.discoverViewPager2);

        resetBtn = view.findViewById(R.id.resetBtn);
        applyBtn =view.findViewById(R.id.applyBtn);
    }

    private void setupViewPagerAndTabs() {
        if(viewPager2.getAdapter() == null){
            movieOrTvAdapter = new MovieOrTvAdapter(this);
            viewPager2.setAdapter(movieOrTvAdapter);
        }

        viewPager2.setSaveEnabled(false);

        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Movies");
                    break;
                case 1:
                    tab.setText("TV Shows");
                    break;
            }
        });
        tabLayoutMediator.attach();
    }

    private void setupFilterListeners() {
        filterBtn.setOnClickListener(v -> {
            // Determine type dynamically based on current ViewPager position
            String currentType = (viewPager2.getCurrentItem() == 1) ? "tv" : "movie";
            setUpDynamicChipItem(currentType);
            openFilter();
        });
    }

    private void openFilter() {
        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        }
    }

    private void setupSwitches(View view) {
        // Genre Section Toggle
        View genreGroup = view.findViewById(R.id.genresGroup);
        genreSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            genreGroup.setEnabled(isChecked);
            genreGroup.setAlpha(isChecked ? 1.0f : 0.5f);
            if (genreGroup instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) genreGroup;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    vg.getChildAt(i).setEnabled(isChecked);
                }
            }
        });

        // Year Section Toggle
        View yearContainer = view.findViewById(R.id.yearSectionContainer);
        yearSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            slider.setEnabled(isChecked);
            yearValueText.setEnabled(isChecked);
            yearContainer.setAlpha(isChecked ? 1.0f : 0.5f);
        });

        // Rating Section Toggle
        View ratingGroup = view.findViewById(R.id.ratingChipGroup);
        ratingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ratingGroup.setEnabled(isChecked);
            ratingGroup.setAlpha(isChecked ? 1.0f : 0.5f);
            if (ratingGroup instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) ratingGroup;
                for (int k = 0; k < vg.getChildCount(); k++) {
                    vg.getChildAt(k).setEnabled(isChecked);
                }
            }
        });
    }

    private void dynamicYearText() {
        slider.addOnChangeListener((sliderInstance, value, fromUser) -> {
            if (fromUser) {
                yearValueText.setText(String.valueOf((int) value));
            }
        });

        yearValueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int year = Integer.parseInt(s.toString());
                    if (year >= slider.getValueFrom() && year <= slider.getValueTo()) {
                        slider.setValue(year);
                    }
                } catch (NumberFormatException ignored) {}
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setUpDynamicChipItem(String type) {
        chipGroup.removeAllViews();

        // LinkedHashMap preserves insertion order (Alphabetical)
        Map<String, Integer> genres = new LinkedHashMap<>();

        if ("tv".equals(type)) {
            genres.put("Action & Adventure", 10759);
            genres.put("Animation", 16);
            genres.put("Comedy", 35);
            genres.put("Crime", 80);
            genres.put("Documentary", 99);
            genres.put("Drama", 18);
            genres.put("Family", 10751);
            genres.put("Kids", 10762);
            genres.put("Mystery", 9648);
            genres.put("News", 10763);
            genres.put("Reality", 10764);
            genres.put("Sci-Fi & Fantasy", 10765);
            genres.put("Soap", 10766);
            genres.put("Talk", 10767);
            genres.put("War & Politics", 10768);
            genres.put("Western", 37);
        } else {
            genres.put("Action", 28);
            genres.put("Adventure", 12);
            genres.put("Animation", 16);
            genres.put("Comedy", 35);
            genres.put("Crime", 80);
            genres.put("Documentary", 99);
            genres.put("Drama", 18);
            genres.put("Family", 10751);
            genres.put("Fantasy", 14);
            genres.put("History", 36);
            genres.put("Horror", 27);
            genres.put("Music", 10402);
            genres.put("Mystery", 9648);
            genres.put("Romance", 10749);
            genres.put("Science Fiction", 878);
            genres.put("TV Movie", 10770);
            genres.put("Thriller", 53);
            genres.put("War", 10752);
            genres.put("Western", 37);
        }

        FilterData data = "tv".equals(type) ? filterViewModel.getTvFilters().getValue() : filterViewModel.getMovieFilters().getValue();
        String savedGenres = data != null ? data.getGenres() : null;
        List<String> savedGenresList = savedGenres != null ? Arrays.asList(savedGenres.split(",")) : new ArrayList<>();

        for (Map.Entry<String, Integer> entry : genres.entrySet()) {
            Chip chip = new Chip(requireContext());
            chip.setText(entry.getKey());
            chip.setTag(entry.getValue());
            chip.setCheckable(true);
            chip.setClickable(true);

            if(savedGenresList.contains(entry.getValue().toString())){
                chip.setChecked(true);
            }

            chipGroup.addView(chip);
        }

    }

    private void filter() {

        resetBtn.setOnClickListener(v -> {
            chipGroup.clearCheck();
            ratingChipGroup.clearCheck();
            genreSwitch.setChecked(false);
            yearSwitch.setChecked(false);
            ratingSwitch.setChecked(false);
        });

        applyBtn.setOnClickListener(v -> {
            String genres = null;
            if (genreSwitch.isChecked()) {
                StringBuilder genresBuilder = new StringBuilder();
                List<Integer> checkedIds = chipGroup.getCheckedChipIds();
                for (Integer id : checkedIds) {
                    Chip chip = chipGroup.findViewById(id);
                    if (chip != null && chip.getTag() != null) {
                        if (genresBuilder.length() > 0) genresBuilder.append(",");
                        genresBuilder.append(chip.getTag().toString());
                    }
                }
                genres = genresBuilder.toString();
            }

            Integer year = null;
            if (yearSwitch.isChecked()) {
                year = (int) slider.getValue();
            }

            Double rating = null;
            if (ratingSwitch.isChecked()) {
                int checkedRatingId = ratingChipGroup.getCheckedChipId();
                if (checkedRatingId != View.NO_ID) { // View.NO_ID mean nothing is selected, unassigned or invalid
                    Chip chip = ratingChipGroup.findViewById(checkedRatingId);
                    if (chip != null && chip.getTag() != null) {
                        try {
                            rating = Double.parseDouble(chip.getTag().toString());
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }

            if(viewPager2.getCurrentItem() == 1){
                filterViewModel.setTvFilters(genres, year, rating);
            }else {
                filterViewModel.setMovieFilters(genres, year, rating);
            }

           if(drawerLayout != null){
               drawerLayout.closeDrawer(GravityCompat.END);
           }
        });
    }

//    /**
//     * Process Step-by-Step: How filtering works across components.
//     *
//     * STEP 1: UI Interaction (DiscoverFragment)
//     * The user selects genres, year, or rating in the filter drawer and clicks "Apply".
//     *
//     * STEP 2: Identifying the Target (DiscoverFragment)
//     * We determine if the user is currently looking at the "Movies" tab or "TV Shows" tab
//     * using viewPager2.getCurrentItem().
//     */
//    private void filterMedia(String type, String genres, Integer year, Double rating) {
//        // STEP 3: Finding the Active Fragment (DiscoverFragment -> Child Fragments)
//        // ViewPager2 internally tags fragments as "f0", "f1", etc. We use getChildFragmentManager()
//        // to find the fragment instance that is currently active in the ViewPager.
//        Fragment currentFragment = getChildFragmentManager().findFragmentByTag("f" + viewPager2.getCurrentItem());
//
//        if ("tv".equals(type)) {
//            // STEP 4: Communication (DiscoverFragment -> TvShowListFragment)
//            // If the current tab is TV, we cast to TvShowListFragment and call its updateFilters method.
//            if (currentFragment instanceof TvShowListFragment) {
//                ((TvShowListFragment) currentFragment).updateFilters(genres, year, rating);
//            }
//        } else {
//            // STEP 4: Communication (DiscoverFragment -> MovieListFragment)
//            // If the current tab is Movie, we cast to MovieListFragment and call its updateFilters method.
//            if (currentFragment instanceof MovieListFragment) {
//                ((MovieListFragment) currentFragment).updateFilters(genres, year, rating);
//            }
//        }
//
//        // STEP 8: Close UI (DiscoverFragment)
//        // Close the drawer after applying filters.
//        drawerLayout.closeDrawer(GravityCompat.END);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (tabLayoutMediator != null) {
            tabLayoutMediator.detach();
            tabLayoutMediator = null;
        }
        viewPager2.setAdapter(null);
    }
}