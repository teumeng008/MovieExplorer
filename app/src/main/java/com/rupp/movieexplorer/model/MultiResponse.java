package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MultiResponse {
    @SerializedName("results")
    private List<MediaItem> results;

    public List<MediaItem> getResults(){
        return results;
    }
}
