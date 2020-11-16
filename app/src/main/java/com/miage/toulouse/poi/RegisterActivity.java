package com.miage.toulouse.poi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText TextNom, TextPrenom, TextMail, TextMdp;
    Button BoutonCreerCompte;
    FirebaseAuth fireBaseAuth;
    ProgressBar progressBar2;
    FirebaseFirestore fStore;
    String utilisateurID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextNom = findViewById(R.id.TextNom);
        TextPrenom = findViewById(R.id.TextPrenom);
        TextMail = findViewById(R.id.TextMail);
        TextMdp = findViewById(R.id.TextMdp);
        BoutonCreerCompte = findViewById(R.id.BoutonCreerCompte);

        fireBaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar2 = findViewById(R.id.progressBar2);

        BoutonCreerCompte.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String nom = TextNom.getText().toString().trim();
                String prenom = TextPrenom.getText().toString().trim();
                String mail = TextMail.getText().toString().trim();
                String mdp = TextMdp.getText().toString().trim();

                if(TextUtils.isEmpty(prenom)){
                    TextPrenom.setError("Le prénom est obligatoire.");
                    return;
                }
                if(TextUtils.isEmpty(mail)){
                    TextMail.setError("Le mail est obligatoire.");
                    return;
                }
                if(TextUtils.isEmpty(mdp)){
                    TextMdp.setError("Le mot de passe est obligatoire.");
                    return;
                }
                if(mdp.length() < 4){
                    TextMdp.setError("Le mot de passe doit contenir au minimum 4 caractères.");
                    return;
                }

                progressBar2.setVisibility(View.VISIBLE);

                // Enregistrer l'utilisateur dans FireBase

                fireBaseAuth.createUserWithEmailAndPassword(mail, mdp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"Utilisateur créé",Toast.LENGTH_SHORT).show();
                            utilisateurID = fireBaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("utilisateur").document(utilisateurID);
                            Map<String, Object> utilisateur = new HashMap<>();
                            utilisateur.put("nom",nom);
                            utilisateur.put("prenom",prenom);
                            utilisateur.put("mail",mail);
                            documentReference.set(utilisateur).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG","Succès ! Le profil utilisateur a bien été créé");
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } else{
                            Toast.makeText(RegisterActivity.this,"Erreur ! "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar2.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}