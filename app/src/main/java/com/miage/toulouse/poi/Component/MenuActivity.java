package com.miage.toulouse.poi.Component;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.miage.toulouse.poi.Authentication.LoginActivity;
import com.miage.toulouse.poi.Entity.PointInteret;
import com.miage.toulouse.poi.Entity.Theme;
import com.miage.toulouse.poi.Entity.Utilisateur;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;
import com.miage.toulouse.poi.Services.GestionAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends AppCompatActivity {

    final String BASE_URL = "https://us-central1-projetmobilite-a0b6f.cloudfunctions.net/app/";
    FirebaseAuth fireBaseAuth;
    String utilisateurID;
    ImageView imageView;
    APIService apiService;
    StorageReference storageReference;
    GestionAPI gestionAPI = new GestionAPI();
    public static List<Theme> listThemes = new ArrayList<>();
    public static Utilisateur currentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireBaseAuth = FirebaseAuth.getInstance();
        utilisateurID = fireBaseAuth.getCurrentUser().getUid();
        setContentView(R.layout.activity_menu);

        apiService = gestionAPI.initApiService();
        imageView = (ImageView) findViewById(R.id.imageView);
        final Call<List<Theme>> listThemesRequest = apiService.getAllThemes();
        listThemesRequest.enqueue(new Callback<List<Theme>>(){

            @Override
            public void onResponse(Call<List<Theme>> call, Response<List<Theme>> response) {
                List<Theme> listThemesResult = response.body();
                assert listThemesResult != null;
                listThemes = new ArrayList<>();
                listThemes.addAll(listThemesResult);
            }

            @Override
            public void onFailure(Call<List<Theme>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "Erreur get Themes", Toast.LENGTH_SHORT).show();
            }
        });

        getUserById();
    }

    private void afficherLaPhotoDeProfil() {
        if(currentUser!=null) {
            String photoURL = currentUser.getPhotoURL();
            try {
                storageReference = FirebaseStorage.getInstance().getReference().child("images/" + photoURL);
                File localFile = File.createTempFile(photoURL, "png");
                storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void goToLoginActivity(View view) {
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToRechercheParNomActivity(View view) {
        Intent intent=new Intent(this, RechercheParNomActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToRechercheParThemeActivity(View view) {
        Intent intent=new Intent(this, RechercheParThemeActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToMonProfilActivity(View view) {
        Intent intent=new Intent(this, MonProfilActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToListPointInteret(View view) {
        Intent intent = new Intent(this, MenuPointInteret.class);
        startActivity(intent);
        finish();
    }

    public void getUserById(){
        final Call<Utilisateur> user = apiService.getUserById(utilisateurID);
        user.enqueue(new Callback<Utilisateur>() {
            @Override
            public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
                if(response.isSuccessful()){
                    currentUser = response.body();
                    String stringToPrint = "User is "+currentUser.getNom()+" "+currentUser.getPrenom();
                    Log.d("MenuActivity", stringToPrint);
                    afficherLaPhotoDeProfil();
                }
                else{
                    Toast.makeText(MenuActivity.this, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Utilisateur> call, Throwable t) {
                System.out.println("Erreur get User : "+t);
                Toast.makeText(MenuActivity.this, "Erreur get User : "+t, Toast.LENGTH_SHORT).show();
            }
        });
    }
}