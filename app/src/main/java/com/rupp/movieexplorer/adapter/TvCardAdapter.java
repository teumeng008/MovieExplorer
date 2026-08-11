package com.rupp.movieexplorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.model.TVShow;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TvCardAdapter extends RecyclerView.Adapter<TvCardAdapter.ViewHolder> {
    private  List<TVShow> tvShowList;
    private  OnClickListener onClickListener;

    public interface OnClickListener{
        void OnClick(TVShow tvShow);
    }
    public  TvCardAdapter(List<TVShow> tvShowList,OnClickListener onClickListener){
        this.tvShowList = tvShowList;
        this.onClickListener = onClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            TVShow tvShow = tvShowList.get(position);
            holder.name.setText(tvShow.getName());
            holder.rating.setText("⭐" + String.format("%.2f",tvShow.getVoteAverage()));
            String date = tvShow.getFirstAirDate();
            try {
                SimpleDateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputDate = new SimpleDateFormat("dd MMM yyyy",Locale.getDefault());
                Date parseDate = inputDate.parse(date);
                holder.date.setText(outputDate.format(parseDate));
            }catch (Exception e){
                holder.date.setText(date);
            }
            String ImageURL = "https://image.tmdb.org/t/p/w200" + tvShow.getPosterPath();
            Picasso.get().load(ImageURL).into(holder.poster);

            holder.itemView.setOnClickListener(v ->{
                if(onClickListener != null){
                    onClickListener.OnClick(tvShow);
                }
            });
    }

    @Override
    public int getItemCount() {
        return (tvShowList == null) ? 0 : tvShowList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView poster;
        TextView date,name,rating;
        public ViewHolder(View view){
            super(view);
            poster = view.findViewById(R.id.movieImageCard);
            date = view.findViewById(R.id.movieDateCard);
            name = view.findViewById(R.id.movieNameCard);
            rating = view.findViewById(R.id.movieRatingCard);
        }
    }
}
