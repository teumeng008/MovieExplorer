package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MediaItem {

    @SerializedName("id")
    private int id;

    @SerializedName("title")        // movies have "title"
    private String title;

    @SerializedName("name")         // TV shows have "name"
    private String name;
    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("overview")
    private String overview;

    @SerializedName("media_type")   // "movie" or "tv"
    private String mediaType;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("first_air_date")
    private String firstAirDate;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("spoken_languages")
    private List<lang> spoken_languages;

    @SerializedName("cast")
    private List<People> cast;

    @SerializedName("crew")
    private List<People> crew;

    @SerializedName("created_by")
    private List<Creator> creator;

    private double itemScore;

    // Unified getter — returns title for movies, name for TV shows
    public String getTitle() {
        return title != null ? title : name;
    }

    public int getId() { return id; }
    public String getPosterPath() { return posterPath; }
    public String getOverview() { return overview; }
    public String getMediaType() { return mediaType; }
    public String getReleaseDate() {
        return releaseDate != null ? releaseDate : firstAirDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setItemScore(double itemScore) {
        this.itemScore = itemScore;
    }

    public double getItemScore() {
        return itemScore;
    }

    public List<People> getCast() {
        return cast;
    }

    public List<People> getCrew() {
        return crew;
    }

    public List<Creator> getCreator() {
        return creator;
    }
}
