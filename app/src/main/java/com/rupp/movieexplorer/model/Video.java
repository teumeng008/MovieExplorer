package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("name")
    private  String name;

    @SerializedName("key")
    private String key;

    @SerializedName("type")
    private String type;

    @SerializedName("site")
    private String site;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getSite() {
        return site;
    }
}
