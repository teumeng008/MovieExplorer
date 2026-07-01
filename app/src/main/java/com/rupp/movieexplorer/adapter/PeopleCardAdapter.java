package com.rupp.movieexplorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rupp.movieexplorer.R;
import com.rupp.movieexplorer.model.MediaItem;
import com.rupp.movieexplorer.model.People;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PeopleCardAdapter extends RecyclerView.Adapter<PeopleCardAdapter.ViewHolder> {

    private List<People> peoples;
    public PeopleCardAdapter(List<People> peoples){
        this.peoples = peoples;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_template,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         People people = peoples.get(position);
         String imageURL ="https://image.tmdb.org/t/p/w500"+ people.getProfile_image();
        Picasso.get().load(imageURL).placeholder(R.drawable.user_icon).into(holder.imageView);
        holder.textView.setText(people.getName());
        if(people.getCharacter() != null){
            holder.Character.setText(people.getCharacter());
            holder.PlayAsLabel.setVisibility(View.VISIBLE);
            holder.Character.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return peoples.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView,PlayAsLabel,Character;

       public ViewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.peopleImage);
            textView = view.findViewById(R.id.peopleName);
            PlayAsLabel = view.findViewById(R.id.textPlayAs);
            Character = view.findViewById(R.id.textCharactor);
        }
    }
}
