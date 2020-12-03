package com.miage.toulouse.poi.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miage.toulouse.poi.Component.ListThemesActivity;
import com.miage.toulouse.poi.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    EditText TextNom, TextPrenom, TextMail, TextMdp;
    Button BoutonCreerCompte;
    FirebaseAuth fireBaseAuth;
    ProgressBar progressBar2;
    FirebaseFirestore fStore;
    String utilisateurID;
    ArrayList<String> listThemesUser = new ArrayList<String>();

    DatabaseReference dbRef ;
    ImageView profilePic;
    Uri imageUri;

    ListView listViewTheme ;
    ArrayList<String> listThemes = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;

    FirebaseStorage storage;
    StorageReference storageReference;

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
        TextMdp = findViewById(R.id.TextMdp);
        BoutonCreerCompte = findViewById(R.id.BoutonCreerCompte);
        progressBar2 = findViewById(R.id.progressBar2);
        profilePic = findViewById(R.id.profilePic);
        listViewTheme = findViewById(R.id.ListViewThemes);
        listViewTheme.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        addDataToListView(listViewTheme);
        listViewTheme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedTheme = ((TextView) view).getText().toString();
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
                String themes = createListStringThemes(listThemesUser);

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
                if(listThemesUser.size()==0){
                    Toast.makeText(RegisterActivity.this, "Veuillez choisir au moins un thème parmis la liste.", Toast.LENGTH_LONG).show();
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
                            utilisateur.put("identifiant",utilisateurID);
                            utilisateur.put("nom",nom);
                            utilisateur.put("prenom",prenom);
                            utilisateur.put("mail",mail);
                            utilisateur.put("themes",themes);
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

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });
    }

    private String createListStringThemes(ArrayList<String> listThemesUser) {
        String themes="";
        for(String theme : listThemesUser){
            themes = themes+theme+";";
        }
        return themes;
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
        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/"+randomKey);

        riversRef.putFile(imageUri)
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

    private void addDataToListView(ListView listViewTheme){
        fStore.collection("Theme").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                listThemes.clear();
                for (DocumentSnapshot snapshot : value) {
                    listThemes.add(snapshot.getString("Nom"));
                }
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, listThemes);
                arrayAdapter.notifyDataSetChanged();
                listViewTheme.setAdapter(arrayAdapter);
            }
        });
    }

}