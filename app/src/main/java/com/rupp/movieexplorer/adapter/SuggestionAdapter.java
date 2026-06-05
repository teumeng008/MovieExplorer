package com.rupp.movieexplorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.model.MediaItem;
import com.rupp.movieexplorer.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private List<MediaItem> mediaList;
    private onMediaClickListener onMediaClickListener;
    public interface onMediaClickListener{
        void onMouseClick(MediaItem item);
    }
    public SuggestionAdapter(List<MediaItem> mediaList,onMediaClickListener onMediaClickListener){
        this.onMediaClickListener = onMediaClickListener;
        this.mediaList = mediaList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_movie,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mediaList == null || mediaList.isEmpty()){
            holder.NoResults.setVisibility(View.VISIBLE);
            holder.name.setVisibility(View.GONE);
            holder.poster.setVisibility(View.GONE);
            holder.rating.setVisibility(View.GONE);
            return;
        }
        holder.NoResults.setVisibility(View.GONE);
       MediaItem item = mediaList.get(position);
       String imageURL = "https://image.tmdb.org/t/p/w500" + item.getPosterPath();
        Picasso.get().load(imageURL).placeholder(R.drawable.movie_list).into(holder.poster);
        holder.name.setText(item.getTitle());
        holder.rating.setText("⭐ " + String.format("%.2f", item.getVoteAverage()));

        holder.itemView.setOnClickListener( v -> {
            if(onMediaClickListener != null){
                onMediaClickListener.onMouseClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mediaList == null || mediaList.isEmpty()){
            return 1;
        }
        return mediaList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView poster;
        TextView name;
        TextView rating;
        TextView NoResults;
        public ViewHolder(View view){
            super(view);
            poster = view.findViewById(R.id.S_moviePoster);
            name = view.findViewById(R.id.S_movieTitle);
            rating = view.findViewById(R.id.S_movieRating);
            NoResults = view.findViewById(R.id.noResultsText);
        }
    }
}
