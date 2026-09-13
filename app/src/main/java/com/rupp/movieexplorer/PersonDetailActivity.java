package com.rupp.movieexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rupp.movieexplorer.adapter.CreditsAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.helperClass.GenresList;
import com.rupp.movieexplorer.helperClass.MediaTypeList;
import com.rupp.movieexplorer.model.ConsolidatedCredit;
import com.rupp.movieexplorer.model.FavoritePerson;
import com.rupp.movieexplorer.model.PersonDetails;
import com.rupp.movieexplorer.model.PersonInfo;
import com.rupp.movieexplorer.viewModel.FavoritePersonViewModel;
import com.rupp.movieexplorer.viewModel.PersonDetailViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView profile_image, favIcon;
    private TextView name, gender, birthday, deathday, birth_place, biography, nicknames, favText, noDataCastText, noDataCrewText;
    private MaterialCardView favBtn;
    private TextView actingCreditsText, crewCreditsText;
    private RecyclerView castRecycler, crewRecycler;
    private List<ConsolidatedCredit> castInfoList;
    private List<ConsolidatedCredit> crewInfoList;
    private CreditsAdapter castAdapter, crewAdapter;
    private View loadingLayout, mainLayout;

    private ChipGroup media_type_groups, release_year_groups, genres_groups;

    private FirebaseAuth auth;
    private FirebaseFirestore database;

    private int Person_id, genderData;
    private String nameData, profile_path;

    private PersonDetailViewModel viewModel;

    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        Person_id = getIntent().getIntExtra("Person_id", -1);
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        viewModel = new PersonDetailViewModel();

        binding();
        setupAdapter();
        checkStateBtn();
        setupButton();
        setupChipGroupItem();
        setUpObservers();

        showLoading(true);
        viewModel.fetchPerson(Person_id);
        viewModel.fetchPersonCredits(Person_id);
    }

    private void showLoading(boolean isLoading) {
        if (loadingLayout != null && mainLayout != null) {
            loadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            mainLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    private void binding() {
        toolbar = findViewById(R.id.toolbar);
        profile_image = findViewById(R.id.ProfileImage);
        name = findViewById(R.id.Name);
        gender = findViewById(R.id.Gender);
        birthday = findViewById(R.id.BirthDay);
        deathday = findViewById(R.id.DeathDay);
        birth_place = findViewById(R.id.BirthPlace);
        biography = findViewById(R.id.Biography);
        nicknames = findViewById(R.id.Nicknames);
        castRecycler = findViewById(R.id.castingRecycler);
        crewRecycler = findViewById(R.id.crewRecycler);
        actingCreditsText = findViewById(R.id.ActingCreditsText);
        crewCreditsText = findViewById(R.id.CrewCreditsText);
        favBtn = findViewById(R.id.favBtn);
        favIcon = findViewById(R.id.favIcon);
        favText = findViewById(R.id.favText);
        media_type_groups = findViewById(R.id.media_type_groups);
        release_year_groups = findViewById(R.id.release_year_groups);
        genres_groups = findViewById(R.id.genre_groups);
        noDataCastText = findViewById(R.id.noDataCastText);
        noDataCrewText = findViewById(R.id.noDataCrewText);
        loadingLayout = findViewById(R.id.loadingLayout);
        mainLayout = findViewById(R.id.mainContent);
    }

    private void setupButton() {
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        favBtn.setOnClickListener(v -> {
            toggleFavoritePerson();
//            Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkStateBtn() {
        String UserUID = auth.getCurrentUser().getUid();
        database.collection("Users").document(UserUID).collection("favorite_person").document(String.valueOf(Person_id)).get().addOnSuccessListener(documentSnapshot -> {
            isFavorite = documentSnapshot.exists();
            if (isFavorite) {
                favIcon.setImageResource(R.drawable.heart_filled);
            } else {
                favIcon.setImageResource(R.drawable.favorite);
            }
        });
    }

    private void toggleFavoritePerson() {
        if (isFavorite) {
            removeFavoritePerson();
            favIcon.setImageResource(R.drawable.favorite);
            isFavorite = false;
        } else {
            addFavoritePerson();
            favIcon.setImageResource(R.drawable.heart_filled);
            isFavorite = true;
        }
    }

    private void addFavoritePerson() {
        FavoritePerson favoritePerson = new FavoritePerson(Person_id, nameData, genderData, profile_path);
        String UserUID = auth.getCurrentUser().getUid();
        database.collection("Users").document(UserUID).collection("favorite_person").document(String.valueOf(Person_id)).set(favoritePerson).addOnSuccessListener(unused -> {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "Added Person to favorites",
                    Snackbar.LENGTH_SHORT
            ).show();
        });

    }

    private void removeFavoritePerson() {
        String UserUID = auth.getCurrentUser().getUid();
        database.collection("Users").document(UserUID).collection("favorite_person").document(String.valueOf(Person_id)).delete().addOnSuccessListener(unused -> {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    "Remove Person from favorites",
                    Snackbar.LENGTH_SHORT
            ).show();
        });
    }

    private void setupAdapter() {
        castInfoList = new ArrayList<>();
        crewInfoList = new ArrayList<>();

        castAdapter = new CreditsAdapter(castInfoList, cast -> {
            openDetailPage(cast);
        });
        crewAdapter = new CreditsAdapter(crewInfoList, crew -> {
            openDetailPage(crew);
        });

        castRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        crewRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        castRecycler.setAdapter(castAdapter);
        crewRecycler.setAdapter(crewAdapter);
    }


    //    private void fetch(){
//        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
//        Call<PersonInfo> call = api.getPersonInfo(Person_id, Constants.API_KEY);
//        Call<PersonInfo> personDetailsCall = api.getPersonCredits(Person_id, Constants.API_KEY);
//
//        call.enqueue(new Callback<PersonInfo>() {
//            @Override
//            public void onResponse(Call<PersonInfo> call, Response<PersonInfo> response) {
//                if(response.isSuccessful() && response.body() != null) {
//                    PersonInfo personInfo = response.body();
//
//                    nameData = personInfo.getName();
//                    genderData = personInfo.getGender();
//                    profile_path = personInfo.getProfile_path();
//
//                    String nicknameList, ImageURL, TextGender;
//                    ImageURL = "https://image.tmdb.org/t/p/w500" + personInfo.getProfile_path();
//                    StringBuilder builder = new StringBuilder();
//
//                    Picasso.get().load(ImageURL).into(profile_image);
//                    name.setText(personInfo.getName());
//                    if (personInfo.getGender() == 2) {
//                        TextGender = "Male";
//                    } else {
//                        TextGender = "Female";
//                    }
//                    gender.setText(TextGender);
//                    birthday.setText(personInfo.getBirthday());
//                    deathday.setText(personInfo.getDeathday());
//                    birth_place.setText(personInfo.getPlace_of_birth());
//                    biography.setText(personInfo.getBiography());
//
//                    for (String name : personInfo.getAlso_known_as()) {
//                        builder.append(name);
//                        builder.append("/");
//                    }
//                    nicknameList = builder.toString();
//                    nicknames.setText(nicknameList);
//                }
//
//                personDetailsCall.enqueue(new Callback<PersonInfo>() {
//                    @Override
//                    public void onResponse(Call<PersonInfo> call, Response<PersonInfo> response) {
//                        if(response.body() != null && response.isSuccessful()){
//                            if(!response.body().getCastInfos().isEmpty()){
//                                actingCreditsText.setVisibility(View.VISIBLE);
//                                castRecycler.setVisibility(View.VISIBLE);
//
//                                List<PersonDetails> casts = response.body().getCastInfos();
//                                castInfoList.clear();
//                                castInfoList.addAll(groupCredits(casts));
//                                castAdapter.notifyDataSetChanged();
//                            }else {
//                                actingCreditsText.setVisibility(View.GONE);
//                                castRecycler.setVisibility(View.GONE);
//                            }
//
//
//                            if(!response.body().getCrewInfos().isEmpty()){
//                                crewCreditsText.setVisibility(View.VISIBLE);
//                                crewRecycler.setVisibility(View.VISIBLE);
//
//                                List<PersonDetails> crews = response.body().getCrewInfos();
//                                crewInfoList.clear();
//                                crewInfoList.addAll(groupCredits(crews));
//                                crewAdapter.notifyDataSetChanged();
//                            }else {
//                                crewCreditsText.setVisibility(View.GONE);
//                                crewRecycler.setVisibility(View.GONE);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<PersonInfo> call, Throwable t) {
//                        Log.d("Cast Or Crew Fetch Error", t.getMessage());
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Call<PersonInfo> call, Throwable t) {
//                    Log.d("PersonInfoFetching Error", t.getMessage());
//            }
//        });
//    }
    private void setUpObservers() {
        viewModel.getPersonInfo().observe(this, personInfo -> {
            showLoading(false);
            nameData = personInfo.getName();
            genderData = personInfo.getGender();
            profile_path = personInfo.getProfile_path();

            String nicknameList, ImageURL, TextGender;
            ImageURL = "https://image.tmdb.org/t/p/w500" + personInfo.getProfile_path();
            StringBuilder builder = new StringBuilder();

            Picasso.get().load(ImageURL).into(profile_image);
            name.setText(personInfo.getName());
            if (personInfo.getGender() == 2) {
                TextGender = "Male";
            } else {
                TextGender = "Female";
            }
            gender.setText(TextGender);
            birthday.setText(personInfo.getBirthday());
            if(personInfo.getDeathday() != null && !personInfo.getDeathday().isEmpty()){
                deathday.setVisibility(View.VISIBLE);
                deathday.setText(personInfo.getDeathday());
            }else {
                deathday.setVisibility(View.GONE);
            }

            birth_place.setText(personInfo.getPlace_of_birth());
            biography.setText(personInfo.getBiography());

            for (String name : personInfo.getAlso_known_as()) {
                builder.append(name);
                builder.append("/");
            }
            nicknameList = builder.toString();
            nicknames.setText(nicknameList);
        });

        viewModel.getCastCredits().observe(this, consolidatedCredits -> {
            castInfoList.clear();
            castInfoList.addAll(consolidatedCredits);
            castAdapter.notifyDataSetChanged();
            castRecycler.setVisibility(consolidatedCredits.isEmpty() ? View.GONE : View.VISIBLE);
            noDataCastText.setVisibility(consolidatedCredits.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getCrewCredits().observe(this, consolidatedCredits -> {
            crewInfoList.clear();
            crewInfoList.addAll(consolidatedCredits);
            crewAdapter.notifyDataSetChanged();
            crewRecycler.setVisibility(consolidatedCredits.isEmpty() ? View.GONE : View.VISIBLE);
            noDataCrewText.setVisibility(consolidatedCredits.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getReleaseYearList().observe(this, years -> {
            setupReleaseYearChips(years);
        });

        viewModel.getFilterCastList().observe(this, consolidatedCredits -> {
            castInfoList.clear();
            castInfoList.addAll(consolidatedCredits);
            castAdapter.notifyDataSetChanged();
            castRecycler.setVisibility(consolidatedCredits.isEmpty() ? View.GONE : View.VISIBLE);
            noDataCastText.setVisibility(consolidatedCredits.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getFilterCrewList().observe(this, consolidatedCredits -> {
            crewInfoList.clear();
            crewInfoList.addAll(consolidatedCredits);
            crewAdapter.notifyDataSetChanged();
            crewRecycler.setVisibility(consolidatedCredits.isEmpty() ? View.GONE : View.VISIBLE);
            noDataCrewText.setVisibility(consolidatedCredits.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void setupChipGroupItem() {

        // MediaType ChipGroup
        for (String type : MediaTypeList.getMediaTypeList()) {
            if (!type.equals("person")) {
                Chip chip = new Chip(this);
                chip.setTag(type);
                chip.setText(type);
                chip.setCheckable(true);
                chip.setClickable(true);
                media_type_groups.addView(chip);
            }
        }
        media_type_groups.setOnCheckedStateChangeListener((group, checkedIds )->{
            if(!checkedIds.isEmpty()){
                Chip chip = group.findViewById(checkedIds.get(0));
                viewModel.setSelectedMediaType(chip.getTag().toString());

                // Genres ChipGroup
                genres_groups.removeAllViews();
                for (Map.Entry<String, Integer> entry : GenresList.getGenres((String) chip.getText()).entrySet()) {
                    Chip Gchip = new Chip(this);
                    Gchip.setTag(entry.getValue());
                    Gchip.setText(entry.getKey());
                    Gchip.setClickable(true);
                    Gchip.setCheckable(true);
                    genres_groups.addView(Gchip);
                }

            }else {
                viewModel.setSelectedMediaType(null);
            }
        });



        genres_groups.setOnCheckedStateChangeListener((group, checkedIds) ->{
            List<Integer> selectedGenreIds = new ArrayList<>();
            for (Integer id : checkedIds){
                Chip chip = group.findViewById(id);
                selectedGenreIds.add((Integer) chip.getTag());
            }
            viewModel.setSelectedGenres(selectedGenreIds);
        });

        release_year_groups.setOnCheckedStateChangeListener((group, checkedIds) ->{
            if (!checkedIds.isEmpty()){
                Chip chip = group.findViewById(checkedIds.get(0));
                viewModel.setSelectedYear((Integer) chip.getTag());
            }else {
                viewModel.setSelectedYear(null);
            }
        });
    }

    private void setupReleaseYearChips(List<Integer> yearList) {


        release_year_groups.removeAllViews();
        // ReleaseYear ChipGroup
        for (int year : yearList) {
            Chip chip = new Chip(this);
            chip.setText(String.valueOf(year));
            chip.setTag(year);
            chip.setClickable(true);
            chip.setCheckable(true);
            release_year_groups.addView(chip);
        }
    }


    private void openDetailPage(PersonDetails personDetails) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("id", personDetails.getId());
        intent.putExtra("type", personDetails.getMedia_type());

        startActivity(intent);
    }
}
