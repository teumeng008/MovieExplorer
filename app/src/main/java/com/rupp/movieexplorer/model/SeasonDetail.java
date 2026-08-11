package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SeasonDetail {
    @SerializedName("air_date")
    private String air_date;

    @SerializedName("episodes")
    private List<Episode> episodes;

    @SerializedName("name")
    private String name;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String poster_path;

    @SerializedName("season_number")
    private  String season_number;

    @SerializedName("vote_average")
    private double vote_average;

    public String getAir_date() {
        return air_date;
    }

    public String getOverview() {
        return overview;
    }

    public String getName() {
        return name;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public double getVote_average() {
        return vote_average;
    }

    public String getSeason_number() {
        return season_number;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }
}
