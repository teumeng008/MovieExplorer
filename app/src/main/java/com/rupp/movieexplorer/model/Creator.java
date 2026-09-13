package com.rupp.movieexplorer.model;

public class Creator {
    private int id;
    private String name;
    private String original_name;
    private int gender;
    private String profile_path;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGender() {
        return gender;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public String getOriginal_name() {
        return original_name;
    }
}
