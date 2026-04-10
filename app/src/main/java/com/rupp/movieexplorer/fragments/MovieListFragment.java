package com.rupp.movieexplorer.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.MovieAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.MovieResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

/**
 * Fragment that displays a list of movies using a RecyclerView.
 * This class handles initiating the network request and updating the UI with results.
 */
public class MovieListFragment extends Fragment {
    public MovieListFragment() {

    }

    RecyclerView recyclerView;
    MovieAdapter adapter;
    List<Movie> movieList = new ArrayList<>();
    int currentPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        recyclerView = view.findViewById(R.id.moviesRecycler);

        // Setup RecyclerView with a linear layout manager and our custom adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MovieAdapter(movieList);
        recyclerView.setAdapter(adapter);

        adapter.setPaginationListener(new MovieAdapter.PaginationListener() {
            @Override
            public void onNextPage() {
                currentPage++;
                adapter.setCurrentPage(currentPage);
                fetchMovies();
            }

            @Override
            public void onPreviousPage() {
                if (currentPage == 1){
                    return;
                }
                else {
                    currentPage--;
                    adapter.setCurrentPage(currentPage);
                    fetchMovies();
                }
            }
        });
        fetchMovies();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        fetchMovies();
        adapter.setCurrentPage(currentPage);
    }



    private void fetchMovies() {
        // Create an instance of the API interface using our Retrofit client
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        // Prepare the popular movies API call
        Call<MovieResponse> call = api.getAllMovies("9248253c09ac61d8b459b1b599ab133b", currentPage);

        // Execute the call asynchronously
        call.enqueue(new Callback<MovieResponse>() {

            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                // If response is successful, update our list and notify the adapter
                if (response.isSuccessful() && response.body() != null) {
                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(0);
                } else {
                    Log.d("API_ERROR", "Response unsuccessful or empty body");
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Handle network failure
                Log.d("API_ERROR", t.getMessage());
            }
        });
    }
}