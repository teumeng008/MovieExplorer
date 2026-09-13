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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.rupp.movieexplorer.MovieDetailActivity;
import com.rupp.movieexplorer.PersonDetailActivity;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.FavPersonAdapter;
import com.rupp.movieexplorer.model.FavoritePerson;
import com.rupp.movieexplorer.model.PersonInfo;
import com.rupp.movieexplorer.viewModel.FavoritePersonViewModel;

import java.util.ArrayList;
import java.util.List;

public class FavoritePersonFragment extends Fragment {

    private FavoritePersonViewModel mViewModel;

    private RecyclerView FavPersonRecycler;
    private List<FavoritePerson> personList;
    private FavPersonAdapter favPersonAdapter;

    public FavoritePersonFragment () {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FavPersonRecycler = view.findViewById(R.id.FavPersonRecycler);
        personList = new ArrayList<>();
        favPersonAdapter = new FavPersonAdapter(personList, this:: openDetail);
        FavPersonRecycler.setAdapter(favPersonAdapter);
        FavPersonRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        mViewModel = new ViewModelProvider(this).get(FavoritePersonViewModel.class);


        mViewModel.getFavoritePersonMutableLiveData().observe(getViewLifecycleOwner(), items ->{
            if (items != null) {
                favPersonAdapter.updateList(items);
            }
        });

        mViewModel.loadFavoritePerson();
    }

    private void openDetail(FavoritePerson favoritePerson){
        Intent intent = new Intent(requireContext(), PersonDetailActivity.class);
        intent.putExtra("Person_id", favoritePerson.getId());
        startActivity(intent);
    }

}