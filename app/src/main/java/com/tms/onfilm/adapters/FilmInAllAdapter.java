package com.tms.onfilm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tms.onfilm.R;
import com.tms.onfilm.listeners.SelectFilmListener;
import com.tms.onfilm.models.Film;
import com.tms.onfilm.models.Picture;
import com.tms.onfilm.utilities.DownloadImageTask;

import java.util.ArrayList;

public class FilmInAllAdapter extends RecyclerView.Adapter<FilmInAllAdapter.FilmHolder> {
    private Context context;
    private ArrayList<Film> filmArrayList;
    private SelectFilmListener selectFilmListener;

    public FilmInAllAdapter(Context context, ArrayList<Film> filmArrayList, SelectFilmListener selectFilmListener) {
        this.context = context;
        this.filmArrayList = filmArrayList;
        this.selectFilmListener = selectFilmListener;
    }

    @NonNull
    @Override
    public FilmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilmInAllAdapter.FilmHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_item_film_in_all,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull FilmHolder holder, int position) {
        holder.setData(filmArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return filmArrayList.size();
    }

    class FilmHolder extends RecyclerView.ViewHolder {
        private ImageView imgPicFilm;
        private TextView txtNameFilm;
        private ConstraintLayout layoutItemFilmInAll;

        public FilmHolder(@NonNull View itemView) {
            super(itemView);
            imgPicFilm = itemView.findViewById(R.id.image_pic_film);
            txtNameFilm = itemView.findViewById(R.id.text_name_film);
            layoutItemFilmInAll = itemView.findViewById(R.id.layout_item_film_in_all);
        }

        public void setData(Film film) {
            txtNameFilm.setText(film.getNameFilm());

            setPicStandardForImage(film);

            layoutItemFilmInAll.setOnClickListener(view -> selectFilmListener.setOnClickFilm(film));
        }

        private void setPicStandardForImage(Film film) {
            for (Picture picture : film.getPictureArrayList()) {
                if (picture.getTypePic().equals(context.getString(R.string.Standard))) {
                    new DownloadImageTask(imgPicFilm).execute(picture.getLinkPic());
                    return;
                }
            }
        }
    }

    public void releaseContext() {
        context = null;
    }
}
