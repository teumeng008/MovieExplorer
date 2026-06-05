package com.rupp.movieexplorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.model.Season;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SeasonCardAdapter extends RecyclerView.Adapter<SeasonCardAdapter.ViewHolder> {
    List<Season> seasonList;

    public SeasonCardAdapter(List<Season> seasonList){
        this.seasonList = seasonList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tv_show_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       Season season = seasonList.get(position);
       holder.name.setText(season.getName());
       holder.ep_count.setText(String.valueOf(season.getEpisode_count()) + " episode");
       holder.rating.setText("⭐" + String.format("%.2f",season.getVote_average()));
       holder.overview.setText(season.getOverview());
       holder.release_date.setText(season.getAir_date());
       String ImageURL = "https://image.tmdb.org/t/p/w500" + season.getPoster_path();
        Picasso.get().load(ImageURL).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return seasonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView poster;
        TextView name,overview,rating,release_date,ep_count;

        public ViewHolder(View view){
            super(view);
            name = view.findViewById(R.id.name);
            overview = view.findViewById(R.id.overview);
            rating = view.findViewById(R.id.rating);
            release_date = view.findViewById(R.id.date);
            ep_count = view.findViewById(R.id.ep_count);
            poster = view.findViewById(R.id.SeasonImage);

        }
    }

}
