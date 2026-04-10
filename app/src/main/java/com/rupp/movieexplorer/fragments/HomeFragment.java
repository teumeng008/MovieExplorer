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
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.MovieResponse;

import java.util.ArrayList;
import java.util.List;

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

    private ViewPager2 viewPager;
    private CarouselAdapter adapter;
    private List<Movie> Movies = new ArrayList<>(); // stored object's movies that each object contain title, overview, posterPath, voteAverage, backdropPath

    private Handler slideHandler = new Handler(Looper.getMainLooper());

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.cardScollBar);
        viewPager = view.findViewById(R.id.viewPager);
        adapter = new CarouselAdapter(Movies); // everything inside Movies arraylist that contain each different movie object will be passed to the adapter
        viewPager.setAdapter(adapter); // viewPager alone can't understand object of each movie directly so it need adapter to translate for it
        // so what adapter does is  //Movie object
        //     ↓
        //Convert to UI layout
        //     ↓
        //ImageView + TextView
        fetch5PopularMovies();
        AutoRunSlider();
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false)
        );
        return view;
    }

    private void fetch5PopularMovies() {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class); // Create an API tool that can talk to TMDB
        Call<MovieResponse> call = api.getAllMovies("9248253c09ac61d8b459b1b599ab133b", 1); // preparing the HTTP request
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
        if(!Movies.isEmpty()){
            AutoRunSlider();
        };
    }


//    private ViewPager2 viewPager;
//    private CarouselAdapter adapter;
//    private List<Movie> carouselMovies = new ArrayList<>();
//    private Handler sliderHandler = new Handler(Looper.getMainLooper());
//
//    public HomeFragment() {
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        viewPager = view.findViewById(R.id.viewPager);
//
//        adapter = new CarouselAdapter(carouselMovies);
//        viewPager.setAdapter(adapter);
//
//        fetchCarouselMovies();
//
//        return view;
//    }
//
//    private void fetchCarouselMovies() {
//        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
//        Call<MovieResponse> call = api.getPopularMovies("9248253c09ac61d8b459b1b599ab133b");
//
//        call.enqueue(new Callback<MovieResponse>() {
//            @Override
//            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<Movie> allMovies = response.body().getResults();
//                    carouselMovies.clear();
//
//                    // Get only the first 5 popular movies
//                    for (int i = 0; i < Math.min(5, allMovies.size()); i++) {
//                        carouselMovies.add(allMovies.get(i));
//                    }
//
//                    adapter.notifyDataSetChanged();
//                    startAutoSlider();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MovieResponse> call, Throwable t) {
//                Log.d("CAROUSEL_ERROR", t.getMessage());
//            }
//        });
//    }
//
//    private void startAutoSlider() {
//        sliderHandler.removeCallbacks(sliderRunnable); // prevent previous callbacks
//        sliderHandler.postDelayed(sliderRunnable, 3000); // 3 seconds interval
//    }
//
//    private Runnable sliderRunnable = new Runnable() {
//        @Override
//        public void run() {
//            int nextItem;
//            if (viewPager != null && !carouselMovies.isEmpty()) {
//                nextItem = viewPager.getCurrentItem() + 1;
//                if(viewPager.getCurrentItem() == carouselMovies.size() - 1){
//                    nextItem = 0;
//                }
//                viewPager.setCurrentItem(nextItem, true);
//                sliderHandler.postDelayed(this, 3000);
//            }
//        }
//    };
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        sliderHandler.removeCallbacks(sliderRunnable);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (!carouselMovies.isEmpty()) {
//            startAutoSlider();
//        }
//    }
}
