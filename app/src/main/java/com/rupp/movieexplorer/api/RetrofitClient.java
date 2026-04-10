package com.rupp.movieexplorer.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Retrofit is a library for making HTTP requests in Android.
// HTTP is a protocol for transferring data between web servers and clients.
// HTTP EX: https://api.themoviedb.org/3/movie/550?api_key=<api_key>



/**
 * Singleton class to configure and provide a Retrofit instance.
 */

// what RetrofitClient does?
    // answer:
public class RetrofitClient {
    private static Retrofit retrofit;

    /**
     * Returns the singleton Retrofit instance.
     * @return Retrofit instance configured with base URL and GSON converter.
     */
    public static Retrofit getRetrofit(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/3/") // Base URL of the TMDB API
                    .addConverterFactory(GsonConverterFactory.create()) // Automatic JSON-to-Java conversion
                    .build();
        }
        return retrofit;
    }
}
