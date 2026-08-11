package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model class representing a single movie object from the TMDB API response.
 */
public class Movie extends MediaItem {

    @SerializedName("runtime")
    private int runtime;

    public int getRuntime() {
        return runtime;
    }

    // You can keep getRelease_date if it's used elsewhere,
    // but it's better to use getReleaseDate() from MediaItem
    public String getRelease_date() {
        return getReleaseDate();
    }
}
