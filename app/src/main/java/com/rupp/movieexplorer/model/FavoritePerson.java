package com.rupp.movieexplorer.model;

public class FavoritePerson {
    private int id, gender;
    private String name, profile_path;

    public FavoritePerson() {
    }

    public FavoritePerson(int id, String name, int gender, String profile_path){
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.profile_path = profile_path;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getGender() {
        return gender;
    }
    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public String getProfile_path() {
        return profile_path;
    }
}
