package com.rupp.movieexplorer.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.ViewPagerAdapter;

public class WatchlistFragment extends Fragment {
    public WatchlistFragment(){

    }

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_watchlist,container,false);

        tabLayout = view.findViewById(R.id.WatchListTabLayout);
        viewPager2 = view.findViewById(R.id.WatchListViewPager2);
        viewPagerAdapter = new ViewPagerAdapter(getActivity());
        viewPager2.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout,viewPager2,(tab,position) -> {
            switch (position){
                case 0:
                    tab.setText("Watch Later");
                    break;
                case 1:
                    tab.setText("Favorite");
                    break;
            }
        }).attach();
        return view;
    }
}