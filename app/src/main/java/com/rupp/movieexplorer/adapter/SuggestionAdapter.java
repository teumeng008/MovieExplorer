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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String PERSON = "person";
    private static final String MOVIE = "movie";
    private static final String TV = "tv";

    private static final int TYPE_PERSON = 0;
    private static final int TYPE_MEDIA = 1;

    private List<MediaItem> mediaList;
    private onClickListener onClickListener;

    public interface onClickListener {
        void onMouseClick(MediaItem item);
    }

    public SuggestionAdapter(List<MediaItem> mediaList, onClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.mediaList = mediaList;
    }

    @Override
    public int getItemViewType(int position) {
        if (mediaList == null || mediaList.isEmpty()) {
            return TYPE_MEDIA;
        }
        MediaItem mediaItem = mediaList.get(position);
        String mediaType = mediaItem.getMediaType();
        if (PERSON.equals(mediaType)) {
            return TYPE_PERSON;
        }

        if (MOVIE.equals(mediaType) || TV.equals(mediaType)) {
            return TYPE_MEDIA;
        }

        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_PERSON) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_card_template, parent, false);
            return new PersonHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_movie, parent, false);
        return new MediaHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (mediaList == null || mediaList.isEmpty()) {
            MediaHolder mediaHolder = (MediaHolder) holder;
            mediaHolder.NoResults.setVisibility(View.VISIBLE);
            mediaHolder.name.setVisibility(View.GONE);
            mediaHolder.poster.setVisibility(View.GONE);
            mediaHolder.rating.setVisibility(View.GONE);
            mediaHolder.itemView.setOnClickListener(null);
            return;
        }

        MediaItem item = mediaList.get(position);
        if (PERSON.equals(item.getMediaType())) {
            PersonHolder personHolder = (PersonHolder) holder;
            personHolder.peopleName.setText(item.getName());
            personHolder.gender.setText(item.getGender());
            String imageURL = "https://image.tmdb.org/t/p/w200" + item.getProfile_path();
            Picasso.get().load(imageURL).placeholder(R.drawable.user_icon).into(personHolder.peopleProfile);

            personHolder.itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onMouseClick(item);
                }
            });

        } else {
            MediaHolder mediaHolder = (MediaHolder) holder;
            mediaHolder.NoResults.setVisibility(View.GONE);
            mediaHolder.name.setVisibility(View.VISIBLE);
            mediaHolder.poster.setVisibility(View.VISIBLE);
            mediaHolder.rating.setVisibility(View.VISIBLE);

            String imageURL = "https://image.tmdb.org/t/p/w500" + item.getPosterPath();
            Picasso.get().load(imageURL).placeholder(R.drawable.movie_list).into(mediaHolder.poster);
            mediaHolder.name.setText(item.getTitle());
            mediaHolder.rating.setText(String.format(Locale.getDefault(), "⭐ %.2f", item.getVoteAverage()));

            mediaHolder.itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onMouseClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mediaList == null || mediaList.isEmpty()) {
            return 1;
        }
        return mediaList.size();
    }

    static class MediaHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView name;
        TextView rating;
        TextView NoResults;

        public MediaHolder(View view) {
            super(view);
            poster = view.findViewById(R.id.S_moviePoster);
            name = view.findViewById(R.id.S_movieTitle);
            rating = view.findViewById(R.id.S_movieRating);
            NoResults = view.findViewById(R.id.noResultsText);
        }
    }

    static class PersonHolder extends RecyclerView.ViewHolder {
        ImageView peopleProfile;
        TextView peopleName, gender;

        public PersonHolder(View view) {
            super(view);
            peopleProfile = view.findViewById(R.id.PersonImage);
            peopleName = view.findViewById(R.id.Name);
            gender = view.findViewById(R.id.Gender);
        }
    }
}
