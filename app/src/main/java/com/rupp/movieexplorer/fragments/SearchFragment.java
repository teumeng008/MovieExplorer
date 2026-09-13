package com.rupp.movieexplorer.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
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
import android.widget.ImageButton;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.rupp.movieexplorer.Constants;
import com.rupp.movieexplorer.MovieDetailActivity;
import com.rupp.movieexplorer.PersonDetailActivity;
import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.adapter.LoadingAdapter;
import com.rupp.movieexplorer.adapter.PeopleCardAdapter;
import com.rupp.movieexplorer.adapter.SuggestionAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.Genres;
import com.rupp.movieexplorer.model.MediaItem;
import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.MultiResponse;
import com.rupp.movieexplorer.model.People;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchFragment extends Fragment {


    public SearchFragment() {
        // Required empty public constructor
    }
    RecyclerView suggestion_view;
    SuggestionAdapter suggestionAdapter;
    List<MediaItem> suggestList = new ArrayList<>();
    TextInputEditText search_text;
    ImageButton imageButton;
    NestedScrollView categoryCtn;
    ChipGroup categoryGroup;
    List<Integer> selectedGenreIds = new ArrayList<>();
    private Handler searchHandler;
    private Runnable searchRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchHandler = new Handler();

        imageButton = view.findViewById(R.id.categoryBtn);
        categoryGroup = view.findViewById(R.id.categoryChipGroup);
        categoryCtn = view.findViewById(R.id.categoryCtn);

        createDynamicGenreChips();

        search_text = view.findViewById(R.id.search_text);
        suggestion_view = view.findViewById(R.id.suggestion_view);
        suggestionAdapter = new SuggestionAdapter(suggestList,item ->{
            if(item.getMediaType().equals("person")){
                People people = new People();
                people.setId(item.getId());
                openPersonDetail(people);
                return;
            }
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
                    search(query);

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

        imageButton.setOnClickListener( v -> {
            openCategory();
        });

        categoryGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            selectedGenreIds.clear();
            if(!checkedIds.isEmpty()){
                int checkedId = checkedIds.get(0);
                Chip chip = group.findViewById(checkedId);
                if(chip != null && chip.getTag() != null){
                    selectedGenreIds.addAll((List<Integer>) chip.getTag());
                }
            }
            String CurrentQuery = search_text.getText().toString().trim();
            if(CurrentQuery.length()>= 2 ){
                search(CurrentQuery);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void  openCategory(){

        if(categoryCtn.getVisibility() == View.VISIBLE){
            categoryCtn.animate().alpha(0f).translationY(-20f).setDuration(200).withEndAction(() -> categoryCtn.setVisibility(View.GONE)).start();
        }else {
            categoryCtn.setVisibility(View.VISIBLE);
            categoryCtn.setAlpha(0f);
            categoryCtn.setTranslationY(-20f);

            categoryCtn.animate().alpha(1f).translationY(0f).setDuration(200).start();
        }
    }
    private void createDynamicGenreChips(){
        categoryGroup.removeAllViews();

        Map<String, List<Integer>> unifiedGenres = new HashMap<>();
        addGenreMap(unifiedGenres, "Action & Adventure", new int[]{28, 12, 10759});
        addGenreMap(unifiedGenres, "Animation", new int[]{16});
        addGenreMap(unifiedGenres, "Comedy", new int[]{35});
        addGenreMap(unifiedGenres, "Crime", new int[]{80});
        addGenreMap(unifiedGenres, "Documentary", new int[]{99});
        addGenreMap(unifiedGenres, "Drama", new int[]{18});
        addGenreMap(unifiedGenres, "Family", new int[]{10751});
        addGenreMap(unifiedGenres, "Kids", new int[]{10762});
        addGenreMap(unifiedGenres, "Mystery", new int[]{9648});
        addGenreMap(unifiedGenres, "Sci-Fi & Fantasy", new int[]{878, 14, 10765});
        addGenreMap(unifiedGenres, "War & Politics", new int[]{10752, 10768});
        addGenreMap(unifiedGenres, "Western", new int[]{37});

        // Missing genres added below
        addGenreMap(unifiedGenres, "History", new int[]{36});
        addGenreMap(unifiedGenres, "Horror", new int[]{27});
        addGenreMap(unifiedGenres, "Music", new int[]{10402});
        addGenreMap(unifiedGenres, "Romance", new int[]{10749});
        addGenreMap(unifiedGenres, "Thriller", new int[]{53});
        addGenreMap(unifiedGenres, "TV Movie", new int[]{10770});
        addGenreMap(unifiedGenres, "News", new int[]{10763});
        addGenreMap(unifiedGenres, "Reality", new int[]{10764});
        addGenreMap(unifiedGenres, "Soap", new int[]{10766});
        addGenreMap(unifiedGenres, "Talk", new int[]{10767});

        for (Map.Entry<String, List<Integer>> entry : unifiedGenres.entrySet()){
            Chip chip = new Chip(getContext());
            // Configure behavior and look
            chip.setText(entry.getKey());
            chip.setTag(entry.getValue()); // Stashing our ID array secretly here!
            chip.setCheckable(true);
            chip.setClickable(true);

//            // Optional: Style your programmatic chips to match Material standards
//            chip.setChipCornerRadius(24f);

            // 4. Inject the newly minted chip into your layout XML group container
            categoryGroup.addView(chip);
        }
    }
    private void addGenreMap(Map<String, List<Integer>> map, String name, int[] ids){
        List<Integer> idList = new ArrayList<>();
        for(int id : ids){
            idList.add(id);
        }
        map.put(name, idList);
    }
    private void search(String query){
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call<MultiResponse> call = api.searchMovieAndTVShow(Constants.API_KEY,query);
        call.enqueue(new Callback<MultiResponse>() {
            @Override
            public void onResponse(Call<MultiResponse> call, Response<MultiResponse> response) {
                List<MediaItem> allItem = response.body().getResults();
                List<MediaItem> SelectedItem = new ArrayList<>();
                List<MediaItem> peopleList = new ArrayList<>();
                // clean query
                for(MediaItem i : allItem ){

                    if(i.getMediaType().equals("person")){
                        peopleList.add(i);
                        continue;
                    }

                    if(!selectedGenreIds.isEmpty()){
                        if(i.getGenre_ids() == null){
                            continue;
                        }

                        boolean matchGenre = false;
                        for (int id : i.getGenre_ids()){
                            if(selectedGenreIds.contains(id)){
                                matchGenre = true;
                                break;
                            }
                        }
                        if(!matchGenre){
                            continue;
                        }
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
                Collections.sort(SelectedItem,(a, b)->
                        Double.compare(b.getItemScore(), a.getItemScore())
                );
                suggestion_view.setAdapter(suggestionAdapter);
                suggestList.clear();
                suggestList.addAll(peopleList);
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
        Intent intent = new Intent(requireContext(), MovieDetailActivity.class);

        intent.putExtra("id", item.getId());
        intent.putExtra("type",item.getMediaType());

        startActivity(intent);
    }
    private void openPersonDetail(People people){
        Intent intent = new Intent(requireContext(), PersonDetailActivity.class);
        intent.putExtra("Person_id", people.getId());
        startActivity(intent);
    }
//    private void openMovieDetail(Movie movie){
//        Intent intent = new Intent(requireContext(),MovieDetailActivity.class);
//
//        intent.putExtra("id", movie.getId());
//        intent.putExtra("type","movie");
//
//        startActivity(intent);
//    }
}