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

public class StandardFilmAdapter extends RecyclerView.Adapter<StandardFilmAdapter.StandardFilmHolder> {
    private Context context;
    private ArrayList<Film> filmArrayList;
    private SelectFilmListener selectFilmListener;

    public StandardFilmAdapter(Context context, ArrayList<Film> filmArrayList, SelectFilmListener selectFilmListener) {
        this.context = context;
        this.filmArrayList = filmArrayList;
        this.selectFilmListener = selectFilmListener;
    }

    @NonNull
    @Override
    public StandardFilmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StandardFilmHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_item_standard_film,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull StandardFilmHolder holder, int position) {
        holder.setData(filmArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return filmArrayList.size();
    }

    class StandardFilmHolder extends RecyclerView.ViewHolder {
        private TextView txtNameFilm, txtLimitFilm;
        private ImageView imgPicFilm;

        private ConstraintLayout layoutItemNewFilm;

        public StandardFilmHolder(@NonNull View itemView) {
            super(itemView);
            txtNameFilm = itemView.findViewById(R.id.text_name_film);
            imgPicFilm = itemView.findViewById(R.id.image_pic_film);
            layoutItemNewFilm = itemView.findViewById(R.id.layout_item_new_film);
            txtLimitFilm = itemView.findViewById(R.id.text_limit_film);
        }

        public void setData(Film film) {
            txtNameFilm.setText(film.getNameFilm());

            if(film.getLimitFilm() > 0) {
                txtLimitFilm.setText(film.getLimitFilm() + "");
                txtLimitFilm.setVisibility(View.VISIBLE);
            } else {
                txtLimitFilm.setVisibility(View.INVISIBLE);
            }


            setPicStandardForImage(film);

            layoutItemNewFilm.setOnClickListener(view -> selectFilmListener.setOnClickFilm(film));
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
