package com.tms.onfilm.listeners;

import com.tms.onfilm.models.Genre;

public interface SelectGenreListener {
    void selectGenre(Genre genre);
    boolean isCheckSelected(Genre genre);
    void cancleSelectGenre(Genre genre);
}
