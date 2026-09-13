package com.rupp.movieexplorer.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rupp.movieexplorer.fragments.FavoritePersonFragment;
import com.rupp.movieexplorer.fragments.FavoritesFragment;
import com.rupp.movieexplorer.fragments.SavedFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter (FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }

    public ViewPagerAdapter (@NonNull Fragment fragment){
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new SavedFragment();
            case 1:
                return new FavoritesFragment();
            case 2:
                return new FavoritePersonFragment();
            default:
                return new SavedFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
