package com.tms.onfilm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tms.onfilm.R;
import com.tms.onfilm.listeners.SelectGenreListener;
import com.tms.onfilm.models.Genre;

import java.util.ArrayList;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreHolder> {
    private Context context;
    private ArrayList<Genre> genreArrayList;
    private SelectGenreListener selectGenreListener;

    public GenreAdapter(Context context, ArrayList<Genre> genreArrayList, SelectGenreListener selectGenreListener) {
        this.context = context;
        this.genreArrayList = genreArrayList;
        this.selectGenreListener = selectGenreListener;
    }

    @NonNull
    @Override
    public GenreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GenreHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_item_genre,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull GenreHolder holder, int position) {
        holder.setData(genreArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return genreArrayList.size();
    }

    class GenreHolder extends RecyclerView.ViewHolder {
        private TextView txtNameGenre;
        private ConstraintLayout layoutItemGenre;

        public GenreHolder(@NonNull View itemView) {
            super(itemView);
            txtNameGenre = itemView.findViewById(R.id.text_name_genre);
            layoutItemGenre = itemView.findViewById(R.id.layout_item_genre);
        }

        public void setData(Genre genre) {
            txtNameGenre.setText(genre.getNameGenre());

            layoutItemGenre.setOnClickListener(view -> {
                if(selectGenreListener.isCheckSelected(genre)) {
                    selectGenreListener.cancleSelectGenre(genre);
                    layoutItemGenre.setBackground(context.getDrawable(R.drawable.background_item_genre));
                    txtNameGenre.setTextColor(context.getResources().getColor(R.color.gray_light));
                } else {
                    selectGenreListener.selectGenre(genre);
                    layoutItemGenre.setBackground(context.getResources().getDrawable(R.drawable.background_item_genre_selected));
                    txtNameGenre.setTextColor(context.getResources().getColor(R.color.yellow));
                }
            });
        }
    }

    public void releaseContext() {
        context = null;
    }
}
