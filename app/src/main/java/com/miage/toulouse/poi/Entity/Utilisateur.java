package com.miage.toulouse.poi.Entity;

import java.io.Serializable;
import java.util.List;

public class Utilisateur implements Serializable {
    private String nom;
    private String prenom;
    private String mail;
    private String themes;
    private String identifiant;

    public Utilisateur(String identifiant, String nom, String prenom, String mail, String themes) {
        this.identifiant=identifiant;
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

    public String getThemes() {
        return themes;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }
}
