package com.miage.toulouse.poi.Component;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.miage.toulouse.poi.Entity.PointInteret;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;
import com.miage.toulouse.poi.Services.GestionAPI;
import com.miage.toulouse.poi.Services.ListPointInteretAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesPointInteretActivity extends AppCompatActivity {

    APIService apiService;
    GestionAPI gestionAPI = new GestionAPI();
    private List<PointInteret> listPointInterets;
    private ArrayList<String> listMessages = new ArrayList<>();
    ListView listViewMessages;
    PointInteret pointInteret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        pointInteret= (PointInteret)intent.getSerializableExtra("myPointInteret");
        setContentView(R.layout.activity_messages_point_interet);
        TextView textNomPointInteret = findViewById(R.id.textNomPointInteret);
        textNomPointInteret.setText(pointInteret.getNom());
        EditText textMessage = findViewById(R.id.editTextMessage);
        Button boutonPublier = findViewById(R.id.BoutonPublier);
        listViewMessages = findViewById(R.id.ListViewMessages);
        apiService= gestionAPI.initApiService();

        if(pointInteret.getMessages() != null){
            afficherListViewMessages(pointInteret.getMessages());
        }


        boutonPublier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textMessage.getText().toString().trim();

                if(TextUtils.isEmpty(message)){
                    textMessage.setError("Le mot de passe est obligatoire.");
                    return ;
                }

                String messagesPointInteret = pointInteret.getMessages();
                messagesPointInteret = messagesPointInteret +MenuActivity.currentUser.getPrenom()+" "+MenuActivity.currentUser.getNom()+" : "+message+";";
                pointInteret.setMessages(messagesPointInteret);
                Call<Void> res =apiService.updateMessagesPointInteret(pointInteret);
                res.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(MessagesPointInteretActivity.this, "Message publié", Toast.LENGTH_SHORT).show();
                            afficherListViewMessages(pointInteret.getMessages());
                            textMessage.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MessagesPointInteretActivity.this, "Erreur : "+t, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }

    private void afficherListViewMessages(String messages) {
        listMessages.clear();
        String[] messagesTab = messages.split(";");
        for(String message : messagesTab){
            listMessages.add(message);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listMessages);
        arrayAdapter.notifyDataSetChanged();
        listViewMessages.setAdapter(arrayAdapter);
    }

    public void goToListPointInteret(View view) {
        Intent intent = new Intent(this, MenuPointInteret.class);
        startActivity(intent);
        finish();
    }

}