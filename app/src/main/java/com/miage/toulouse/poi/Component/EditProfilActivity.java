package com.miage.toulouse.poi.Component;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.miage.toulouse.poi.Authentication.RegisterActivity;
import com.miage.toulouse.poi.Component.MonProfilActivity;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;
import com.miage.toulouse.poi.Services.GestionAPI;
import com.miage.toulouse.poi.Services.GestionListThemes;
import com.miage.toulouse.poi.Services.GestionUtilisateur;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfilActivity extends AppCompatActivity {
    FirebaseAuth fireBaseAuth;
    APIService apiService;
    String utilisateurID;
    String photoURL;
    ImageView profilePic;
    StorageReference storageReference;
    GestionAPI gestionAPI = new GestionAPI();
    GestionListThemes gestionListThemes = new GestionListThemes();
    ArrayList<String> listThemes = new ArrayList<String>();
    String themes;
    ListView listViewTheme ;
    ArrayList<String> listThemesUser = new ArrayList<String>();
    GestionUtilisateur gestionUtilisateur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        EditText textPrenom = findViewById(R.id.TextPrenom);
        EditText textNom = findViewById(R.id.TextNom);
        EditText textMail = findViewById(R.id.TextMail);
        fireBaseAuth = FirebaseAuth.getInstance();
        utilisateurID = fireBaseAuth.getCurrentUser().getUid();
        apiService= gestionAPI.initApiService();
        Button boutonValider = findViewById(R.id.BoutonValider);
        listViewTheme = findViewById(R.id.ListViewThemes);
        listViewTheme.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        final Call<JsonElement> user = apiService.getUserById(utilisateurID);
        user.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    JsonElement res = response.body();
                    JsonObject jsonObject = res.getAsJsonObject();
                    textPrenom.setText(jsonObject.get("prenom").getAsString());
                    textNom.setText(jsonObject.get("nom").getAsString());
                    textMail.setText(jsonObject.get("mail").getAsString());
                    photoURL = jsonObject.get("photoURL").getAsString();
                    themes = jsonObject.get("themes").getAsString();
                }
                else{
                    Toast.makeText(EditProfilActivity.this, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(EditProfilActivity.this, "Erreur connexion", Toast.LENGTH_SHORT).show();
            }
        });

        listViewTheme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedTheme = ((TextView) view).getText().toString();
                if (listThemesUser.contains(selectedTheme)) {
                    listThemesUser.remove(selectedTheme);
                } else {
                    listThemesUser.add(selectedTheme);
                }
            }
        });

        boutonValider.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 gestionUtilisateur = new GestionUtilisateur(textPrenom, textNom, textMail, themes);
                 gestionUtilisateur.checkSaisieOkWithoutMdp(EditProfilActivity.this);
                 // ici modifier l'utilisateur en base
             }
         });

    }

    public void goToMonProfilActivity(View view) {
        Intent intent=new Intent(this, MonProfilActivity.class);
        startActivity(intent);
    }
}