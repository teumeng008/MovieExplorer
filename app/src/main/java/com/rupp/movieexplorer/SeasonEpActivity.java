package com.rupp.movieexplorer;

import android.os.Bundle;


import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.adapter.SeasonEpAdapter;
import com.rupp.movieexplorer.adapter.VideoCardAdapter;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.Episode;
import com.rupp.movieexplorer.model.SeasonDetail;
import com.rupp.movieexplorer.model.Video;
import com.rupp.movieexplorer.model.VideoResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeasonEpActivity extends AppCompatActivity {
    private CollapsingToolbarLayout header;
    private ImageButton backBtn;
    private ImageView seasonPoster;
    private TextView  airDate, avgRating, overview;
    private RecyclerView seasonEpRecycler, seasonTrailerRecycler;
    private SeasonEpAdapter epAdapter;
    private VideoCardAdapter seasonTrailerAdapter;
    private List<Episode> episodes;
    private List<Video> seasonTrailerList;
    private int Tv_Show_id, Season_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        episodes = new ArrayList<>();
        seasonTrailerList = new ArrayList<>();

       Tv_Show_id = getIntent().getIntExtra("id",-1);
       Season_number = getIntent().getIntExtra("season_number",-1);

        setContentView(R.layout.activity_season_ep);


        binding();
        setupRecyclerAndAdapter();
        setupBtn();
        fetchSeasonDetail();
        fetchSeasonTrailer();
    }
    private void binding(){
        header = findViewById(R.id.header);
        backBtn = findViewById(R.id.backBtn);
        seasonPoster = findViewById(R.id.headerBackground);
        airDate = findViewById(R.id.season_air_date);
        avgRating = findViewById(R.id.season_avg_rating);
        overview = findViewById(R.id.season_overview);
        seasonEpRecycler = findViewById(R.id.seasonEpRecycler);
        seasonTrailerRecycler = findViewById(R.id.seasonTrailerRecycler);
    }

    private void setupRecyclerAndAdapter(){
        epAdapter = new SeasonEpAdapter(episodes, (epNum,position) -> {
            fetchClip(epNum, position);
        });
        seasonTrailerAdapter = new VideoCardAdapter(seasonTrailerList);

        seasonTrailerRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        seasonEpRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        seasonTrailerRecycler.setAdapter(seasonTrailerAdapter);
        seasonEpRecycler.setAdapter(epAdapter);

    }

    private void setupBtn(){
        backBtn.setOnClickListener(view -> {
            finish();
        });
    }
    private void fetchSeasonDetail(){
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call callEp = api.getTvShowSeasonDetail(Tv_Show_id, Season_number, Constants.API_KEY);
        callEp.enqueue(new Callback<SeasonDetail>() {
            @Override
            public void onResponse(Call<SeasonDetail> call, Response<SeasonDetail> response) {
                if(response.body() != null){
                    SeasonDetail seasonDetail = response.body();
                    List<Episode> Results = response.body().getEpisodes();
                    String ImageURL = "https://image.tmdb.org/t/p/w500" + seasonDetail.getPoster_path();
                    Picasso.get().load(ImageURL).into(seasonPoster);
                    header.setTitle("Season " + seasonDetail.getSeason_number());
                    overview.setText(seasonDetail.getOverview());
                    avgRating.setText(String.format("%.02f",seasonDetail.getVote_average()));
                    airDate.setText(seasonDetail.getAir_date());
                    episodes.clear();
                    episodes.addAll(Results);
                    epAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<SeasonDetail> call, Throwable t) {
                Log.d("SeasonDetailError", t.getMessage());
            }
        });
    }

    private void fetchClip(int epNum,int position){
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call call = api.getTvShowEpClip(Tv_Show_id, Season_number,epNum,Constants.API_KEY);
        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<Video> videos = response.body().getVideos();
                    episodes.get(position).setVideos(videos);
                    epAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Log.d("ClipError", t.getMessage());
            }
        });
    }

    private void fetchSeasonTrailer(){
        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
        Call call = api.getTvShowSeasonVideo(Tv_Show_id, Season_number, Constants.API_KEY);

        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<Video> videos = response.body().getVideos();
                    seasonTrailerList.clear();
                    for(Video video : videos){
                        if(video.getType().equals("Trailer")){
                            seasonTrailerList.add(video);
                        }
                    }
                    for(Video video : videos){
                        if(video.getType().equals("Teaser")){
                            seasonTrailerList.add(video);
                        }
                    }
                    seasonTrailerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                    Log.d("Error_Season_Trailer_Fetch", t.getMessage());
            }
        });
    }

}