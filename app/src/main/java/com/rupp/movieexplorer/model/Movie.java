package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model class representing a single movie object from the TMDB API response.
 */
public class Movie {
    private int id;
    private String title;
    private String overview;
    private String release_date;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("spoken_languages")
    private List<lang> spoken_languages;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("genres")
    private List<Genres> genres;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getBackdropPath() {
        return backdropPath;
    }
    public String getRelease_date(){ return release_date; }

    public List<lang> getSpoken_languages() {
        return spoken_languages;
    }

    public int getRuntime() {
        return runtime;
    }

    public List<Genres> getGenres() {
        return genres;
    }
}

