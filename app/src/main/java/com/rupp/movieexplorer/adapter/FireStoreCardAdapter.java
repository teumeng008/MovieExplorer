package com.rupp.movieexplorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.model.FavoriteItem;
import com.rupp.movieexplorer.model.Movie;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class FireStoreCardAdapter extends RecyclerView.Adapter<FireStoreCardAdapter.ViewHolder> {
     private List<FavoriteItem> List;
     private ClickListener clickListener;

    public interface ClickListener {
        void  onClick(FavoriteItem item);
    }
    //Constructor
    public FireStoreCardAdapter(List<FavoriteItem> List, ClickListener clickListener){
        this.List = List;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FireStoreCardAdapter.ViewHolder holder, int position) {
        FavoriteItem item = List.get(position);
        String imageURL = "https://image.tmdb.org/t/p/w200" + item.getPosterPath();
        Picasso.get()
                .load(imageURL)
                .error(R.drawable.coming_soon)
                .into(holder.poster);

        holder.movie_name.setText(item.getTitle());
        holder.rating.setText("⭐ " +String.format(Locale.getDefault(),"%.2f", item.getRating()));

        holder.itemView.setOnClickListener(v ->{
            if(clickListener != null){
                clickListener.onClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return List.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView poster;
        TextView movie_name;
        TextView rating;
        public ViewHolder(View view){
            super(view);
            poster = view.findViewById(R.id.moviePoster);
            movie_name = view.findViewById(R.id.movieTitle);
            rating = view.findViewById(R.id.movieRating);
        }
    }
}