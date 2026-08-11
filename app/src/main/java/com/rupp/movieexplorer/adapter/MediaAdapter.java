package com.rupp.movieexplorer.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.model.MediaItem;
import com.rupp.movieexplorer.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

// Adapter = bridge between data (movieList) and UI (RecyclerView)
public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 🔥 Two types of items
    private static final int VIEW_TYPE_MOVIE = 0;
    private static final int VIEW_TYPE_PAGINATION = 1;
    private OnMediaClickListener onMediaClickListener;
    List<MediaItem> mediaList = new ArrayList<>();
    int currentPage = 1;

    public MediaAdapter(List<MediaItem> mediaList, OnMediaClickListener onMediaClickListener) {
        this.onMediaClickListener = onMediaClickListener;
        if (mediaList != null) {
            this.mediaList = mediaList;
        }
    }

    // 🔥 Listener for pagination button clicks
    public interface PaginationListener {
        void onNextPage();
        void onPreviousPage();
    }
    public interface OnMediaClickListener{
        void OnMediaClick(MediaItem item);
    }

    private PaginationListener paginationListener;

    public void setPaginationListener(PaginationListener listener) {
        paginationListener = listener;
    }

    // 🔥 Decide which type each position is
    @Override
    public int getItemViewType(int position) {
        // If it's the last position → pagination
        if (mediaList == null || position == mediaList.size()) {
            return VIEW_TYPE_PAGINATION;
        }
        return VIEW_TYPE_MOVIE;
    }

    // 🔥 Create ViewHolder based on type
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_MOVIE) {
            // Normal movie item
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_movie, parent, false);
            return new MovieViewHolder(view);

        } else {
            // Pagination item (last row)
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pagination, parent, false);
            return new PaginationViewHolder(view);
        }
    }

    // 🔥 Bind data to each item
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // 👉 If it's a movie item
        if (holder instanceof MovieViewHolder) {

            MediaItem media = mediaList.get(position);

            MovieViewHolder h = (MovieViewHolder) holder;
            h.title.setText(media.getTitle());
            h.rating.setText("⭐ " + String.format("%.2f", media.getVoteAverage()));

            String imageUrl = "https://image.tmdb.org/t/p/w500" + media.getPosterPath();

            Picasso.get()
                    .load(imageUrl)
                    .into(h.poster);
            holder.itemView.setOnClickListener(v ->{
                if(onMediaClickListener != null){
                    onMediaClickListener.OnMediaClick(media);
                }
            });
        }
        // 👉 If it's pagination item
        else if (holder instanceof PaginationViewHolder) {

            PaginationViewHolder p = (PaginationViewHolder) holder;

            // Next button
            p.nextButton.setOnClickListener(v -> {
                if (paginationListener != null) {
                    paginationListener.onNextPage();
                }
            });
            addStyle(p.button);
            p.button.setText(String.valueOf(currentPage));
            p.button2.setText(String.valueOf(currentPage + 1));
            p.button3.setText(String.valueOf(currentPage + 2));


            p.button2.setOnClickListener(v -> {
                if (paginationListener != null) {
                    paginationListener.onNextPage();
                }
            });
            p.button3.setOnClickListener(v -> {
                if (paginationListener != null) {
                    paginationListener.onNextPage();
                    paginationListener.onNextPage();
                }
            });
            // Previous button
            p.prevButton.setOnClickListener(v -> {
                if (paginationListener != null) {
                    paginationListener.onPreviousPage();
                }
            });
        }
    }

    // 🔥 Total items = movies + 1 pagination row
    @Override
    public int getItemCount() {
        return (mediaList == null) ? 1 : mediaList.size() + 1;
    }

    // =========================
    // 🎬 Movie ViewHolder
    // =========================
    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        TextView title;
        TextView rating;

        public MovieViewHolder(View itemView) {
            super(itemView);

            poster = itemView.findViewById(R.id.moviePoster);
            title = itemView.findViewById(R.id.movieTitle);
            rating = itemView.findViewById(R.id.movieRating);
        }
    }

    // =========================
    // 📄 Pagination ViewHolder
    // =========================
    public static class PaginationViewHolder extends RecyclerView.ViewHolder {

        ImageView nextButton, prevButton;
        Button button,button2,button3;

        public PaginationViewHolder(View itemView) {
            super(itemView);

            nextButton = itemView.findViewById(R.id.nextButton);
            button = itemView.findViewById(R.id.button);
            button2 = itemView.findViewById(R.id.button2);
            button3 = itemView.findViewById(R.id.button3);
            prevButton = itemView.findViewById(R.id.prevButton);
        }
    }

    public void setCurrentPage (int currentPage) {
        this.currentPage = currentPage;
        notifyItemChanged(currentPage);
    }

    //helper with bg of pagination button

    private void addStyle(Button button){
        button.setBackgroundResource(R.drawable.rounded_bg_2);
        button.setBackgroundTintList(
                ColorStateList.valueOf(
                        button.getContext().getResources().getColor(R.color.light_gray)
                )
        );
        button.setTextColor(button.getContext().getResources().getColor(R.color.bg));
    }

}