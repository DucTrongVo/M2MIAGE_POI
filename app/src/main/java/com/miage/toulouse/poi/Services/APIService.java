package com.miage.toulouse.poi.Services;

import com.google.firebase.firestore.auth.User;
import com.google.gson.JsonElement;
import com.miage.toulouse.poi.Entity.PointInteret;
import com.miage.toulouse.poi.Entity.Theme;
import com.miage.toulouse.poi.Entity.Utilisateur;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {
    @GET("api/users/{idUser}")
    Call<Utilisateur> getUserById(@Path("idUser") String idUser);

    @GET("api/search/users/nom/{nom}")
    Call<List<Utilisateur>> getUserByNom(@Path("nom") String nom);

    @GET("api/search/users/theme/{theme}")
    Call<List<Utilisateur>> getUserByTheme(@Path("theme") String theme);

    @GET("hello-world")
    Call<JsonElement> getHelloWorld();

    @POST("api/users/create")
    Call<Void> createUtilisateur(@Body Utilisateur utilisateur);

    @GET("api/pointInteret")
    Call<List<PointInteret>> getAllPointInteret();

    @GET("api/themes")
    Call<List<Theme>> getAllThemes();

    @PUT("api/update/user")
    Call<Void> modifyUtilisateur(@Body Utilisateur utilisateur);

}
