package com.tms.onfilm.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Account implements Serializable {
    private String idAccount;
    private String emailAccount;
    private String passwordAccount;
    private ArrayList<String> idFilmFavoriteArrayList = new ArrayList<>();

    public Account() {
    }

    public Account(String idAccount, String emailAccount, String passwordAccount) {
        this.idAccount = idAccount;
        this.emailAccount = emailAccount;
        this.passwordAccount = passwordAccount;
    }

    public Account(String idAccount, String emailAccount, String passwordAccount, ArrayList<String> idFilmFavoriteArrayList) {
        this.idAccount = idAccount;
        this.emailAccount = emailAccount;
        this.passwordAccount = passwordAccount;
        this.idFilmFavoriteArrayList = idFilmFavoriteArrayList;
    }

    public String getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(String idAccount) {
        this.idAccount = idAccount;
    }

    public String getEmailAccount() {
        return emailAccount;
    }

    public void setEmailAccount(String emailAccount) {
        this.emailAccount = emailAccount;
    }

    public String getPasswordAccount() {
        return passwordAccount;
    }

    public void setPasswordAccount(String passwordAccount) {
        this.passwordAccount = passwordAccount;
    }

    public ArrayList<String> getIdFilmFavoriteArrayList() {
        return idFilmFavoriteArrayList;
    }

    public void setIdFilmFavoriteArrayList(ArrayList<String> idFilmFavoriteArrayList) {
        this.idFilmFavoriteArrayList = idFilmFavoriteArrayList;
    }
}
