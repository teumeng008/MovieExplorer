package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShow {

    @SerializedName("id")
    private int id;

    @SerializedName("created_by")
    private List<Creator> creator;

    @SerializedName("name")              // TV uses "name", Movie uses "title"
    private String name;

    @SerializedName("genres")
    private List<Genres> genres;

    @SerializedName("original_name")
    private String originalName;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("first_air_date")    // TV uses "first_air_date", Movie uses "release_date"
    private String firstAirDate;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("languages")
    private List<String> languages;

    @SerializedName("seasons")
    private List<Season> seasons;

    @SerializedName("number_of_seasons")
    private int number_of_seasons;

    @SerializedName("number_of_episodes")
    private int number_of_episodes;

    @SerializedName("media_type")        // useful when using search/multi
    private String mediaType;

    // Getters and Setters...

    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }

    public List<Genres> getGenres() {
        return genres;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public List<String> getLanguages(){
        return languages;
    }

    public List<Creator> getCreator() {
        return creator;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public int getNumber_of_seasons() {
        return number_of_seasons;
    }

    public int getNumber_of_episodes() {
        return number_of_episodes;
    }
}