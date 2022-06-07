package com.tms.onfilm.listeners;

import com.tms.onfilm.models.Episode;

public interface SelectEpisodeListener {
    void selectEpisode(Episode episode);
    boolean isSelectingEpisode(Episode episode);
}
