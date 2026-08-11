package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private List<Video> videos;

    public List<Video> getVideos() {
        return videos;
    }
}
