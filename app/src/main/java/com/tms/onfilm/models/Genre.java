package com.tms.onfilm.models;

import java.io.Serializable;

public class Genre implements Serializable {
    private String idGenre;
    private String nameGenre;

    public Genre() {
    }

    public Genre(String idGenre, String nameGenre) {
        this.idGenre = idGenre;
        this.nameGenre = nameGenre;
    }

    public String getIdGenre() {
        return idGenre;
    }

    public void setIdGenre(String idGenre) {
        this.idGenre = idGenre;
    }

    public String getNameGenre() {
        return nameGenre;
    }

    public void setNameGenre(String nameGenre) {
        this.nameGenre = nameGenre;
    }
}
