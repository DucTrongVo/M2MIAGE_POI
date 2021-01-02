package com.miage.toulouse.poi.Services;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.miage.toulouse.poi.Component.MenuActivity;
import com.miage.toulouse.poi.Entity.Theme;

import java.util.ArrayList;

public class GestionListThemes {
    FirebaseFirestore fStore;
    ArrayAdapter<String> arrayAdapter;

    public GestionListThemes(){};
    public void addDataToListView(ListView listViewTheme, ArrayList<String> listThemes, Context context){
        fStore = FirebaseFirestore.getInstance();
        fStore.collection("Theme").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                listThemes.clear();
                for (DocumentSnapshot snapshot : value) {
                    listThemes.add(snapshot.getString("Nom"));
                }
                arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, listThemes);
                arrayAdapter.notifyDataSetChanged();
                listViewTheme.setAdapter(arrayAdapter);
            }
        });
    }

    public void addDataToListView(ListView listViewTheme, ArrayList<String> listThemes, Context context, String[] listThemesUser ){
        fStore = FirebaseFirestore.getInstance();
        fStore.collection("Theme").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                listThemes.clear();
                for (DocumentSnapshot snapshot : value) {
                    String nomTheme = snapshot.getString("Nom");
                    for(String nomThemeUser : listThemesUser){
                        if(nomTheme.equals(nomThemeUser)){
                            listThemes.add(snapshot.getString("Nom"));
                        }
                    }
                }
                arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listThemes);
                arrayAdapter.notifyDataSetChanged();
                listViewTheme.setAdapter(arrayAdapter);
            }
        });
    }

    public String createListStringThemes(ArrayList<String> listThemesUser) {
        String themes="";
        for(String theme : listThemesUser){
            for(Theme th : MenuActivity.listThemes){
                if(th.getNom().equalsIgnoreCase(theme)){
                    themes = themes+th.getId()+";";
                }
            }
        }
        return themes;
    }
}
