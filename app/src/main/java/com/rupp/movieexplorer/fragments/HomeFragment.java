package com.rupp.movieexplorer.fragments;

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

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.CarouselAdapter;
import com.rupp.movieexplorer.adapter.MovieCardAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.MovieResponse;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Full Flow:

// API fetch movies
//      ↓
//Movies list filled
//      ↓
//adapter = new CarouselAdapter(Movies)
//      ↓
//viewPager.setAdapter(adapter)
//      ↓
//Adapter creates slides
//      ↓
//ViewPager shows them

public class HomeFragment extends Fragment {

    private String apiKey = "9248253c09ac61d8b459b1b599ab133b";
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
    private List<Movie> Movies = new ArrayList<>(); // stored object's movies that each object contain title, overview, posterPath, voteAverage, backdropPath
    private List<Movie> popularMovies = new ArrayList<>();
    private List<Movie> nowPlayingMovies = new ArrayList<>();
    private List<Movie> topRatedMovies = new ArrayList<>();
    private List<Movie> upcomingMovies = new ArrayList<>();
    private Handler slideHandler = new Handler(Looper.getMainLooper());

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        PopularRecyclerView = view.findViewById(R.id.PopularCardScrollBar);
        NowPlayingRecyclerView = view.findViewById(R.id.NowPlayingCardScrollBar);
        TopRatingRecyclerView = view.findViewById(R.id.TopRatingCardScrollBar);
        UpComingRecyclerView = view.findViewById(R.id.UpComingCardScrollBar);


        adapter = new CarouselAdapter(Movies);// everything inside Movies arraylist that contain each different movie object will be passed to the adapter
        PopularAdapter = new MovieCardAdapter(popularMovies);
        NowPlayingAdapter = new MovieCardAdapter(nowPlayingMovies);
        TopRatingAdapter = new MovieCardAdapter(topRatedMovies);
        UpComingAdapter = new MovieCardAdapter(upcomingMovies);

        viewPager.setAdapter(adapter); // viewPager alone can't understand object of each movie directly so it need adapter to translate for it
        PopularRecyclerView.setAdapter(PopularAdapter);
        NowPlayingRecyclerView.setAdapter(NowPlayingAdapter);
        TopRatingRecyclerView.setAdapter(TopRatingAdapter);
        UpComingRecyclerView.setAdapter(UpComingAdapter);

        // so what adapter does is  //Movie object
        //     ↓
        //Convert to UI layout
        //     ↓
        //ImageView + TextView

        PopularRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        NowPlayingRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        TopRatingRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        UpComingRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        fetchPopularMovies();
        fetchNowPlayingMovies();
        fetchTopRatingMovies();
        fetchUpComingMovies();
        fetch5PopularMovies();
        AutoRunSlider();

        return view;
    }

    private void fetchPopularMovies() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call<MovieResponse> call = api.getPopularMovies(apiKey);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                List<Movie> allMovies = response.body().getResults();
                popularMovies.clear();
                popularMovies.addAll(allMovies);
                PopularAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
    private void fetchNowPlayingMovies() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call<MovieResponse> call = api.getNowPlayingMovies(apiKey);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                List<Movie> allMovies = response.body().getResults();
                nowPlayingMovies.clear();
                nowPlayingMovies.addAll(allMovies);
                NowPlayingAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
    private void fetchTopRatingMovies() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call<MovieResponse> call = api.getTopRatedMovies(apiKey);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                List<Movie> allMovies = response.body().getResults();
                topRatedMovies.clear();
                topRatedMovies.addAll(allMovies);
                TopRatingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }


    private void fetchUpComingMovies() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call<MovieResponse> call = api.getUpcomingMovies(apiKey);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                List<Movie> allMovies = response.body().getResults();
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                upcomingMovies.clear();
                for(Movie movie : allMovies){
                    if(movie.getRelease_date().compareTo(today) > 0){
                        upcomingMovies.add(movie);
                    }
                }
                UpComingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }

    private void fetch5PopularMovies() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class); // Create an API tool that can talk to TMDB
        Call<MovieResponse> call = api.getAllMovies(apiKey, 1); // preparing the HTTP request
        call.enqueue(new Callback<MovieResponse>() { // Now the request is sent to the server
            // enqueue() means:
            //Run network in background
            //Return result later

            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                List<Movie> allMovies = response.body().getResults();
                Movies.clear();
                for (int i = 0; i < Math.min(5, allMovies.size()); i++) {
                    Movies.add(allMovies.get(i));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    private void AutoRunSlider() {
        slideHandler.removeCallbacks(sliderRunnable);
        slideHandler.postDelayed(sliderRunnable, 3000);
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int nextIndex;
            if (viewPager == null || Movies.isEmpty()) {
                return;
            }
            nextIndex = (viewPager.getCurrentItem() + 1) % Movies.size();
            viewPager.setCurrentItem(nextIndex, true);
            slideHandler.postDelayed(this, 3000);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Movies.isEmpty()) {
            AutoRunSlider();
        }
        ;
    }
}
