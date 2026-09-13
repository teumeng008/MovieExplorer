package com.rupp.movieexplorer.viewModel;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rupp.movieexplorer.Constants;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.ConsolidatedCredit;
import com.rupp.movieexplorer.model.FilterData;
import com.rupp.movieexplorer.model.Genres;
import com.rupp.movieexplorer.model.PersonDetails;
import com.rupp.movieexplorer.model.PersonInfo;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonDetailViewModel extends ViewModel {
    private MutableLiveData<PersonInfo> personInfo = new MutableLiveData<>();
    private MutableLiveData<List<ConsolidatedCredit>> castCredits = new MutableLiveData<>();
    private MutableLiveData<List<ConsolidatedCredit>> crewCredits = new MutableLiveData<>();
    private MutableLiveData<Boolean> favorite = new MutableLiveData<>();
    private MutableLiveData<String> selectedMediaType = new MutableLiveData<>();
    private MutableLiveData<List<Integer>> selectedGenres = new MutableLiveData<>();
    private MutableLiveData<Integer> selectedYear = new MutableLiveData<>();
    private MutableLiveData<List<Integer>> releaseYearList= new MutableLiveData<>();

    private MutableLiveData<List<ConsolidatedCredit>> filterCastList = new MutableLiveData<>();
    private MutableLiveData<List<ConsolidatedCredit>> filterCrewList = new MutableLiveData<>();

    public LiveData<List<ConsolidatedCredit>> getFilterCastList() {
        return filterCastList;
    }
    public LiveData<List<ConsolidatedCredit>> getFilterCrewList() {
        return filterCrewList;
    }
    public LiveData<PersonInfo> getPersonInfo() {
        return personInfo;
    }

    public LiveData<List<ConsolidatedCredit>> getCastCredits() {
        return castCredits;
    }

    public LiveData<List<ConsolidatedCredit>> getCrewCredits() {
        return crewCredits;
    }

    public LiveData<List<Integer>> getReleaseYearList() {
        return releaseYearList;
    }

    public LiveData<Boolean> getFavorite() {
        return favorite;
    }

    public void setSelectedMediaType(String type){
        selectedMediaType.setValue(type);
        applyingFilters();
    }
    public void setSelectedGenres(List<Integer> genres){
        selectedGenres.setValue(genres);
        applyingFilters();
    }
    public void setSelectedYear(Integer year){
        selectedYear.setValue(year);
        applyingFilters();
    }

    public void applyingFilters(){
        List<ConsolidatedCredit> allCast = castCredits.getValue();
        List<ConsolidatedCredit> allCrew = crewCredits.getValue();

        filterCastList.setValue(filter(allCast));
        filterCrewList.setValue(filter(allCrew));
    }
    private Integer getYear(String date){
        LocalDate Date = LocalDate.parse(date);
        return Date.getYear();
    }

    private List<ConsolidatedCredit> groupCredits(List<PersonDetails> rawList) {
        Map<Integer, ConsolidatedCredit> map = new LinkedHashMap<>();

        for (PersonDetails personDetails : rawList) {
            int id = personDetails.getId();
            if (map.containsKey(id)) {
                if (!map.get(id).getJobs().contains(personDetails.getJob())) {
                    map.get(id).getJobs().add(personDetails.getJob());
                }
            } else {
                // Create a new entry
                List<String> jobs = new ArrayList<>();
                if (personDetails.getJob() != null) {
                    jobs.add(personDetails.getJob());
                }
                map.put(id, new ConsolidatedCredit(personDetails, jobs));
            }
        }
        return new ArrayList<>(map.values());
    }

    public void fetchPerson(int Person_Id) {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call<PersonInfo> call = api.getPersonInfo(Person_Id, Constants.API_KEY);
        call.enqueue(new Callback<PersonInfo>() {
            @Override
            public void onResponse(Call<PersonInfo> call, Response<PersonInfo> response) {
                PersonInfo info = response.body();

                personInfo.setValue(info);
            }

            @Override
            public void onFailure(Call<PersonInfo> call, Throwable t) {
                Log.d("PERSON_FETCH_ERROR", t.getMessage());
            }
        });
    }

    public void fetchPersonCredits(int Person_Id) {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call<PersonInfo> call = api.getPersonCredits(Person_Id, Constants.API_KEY);
        call.enqueue(new Callback<PersonInfo>() {
            @Override
            public void onResponse(Call<PersonInfo> call, Response<PersonInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PersonInfo info = response.body();
                    List<PersonDetails> castCreditList = info.getCastInfos();
                    List<PersonDetails> crewCreditList = info.getCrewInfos();

                    castCredits.setValue(groupCredits(castCreditList));
                    crewCredits.setValue(groupCredits(crewCreditList));

                    setReleaseYear(castCreditList, crewCreditList);
                }
            }

            @Override
            public void onFailure(Call<PersonInfo> call, Throwable t) {
                Log.d("PERSON_FETCH_ERROR", t.getMessage());
            }
        });
    }

    private void setReleaseYear(List<PersonDetails> castCredits, List<PersonDetails> crewCredits) {
        Set<Integer> allYear = new TreeSet<>(); // List which has sort and NON-Dup value BuiltIn system

        if (castCredits != null) {
            for (PersonDetails detail : castCredits) {
                String dateStr = detail.getReleaseDate();
                if (dateStr != null && !dateStr.isEmpty()) {
                    try {
                        allYear.add(LocalDate.parse(dateStr).getYear());
                    } catch (Exception ignored) {}
                }
            }
        }

        if (crewCredits != null) {
            for (PersonDetails detail : crewCredits) {
                String dateStr = detail.getReleaseDate();
                if (dateStr != null && !dateStr.isEmpty()) {
                    try {
                        allYear.add(LocalDate.parse(dateStr).getYear());
                    } catch (Exception ignored) {}
                }
            }
        }

        if (!allYear.isEmpty()) {
            releaseYearList.setValue(new ArrayList<>(allYear));
        }
    }

    private List<ConsolidatedCredit> filter(List<ConsolidatedCredit> rawList){
       List<ConsolidatedCredit> filteredList = new ArrayList<>();
       String type = selectedMediaType.getValue();
       List<Integer> genres = selectedGenres.getValue();
       Integer year = selectedYear.getValue();

       // Filters Rule
        for (ConsolidatedCredit credit : rawList){
            PersonDetails creditDetail = credit.getPersonDetails();
            boolean match = true; //

            // Filter by Type (Movie/TV)
            if(type != null && !type.isEmpty() && !creditDetail.getMedia_type().equalsIgnoreCase(type)){
                match = false;
            }
            // Filter by Genre (Matches if any selected genre is in the credit)
            if (match && genres != null && !genres.isEmpty()){
                boolean hasGenres = false;
                if(creditDetail.getGenres_ids() != null){
                    for (Integer g : genres){
                        if(creditDetail.getGenres_ids().contains(g)){
                            hasGenres = true;
                            break;
                        }
                    }
                }
                if (!hasGenres) match = false;
            }

            if (match && year != null){
                String date = creditDetail.getReleaseDate();
                if (date == null || date.isEmpty() || !getYear(date).equals(year)){
                    match = false;
                }
            }

            if (match){
                filteredList.add(credit);
            }
        }
       return filteredList;
    }
}
