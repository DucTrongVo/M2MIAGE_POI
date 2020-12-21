package com.miage.toulouse.poi.Services;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miage.toulouse.poi.Authentication.RegisterActivity;

import java.util.ArrayList;

public class GestionUtilisateur {
    EditText textNom, textPrenom, textMail, textMdp;
    String nom, prenom, mail, mdp, themes;
    ArrayList<String> listThemesUser;

    public GestionUtilisateur(EditText textPrenom, EditText textNom, EditText textMail, EditText textMdp, String themes){
        this.textNom=textNom;
        this.nom = this.textNom.getText().toString().trim();
        this.textPrenom=textPrenom;
        this.prenom = this.textPrenom.getText().toString().trim();
        this.textMail=textMail;
        this.mail = this.textMail.getText().toString().trim();
        this.textMdp = textMdp;
        this.mdp = this.textMdp.getText().toString().trim();
        this.themes = themes;
    }
    public GestionUtilisateur(EditText textPrenom, EditText textNom, EditText textMail, String themes){
        this.textNom=textNom;
        this.nom = this.textNom.getText().toString().trim();
        this.textPrenom=textPrenom;
        this.prenom = this.textPrenom.getText().toString().trim();
        this.textMail=textMail;
        this.mail = this.textMail.getText().toString().trim();
        this.themes = themes;
    }

    public boolean checkSaisieOk(Context context){
        if(! checkSaisieOkWithoutMdp(context)){
            return false;
        }
        if(TextUtils.isEmpty(mdp)){
            textMdp.setError("Le mot de passe est obligatoire.");
            return false;
        }
        return true;
    }

    public boolean checkSaisieOkWithoutMdp(Context context){
        if(TextUtils.isEmpty(prenom)){
            textPrenom.setError("Le prénom est obligatoire.");
            return false;
        }
        if(TextUtils.isEmpty(mail)){
            textMail.setError("Le mail est obligatoire.");
            return false;
        }
        if(themes.isEmpty()){
            Toast.makeText(context, "Veuillez choisir au moins un thème parmis la liste.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
