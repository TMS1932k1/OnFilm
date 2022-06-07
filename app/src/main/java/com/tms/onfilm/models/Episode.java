package com.tms.onfilm.models;

import java.io.Serializable;

public class Episode implements Serializable {
    private String idEpisode;
    private String nameEpisode;
    private String linkEpisode;

    public Episode() {
    }

    public Episode(Episode episode) {
        setIdEpisode(episode.idEpisode);
        setNameEpisode(episode.nameEpisode);
        setLinkEpisode(episode.linkEpisode);
    }

    public Episode(String idEpisode, String nameEpisode, String linkEpisode) {
        this.idEpisode = idEpisode;
        this.nameEpisode = nameEpisode;
        this.linkEpisode = linkEpisode;
    }

    public String getIdEpisode() {
        return idEpisode;
    }

    public void setIdEpisode(String idEpisode) {
        this.idEpisode = idEpisode;
    }

    public String getNameEpisode() {
        return nameEpisode;
    }

    public void setNameEpisode(String nameEpisode) {
        this.nameEpisode = nameEpisode;
    }

    public String getLinkEpisode() {
        return linkEpisode;
    }

    public void setLinkEpisode(String linkEpisode) {
        this.linkEpisode = linkEpisode;
    }
}
