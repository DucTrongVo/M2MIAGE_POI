package com.miage.toulouse.poi.Entity;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String nom;
    private String prenom;
    private String mail;
    private List<String> themes;

    public User(String nom, String prenom, String mail, List<String> themes) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.themes = themes;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<String> getThemes() {
        return themes;
    }

    public void setThemes(List<String> themes) {
        this.themes = themes;
    }
}
