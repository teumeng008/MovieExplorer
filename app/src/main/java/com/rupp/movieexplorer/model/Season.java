package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

public class Season {
    @SerializedName("air_date")
    private String air_date;

    @SerializedName("episode_count")
    private int episode_count;

    @SerializedName("name")
    private String name;

    @SerializedName("overview")
    private String overview;

    @SerializedName("vote_average")
    private  double vote_average;

    @SerializedName("poster_path")
    private String poster_path;

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public double getVote_average() {
        return vote_average;
    }

    public int getEpisode_count() {
        return episode_count;
    }

    public String getAir_date() {
        return air_date;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }
}
