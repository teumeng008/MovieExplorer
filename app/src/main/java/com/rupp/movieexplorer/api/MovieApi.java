package com.rupp.movieexplorer.api;

import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface defining the TMDB API endpoints using Retrofit annotations.
 */
public interface MovieApi {

    /**
     * Fetches a list of popular movies.
     * @param apiKey Your TMDB API key.
     * @return A Call object that can be executed to get a MovieResponse.
     */
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey
    );
    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(
      @Query("api_key") String apikey
    );

    /**
     * Fetches a list of top-rated movies.
     * @param apiKey Your TMDB API key.
     * @return A Call object that can be executed to get a MovieResponse.
     */
    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey
    );
    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(
            @Query("api_key") String apikey
    );

    /**
     * Fetches details for a specific movie by its ID.
     * @param apiKey Your TMDB API key.
     * @param id The ID of the movie to fetch.
     * @return A Call object that can be executed to get a Movie object.
     */
    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(
            @Query("api_key") String apiKey,
            @Path("movie_id") int id
    );

    /**
     * Discovers movies from the database. This acts as a general "all movies" endpoint.
     * @param apiKey Your TMDB API key.
     * @param page The page number to fetch (for pagination).
     * @return A Call object that can be executed to get a MovieResponse.
     */
    @GET("discover/movie")
    Call<MovieResponse> getAllMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );
}
