package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.rupp.movieexplorer.MovieDetailActivity;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.CarouselAdapter;
import com.rupp.movieexplorer.adapter.CarouselTvAdapter;
import com.rupp.movieexplorer.adapter.LoadingCardAdapter;
import com.rupp.movieexplorer.adapter.LoadingCarouselAdapter;
import com.rupp.movieexplorer.adapter.MovieCardAdapter;
import com.rupp.movieexplorer.adapter.TvCardAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.helperClass.NonFilterableArrayAdapter;
import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.MovieResponse;
import com.rupp.movieexplorer.model.TVResponse;
import com.rupp.movieexplorer.model.TVShow;

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
    private String selected = items.get(0);
    private boolean isTv = false, isMovie = false;
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

    private CarouselTvAdapter TvAdapter;
    private TvCardAdapter PopularTvAdapter ;
    private TvCardAdapter OnTheAirTvAdapter;
    private TvCardAdapter TopRatingTvAdapter;
    private TvCardAdapter AiringTvAdapter;

    private List<Movie> Movies = new ArrayList<>();
    private List<Movie> popularMovies = new ArrayList<>();
    private List<Movie> nowPlayingMovies = new ArrayList<>();
    private List<Movie> topRatedMovies = new ArrayList<>();
    private List<Movie> upcomingMovies = new ArrayList<>();

    private List<TVShow> Tv = new ArrayList<>();
    private List<TVShow> popularTv = new ArrayList<>();
    private List<TVShow> OnTheAirTv = new ArrayList<>();
    private List<TVShow> topRatedTv = new ArrayList<>();
    private List<TVShow> AiringTv = new ArrayList<>();


    private Handler slideHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            selected = savedInstanceState.getString("selected_type", items.get(0));
        }
        isTv = false;
        isMovie = false;

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

        TvAdapter = new CarouselTvAdapter(Tv, tv -> {
            openTvDetail(tv);
        });
        PopularTvAdapter = new TvCardAdapter(popularTv,tv -> {
            openTvDetail(tv);
        });
        AiringTvAdapter = new TvCardAdapter(AiringTv,tv -> {
            openTvDetail(tv);
        });
        TopRatingTvAdapter = new TvCardAdapter(topRatedTv, tv -> {
            openTvDetail(tv);
        });
        OnTheAirTvAdapter = new TvCardAdapter(OnTheAirTv, tv -> {
            openTvDetail(tv);
        });


        dropdown.setAdapter(dropdownAdapter);
        viewPager.setAdapter(new LoadingCarouselAdapter());

        dropdown.setText(selected,false);

        dropdown.setOnItemClickListener((parent,v,position,id)->{
            selected = parent.getItemAtPosition(position).toString();
            Toast.makeText(requireContext(),selected,Toast.LENGTH_SHORT).show();
            mediaFetch(selected);
        });
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

        mediaFetch(selected);
        return view;
    }

    public void mediaFetch(String type){
        if(type.equals("TV Show")){
            if(isTv){
                return;
            }
            fetchTvShows();
            isTv = true;
            isMovie = false;
        }else {
            if(isMovie){
                return;
            }
            fetchMovies();
            isTv = false;
            isMovie = true;
        }
    }
    public void fetchMovies(){
        fetchPopularMovies();
        fetchNowPlayingMovies();
        fetchTopRatingMovies();
        fetchUpComingMovies();
        fetch5PopularMovies();
    }
    public void fetchTvShows(){
        fetchPopularTv();
        fetchOnTheAirTv();
        fetchTopRatingTv();
        fetchAiringTv();
        fetch5PopularTv();
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
    private void fetchPopularTv() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        api.getPopularTVShows(apiKey).enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(Call<TVResponse> call, Response<TVResponse> response) {

                Log.d("POPULAR_CODE", String.valueOf(response.code()));

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("POPULAR_ERROR", "Response failed");
                    return;
                }

                List<TVShow> list = response.body().getResults();
                if (list == null) return;

                PopularRecyclerView.setAdapter(PopularTvAdapter);
                popularTv.clear();
                popularTv.addAll(list);
                PopularTvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<TVResponse> call, Throwable t) {
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
    private void fetchOnTheAirTv() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        api.getOnTheAirTVShows(apiKey).enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(Call<TVResponse> call, Response<TVResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<TVShow> list = response.body().getResults();
                if (list == null) return;

                NowPlayingRecyclerView.setAdapter(OnTheAirTvAdapter);
                OnTheAirTv.clear();
                OnTheAirTv.addAll(list);
                OnTheAirTvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<TVResponse> call, Throwable t) {
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
    private void fetchTopRatingTv() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        api.getTopRatedTVShows(apiKey).enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(Call<TVResponse> call, Response<TVResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<TVShow> list = response.body().getResults();
                if (list == null) return;

                TopRatingRecyclerView.setAdapter(TopRatingTvAdapter);
                topRatedTv.clear();
                topRatedTv.addAll(list);
                TopRatingTvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<TVResponse> call, Throwable t) {
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
    private void fetchAiringTv() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);


        api.getAiringTodayTVShows(apiKey)
                .enqueue(new Callback<TVResponse>() {
                    @Override
                    public void onResponse(Call<TVResponse> call, Response<TVResponse> response) {

                        if (!response.isSuccessful() || response.body() == null) return;

                        List<TVShow> list = response.body().getResults();
                        if (list == null) return;
                        UpComingRecyclerView.setAdapter(AiringTvAdapter);
                        AiringTv.clear();
                        AiringTv.addAll(list);
                        AiringTvAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<TVResponse> call, Throwable t) {
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
    private void openTvDetail(TVShow tvShow){
        Intent intent = new Intent(requireContext(),MovieDetailActivity.class);

        intent.putExtra("id", tvShow.getId());
        intent.putExtra("type","tv");

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
                viewPager.setAdapter(adapter);
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
    private void fetch5PopularTv() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        api.getPopularTVShows(apiKey).enqueue(new Callback<TVResponse>() {
            @Override
            public void onResponse(Call<TVResponse> call, Response<TVResponse> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<TVShow> list = response.body().getResults();
                if (list == null) return;

                Tv.clear();

                for (int i = 0; i < Math.min(5, list.size()); i++) {
                    Tv.add(list.get(i));
                }
                viewPager.setAdapter(TvAdapter);
                TvAdapter.notifyDataSetChanged();

                if (!Tv.isEmpty()) {
                    startSlider();
                }
            }

            @Override
            public void onFailure(Call<TVResponse> call, Throwable t) {
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
            if (viewPager == null) return;

            int size = selected.equals("TV Show") ? Tv.size() : Movies.size();
            if (size == 0) return;

            int next = (viewPager.getCurrentItem() + 1) % size;
            viewPager.setCurrentItem(next, true);

            slideHandler.postDelayed(this, 3000);
        }
    };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selected_type", selected);
    }

    @Override
    public void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }
}