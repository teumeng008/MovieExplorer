package com.rupp.movieexplorer.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.model.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoCardAdapter extends RecyclerView.Adapter<VideoCardAdapter.ViewHolder> {
    private List<Video> videos;
    public VideoCardAdapter(List<Video> videos){
        this.videos = videos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trailer_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.videoType.setText(video.getType());
        holder.videoName.setText(video.getName());

        String thumbnailUrl = "https://img.youtube.com/vi/" + video.getKey() + "/hqdefault.jpg";
        Picasso.get()
                .load(thumbnailUrl)
                .placeholder(R.color.gold)
                .into(holder.videoThumbnail);

        View.OnClickListener clickListener = v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + video.getKey()));
            v.getContext().startActivity(intent);
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.watchBtn.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView videoType, videoName;
        ImageView watchBtn, videoThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoType = itemView.findViewById(R.id.videoType);
            videoName = itemView.findViewById(R.id.videoName);
            watchBtn = itemView.findViewById(R.id.watchBtn);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
        }
    }
}
