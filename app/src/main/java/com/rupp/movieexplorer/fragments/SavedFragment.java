package com.rupp.movieexplorer.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rupp.movieexplorer.MovieDetailActivity;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.FireStoreCardAdapter;
import com.rupp.movieexplorer.adapter.LoadingAdapter;
import com.rupp.movieexplorer.model.FavoriteItem;
import com.rupp.movieexplorer.viewModel.SavedViewModel;

import java.util.ArrayList;

public class SavedFragment extends Fragment {

    private SavedViewModel ViewModel;
    private RecyclerView watchlistRecycler;
    private FireStoreCardAdapter adapter;

    public static SavedFragment newInstance() {
        return new SavedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        watchlistRecycler = view.findViewById(R.id.savedRecycler);
        adapter = new FireStoreCardAdapter(new ArrayList<>(),this::onOpenDetail);
        watchlistRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        watchlistRecycler.setAdapter(new LoadingAdapter());

        ViewModel = new ViewModelProvider(this).get(SavedViewModel.class);

        ViewModel.getWatchlist().observe(getViewLifecycleOwner(), items -> {
            adapter = new FireStoreCardAdapter(items, this::onOpenDetail);
            watchlistRecycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });

        ViewModel.loadWatchlist();

        return view;
    }

    public void onOpenDetail(FavoriteItem item){
        Intent intent = new Intent(requireContext(), MovieDetailActivity.class);
        intent.putExtra("id",item.getId());
        intent.putExtra("type",item.getType());
        startActivity(intent);
    }
}