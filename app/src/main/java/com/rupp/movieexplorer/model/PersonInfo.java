package com.rupp.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonInfo {
    @SerializedName("id")
    private int id;

    @SerializedName("also_known_as")
    private List<String> also_known_as;

    @SerializedName("name")
    private String name;

    @SerializedName("place_of_birth")
    private String place_of_birth;

    @SerializedName("biography")
    private String biography;

    @SerializedName("birthday")
    private String birthday;

    @SerializedName("deathday")
    private String deathday;

    @SerializedName("gender")
    private int gender;

    @SerializedName("homepage")
    private String homepage;

    @SerializedName("popularity")
    private Double popularity;

    @SerializedName("profile_path")
    private String profile_path;

    @SerializedName("cast")
    private List<PersonDetails> castInfos;

    @SerializedName("crew")
    private List<PersonDetails> crewInfos;

    @SerializedName("known_for_department")
    private String known_for_department;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public int getGender() {
        return gender;
    }

    public Double getPopularity() {
        return popularity;
    }

    public List<String> getAlso_known_as() {
        return also_known_as;
    }

    public String getBiography() {
        return biography;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getDeathday() {
        return deathday;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getPlace_of_birth() {
        return place_of_birth;
    }

    public List<PersonDetails> getCastInfos() {
        return castInfos;
    }

    public List<PersonDetails> getCrewInfos() {
        return crewInfos;
    }

    public String getKnown_for_department() {
        return known_for_department;
    }
}
