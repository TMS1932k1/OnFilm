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
import com.tms.onfilm.listeners.SelectEpisodeListener;
import com.tms.onfilm.models.Episode;

import java.util.ArrayList;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeHolder> {
    private ArrayList<Episode> episodeArrayList;
    private SelectEpisodeListener selectEpisodeListener;
    private Context context;

    public EpisodeAdapter(ArrayList<Episode> episodeArrayList, Context context) {
        this.episodeArrayList = episodeArrayList;
        this.selectEpisodeListener = (SelectEpisodeListener) context;
        this.context = context;
    }

    @NonNull
    @Override
    public EpisodeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EpisodeHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_item_episode,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeHolder holder, int position) {
        holder.setData(episodeArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return episodeArrayList.size();
    }

    class EpisodeHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout layoutItemEpisode;
        private TextView txtNameEpisode;

        public EpisodeHolder(@NonNull View itemView) {
            super(itemView);
            layoutItemEpisode = itemView.findViewById(R.id.layout_item_episode);
            txtNameEpisode = itemView.findViewById(R.id.text_name_episode);
        }

        public void setData(Episode episode) {
            txtNameEpisode.setText(episode.getNameEpisode());

            if(!selectEpisodeListener.isSelectingEpisode(episode)) {
                txtNameEpisode.setTextColor(context.getResources().getColor(R.color.gray_light));
                layoutItemEpisode.setBackground(context.getResources().getDrawable(R.drawable.background_item_genre));
            } else {
                txtNameEpisode.setTextColor(context.getResources().getColor(R.color.yellow));
                layoutItemEpisode.setBackground(context.getResources().getDrawable(R.drawable.background_item_genre_selected));
            }

            layoutItemEpisode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!selectEpisodeListener.isSelectingEpisode(episode)) {
                        selectEpisodeListener.selectEpisode(episode);
                    }
                }
            });
        }
    }

    public void releaseContext() {
        context = null;
    }

}
