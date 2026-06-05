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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MovieCardAdapter extends RecyclerView.Adapter<MovieCardAdapter.ViewHolder> {
     private List<Movie> movieList = new ArrayList<>();
     private OnMovieClickListener movieClickListener;

    public interface OnMovieClickListener {
        void  onMovieClick(Movie movie);
    }
    //Constructor
    public MovieCardAdapter(List<Movie> movieList, OnMovieClickListener movieClickListener){
        this.movieList = movieList;
        this.movieClickListener = movieClickListener;
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
                .error(R.drawable.coming_soon)
                .into(holder.poster);
        String date = movie.getRelease_date();
        try{
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy",Locale.getDefault());
            Date parseDate = inputFormat.parse(date);
            holder.date.setText(outputFormat.format(parseDate));
        }catch (Exception e){
            holder.date.setText(date);
        }
        holder.movie_name.setText(movie.getTitle());
        holder.rating.setText("⭐ " +String.format(Locale.getDefault(),"%.2f", movie.getVoteAverage()));

        holder.itemView.setOnClickListener(v ->{
            if(movieClickListener != null){
                movieClickListener.onMovieClick(movie);
            }
        });
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