package com.rupp.movieexplorer.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rupp.movieexplorer.MovieDetailActivity;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.FireStoreCardAdapter;
import com.rupp.movieexplorer.adapter.LoadingAdapter;
import com.rupp.movieexplorer.adapter.MovieCardAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.model.FavoriteItem;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private RecyclerView favoriteRecycler;
    private List<FavoriteItem> items = new ArrayList<>();
    private FireStoreCardAdapter adapter;
    private FirebaseAuth auth;
    private FirebaseFirestore database;

    public FavoritesFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_favorites, container, false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        favoriteRecycler = view.findViewById(R.id.favoriteRecycler);
        adapter = new FireStoreCardAdapter(items, v ->{
            onOpenDetail(v);
        });
        favoriteRecycler.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        favoriteRecycler.setAdapter(new LoadingAdapter());

        fetch();

        return view;
    }

    public void onOpenDetail(FavoriteItem item){
        Intent intent = new Intent(requireContext(), MovieDetailActivity.class);
        intent.putExtra("id",item.getId());
        intent.putExtra("type",item.getType());
        startActivity(intent);
    }
    public void fetch(){
        String userUID = auth.getCurrentUser().getUid();
        database.collection("Users").document(userUID).collection("favorites").addSnapshotListener(((value, error) -> {
            favoriteRecycler.setAdapter(adapter);
            items.clear();
            for (DocumentSnapshot doc : value.getDocuments()){
                FavoriteItem item = doc.toObject(FavoriteItem.class);
                items.add(item);
            }
            adapter.notifyDataSetChanged();
        }));
    }

// method to reload data when back to this fragment
//    @Override
//    public void onResume(){
//        super.onResume();
//        fetch();
//    }
}