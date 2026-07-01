package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.rupp.movieexplorer.MovieDetailActivity;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.LoadingAdapter;
import com.rupp.movieexplorer.adapter.MovieAdapter;
import com.rupp.movieexplorer.adapter.SuggestionAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.MediaItem;
import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.MovieResponse;
import com.rupp.movieexplorer.model.MultiResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;
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
    List<MediaItem> suggestList = new ArrayList<>();
    int currentPage = 1;
    TextInputEditText search_text;
    RecyclerView suggestion_view;
    SuggestionAdapter suggestionAdapter;

    private Handler searchHandler;
    private Runnable searchRunnable;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        searchHandler = new Handler();

        recyclerView = view.findViewById(R.id.moviesRecycler);

        // Setup RecyclerView with a linear layout manager and our custom adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MovieAdapter(movieList, movie ->{
            openMovieDetail(movie);
        });
        recyclerView.setAdapter(new LoadingAdapter());

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

        search_text = view.findViewById(R.id.search_text);
        suggestion_view = view.findViewById(R.id.suggestion_view);
        suggestionAdapter = new SuggestionAdapter(suggestList,item ->{
            openDetail(item);
        });
        suggestion_view.setLayoutManager(new LinearLayoutManager(getContext()));
        suggestion_view.setAdapter(suggestionAdapter);
        suggestion_view.setVisibility(View.GONE);

        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //if the text still add or still typing, it will the restart cooldown by | but if wait enough like 400 millis later it will execute the searchRunnable
                String query = charSequence.toString().trim();//                         |
                if(searchRunnable != null){//                                            |
                    searchHandler.removeCallbacks(searchRunnable);//   <---------------- |
                }

                if(charSequence.length() < 2) {
                    suggestion_view.setVisibility(View.GONE);
                    return;
                }

                    searchRunnable = () ->{
                        suggestion_view.setAdapter(new LoadingAdapter());
                        search_movie(query);

                        suggestion_view.setVisibility(View.VISIBLE);
                        suggestion_view.setAlpha(0f);
                        suggestion_view.setTranslationY(-20f);

                        suggestion_view.animate()
                                .alpha(1f)
                                .translationY(0f)
                                .setDuration(200)
                                .start();
                    };
                searchHandler.postDelayed(searchRunnable,400);


//                }else {
//                    suggestion_view.animate()
//                            .alpha(0f)
//                            .translationY(-20f)
//                            .setDuration(150)
//                            .withEndAction(() -> suggestion_view.setVisibility(View.GONE))
//                            .start();
//                }
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

    private void search_movie(String query){
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call<MultiResponse> call = api.searchMovieAndTVShow("9248253c09ac61d8b459b1b599ab133b",query);
        call.enqueue(new Callback<MultiResponse>() {
            @Override
            public void onResponse(Call<MultiResponse> call, Response<MultiResponse> response) {
                List<MediaItem> allItem = response.body().getResults();
                List<MediaItem> SelectedItem = new ArrayList<>();
                // clean query
                for(MediaItem i : allItem ){
                    if("person".equals(i.getMediaType())){
                        continue;
                    }
                    String searchText = i.getTitle() + " " +i.getOverview()+" "+i.getMediaType();
                    int similarity = FuzzySearch.tokenSetRatio(searchText.toLowerCase(),query.toLowerCase());

                    double textScore = similarity / 100.0;
                    double popularityScore = Math.log(i.getPopularity()+ 1 ) / 10.0;
                    double ratingScore = i.getVoteAverage() / 10.0;
                    //item score (the higher it got the more priority it gets)
                    double itemScore = (textScore * 0.6) + (popularityScore * 0.2) + (ratingScore * 0.2);
                    i.setItemScore(itemScore);
                    SelectedItem.add(i);
                }
                //sort from the highest score to less
                Collections.sort(SelectedItem,(a,b)->
                        Double.compare(b.getItemScore(), a.getItemScore())
                );
                suggestion_view.setAdapter(suggestionAdapter);
                suggestList.clear();
                suggestList.addAll(SelectedItem);
                suggestionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MultiResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
    private void openDetail(MediaItem item){
        Intent intent = new Intent(requireContext(),MovieDetailActivity.class);

        intent.putExtra("id", item.getId());
        intent.putExtra("type",item.getMediaType());

        startActivity(intent);
    }
    private void openMovieDetail(Movie movie){
        Intent intent = new Intent(requireContext(),MovieDetailActivity.class);

        intent.putExtra("id", movie.getId());
        intent.putExtra("type","movie");

        startActivity(intent);
    }
}