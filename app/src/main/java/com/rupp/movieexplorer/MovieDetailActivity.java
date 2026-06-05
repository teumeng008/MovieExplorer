package com.rupp.movieexplorer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rupp.movieexplorer.adapter.LoadingPeopleCardAdapter;
import com.rupp.movieexplorer.adapter.LoadingSeasonCardAdapter;
import com.rupp.movieexplorer.adapter.PeopleCardAdapter;
import com.rupp.movieexplorer.adapter.SeasonCardAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.Creator;
import com.rupp.movieexplorer.model.FavoriteItem;
import com.rupp.movieexplorer.model.Genres;
import com.rupp.movieexplorer.model.MediaItem;
import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.People;
import com.rupp.movieexplorer.model.Season;
import com.rupp.movieexplorer.model.TVShow;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageButton backBtn, favBtn;
    private ImageView poster;
    private TextView nameDetail, rating, mediaType, releaseDate, spokenLang, duration, overview,DirectorLabel,genres;

    private RecyclerView castRecyclerView, crewRecyclerView, seasonRecycle;
    private PeopleCardAdapter castAdapter, crewAdapter;
    private SeasonCardAdapter seasonAdapter;
    private List<People> castList = new ArrayList<>();
    private List<People> crewList = new ArrayList<>();
    private List<Season> seasons = new ArrayList<>();
    private LinearLayout SeasonLayout;
    private View loadingLayout;
    private View mainLayout;

    private String currentTitle;
    private String currentPosterPath;
    private double currentRating;
    private Boolean isFavorite = false;

    private int id;
    private String type; // "movie" or "tv"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        id = getIntent().getIntExtra("id", -1);
        type = getIntent().getStringExtra("type");

        if (type == null) type = "movie";

        bindViews();
        setupRecyclerViews();
        checkFavoriteState();
        setupButtons();

        fetchCredits(id);
        fetchDetail(id);
    }

    private void bindViews() {
        backBtn = findViewById(R.id.backBtn);
        favBtn = findViewById(R.id.favBtn);
        poster = findViewById(R.id.imagePosterDetail);
        nameDetail = findViewById(R.id.nameDetail);
        rating = findViewById(R.id.textRatingDetail);
        mediaType = findViewById(R.id.textTypeDetail);
        releaseDate = findViewById(R.id.textDateDetail);
        spokenLang = findViewById(R.id.textLanguageDetail);
        duration = findViewById(R.id.textDuration);
        overview = findViewById(R.id.textOverview);
        DirectorLabel = findViewById(R.id.DirectorORCreatorLabel);
        SeasonLayout = findViewById(R.id.SeasonLayout);
        genres = findViewById(R.id.textGenresDetail);
        loadingLayout = findViewById(R.id.loadingLayout);
        mainLayout = findViewById(R.id.mainContent);

    }

    private void setupRecyclerViews() {
        castRecyclerView = findViewById(R.id.castLayout);
        crewRecyclerView = findViewById(R.id.directorLayout);
        seasonRecycle = findViewById(R.id.SeasonRecycler);

        castAdapter = new PeopleCardAdapter(castList);
        crewAdapter = new PeopleCardAdapter(crewList);
        seasonAdapter = new SeasonCardAdapter(seasons);

        castRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        crewRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        seasonRecycle.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        castRecyclerView.setAdapter(new LoadingPeopleCardAdapter());
        crewRecyclerView.setAdapter(new LoadingPeopleCardAdapter());
        seasonRecycle.setAdapter(new LoadingSeasonCardAdapter());
    }

    private void checkFavoriteState(){
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("Users").document(userUID).collection("favorites").document(String.valueOf(id)).get().addOnSuccessListener(documentSnapshot->{
            isFavorite = documentSnapshot.exists();
            if(isFavorite){
                favBtn.setImageResource(R.drawable.heart_filled);
            }else {
                favBtn.setImageResource(R.drawable.favorite);
            }
        });
    }

    private void setupButtons() {
        backBtn.setOnClickListener(v -> finish());

        favBtn.setOnClickListener(v ->
            toggleFavorite()
        );
    }

    private void toggleFavorite(){
        if(isFavorite){
            removeFavorite();
            isFavorite = false;
            favBtn.setImageResource(R.drawable.favorite);
        }else {
            addFavorite();
            isFavorite = true;
            favBtn.setImageResource(R.drawable.heart_filled);
        }

    }

    private void addFavorite(){
        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FavoriteItem favoriteItem = new  FavoriteItem(id,type,currentTitle,currentPosterPath,currentRating);
        FirebaseFirestore.getInstance().collection("Users").document(userUID).collection("favorites").document(String.valueOf(id)).set(favoriteItem).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        });
    }
    private void removeFavorite(){
        String userUID =FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("Users").document(userUID).collection("favorites").document(String.valueOf(id)).delete().addOnSuccessListener(unused -> {
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        });
    }

    private void ShowLoading(Boolean isLoading){
        loadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mainLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void fetchDetail(int id) {
        ShowLoading(true);
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        if ("tv".equals(type)) {
            SeasonLayout.setVisibility(View.VISIBLE);

            Call<TVShow> call = api.getTvShowDetails(id, Constants.API_KEY);

            call.enqueue(new Callback<TVShow>() {
                @Override
                public void onResponse(Call<TVShow> call, Response<TVShow> response) {
                    TVShow result = response.body();
                    if (result == null) return;
                    ShowLoading(false);

                    //store data for Fav list
                    currentTitle = result.getName();
                    currentPosterPath = result.getPosterPath();
                    currentRating = result.getVoteAverage();

                    seasonRecycle.setAdapter(seasonAdapter);

                    nameDetail.setText(result.getName());
                    rating.setText(String.format("%.2f", result.getVoteAverage()));
                    String genresText = "";

                    for (int i = 0; i < result.getGenres().size(); i++) {
                        genresText += result.getGenres().get(i).getName();

                        if (i < result.getGenres().size() - 1) {
                            genresText += "/";
                        }
                    }
                    genres.setText(genresText);
                    mediaType.setText("TV Show");
                    releaseDate.setText(result.getFirstAirDate());
                    overview.setText(result.getOverview());
                    duration.setText(result.getNumber_of_seasons() + " Seasons");
                    seasons.addAll(result.getSeasons());
                    seasonAdapter.notifyDataSetChanged();


                    if (result.getLanguages() != null && !result.getLanguages().isEmpty()) {
                        String text = new String();
                        for(int i = 0 ;i< result.getLanguages().size();i++){
                            text += result.getLanguages().get(i);
                            if(i < result.getLanguages().size()- 1){
                                text += "\n/ ";
                            }
                        }
                        spokenLang.setText(text);
                    }

                    Picasso.get()
                            .load("https://image.tmdb.org/t/p/w500" + result.getPosterPath()).placeholder(R.drawable.black_overlay).error(R.drawable.coming_soon)
                            .into(poster);
                }

                @Override
                public void onFailure(Call<TVShow> call, Throwable t) {
                    Log.e("TV_ERROR", t.getMessage());
                }
            });

        } else {
            Call<Movie> call = api.getMovieDetails(id, Constants.API_KEY);

            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {
                    Movie result = response.body();
                    if (result == null) return;
                    ShowLoading(false);

                    //store data for Fav list
                    currentTitle = result.getTitle();
                    currentPosterPath = result.getPosterPath();
                    currentRating = result.getVoteAverage();

                    poster = findViewById(R.id.imagePosterDetail);
                    nameDetail = findViewById(R.id.nameDetail);
                    rating = findViewById(R.id.textRatingDetail);
                    mediaType = findViewById(R.id.textTypeDetail);
                    releaseDate = findViewById(R.id.textDateDetail);
                    spokenLang = findViewById(R.id.textLanguageDetail);
                    duration = findViewById(R.id.textDuration);
                    overview = findViewById(R.id.textOverview);
                    genres = findViewById(R.id.textGenresDetail);

                    nameDetail.setText(result.getTitle());
                    rating.setText(String.format("%.2f", result.getVoteAverage()));
                    mediaType.setText("Movie");
                    releaseDate.setText(result.getRelease_date());
                    String genresText = "";

                    for (int i = 0; i < result.getGenres().size(); i++) {
                        genresText += result.getGenres().get(i).getName();

                        if (i < result.getGenres().size() - 1) {
                            genresText += " / ";
                        }
                    }
                    genres.setText(genresText);
                    overview.setText(result.getOverview());

                    int hour = result.getRuntime() / 60;
                    int min = result.getRuntime() % 60;
                    duration.setText(hour + "h " + min + "m");

                    if (result.getSpoken_languages() != null && !result.getSpoken_languages().isEmpty()) {
                        String text = new String();
                        for(int i = 0 ;i< result.getSpoken_languages().size();i++){
                                text += result.getSpoken_languages().get(i).getEnglish_name();
                            if(i < result.getSpoken_languages().size()- 1){
                                text += "\n/ ";
                            }
                        }
                        spokenLang.setText(text);
                    }

                    Picasso.get()
                            .load("https://image.tmdb.org/t/p/w500" + result.getPosterPath())
                            .into(poster);
                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {
                    Log.e("MOVIE_ERROR", t.getMessage());
                }
            });
        }
    }

    private void fetchCredits(int id) {
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);

        Call<MediaItem> call;
        Call<TVShow> CreatorCall;
        if ("tv".equals(type)) {

            call = api.getTvPeople(id, Constants.API_KEY);
            call.enqueue(new Callback<MediaItem>() {
                @Override
                public void onResponse(Call<MediaItem> call, Response<MediaItem> response) {
                    castRecyclerView.setAdapter(castAdapter);
                    List<People> peoples = response.body().getCast();
                    castList.clear();
                    castList.addAll(peoples);
                    castAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<MediaItem> call, Throwable t) {
                    Log.d("False to Fetch Cast",t.getMessage());
                }
            });


            CreatorCall = api.getTvShowDetails(id,Constants.API_KEY);
            CreatorCall.enqueue(new Callback<TVShow>() {
                @Override
                public void onResponse(Call<TVShow> call, Response<TVShow> response) {
                    crewRecyclerView.setAdapter(crewAdapter);
                   List<Creator> creator = response.body().getCreator();


                    if(creator.isEmpty() || creator == null){
                        DirectorLabel.setVisibility(View.GONE);
                    }else {
                        DirectorLabel.setText("Creator");
                    }

                    crewList.clear();
                   for(Creator c : creator){
                       People selected = new People();
                       selected.setProfile_image(c.getProfile_path());
                       selected.setName(c.getName());
                       crewList.add(selected);
                   }
                   crewAdapter.notifyDataSetChanged();
//                   Log.d("creator",String.valueOf(result.size()));
                }

                @Override
                public void onFailure(Call<TVShow> call, Throwable t) {
                    Log.d("CreatorError",t.getMessage());
                }
            });


        } else {
            call = api.getPeople(id, Constants.API_KEY);
            call.enqueue(new Callback<MediaItem>() {
                @Override
                public void onResponse(Call<MediaItem> call,
                                       Response<MediaItem> response) {

                    if (response.body() == null) return;
                    castRecyclerView.setAdapter(castAdapter);
                    crewRecyclerView.setAdapter(crewAdapter);
                    castList.clear();
                    crewList.clear();

                    castList.addAll(response.body().getCast());
                    for (People p : response.body().getCrew()) {
                        if ("Director".equals(p.getJob())) {
                            if(p.getJob() == null){
                                DirectorLabel.setVisibility(View.GONE);
                            }
                            crewList.add(p);
                        }
                    }


                    castAdapter.notifyDataSetChanged();
                    crewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<MediaItem> call, Throwable t) {
                    Log.e("CREDITS_ERROR", t.getMessage());
                }
            });
        }
    }
}