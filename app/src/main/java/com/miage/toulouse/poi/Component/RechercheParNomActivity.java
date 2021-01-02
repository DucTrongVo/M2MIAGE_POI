package com.miage.toulouse.poi.Component;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.miage.toulouse.poi.Authentication.LoginActivity;
import com.miage.toulouse.poi.Entity.Utilisateur;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;
import com.miage.toulouse.poi.Services.GestionAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RechercheParNomActivity extends AppCompatActivity {
    ListView listViewUsers ;
    APIService apiService;
    GestionAPI gestionAPI = new GestionAPI();
    Button BoutonRechercher;
    List<Utilisateur> listUsers;
    ArrayAdapter<String> arrayAdapter;

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
                }
                else{
                    final Call<List<Utilisateur>> listUs = apiService.getUserByNom(nom);
                    listUs.enqueue(new Callback<List<Utilisateur>>() {

                        @Override
                        public void onResponse(Call<List<Utilisateur>> call, Response<List<Utilisateur>> response) {
                            if(response.isSuccessful()){
                                listUsers = response.body();
                                if(listUsers.size() == 0){
                                    List<String> noResponse = new ArrayList<>(Arrays.asList("Aucun utilisateur trouvé"));
                                    arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, noResponse);
                                    listViewUsers.setAdapter(arrayAdapter);
                                }else{
                                    List<String> listNoms = new ArrayList<>();
                                    for(Utilisateur user : listUsers){
                                        listNoms.add(user.getNom()+" "+user.getPrenom());
                                    }
                                    arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listNoms);
                                    listViewUsers.setAdapter(arrayAdapter);
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<List<Utilisateur>> call, Throwable t) {
                            Toast.makeText(RechercheParNomActivity.this, "Erreur Fetch User By Nom", Toast.LENGTH_SHORT).show();
                        }
                    });
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