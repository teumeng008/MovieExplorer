package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

public class Genres {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}
