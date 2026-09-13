package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonDetails {
    @SerializedName("adult") //
    private boolean adult;

    @SerializedName("backdrop_path") //
    private String backdrop_path;

    @SerializedName("poster_path") //
    private String poster_path;

    @SerializedName("genre_ids") //
    private List<Integer> genres_ids;

    @SerializedName("id") //
    private int id;

    @SerializedName("title") //
    private String title;

    @SerializedName("name") //
    private String name;

    @SerializedName("original_language") //
    private String originalLanguage;

    @SerializedName("original_title") //
    private String originalTitle;

    @SerializedName("overview") //
    private String overview;

    @SerializedName("popularity") //
    private double popularity;

    @SerializedName("character")
    private String character;

    @SerializedName("release_date") //
    private String release_date;

    @SerializedName("first_air_date") //
    private String first_air_date;

    @SerializedName("softcore") //
    private boolean softcore;

    @SerializedName("video") //
    private boolean video;

    @SerializedName("vote_average") //
    private Double vote_average;

    @SerializedName("vote_count") //
    private int voteCount;

    @SerializedName("credit_id") //
    private String creditId;

    @SerializedName("department") //
    private String department;

    @SerializedName("job") //
    private String job;

    @SerializedName("media_type") //
    private String media_type;

    public int getId() {
        return id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public String getTitle() {
        return (title != null && !title.isEmpty()) ? title : name;
    }

    public String getCharacter() {
        return character;
    }

    public String getReleaseDate() {
        return (release_date != null && !release_date.isEmpty()) ? release_date : first_air_date;
    }

    public double getPopularity() {
        return popularity;
    }

    public boolean isAdult() {
        return adult;
    }

    public List<Integer> getGenres_ids() {
        return genres_ids;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getMedia_type() {
        return media_type;
    }

    public String getJob() {
        return job;
    }
}
