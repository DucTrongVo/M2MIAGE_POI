package com.miage.toulouse.poi.Component;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;

import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MonProfilActivity extends AppCompatActivity {
    FirebaseAuth fireBaseAuth;
    APIService apiService;
    Retrofit retrofit;
    String utilisateurID;
    String photoURL;
    ImageView profilePic;
    StorageReference storageReference;
    final String BASE_URL = "https://us-central1-projetmobilite-a0b6f.cloudfunctions.net/app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_profil);
        TextView textPrenom = (TextView) findViewById(R.id.TextPrenom);
        TextView textNom = (TextView) findViewById(R.id.TextNom);
        TextView textMail = (TextView) findViewById(R.id.TextMail);
        TextView textPhotoURL = (TextView) findViewById(R.id.TextPhotoURL);
        ImageView profilePic = (ImageView) findViewById(R.id.profilePic);
        fireBaseAuth = FirebaseAuth.getInstance();
        utilisateurID = fireBaseAuth.getCurrentUser().getUid();
        initApiService();

        final Call<JsonElement> user = apiService.getUserById(utilisateurID);

        user.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    JsonElement res = response.body();
                    JsonObject jsonObject = res.getAsJsonObject();
                    textPrenom.setText(jsonObject.get("prenom").getAsString());
                    textNom.setText(jsonObject.get("nom").getAsString());
                    textMail.setText(jsonObject.get("mail").getAsString());
                    photoURL = jsonObject.get("photoURL").getAsString();
                    try{
                        storageReference = FirebaseStorage.getInstance().getReference().child("images/"+photoURL);
                        File localFile = File.createTempFile(photoURL,"png");
                        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                profilePic.setImageBitmap(bitmap);
                            }
                        });
                    }catch (IOException e){
                        e.printStackTrace();
                    };
                }
                else{
                    Toast.makeText(MonProfilActivity.this, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                System.out.println(t);
                Toast.makeText(MonProfilActivity.this, "Erreur connexion", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void goToMenuActivity(View view) {
        Intent intent=new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void initApiService(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIService.class);
    }

}