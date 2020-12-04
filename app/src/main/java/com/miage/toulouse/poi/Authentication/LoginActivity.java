package com.miage.toulouse.poi.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.miage.toulouse.poi.Component.MenuActivity;
import com.miage.toulouse.poi.R;

public class LoginActivity extends AppCompatActivity {

    EditText TextMail, TextMdp;
    Button BoutonConnexion, BoutonCreerUnCompte;
    FirebaseAuth fireBaseAuth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextMail = findViewById(R.id.TextMail);
        TextMdp = findViewById(R.id.TextPhotoURL);
        BoutonConnexion = findViewById(R.id.BoutonConnexion);
        BoutonCreerUnCompte = findViewById(R.id.BoutonCreerUnCompte);

        fireBaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        BoutonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = TextMail.getText().toString().trim();
                String mdp = TextMdp.getText().toString().trim();

                if(TextUtils.isEmpty(mail)){
                    TextMail.setError("Le mail est obligatoire.");
                    return;
                }
                if(TextUtils.isEmpty(mdp)){
                    TextMdp.setError("Le mot de passe est obligatoire.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // vérifier que l'user est dans la BD
                fireBaseAuth.signInWithEmailAndPassword(mail,mdp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Connexion réussie !",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                        } else{
                            Toast.makeText(LoginActivity.this,"Erreur ! "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

    }

    public void goToRegisterActivity(View view) {
        Intent intent=new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}