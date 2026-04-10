package com.rupp.movieexplorer.model;

import java.util.List;

/**
 * Model class wrapping the TMDB API search results.
 */
public class MovieResponse {

    // The JSON key "results" contains a list of movie objects
    private List<Movie> results;

    public List<Movie> getResults(){
        return results;
    }
}
