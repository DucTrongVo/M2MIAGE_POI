package com.miage.toulouse.poi.Component;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.miage.toulouse.poi.Authentication.LoginActivity;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends AppCompatActivity {

    final String BASE_URL = "https://us-central1-projetmobilite-a0b6f.cloudfunctions.net/app/";
    final String testIdUser = "ZgJERYMXh6eMQgCOWjcftGqULn33";

    Retrofit retrofit;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIService.class);

    }

    public void goToLoginActivity(View view) {
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToMonProfilActivity(View view) {
        Intent intent=new Intent(this, MonProfilActivity.class);
        startActivity(intent);
        finish();
    }

    public void getHelloWorld(View view){
        final Call<JsonElement> hello = apiService.getHelloWorld();
        hello.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    TextView textView = (TextView) findViewById(R.id.textViewTest);
                    JsonElement res = response.body();
                    JsonObject jsonObject = res.getAsJsonObject();
                    String stringToPrint = "User is "+jsonObject.toString();
                    System.out.println(stringToPrint);
                    textView.setText(stringToPrint);

                }
                else{
                    Toast.makeText(MenuActivity.this, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Erreur connexion "+t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getUserById(View view){
        final Call<JsonElement> user = apiService.getUserById(testIdUser);
        user.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    TextView textView = (TextView) findViewById(R.id.textViewTest);
                    JsonElement res = response.body();
                    JsonObject jsonObject = res.getAsJsonObject();
                    String stringToPrint = "User is "+jsonObject.get("prenom").getAsString();
                    System.out.println(stringToPrint);
                    textView.setText(stringToPrint);
                }
                else{
                    Toast.makeText(MenuActivity.this, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                System.out.println(t);
                Toast.makeText(MenuActivity.this, "Erreur connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }
}