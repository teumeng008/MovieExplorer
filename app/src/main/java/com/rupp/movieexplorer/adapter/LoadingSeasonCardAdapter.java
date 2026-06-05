package com.rupp.movieexplorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;

public class LoadingSeasonCardAdapter extends RecyclerView.Adapter<LoadingSeasonCardAdapter.ViewHolder> {
@NonNull
@Override
public LoadingSeasonCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tv_show_card_loading,parent,false);
    return new ViewHolder(view);
}

@Override
public void onBindViewHolder(@NonNull LoadingSeasonCardAdapter.ViewHolder holder, int position) {

}

@Override
public int getItemCount() {
    return 1;
}

public static class ViewHolder extends RecyclerView.ViewHolder{
    public ViewHolder(View view){
        super(view);
    }
}
}
