package com.miage.toulouse.poi.Component;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.miage.toulouse.poi.R;

import java.util.ArrayList;

public class ListThemesActivity extends AppCompatActivity {
    ListView listViewTheme ;
    ArrayList<String> listThemes = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_themes);

        fStore = FirebaseFirestore.getInstance();
        listViewTheme = findViewById(R.id.ListViewThemes);

        fStore.collection("Theme").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                listThemes.clear();
                for(DocumentSnapshot snapshot : value){
                    listThemes.add(snapshot.getString("Nom"));
                }
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice,listThemes);
                arrayAdapter.notifyDataSetChanged();
                listViewTheme.setAdapter(arrayAdapter);
                //taoung
            }
        });

    }
}