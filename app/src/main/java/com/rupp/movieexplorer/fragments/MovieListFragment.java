package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rupp.movieexplorer.Constants;
import com.rupp.movieexplorer.MovieDetailActivity;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.LoadingAdapter;
import com.rupp.movieexplorer.adapter.MediaAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.MediaItem;
import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.MovieResponse;
import com.rupp.movieexplorer.viewModel.FilterViewModel;

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

    private FilterViewModel filterViewModel;

    RecyclerView recyclerView;
    MediaAdapter adapter;
    List<MediaItem> movieList = new ArrayList<>();
    List<MediaItem> suggestList = new ArrayList<>();
    int currentPage = 1;
    String  genres;
    Integer year;
    Double  rating;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_list, container, false);


        recyclerView = view.findViewById(R.id.moviesRecycler);

        // Setup RecyclerView with a linear layout manager and our custom adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MediaAdapter(movieList, movie ->{
            openMovieDetail((Movie) movie);
        });
        recyclerView.setAdapter(new LoadingAdapter());

        adapter.setPaginationListener(new MediaAdapter.PaginationListener() {
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        filterViewModel.getMovieFilters().observe(getViewLifecycleOwner(), filterData -> {
            if(filterData != null){
                updateFilters(filterData.getGenres(), filterData.getYear(), filterData.getRating());
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        fetchMovies();
        adapter.setCurrentPage(currentPage);
    }

    /**
     * Updates the filtering criteria and refreshes the movie list from the first page.
     *
     * STEP 5: State Update (MovieListFragment)
     * The fragment receives the new parameters, updates its local variables,
     * and resets the current page to 1.
     */
    public void updateFilters(String genres, Integer year, Double rating) {
        this.genres = genres;
        this.year = year;
        this.rating = rating;
        this.currentPage = 1; // Always reset to page 1 when applying new filters

        if (adapter != null) {
            adapter.setCurrentPage(currentPage);
        }

        // STEP 6: Feedback (MovieListFragment)
        // Show loading state while fetching new filtered results
        recyclerView.setAdapter(new LoadingAdapter());

        // STEP 7: Network Request (MovieListFragment)
        // Triggers the Retrofit call with the updated parameters.
        fetchMovies();
    }

    private void fetchMovies() {
        // Create an instance of the API interface using our Retrofit client
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        // Prepare the popular movies API call
        Call<MovieResponse> call = api.getAllMovies(Constants.API_KEY, currentPage, genres, year, rating);

        // Execute the call asynchronously
        call.enqueue(new Callback<MovieResponse>() {

            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                // If response is successful, update our list and notify the adapter
                if (response.isSuccessful() && response.body() != null) {
                    recyclerView.setAdapter(adapter);
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
    private void openMovieDetail(Movie movie){
        Intent intent = new Intent(requireContext(),MovieDetailActivity.class);

        intent.putExtra("id", movie.getId());
        intent.putExtra("type","movie");

        startActivity(intent);
    }
}