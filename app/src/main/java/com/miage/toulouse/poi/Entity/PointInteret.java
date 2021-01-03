package com.miage.toulouse.poi.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PointInteret implements Serializable {
    String id;
    String nom;
    String description;
    String url;
    String street;
    String postalCode;
    String city;
    String country;
    String lat;
    String lon;
    String themes;
    String messages;

    public PointInteret(String id, String nom, String description, String url, String street, String postalCode, String city, String country, String lat, String lon, String themes, String messages) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.url = url;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
        //this.themes = Arrays.asList(themes.split(";"));
        this.themes = themes;
        this.messages = messages;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getThemes() {
        return themes;
    }

    public void setThemes(String themes) {
        this.themes = themes;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }
}
