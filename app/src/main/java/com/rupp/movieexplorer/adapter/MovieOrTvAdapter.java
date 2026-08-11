package com.rupp.movieexplorer.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rupp.movieexplorer.fragments.MovieListFragment;
import com.rupp.movieexplorer.fragments.TvShowListFragment;

public class MovieOrTvAdapter extends FragmentStateAdapter {
//    public MovieOrTvAdapter(@NonNull FragmentActivity fragmentActivity) {
//        super(fragmentActivity);
//    }

    public MovieOrTvAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MovieListFragment();
            case 1:
                return new TvShowListFragment();
            default:
                return new MovieListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
