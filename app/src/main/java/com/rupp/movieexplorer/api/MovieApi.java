package com.rupp.movieexplorer.api;

import com.rupp.movieexplorer.model.MediaItem;
import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.MovieResponse;
import com.rupp.movieexplorer.model.MultiResponse;
import com.rupp.movieexplorer.model.People;
import com.rupp.movieexplorer.model.TVResponse;
import com.rupp.movieexplorer.model.TVShow;

import org.checkerframework.common.reflection.qual.GetClass;

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
            @Path("movie_id") int id,
            @Query("api_key") String apiKey

    );

    @GET("tv/{tv_show_id}")
    Call<TVShow> getTvShowDetails(
            @Path("tv_show_id") int id,
            @Query("api_key") String apikey
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
    @GET("discover/movie")
    Call<MovieResponse> getUpcomingKHMovies(
            @Query("api_key") String apiKey,
            @Query("region") String region,
            @Query("sort_by") String sortBy,
            @Query("primary_release_date.gte") String date
    );
    @GET("search/multi")
    Call<MultiResponse> searchMovieAndTVShow(
            @Query("api_key") String apiKey,
            @Query("query") String mediaQuery
    );

    @GET("movie/{movie_id}/credits")
    Call<MediaItem> getPeople(
            @Path("movie_id") int id,
            @Query("api_key") String apiKey

    );

    @GET("tv/{tv_id}/credits")
    Call<MediaItem> getTvPeople(
            @Path("tv_id") int id,
            @Query("api_key") String apiKey
    );

}
