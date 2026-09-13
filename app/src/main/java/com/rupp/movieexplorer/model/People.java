package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

public class People {
    @SerializedName("id")
    private int id;
    @SerializedName("gender")
    private String gender;

    @SerializedName("name")
    private String name;

    @SerializedName("profile_path")
    private String profile_image;

    @SerializedName("character")
    private String character;

    @SerializedName("job")
    private String job;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_image(){
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getCharacter() {
        return character;
    }

    public String getJob() {
        return job;
    }

}
