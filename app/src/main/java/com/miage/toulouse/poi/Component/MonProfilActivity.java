package com.miage.toulouse.poi.Component;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.miage.toulouse.poi.R;

public class MonProfilActivity extends AppCompatActivity {
    FirebaseAuth fireBaseAuth;
    String utilisateurID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_profil);

        fireBaseAuth = FirebaseAuth.getInstance();
        utilisateurID = fireBaseAuth.getCurrentUser().getUid();
    }

    public void goToMenuActivity(View view) {
        Intent intent=new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}