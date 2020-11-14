package com.miage.toulouse.poi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText TextMail, TextMdp;
    Button BoutonConnexion, BoutonCreerUnCompte;
    FirebaseAuth fireBaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        TextMail = findViewById(R.id.TextMail);
        TextMdp = findViewById(R.id.TextMdp);
        BoutonConnexion = findViewById(R.id.BoutonCreerCompte);
        BoutonCreerUnCompte = findViewById(R.id.BoutonCreerUnCompte);

        fireBaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
    }
}