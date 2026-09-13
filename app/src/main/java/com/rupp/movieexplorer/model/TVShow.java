package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShow extends MediaItem {

    @SerializedName("original_name")
    private String originalName;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("languages")
    private List<String> languages;

    @SerializedName("seasons")
    private List<Season> seasons;

    @SerializedName("number_of_seasons")
    private int number_of_seasons;

    @SerializedName("number_of_episodes")
    private int number_of_episodes;

    @SerializedName("type")
    private String type;
    // Getters and Setters...

    public int getVoteCount() {
        return voteCount;
    }

    public List<String> getLanguages(){
        return languages;
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

    public String getOriginalName() {
        return originalName;
    }

    public String getType() {
        return type;
    }
}
