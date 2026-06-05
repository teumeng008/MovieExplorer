package com.rupp.movieexplorer.model;

public class FavoriteItem {
    private int id;
    private String type;
    private String title;
    private String posterPath;
    private double rating;

    public FavoriteItem(){

    }
    public FavoriteItem(int id,String type,String title,String posterPath,double rating){
        this.id = id;
        this.type = type;
        this.title = title;
        this.posterPath = posterPath;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public double getRating() {
        return rating;
    }
}
