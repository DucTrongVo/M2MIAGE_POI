package com.miage.toulouse.poi.Component;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.miage.toulouse.poi.Entity.Utilisateur;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;
import com.miage.toulouse.poi.Services.GestionAPI;
import com.miage.toulouse.poi.Services.GestionListThemes;
import com.miage.toulouse.poi.Services.GestionUtilisateur;

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

        gestionListThemes.addDataToListView(listViewTheme, listThemes, getApplicationContext());

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
                }
                else{
                    Toast.makeText(EditProfilActivity.this, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Utilisateur> call, Throwable t) {
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
                 if(gestionUtilisateur.checkSaisieOkWithoutMdp(EditProfilActivity.this)) {
                     // ici modifier l'utilisateur en base
                     String nom = textNom.getText().toString().trim();
                     String prenom = textPrenom.getText().toString().trim();
                     String mail = textMail.getText().toString().trim();
                     String themes = gestionListThemes.createListStringThemes(listThemesUser);
                     fireBaseAuth.getCurrentUser().updateEmail(String.valueOf(mail));
                     MenuActivity.currentUser.setMail(mail);
                     MenuActivity.currentUser.setNom(nom);
                     MenuActivity.currentUser.setPrenom(prenom);
                     MenuActivity.currentUser.setThemes(themes);
                     Call<Void> res = apiService.modifyUtilisateur(MenuActivity.currentUser);
                     res.enqueue(new Callback<Void>() {
                         @Override
                         public void onResponse(Call<Void> call, Response<Void> response) {
                             if(response.isSuccessful()){
                                 Toast.makeText(EditProfilActivity.this, "Modifications enregistrées", Toast.LENGTH_SHORT).show();
                                 updateCurrentUser();
                             }
                         }

                         @Override
                         public void onFailure(Call<Void> call, Throwable t) {
                             Toast.makeText(EditProfilActivity.this, "Erreur get User : "+t, Toast.LENGTH_SHORT).show();
                         }
                     });
                 }
             }
         });

    }

    public void updateCurrentUser(){
        final Call<Utilisateur> user = apiService.getUserById(utilisateurID);
        user.enqueue(new Callback<Utilisateur>() {
            @Override
            public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
                if(response.isSuccessful()){
                    MenuActivity.currentUser = response.body();
                    String stringToPrint = "User is "+MenuActivity.currentUser.getNom()+" "+MenuActivity.currentUser.getPrenom();
                    Log.d("MenuActivity", stringToPrint);
                }
                else{
                    Toast.makeText(EditProfilActivity.this, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Utilisateur> call, Throwable t) {
                System.out.println("Erreur get User : "+t);
                Toast.makeText(EditProfilActivity.this, "Erreur get User : "+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void goToMonProfilActivity(View view) {
        Intent intent=new Intent(this, MonProfilActivity.class);
        startActivity(intent);
    }
}