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
import com.rupp.movieexplorer.model.TVShow;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarouselTvAdapter extends RecyclerView.Adapter<CarouselTvAdapter.CarouselViewHolder> {
// ViewPager2 internally uses RecyclerView
//so it need to use 3 function onCreateViewHolder, onBindViewHolder, getItemCount
    private final List<TVShow> tvShowList;
    private TvCardAdapter.OnClickListener onClickListener;
    public CarouselTvAdapter(List<TVShow> tvShowList, TvCardAdapter.OnClickListener onClickListener) {  // getting all movies array object
        this.tvShowList = tvShowList;
        this.onClickListener = onClickListener;
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
        TVShow tvShow = tvShowList.get(position); // get each movie object base on arraylist position
        
        holder.name.setText(tvShow.getName()); // set that exact movie object title to holder.title
        
        // Using backdrop_path for carousel instead of poster_path for a wide landscape look
        String imageUrl = "https://image.tmdb.org/t/p/w780" + tvShow.getBackdropPath(); // get data from movie object and set it to imageUrl

        Picasso.get()
                .load(imageUrl)
                .into(holder.image); // set that imageUrl to holder.image
        holder.itemView.setOnClickListener(v->{
            if(onClickListener != null){
                onClickListener.OnClick(tvShow);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (tvShowList == null) ? 0 : tvShowList.size();  // return the size of arraylist
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
        TextView name;

        public CarouselViewHolder(@NonNull View itemView) { // this what we locate so we can set all of this base on data that we get from our arraylist inside each slide of carousel
            super(itemView); // so each slide has // holder.image
                                                  // holder.title
            image = itemView.findViewById(R.id.carouselImage);
            name = itemView.findViewById(R.id.carouselTitle);
        }
    }
}
