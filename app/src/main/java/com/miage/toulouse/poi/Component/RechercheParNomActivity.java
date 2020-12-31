package com.miage.toulouse.poi.Component;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.miage.toulouse.poi.Authentication.LoginActivity;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;
import com.miage.toulouse.poi.Services.GestionAPI;


public class RechercheParNomActivity extends AppCompatActivity {
    ListView listViewUsers ;
    APIService apiService;
    GestionAPI gestionAPI = new GestionAPI();
    Button BoutonRechercher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_par_nom);

        TextView textNom = findViewById(R.id.TextNom);
        listViewUsers = findViewById(R.id.ListViewUsers);
        BoutonRechercher = findViewById(R.id.BoutonRechercher);
        apiService= gestionAPI.initApiService();

        BoutonRechercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom = textNom.getText().toString().trim();

                if(TextUtils.isEmpty(nom)){
                    textNom.setError("Le nom est vide !");
                    return;
                }


            }
        });
    }

    public void goToMenuActivity(View view) {
        Intent intent=new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}