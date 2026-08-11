package com.rupp.movieexplorer.model;

public class FilterData {
    private final String genres;
    private final Integer year;
    private final Double rating;
    public FilterData(String genres, Integer year, Double rating){
        this.genres = genres;
        this.year = year;
        this.rating = rating;
    }

    public Double getRating() {
        return rating;
    }

    public Integer getYear() {
        return year;
    }

    public String getGenres() {
        return genres;
    }
}
