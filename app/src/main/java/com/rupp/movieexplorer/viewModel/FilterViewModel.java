package com.rupp.movieexplorer.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rupp.movieexplorer.model.FilterData;

public class FilterViewModel extends ViewModel {
    private final MutableLiveData<FilterData> movieFilters = new MutableLiveData<>();
    private final MutableLiveData<FilterData> tvFilters = new MutableLiveData<>();

    public void setMovieFilters(String genres, Integer year, Double rating){
        movieFilters.setValue(new FilterData(genres, year, rating));
    }
    public void setTvFilters(String genres, Integer year, Double rating){
        tvFilters.setValue(new FilterData(genres, year, rating));
    }

    public LiveData<FilterData> getMovieFilters(){
        return  movieFilters;
    }

    public LiveData<FilterData> getTvFilters() {
        return tvFilters;
    }
}
