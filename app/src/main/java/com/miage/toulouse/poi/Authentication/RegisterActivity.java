package com.miage.toulouse.poi.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miage.toulouse.poi.Component.ListThemesActivity;
import com.miage.toulouse.poi.Entity.Utilisateur;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;
import com.miage.toulouse.poi.Services.GestionListThemes;
import com.miage.toulouse.poi.Services.GestionUtilisateur;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseUser user;
    EditText TextNom, TextPrenom, TextMail, TextMdp;
    Button BoutonCreerCompte;
    FirebaseAuth fireBaseAuth;
    ProgressBar progressBar2;
    FirebaseFirestore fStore;
    String utilisateurID;
    String photoURL;
    ArrayList<String> listThemesUser = new ArrayList<String>();
    GestionListThemes gestionListThemes = new GestionListThemes();
    DatabaseReference dbRef ;
    ImageView profilePic;
    Uri imageUri;

    GestionUtilisateur gestionUtilisateur;

    ListView listViewTheme ;
    ArrayList<String> listThemes = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    FirebaseStorage storage;
    StorageReference storageReference;

    Retrofit retrofit;
    APIService apiService;
    final String BASE_URL = "https://us-central1-projetmobilite-a0b6f.cloudfunctions.net/app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbRef = FirebaseDatabase.getInstance().getReference("Theme");
        fireBaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        TextNom = findViewById(R.id.TextNom);
        TextPrenom = findViewById(R.id.TextPrenom);
        TextMail = findViewById(R.id.TextMail);
        TextMdp = findViewById(R.id.TextPhotoURL);
        BoutonCreerCompte = findViewById(R.id.BoutonCreerCompte);
        progressBar2 = findViewById(R.id.progressBar2);
        profilePic = findViewById(R.id.profilePic);
        listViewTheme = findViewById(R.id.ListViewThemes);
        listViewTheme.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIService.class);
        gestionListThemes.addDataToListView(listViewTheme, listThemes, getApplicationContext());
        listViewTheme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedTheme = ((TextView) view).getText().toString();
                Log.d("Register ","SelectedTheme "+selectedTheme);
                if (listThemesUser.contains(selectedTheme)) {
                    listThemesUser.remove(selectedTheme);
                } else {
                    listThemesUser.add(selectedTheme);
                }
            }
        });

        BoutonCreerCompte.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String nom = TextNom.getText().toString().trim();
                String prenom = TextPrenom.getText().toString().trim();
                String mail = TextMail.getText().toString().trim();
                String mdp = TextMdp.getText().toString().trim();
                Log.d("Register ","nom "+nom);
                Log.d("Register ","prenom "+prenom);
                Log.d("Register ","mail "+mail);
                Log.d("Register ","mdp "+mdp);
                String themes = gestionListThemes.createListStringThemes(listThemesUser);
                Log.d("Register ","Themes "+themes);
                gestionUtilisateur = new GestionUtilisateur(TextPrenom, TextNom,TextMail,TextMdp, themes);
                if ( !gestionUtilisateur.checkSaisieOk(RegisterActivity.this)) {
                    return;
                }

                progressBar2.setVisibility(View.VISIBLE);

                // Enregistrer l'utilisateur dans FireBase
                fireBaseAuth.createUserWithEmailAndPassword(mail, mdp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"Utilisateur créé",Toast.LENGTH_SHORT).show();
                            user = fireBaseAuth.getCurrentUser();
                            utilisateurID = user.getUid();
                            createUtilisateur(utilisateurID,nom, prenom, mail, themes, photoURL);
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } else{
                            Toast.makeText(RegisterActivity.this,"Erreur ! "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar2.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        photoURL = UUID.randomUUID().toString();
        StorageReference imageFilePath = storageReference.child("images/"+ photoURL);
        imageFilePath.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(findViewById(android.R.id.content), "Image téléchargée.",Snackbar.LENGTH_LONG) .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Echec du téléchargement.", Toast.LENGTH_LONG).show();
                    }
                });
    }


    public void goToListThemesActivity(View view) {
        Intent intent=new Intent(this, ListThemesActivity.class);
        startActivity(intent);
    }

    public void createUtilisateur(String identifiant, String nom, String prenom, String mail, String themes, String photoURL){
        Utilisateur utilisateur = new Utilisateur(identifiant, nom, prenom, mail, themes, photoURL);
        final Call<Void> user = apiService.createUtilisateur(utilisateur);
        user.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Utilisateur crée dans la base", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Erreur connexion!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void goToLoginActivity(View view) {
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}