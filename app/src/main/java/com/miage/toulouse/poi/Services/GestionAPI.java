package com.miage.toulouse.poi.Services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.miage.toulouse.poi.Entity.Utilisateur;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GestionAPI {
    Retrofit retrofit;
    final String BASE_URL = "https://us-central1-projetmobilite-a0b6f.cloudfunctions.net/app/";
    APIService apiService;

    public APIService initApiService(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIService.class);
        return apiService;
    }
}
