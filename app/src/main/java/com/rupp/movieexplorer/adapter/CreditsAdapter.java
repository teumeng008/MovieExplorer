package com.rupp.movieexplorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.model.ConsolidatedCredit;
import com.rupp.movieexplorer.model.PersonDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.ViewHolder> {

    private List<ConsolidatedCredit> personDetailsList;
    private OnClickListener onClickListener;

    public interface OnClickListener{
        void onclick(PersonDetails personDetails);
    }

    public CreditsAdapter(List<ConsolidatedCredit> personDetailsList,OnClickListener onClickListener ){
        this.personDetailsList = personDetailsList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public CreditsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credit_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditsAdapter.ViewHolder holder, int position) {
        PersonDetails personDetails = personDetailsList.get(position).getPersonDetails();

        holder.mediaName.setText(personDetails.getTitle());
        holder.rating.setText(String.format("%.02f", personDetails.getVote_average()));
        holder.year.setText(personDetails.getReleaseDate());
        holder.mediaType.setText(personDetails.getMedia_type().toUpperCase());
        if(personDetails.getCharacter() == null){
            holder.jobRole.setText(personDetailsList.get(position).getJobs().toString());
        }else {
            holder.jobRole.setText("Play as : "+ personDetails.getCharacter());
        }


        String ImageURL = "https://image.tmdb.org/t/p/w500" + personDetails.getPoster_path();

        Picasso.get().load(ImageURL).into(holder.poster);

        holder.itemView.setOnClickListener( v ->{
            if(onClickListener != null){
                onClickListener.onclick(personDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return personDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView poster;
        TextView mediaName, rating, jobRole, year, mediaType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.imagePoster);
            mediaName = itemView.findViewById(R.id.textTitle);
            rating = itemView.findViewById(R.id.textRating);
            jobRole = itemView.findViewById(R.id.textRole);
            year = itemView.findViewById(R.id.textYear);
            mediaType = itemView.findViewById(R.id.textMediaType);
        }
    }
}


//public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.ViewHolder> {
//
//    private final List<ConsolidatedCredit> creditsList;
//
//    public CreditsAdapter(List<ConsolidatedCredit> creditsList) {
//        this.creditsList = creditsList;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credit_card, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        ConsolidatedCredit item = creditsList.get(position);
//        PersonDetails details = item.getPersonDetails();
//
//        holder.mediaName.setText(details.getTitle());
//        holder.rating.setText(String.format("%.02f", details.getVote_average()));
//        holder.year.setText(details.getReleaseDate());
//
//        // Displays grouped jobs e.g., "Director, Producer"
//        holder.jobRole.setText(item.getFormattedJobs());
//    }
//
//    @Override
//    public int getItemCount() {
//        return creditsList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView poster;
//        TextView mediaName, rating, jobRole, year, mediaType;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            poster = itemView.findViewById(R.id.imagePoster);
//            mediaName = itemView.findViewById(R.id.textTitle);
//            rating = itemView.findViewById(R.id.textRating);
//            jobRole = itemView.findViewById(R.id.textRole);
//            year = itemView.findViewById(R.id.textYear);
//            mediaType = itemView.findViewById(R.id.textMediaType);
//        }
//    }
//}