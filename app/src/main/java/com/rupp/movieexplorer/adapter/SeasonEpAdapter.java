package com.rupp.movieexplorer.adapter;

import android.os.Bundle;
import android.text.Layout;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.model.Episode;
import com.rupp.movieexplorer.model.Video;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeasonEpAdapter extends RecyclerView.Adapter<SeasonEpAdapter.ViewHolder> {
    private List<Episode> episodes;
    private onEpClickListener Listener;
    private Set<Integer> expandedPositions = new HashSet<>();

    public interface onEpClickListener{
        void onEpClick(int epNum, int position);
    }
    public SeasonEpAdapter(List<Episode> episodes, onEpClickListener Listener){
        this.episodes = episodes;
        this.Listener = Listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ep_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Episode episode = episodes.get(position);

        boolean isExpanded = expandedPositions.contains(position);
        holder.FullDetailCtn.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.showBtn.setRotation(isExpanded ? 90 : 270);

        View.OnClickListener toggleListener =  v ->{
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            boolean currentlyExpanded = expandedPositions.contains(currentPosition);
            
            if (currentlyExpanded) {
                expandedPositions.remove(currentPosition);
            } else {
                expandedPositions.add(currentPosition);
            }

            ViewGroup parent = (ViewGroup) holder.itemView.getParent();
            if(parent != null){
                AutoTransition transition = new AutoTransition();
                transition.setDuration(100);
                TransitionManager.beginDelayedTransition(parent,transition);
            }
            
            boolean nextExpanded = !currentlyExpanded;
            holder.FullDetailCtn.setVisibility(nextExpanded ? View.VISIBLE : View.GONE);
            holder.showBtn.animate().rotation(nextExpanded ? 90 : 270).setDuration(200).start();

            // Trigger fetch with position if expanding
            if(nextExpanded && Listener != null){
                Listener.onEpClick(episode.getEpisode_number(), currentPosition);
            }
        };

        // Set up nested RecyclerView for clips (Move this outside the toggleListener)
        if (episode.getVideos() != null && !episode.getVideos().isEmpty()) {
            holder.clipTxt.setVisibility(View.VISIBLE);
            VideoCardAdapter videoAdapter = new VideoCardAdapter(episode.getVideos());
            holder.clipRecycler.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.clipRecycler.setAdapter(videoAdapter);
            holder.clipRecycler.setVisibility(View.VISIBLE);
        } else {
            holder.clipRecycler.setVisibility(View.GONE);
            holder.clipTxt.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(toggleListener);
        holder.showBtn.setOnClickListener(toggleListener);
        holder.ep_name.setText(episode.getName());
        holder.ep_num.setText(" " + episode.getEpisode_number());
        holder.ep_rating.setText(String.format("%.02f",episode.getVote_average()));
        holder.ep_air_date.setText(episode.getAir_date());

        if(episode.getOverview().isEmpty() || episode.getOverview() == null){
            holder.overviewTxt.setVisibility(View.GONE);
        }else {
            holder.overviewTxt.setVisibility(View.VISIBLE);
            holder.ep_overview.setText(episode.getOverview());
        }

        String ImageURL = "https://image.tmdb.org/t/p/w500" + episode.getStill_path();
        Picasso.get().load(ImageURL).into(holder.poster);

    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageButton showBtn;
        ImageView poster;
        LinearLayout FullDetailCtn;
        TextView ep_num, ep_name, ep_rating, ep_air_date, ep_overview, clipTxt,overviewTxt;
        RecyclerView clipRecycler;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showBtn = itemView.findViewById(R.id.showFullDetailBtn);
            FullDetailCtn = itemView.findViewById(R.id.fullDetailCtn);
            ep_num = itemView.findViewById(R.id.epNumber);
            ep_name = itemView.findViewById(R.id.epName);
            ep_rating = itemView.findViewById(R.id.avg_rating);
            ep_air_date = itemView.findViewById(R.id.AirDate);
            overviewTxt = itemView.findViewById(R.id.overviewTxt);
            ep_overview = itemView.findViewById(R.id.overview);
            poster = itemView.findViewById(R.id.ep_poster);
            clipRecycler = itemView.findViewById(R.id.clipRecycler);
            clipTxt = itemView.findViewById(R.id.clipTxt);
        }
    }
}
