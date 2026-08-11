package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Episode {
    @SerializedName("air_date")
    private String air_date;

    @SerializedName("episode_number")
    private int episode_number;

    @SerializedName("name")
    private String name;

    @SerializedName("overview")
    private String overview;

    @SerializedName("id")
    private int id;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("season_number")
    private int season_number;

    @SerializedName("still_path")
    private String still_path;

    @SerializedName("vote_average")
    private Double vote_average;

    private List<Video> videos;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public String getAir_date() {
        return air_date;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public int getSeason_number() {
        return season_number;
    }

    public String getStill_path() {
        return still_path;
    }

    public int getEpisode_number() {
        return episode_number;
    }

    public List<Video> getVideos() {
        return videos;
    }
    public void setVideos(List<Video> videos){
        this.videos = videos;
    }
}
