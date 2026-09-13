package com.rupp.movieexplorer.viewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rupp.movieexplorer.Constants;
import com.rupp.movieexplorer.api.MovieApi;
import com.rupp.movieexplorer.api.RetrofitClient;
import com.rupp.movieexplorer.model.FavoritePerson;
import com.rupp.movieexplorer.model.Movie;
import com.rupp.movieexplorer.model.PersonDetails;
import com.rupp.movieexplorer.model.PersonInfo;
import com.rupp.movieexplorer.model.TVShow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritePersonViewModel extends ViewModel {
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private MutableLiveData<List<FavoritePerson>> favoritePersonMutableLiveData = new MutableLiveData<>();
//    private interface DepartmentCallback {
//        void onResult(String department);
//    }
//    public interface PersonKnown_forCallback {
//        void onResult(String PersonKnown_for);
//    }
//    public interface MediaTypeCallback {
//        void onResult(String type);
//    }
//
//    public interface FilterCallback{
//        void onResult(List<PersonDetails> results);
//    }

    public MutableLiveData<List<FavoritePerson>> getFavoritePersonMutableLiveData() {
        return favoritePersonMutableLiveData;
    }

    public void loadFavoritePerson(){
        String userUID = auth.getCurrentUser().getUid();
        database.collection("Users").document(userUID).collection("favorite_person").addSnapshotListener(((value, error) -> {
            List<FavoritePerson> items = new ArrayList<>();
            for (DocumentSnapshot item : value.getDocuments()){
                items.add(item.toObject(FavoritePerson.class));
            }
            favoritePersonMutableLiveData.setValue(items);
        }));
    }

//    public void getPersonKnown_for(int Person_id, PersonKnown_forCallback personKnownForCallback ){
//
//        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
//        Call<PersonInfo> call = api.getPersonCredits(Person_id, Constants.API_KEY);
//
//        call.enqueue(new Callback<PersonInfo>() {
//            @Override
//            public void onResponse(Call<PersonInfo> call, Response<PersonInfo> response) {
//                if(response.isSuccessful() && response.body() != null){
//
//                            List<PersonDetails> personDetails= response.body().getCastInfos();
//                            List<PersonDetails> sorted_list = sorted_most_popular_creditsList(personDetails);
//                            filter(personDetails, new FilterCallback() {
//                                    @Override
//                                    public void onResult(List<PersonDetails> results) {
//                                        StringBuilder builder = new StringBuilder();
//
//                                        for(int i = 0; i < 3; i++){
//                                            //  if(!personDetails.get(i).getCharacter().equals("Self - Guest") && !personDetails.get(i).getCharacter().equals("Self")  && seen.add(personDetails.get(i).getCharacter())){
//                                            builder.append(sorted_list.get(i).getTitle());
//                                            if(i < 2) builder.append(" / ");
//                                            //}
//                                        }
//                                        personKnownForCallback.onResult(builder.toString());
//                                    }
//                            });
//                }
//            }
//            @Override
//            public void onFailure(Call<PersonInfo> call, Throwable t) {
//                Log.d("GET_KNOWN_PERSON_ERROR", t.getMessage());
//            }
//        });
//    }

//    private void known_for_department(int Person_id, DepartmentCallback departmentCallback){
//
//        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
//        Call<PersonInfo> call = api.getPersonInfo(Person_id, Constants.API_KEY);
//        call.enqueue(new Callback<PersonInfo>() {
//            @Override
//            public void onResponse(Call<PersonInfo> call, Response<PersonInfo> response) {
//                if(response.isSuccessful() && response.body() != null){
//                    PersonInfo personInfo = response.body();
//                  String known_for_department = personInfo.getKnown_for_department();
//                  departmentCallback.onResult(known_for_department);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PersonInfo> call, Throwable t) {
//                Log.d(" known_for_department Fetch Error", t.getMessage());
//            }
//        });
//
//    }
//
//    private List<PersonDetails> sorted_most_popular_creditsList(List<PersonDetails> rawCreditsList){
//        List<PersonDetails> sorted_List;
//
//        rawCreditsList.sort(
//                Comparator.comparingDouble(PersonDetails :: getPopularity).reversed()
//                                .thenComparing(Comparator.comparingDouble(PersonDetails :: getVote_average).reversed())
//        );
//
//        sorted_List = rawCreditsList;
//        return sorted_List;
//    }
//    private void filter(List<PersonDetails> personDetails, FilterCallback filterCallback){
//        List<PersonDetails> filteredList = new ArrayList<>();
//        if(personDetails.isEmpty()){
//            filterCallback.onResult(filteredList);
//            return;
//        }
//
//        int[] completed = {0}; // why we use array instead of normal int ?
//                                //Answer : if we're using normal int passing into an anonymous inner class it doesn't make change to original "int" which assigned at the outside of the anonymous class instead it create a Photocopy int and make change to it.
//
//                                // but if we use array the array itself doesn't contain value instead it contain address or key of each whatever type of array object
//                                //so eventho anonymous inner class create a new Photocopy of original int object but the address of if DOES NOT CHANGE AT ALL.
//
//        for (PersonDetails personDetail : personDetails){
//            int id = personDetail.getId();
//            String type = personDetail.getMedia_type();
//
//                detail_type_of_media(id, type, new MediaTypeCallback() {
//                    @Override
//                    public void onResult(String result) {
//                        if(result.equals("Scripted")){
//                            filteredList.add(personDetail);
//                        }else if(result.equals("false")){
//                            filteredList.add(personDetail);
//                        }
//
//                        completed[0] ++;
//
//                        if(completed[0] == personDetails.size()){
//                            filterCallback.onResult(filteredList);
//                        }
//                    }
//                });
//        }
//    }
//    private void filter(
//            List<PersonDetails> personDetails,
//            FilterCallback callback
//    ) {
//        List<PersonDetails> filteredList = new ArrayList<>();
//
//        if (personDetails.isEmpty()) {
//            callback.onResult(filteredList);
//            return;
//        }
//
//        int[] completed = {0};
//
//        for (PersonDetails personDetail : personDetails) {
//
//            int id = personDetail.getId();
//            String type = personDetail.getMedia_type();
//
//            detail_type_of_media(id, type, new MediaTypeCallback() {
//                @Override
//                public void onResult(String result) {
//
//                    if ("tv".equals(type)) {
//                        if ("Scripted".equals(result)) {
//                            filteredList.add(personDetail);
//                        }
//                    } else {
//                        if ("false".equals(result)) {
//                            filteredList.add(personDetail);
//                        }
//                    }
//
//                    completed[0]++;
//
//                    if (completed[0] == personDetails.size()) {
//                        callback.onResult(filteredList);
//                    }
//                }
//            });
//        }
//    }
//
//    private void detail_type_of_media(int media_id, String type, MediaTypeCallback mediaTypeCallback ){
//        MovieApi api = RetrofitClient.getRetrofit().create(MovieApi.class);
//        Call call;
//        if("tv".equals(type)){
//            call = api.getTvShowDetails(media_id, Constants.API_KEY);
//            call.enqueue(new Callback<TVShow>() {
//                @Override
//                public void onResponse(Call<TVShow> call, Response<TVShow> response) {
//                    if(response.isSuccessful() && response.body() != null){
//                        TVShow tvShow = response.body();
//                        mediaTypeCallback.onResult(tvShow.getType());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<TVShow> call, Throwable t) {
//                    Log.d("FETCH_MEDIA_TYPE_ERROR", t.getMessage());
//                }
//            });
//        }else {
//            call = api.getMovieDetails(media_id, Constants.API_KEY);
//            call.enqueue(new Callback<Movie>() {
//                @Override
//                public void onResponse(Call<Movie> call, Response<Movie> response) {
//                    if(response.isSuccessful() && response.body() != null){
//                        Movie movie = response.body();
//                        mediaTypeCallback.onResult(movie.getVideo());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Movie> call, Throwable t) {
//                    Log.d("FETCH_MEDIA_TYPE_ERROR", t.getMessage());
//                }
//            });
//        }
//
//    }
//
//
}