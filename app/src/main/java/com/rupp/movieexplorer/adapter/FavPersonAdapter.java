package com.rupp.movieexplorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.model.FavoritePerson;
import com.rupp.movieexplorer.model.People;
import com.rupp.movieexplorer.model.PersonInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavPersonAdapter extends RecyclerView.Adapter<FavPersonAdapter.ViewHolder> {

    private List<FavoritePerson> peopleList;
    private OnClickListener onClickListener;

    public interface OnClickListener{
        void onClick(FavoritePerson person);
    }

    public FavPersonAdapter(List<FavoritePerson> personList, OnClickListener onClickListener){
        this.peopleList = personList;
        this.onClickListener = onClickListener;
    }

    public void updateList(List<FavoritePerson> newList) {
        this.peopleList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavPersonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_card_template, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavPersonAdapter.ViewHolder holder, int position) {
        FavoritePerson person = peopleList.get(position);
        holder.Name.setText(person.getName());
        
        String genderText = "Not Specified";
        if (person.getGender() == 1) {
            genderText = "Female";
        } else if (person.getGender() == 2) {
            genderText = "Male";
        }
        holder.Gender.setText(genderText);

        String ImageURL = "https://image.tmdb.org/t/p/w200" + person.getProfile_path();
        Picasso.get().load(ImageURL).into(holder.PersonImage);

        holder.itemView.setOnClickListener( v -> {
            if(onClickListener != null){
                onClickListener.onClick(person);
            }
        });
    }

    @Override
    public int getItemCount() {
        return peopleList != null ? peopleList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView PersonImage;
        TextView Name, Gender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PersonImage = itemView.findViewById(R.id.PersonImage);
            Name = itemView.findViewById(R.id.Name);
            Gender = itemView.findViewById(R.id.Gender);
        }
    }
}
