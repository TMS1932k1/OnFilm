package com.tms.onfilm.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Film implements Serializable {
    private String idFilm;
    private String nameFilm;
    private String describeFilm;
    private String directorFilm;
    private int durationFilm;
    private String actorsFilm;
    private int releaseFilm;
    private int limitFilm;
    private int countfavorite;

    private ArrayList<Genre> genreArrayList = new ArrayList<>();
    private ArrayList<Picture> pictureArrayList = new ArrayList<>();
    private ArrayList<Episode> episodeArrayList = new ArrayList<>();

    public Film() {
    }

    public Film(String idFilm, String nameFilm, String describeFilm, String directorFilm, int durationFilm, String actorsFilm, int releaseFilm, int limitFilm, ArrayList<Genre> genreArrayList, ArrayList<Picture> pictureArrayList, ArrayList<Episode> episodeArrayList, int countfavorite) {
        this.idFilm = idFilm;
        this.nameFilm = nameFilm;
        this.describeFilm = describeFilm;
        this.directorFilm = directorFilm;
        this.durationFilm = durationFilm;
        this.actorsFilm = actorsFilm;
        this.releaseFilm = releaseFilm;
        this.limitFilm = limitFilm;
        this.genreArrayList = genreArrayList;
        this.pictureArrayList = pictureArrayList;
        this.episodeArrayList = episodeArrayList;
        this.countfavorite = countfavorite;
    }

    public String getIdFilm() {
        return idFilm;
    }

    public void setIdFilm(String idFilm) {
        this.idFilm = idFilm;
    }

    public String getNameFilm() {
        return nameFilm;
    }

    public void setNameFilm(String nameFilm) {
        this.nameFilm = nameFilm;
    }

    public String getDescribeFilm() {
        return describeFilm;
    }

    public void setDescribeFilm(String describeFilm) {
        this.describeFilm = describeFilm;
    }

    public String getDirectorFilm() {
        return directorFilm;
    }

    public void setDirectorFilm(String directorFilm) {
        this.directorFilm = directorFilm;
    }

    public int getDurationFilm() {
        return durationFilm;
    }

    public void setDurationFilm(int durationFilm) {
        this.durationFilm = durationFilm;
    }

    public String getActorsFilm() {
        return actorsFilm;
    }

    public void setActorsFilm(String actorsFilm) {
        this.actorsFilm = actorsFilm;
    }

    public int getReleaseFilm() {
        return releaseFilm;
    }

    public void setReleaseFilm(int releaseFilm) {
        this.releaseFilm = releaseFilm;
    }

    public int getLimitFilm() {
        return limitFilm;
    }

    public void setLimitFilm(int limitFilm) {
        this.limitFilm = limitFilm;
    }

    public ArrayList<Genre> getGenreArrayList() {
        return genreArrayList;
    }

    public void setGenreArrayList(ArrayList<Genre> genreArrayList) {
        this.genreArrayList = genreArrayList;
    }

    public ArrayList<Picture> getPictureArrayList() {
        return pictureArrayList;
    }

    public void setPictureArrayList(ArrayList<Picture> pictureArrayList) {
        this.pictureArrayList = pictureArrayList;
    }

    public ArrayList<Episode> getEpisodeArrayList() {
        return episodeArrayList;
    }

    public void setEpisodeArrayList(ArrayList<Episode> episodeArrayList) {
        this.episodeArrayList = episodeArrayList;
    }

    public int getCountFavorite() {
        return countfavorite;
    }

    public void setCountFavorite(int countfavorite) {
        this.countfavorite = countfavorite;
    }
}
