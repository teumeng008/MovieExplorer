package com.rupp.movieexplorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieCardAdapter extends RecyclerView.Adapter<MovieCardAdapter.ViewHolder> {
    List<Movie> movieList = new ArrayList<>();
    //Constructor
    public MovieCardAdapter(List<Movie> movieList){
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCardAdapter.ViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        String imageURL = "https://image.tmdb.org/t/p/w200" + movie.getPosterPath();
        Picasso.get()
                .load(imageURL)
                .placeholder(R.drawable.movie_list)
                .error(R.drawable.movie_list)
                .into(holder.poster);
        holder.date.setText(movie.getRelease_date());
        holder.movie_name.setText(movie.getTitle());
        holder.rating.setText("⭐ " + movie.getVoteAverage());
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView poster;
        TextView date;
        TextView movie_name;
        TextView rating;
        public ViewHolder(View view){
            super(view);
            poster = view.findViewById(R.id.movieImageCard);
            date = view.findViewById(R.id.movieDateCard);
            movie_name = view.findViewById(R.id.movieNameCard);
            rating = view.findViewById(R.id.movieRatingCard);

        }
    }
}