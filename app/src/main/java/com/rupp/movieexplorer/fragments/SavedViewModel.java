package com.rupp.movieexplorer.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rupp.movieexplorer.model.FavoriteItem;

import java.util.ArrayList;
import java.util.List;

public class SavedViewModel extends ViewModel {
    private final MutableLiveData<List<FavoriteItem>> itemsLiveData = new MutableLiveData<>();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public LiveData<List<FavoriteItem>> getWatchlist(){
        return itemsLiveData;
    }
    public void loadWatchlist(){
        String useUID = auth.getCurrentUser().getUid();
        database.collection("Users").document(useUID).collection("watchlist").addSnapshotListener(((value, error) -> {
            List<FavoriteItem> items = new ArrayList<>();
            for(DocumentSnapshot item : value.getDocuments()){
                items.add(item.toObject(FavoriteItem.class));
            }
            itemsLiveData.setValue(items);
        }));
    }
}