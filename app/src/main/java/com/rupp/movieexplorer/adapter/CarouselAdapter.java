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

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {
// ViewPager2 internally uses RecyclerView
//so it need to use 3 function onCreateViewHolder, onBindViewHolder, getItemCount
    private final List<Movie> movieList;
    private MovieCardAdapter.OnMovieClickListener onMovieClickListener;
    public CarouselAdapter(List<Movie> movieList, MovieCardAdapter.OnMovieClickListener onMovieClickListener) {  // getting all movies array object
        this.movieList = movieList;
        this.onMovieClickListener = onMovieClickListener;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carousel, parent, false); // this load the layout of item_carousel.xml for one slide of carousel(think of it like: Create empty slide template)
        return new CarouselViewHolder(view); // pass the that slide template to CarouselViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        Movie movie = movieList.get(position); // get each movie object base on arraylist position
        
        holder.title.setText(movie.getTitle()); // set that exact movie object title to holder.title
        
        // Using backdrop_path for carousel instead of poster_path for a wide landscape look
        String imageUrl = "https://image.tmdb.org/t/p/w780" + movie.getBackdropPath(); // get data from movie object and set it to imageUrl

        Picasso.get()
                .load(imageUrl)
                .into(holder.image); // set that imageUrl to holder.image
        holder.itemView.setOnClickListener(v->{
            if(onMovieClickListener != null){
                onMovieClickListener.onMovieClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();  // return the size of arraylist
                                    //This tells ViewPager:
                                    //
                                    //How many slides exist?
                                    //
                                    //Example:
                                    //
                                    //movieList.size() = 5
                                    //
                                    //So ViewPager shows 5 pages.
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;

        public CarouselViewHolder(@NonNull View itemView) { // this what we locate so we can set all of this base on data that we get from our arraylist inside each slide of carousel
            super(itemView); // so each slide has // holder.image
                                                  // holder.title
            image = itemView.findViewById(R.id.carouselImage);
            title = itemView.findViewById(R.id.carouselTitle);
        }
    }
}
