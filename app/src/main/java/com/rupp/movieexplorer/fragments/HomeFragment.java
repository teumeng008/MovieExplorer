package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.rupp.movieexplorer.MovieDetailActivity;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.CarouselAdapter;
import com.rupp.movieexplorer.adapter.LoadingCardAdapter;
import com.rupp.movieexplorer.adapter.LoadingCarouselAdapter;
import com.rupp.movieexplorer.adapter.MovieCardAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.helperClass.NonFilterableArrayAdapter;
import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.MovieResponse;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private String apiKey = "9248253c09ac61d8b459b1b599ab133b";

    private List<String> items = Arrays.asList("Movie","TV Show");
    private MaterialAutoCompleteTextView dropdown;

    private ViewPager2 viewPager;

    private RecyclerView PopularRecyclerView;
    private RecyclerView NowPlayingRecyclerView;
    private RecyclerView TopRatingRecyclerView;
    private RecyclerView UpComingRecyclerView;

    private CarouselAdapter adapter;
    private MovieCardAdapter PopularAdapter;
    private MovieCardAdapter NowPlayingAdapter;
    private MovieCardAdapter TopRatingAdapter;
    private MovieCardAdapter UpComingAdapter;

    private List<Movie> Movies = new ArrayList<>();
    private List<Movie> popularMovies = new ArrayList<>();
    private List<Movie> nowPlayingMovies = new ArrayList<>();
    private List<Movie> topRatedMovies = new ArrayList<>();
    private List<Movie> upcomingMovies = new ArrayList<>();

    private Handler slideHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dropdown = view.findViewById(R.id.mediaTypeDropdown);

        viewPager = view.findViewById(R.id.viewPager);

        PopularRecyclerView = view.findViewById(R.id.PopularCardScrollBar);
        NowPlayingRecyclerView = view.findViewById(R.id.NowPlayingCardScrollBar);
        TopRatingRecyclerView = view.findViewById(R.id.TopRatingCardScrollBar);
        UpComingRecyclerView = view.findViewById(R.id.UpComingCardScrollBar);

        ArrayAdapter<String> dropdownAdapter = new NonFilterableArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line,items);


        adapter = new CarouselAdapter(Movies, movie -> {
            openDetail(movie);
        });
        PopularAdapter = new MovieCardAdapter(popularMovies, movie ->{
            openDetail(movie);
        });
        NowPlayingAdapter = new MovieCardAdapter(nowPlayingMovies, movie ->{
            openDetail(movie);
        });
        TopRatingAdapter = new MovieCardAdapter(topRatedMovies, movie ->{
            openDetail(movie);
        });
        UpComingAdapter = new MovieCardAdapter(upcomingMovies, movie ->{
            openDetail(movie);
        });


        dropdown.setAdapter(dropdownAdapter);
        viewPager.setAdapter(new LoadingCarouselAdapter());

        dropdown.setText(items.get(0),false);

        PopularRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        NowPlayingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        TopRatingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        UpComingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        PopularRecyclerView.setAdapter(new LoadingCardAdapter());
        NowPlayingRecyclerView.setAdapter(new LoadingCardAdapter());
        TopRatingRecyclerView.setAdapter(new LoadingCardAdapter());
        UpComingRecyclerView.setAdapter(new LoadingCardAdapter());

//        PopularRecyclerView.setAdapter(PopularAdapter);
//        NowPlayingRecyclerView.setAdapter(NowPlayingAdapter);
//        TopRatingRecyclerView.setAdapter(TopRatingAdapter);
//        UpComingRecyclerView.setAdapter(UpComingAdapter);

        fetchPopularMovies();
        fetchNowPlayingMovies();
        fetchTopRatingMovies();
        fetchUpComingMovies();
        fetch5PopularMovies();

        return view;
    }

    // ---------------- SAFE API CALLS ----------------

    private void fetchPopularMovies() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        api.getPopularMovies(apiKey).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {

                Log.d("POPULAR_CODE", String.valueOf(response.code()));

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("POPULAR_ERROR", "Response failed");
                    return;
                }

                List<Movie> list = response.body().getResults();
                if (list == null) return;

                PopularRecyclerView.setAdapter(PopularAdapter);
                popularMovies.clear();
                popularMovies.addAll(list);
                PopularAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("POPULAR_FAILURE", String.valueOf(t));
            }
        });
    }

    private void fetchNowPlayingMovies() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        api.getNowPlayingMovies(apiKey).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<Movie> list = response.body().getResults();
                if (list == null) return;

                NowPlayingRecyclerView.setAdapter(NowPlayingAdapter);
                nowPlayingMovies.clear();
                nowPlayingMovies.addAll(list);
                NowPlayingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("NOW_PLAYING_ERROR", String.valueOf(t));
            }
        });
    }

    private void fetchTopRatingMovies() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        api.getTopRatedMovies(apiKey).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<Movie> list = response.body().getResults();
                if (list == null) return;

                TopRatingRecyclerView.setAdapter(TopRatingAdapter);
                topRatedMovies.clear();
                topRatedMovies.addAll(list);
                TopRatingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("TOP_RATED_ERROR", String.valueOf(t));
            }
        });
    }

    private void fetchUpComingMovies() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        api.getUpcomingKHMovies(apiKey, "KH", "primary_release_date.asc", today)
                .enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) return;

                        List<Movie> list = response.body().getResults();
                        if (list == null) return;
                        viewPager.setAdapter(adapter);
                        UpComingRecyclerView.setAdapter(UpComingAdapter);
                        upcomingMovies.clear();
                        upcomingMovies.addAll(list);
                        UpComingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<MovieResponse> call, Throwable t) {
                        Log.e("UPCOMING_ERROR", String.valueOf(t));
                    }
                });
    }

    private void openDetail(Movie movie){
        Intent intent = new Intent(requireContext(),MovieDetailActivity.class);

        intent.putExtra("id", movie.getId());
        intent.putExtra("type","movie");

        startActivity(intent);
     }

    private void fetch5PopularMovies() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        api.getAllMovies(apiKey, 1).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<Movie> list = response.body().getResults();
                if (list == null) return;

                Movies.clear();

                for (int i = 0; i < Math.min(5, list.size()); i++) {
                    Movies.add(list.get(i));
                }

                adapter.notifyDataSetChanged();

                if (!Movies.isEmpty()) {
                    startSlider();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e("CAROUSEL_ERROR", String.valueOf(t));
            }
        });
    }

    // ---------------- SLIDER ----------------

    private void startSlider() {
        slideHandler.removeCallbacks(sliderRunnable);
        slideHandler.postDelayed(sliderRunnable, 3000);
    }

    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {

            if (viewPager == null || Movies.isEmpty()) return;

            int next = (viewPager.getCurrentItem() + 1) % Movies.size();
            viewPager.setCurrentItem(next, true);

            slideHandler.postDelayed(this, 3000);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }
}