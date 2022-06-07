package com.tms.onfilm.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tms.onfilm.R;
import com.tms.onfilm.models.Genre;

import java.util.ArrayList;

public class GenreInWatchFilmAdapter extends RecyclerView.Adapter<GenreInWatchFilmAdapter.GenreInWatchHolder> {
    private ArrayList<Genre> genreArrayList;

    public GenreInWatchFilmAdapter(ArrayList<Genre> genreArrayList) {
        this.genreArrayList = genreArrayList;
    }

    @NonNull
    @Override
    public GenreInWatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GenreInWatchHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_item_genre_in_watch_film,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull GenreInWatchHolder holder, int position) {
        holder.setData(genreArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return genreArrayList.size();
    }

    class GenreInWatchHolder extends RecyclerView.ViewHolder {
        private TextView txtNameGenre;

        public GenreInWatchHolder(@NonNull View itemView) {
            super(itemView);
            txtNameGenre = itemView.findViewById(R.id.text_name_genre);
        }

        public void setData(Genre genre) {
            txtNameGenre.setText(genre.getNameGenre());
        }
    }
}
