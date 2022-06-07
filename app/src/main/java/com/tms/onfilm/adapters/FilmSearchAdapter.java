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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tms.onfilm.R;
import com.tms.onfilm.listeners.FilmSearchListener;
import com.tms.onfilm.models.Film;
import com.tms.onfilm.models.Picture;
import com.tms.onfilm.utilities.DownloadImageTask;

import java.util.ArrayList;

public class FilmSearchAdapter extends RecyclerView.Adapter<FilmSearchAdapter.FilmHolder> {
    private Context context;
    private ArrayList<Film> filmArrayList;
    private FilmSearchListener filmSearchListener;

    public FilmSearchAdapter(Context context, ArrayList<Film> filmArrayList) {
        this.context = context;
        this.filmArrayList = filmArrayList;
        this.filmSearchListener = (FilmSearchListener) context;
    }

    @NonNull
    @Override
    public FilmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilmHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_item_film_search,
                parent,
                false)
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
        private ImageView imgPicStandard;
        private TextView txtNameFilm, txtRelease, txtFavorite, txtLimitFilm;
        private RecyclerView rvGenre;
        private ConstraintLayout layoutItemFilmSearch;
        private GenreInWatchFilmAdapter genreInWatchFilmAdapter;

        public FilmHolder(@NonNull View itemView) {
            super(itemView);

            imgPicStandard = itemView.findViewById(R.id.image_pic_standard);
            txtNameFilm = itemView.findViewById(R.id.text_name_film);
            txtRelease = itemView.findViewById(R.id.text_release_film);
            txtFavorite = itemView.findViewById(R.id.text_favorite_film);
            rvGenre = itemView.findViewById(R.id.recyclerview_genre);
            layoutItemFilmSearch = itemView.findViewById(R.id.layout_item_film_search);
            txtLimitFilm = itemView.findViewById(R.id.text_limit_film);
        }

        public void setData(Film film) {
            setPicStandardForImage(film);

            txtNameFilm.setText(film.getNameFilm());
            txtRelease.setText(film.getReleaseFilm() + "");
            txtFavorite.setText(film.getCountFavorite() + " " + context.getResources().getString(R.string.title_like));

            if(film.getLimitFilm() > 0) {
                txtLimitFilm.setText(film.getLimitFilm() + "");
                txtLimitFilm.setVisibility(View.VISIBLE);
            } else {
                txtLimitFilm.setVisibility(View.INVISIBLE);
            }

            // code set favorite at here

            genreInWatchFilmAdapter = new GenreInWatchFilmAdapter(film.getGenreArrayList());
            int spanCount = getSpanCount(2, film.getGenreArrayList().size());
            rvGenre.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL));
            rvGenre.setAdapter(genreInWatchFilmAdapter);

            layoutItemFilmSearch.setOnClickListener(view -> filmSearchListener.onClickFilm(film));
        }

        private void setPicStandardForImage(Film film) {
            for (Picture picture : film.getPictureArrayList()) {
                if (picture.getTypePic().equals(context.getString(R.string.Standard))) {
                    new DownloadImageTask(imgPicStandard).execute(picture.getLinkPic());
                    return;
                }
            }
        }

        private int getSpanCount(int countOnLine, int sum) {
            if(sum % countOnLine != 0) {
                sum = sum / 2 + 1;
            } else {
                sum = sum / 2;
            }
            return sum;
        }
    }

    public void releaseContext() {
        context = null;
    }
}
