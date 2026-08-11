package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.Constants;
import com.rupp.movieexplorer.MovieDetailActivity;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.LoadingAdapter;
import com.rupp.movieexplorer.adapter.MediaAdapter;
import com.rupp.movieexplorer.adapter.MovieCardAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.MediaItem;
import com.rupp.movieexplorer.model.TVResponse;
import com.rupp.movieexplorer.model.TVShow;
import com.rupp.movieexplorer.viewModel.FilterViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvShowListFragment extends Fragment {
    public TvShowListFragment(){

    }
    private FilterViewModel filterViewModel;

    RecyclerView tvShowRecycler;
    MediaAdapter adapter;
    List<MediaItem> tvShowList = new ArrayList<>();
    String genres;
    Integer year;
    Double rating;
    int CurrentPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_tv_shows_list, viewGroup, false);

        tvShowRecycler = view.findViewById(R.id.tvShowRecycler);
        adapter = new MediaAdapter(tvShowList, TV ->{
            openTvShowDetail((TVShow) TV);
        });
        adapter.setPaginationListener(new MediaAdapter.PaginationListener() {
            @Override
            public void onNextPage() {
                CurrentPage++;
                adapter.setCurrentPage(CurrentPage);
                fetchTvShow();
            }

            @Override
            public void onPreviousPage() {
                CurrentPage--;
                adapter.setCurrentPage(CurrentPage);
                fetchTvShow();
            }
        });

        tvShowRecycler.setAdapter(new LoadingAdapter());
        tvShowRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchTvShow();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        filterViewModel.getTvFilters().observe(getViewLifecycleOwner(), filterData -> {
            if(filterData != null){
                updateFilters(filterData.getGenres(), filterData.getYear(), filterData.getRating());
            }
        });
    }

    /**
     * Updates the filtering criteria and refreshes the TV show list from the first page.
     */
    public void updateFilters(String genres, Integer year, Double rating) {
        this.genres = genres;
        this.year = year;
        this.rating = rating;
        this.CurrentPage = 1;

        if (adapter != null) {
            adapter.setCurrentPage(CurrentPage);
        }

        fetchTvShow();
    }

    private void fetchTvShow(){
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call call = api.getAllTvShows(Constants.API_KEY, CurrentPage,genres, year, rating);
        call.enqueue(new Callback<TVResponse>(){

            @Override
            public void onResponse(Call<TVResponse> call, Response<TVResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvShowRecycler.setAdapter(adapter);
                    List<TVShow> tvShow = response.body().getResults();
                    tvShowList.clear();
                    tvShowList.addAll(tvShow);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d("Error_TV_FETCH", "Response unsuccessful or empty body");
                }
            }

            @Override
            public void onFailure(Call<TVResponse> call, Throwable t) {
                Log.d("Error_TV_FETCH", t.getMessage());
            }
        });
    }

    private void openTvShowDetail(TVShow tvShow){
        Intent intent = new Intent(requireContext(), MovieDetailActivity.class);
        intent.putExtra("type", "tv");
        intent.putExtra("id", tvShow.getId());

        startActivity(intent);
    }

}
