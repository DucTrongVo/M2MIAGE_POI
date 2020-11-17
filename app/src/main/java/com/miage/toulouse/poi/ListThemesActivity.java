package com.miage.toulouse.poi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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