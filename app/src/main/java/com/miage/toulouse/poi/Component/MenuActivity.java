package com.miage.toulouse.poi.Component;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.miage.toulouse.poi.Authentication.LoginActivity;
import com.miage.toulouse.poi.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }

    public void goToLoginActivity(View view) {
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToMonProfilActivity(View view) {
        Intent intent=new Intent(this, MonProfilActivity.class);
        startActivity(intent);
        finish();
    }
}