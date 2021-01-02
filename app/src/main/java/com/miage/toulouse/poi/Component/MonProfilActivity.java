package com.miage.toulouse.poi.Component;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.miage.toulouse.poi.Component.EditProfilActivity;
import com.miage.toulouse.poi.Entity.Theme;
import com.miage.toulouse.poi.Entity.Utilisateur;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;
import com.miage.toulouse.poi.Services.GestionAPI;
import com.miage.toulouse.poi.Services.GestionListThemes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonProfilActivity extends AppCompatActivity {
    FirebaseAuth fireBaseAuth;
    APIService apiService;
    String utilisateurID;
    String photoURL;
    ImageView profilePic;
    StorageReference storageReference;
    GestionAPI gestionAPI = new GestionAPI();
    GestionListThemes gestionListThemes = new GestionListThemes();
    ArrayList<String> listThemes = new ArrayList<String>();
    String[] listThemesUser = new String[0];
    String themes;
    ListView listViewTheme ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_profil);
        TextView textPrenom = findViewById(R.id.TextPrenom);
        TextView textNom = findViewById(R.id.TextNom);
        TextView textMail = findViewById(R.id.TextMail);
        profilePic = findViewById(R.id.profilePic);
        fireBaseAuth = FirebaseAuth.getInstance();
        utilisateurID = fireBaseAuth.getCurrentUser().getUid();
        apiService= gestionAPI.initApiService();

        listViewTheme = findViewById(R.id.ListViewThemes);
        listViewTheme.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        final Call<Utilisateur> user = apiService.getUserById(utilisateurID);
        user.enqueue(new Callback<Utilisateur>() {
            @Override
            public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
                if(response.isSuccessful()){
                    Utilisateur res = response.body();
                    textPrenom.setText(res.getPrenom());
                    textNom.setText(res.getNom());
                    textMail.setText(res.getMail());
                    photoURL = res.getPhotoURL();
                    themes = res.getThemes();
                    try{
                        storageReference = FirebaseStorage.getInstance().getReference().child("images/"+photoURL);
                        File localFile = File.createTempFile(photoURL,"png");
                        storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            profilePic.setImageBitmap(bitmap);
                        });
                        listThemesUser = getListThemesUser(themes);
                        gestionListThemes.addDataToListView(listViewTheme, listThemes, getApplicationContext(),listThemesUser);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(MonProfilActivity.this, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Utilisateur> call, Throwable t) {
                Toast.makeText(MonProfilActivity.this, "Erreur connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String[] getListThemesUser(String themes){
        String[] listT = themes.split(";");
        for(int i=0; i<listT.length;i++){
            for(Theme t : MenuActivity.listThemes){
                if(t.getId().equalsIgnoreCase(listT[i])){
                    listT[i] = t.getNom();
                }
            }
        }
        return listT;
    }
    public void goToMenuActivity(View view) {
        Intent intent=new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToEditProfilActivity(View view) {
        Intent intent=new Intent(this, EditProfilActivity.class);
        startActivity(intent);
        finish();
    }

}