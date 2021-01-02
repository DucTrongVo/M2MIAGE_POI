package com.miage.toulouse.poi.Component;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.miage.toulouse.poi.Entity.Coordinates;
import com.miage.toulouse.poi.Entity.PointInteret;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;
import com.miage.toulouse.poi.Services.GestionAPI;
import com.miage.toulouse.poi.Services.ListPointInteretAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuPointInteret extends AppCompatActivity implements LocationListener, AdapterView.OnItemClickListener {
    private APIService apiService;
    private GestionAPI gestionAPI;

    private List<PointInteret> listPointInterets;
    private List<PointInteret> listPointInteretsByDistance;
    private List<PointInteret> listPointInteretsByTheme;
    private ListView listView;
    private ListPointInteretAdapter adapterListPointInteret;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Coordinates coordinates;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_interet);

        coordinates = new Coordinates(0, 0);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        listView = findViewById(R.id.listView_pointInteret);
        gestionAPI = new GestionAPI();
        apiService = gestionAPI.initApiService();
        listView.setOnItemClickListener(MenuPointInteret.this);
        checkPermission();
    }

    private void afficheListePointInteret(){
        final Call<List<PointInteret>> listPIs = apiService.getAllPointInteret();
        listPIs.enqueue(new Callback<List<PointInteret>>() {
            @Override
            public void onResponse(Call<List<PointInteret>> call, Response<List<PointInteret>> response) {
                if(response.isSuccessful()){
                    listPointInterets  = response.body();
                    listPointInteretsByDistance = sortByDistance(listPointInterets);
                    listPointInteretsByTheme = sortByThemes(listPointInterets);
                    adapterListPointInteret = new ListPointInteretAdapter(getApplicationContext(), listPointInteretsByDistance, currentLocation, MenuActivity.listThemes);
                    listView.setAdapter(adapterListPointInteret);
                    listView.setOnItemClickListener(MenuPointInteret.this::onItemClick);
                }
            }

            @Override
            public void onFailure(Call<List<PointInteret>> call, Throwable t) {
                Toast.makeText(MenuPointInteret.this, "Erreur Fetch Point Interet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<PointInteret> sortByDistance(List<PointInteret> listPointInterets){
        List<PointInteret> listSorted = new ArrayList<>(listPointInterets);
        for(int j=0;j<listSorted.size();j++){
            for(int i=0;i<listSorted.size()-1;i++){
                Coordinates coord1 = new Coordinates(Float.parseFloat(listSorted.get(i).getLat()), Float.parseFloat(listSorted.get(i).getLon()));
                Coordinates coord2 = new Coordinates(Float.parseFloat(listSorted.get(i+1).getLat()), Float.parseFloat(listSorted.get(i+1).getLon()));
                if(getDistanceFromCurrentLocation(coord1) > getDistanceFromCurrentLocation(coord2)){
                    PointInteret temp = listSorted.get(i);
                    listSorted.set(i,listSorted.get(i+1));
                    listSorted.set(i+1,temp);
                }
            }
        }
        return listSorted;
    }

    private List<PointInteret> sortByThemes(List<PointInteret> listPointInterets){
        List<PointInteret> listFinal = new ArrayList<>();
        for(PointInteret p : listPointInterets){
            List<String> listThemesPointInteret = new ArrayList<>(Arrays.asList(p.getThemes().split(";")));
            List<String> listThemesUser = new ArrayList<>(Arrays.asList(MenuActivity.currentUser.getThemes().split(";")));
            listThemesPointInteret.retainAll(listThemesUser);
            if(listThemesPointInteret.size() > 0){
                listFinal.add(p);
            }
        }
        return listFinal;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 8) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                currentLocation = getLastKnowLocation();
                if (currentLocation != null) {
                    coordinates.setLat((float) currentLocation.getLatitude());
                    coordinates.setLon((float) currentLocation.getLongitude());
                }
                afficheListePointInteret();
            }
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        coordinates.setLat((float) location.getLatitude());
        coordinates.setLon((float) location.getLongitude());
    }
    private void checkPermission(){
        if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MenuPointInteret.this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION }, 8);
        }else{
            currentLocation = getLastKnowLocation();

            if(currentLocation != null){
                coordinates.setLat((float) currentLocation.getLatitude());
                coordinates.setLon((float) currentLocation.getLongitude());
            }
            afficheListePointInteret();
        }
    }
    public float getDistanceFromCurrentLocation(Coordinates destination){
        float[] distances = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), destination.getLat(), destination.getLon(), distances);
        return distances[0]/1000;
    }

    @SuppressLint("MissingPermission")
    private Location getLastKnowLocation(){
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1, this);
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MenuPointInteret.this,MapViewPoint.class);
        intent.putExtra("lat",listPointInterets.get(position).getLat());
        intent.putExtra("lon",listPointInterets.get(position).getLon());
        intent.putExtra("description", listPointInterets.get(position).getNom());
        intent.putExtra("url", listPointInterets.get(position).getUrl());
        startActivity(intent);
        finish();
    }

    public void goToMenuActivity(View view){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void sortListByDistance(View view){
        adapterListPointInteret = new ListPointInteretAdapter(getApplicationContext(), listPointInteretsByDistance, currentLocation, MenuActivity.listThemes);
        listView.setAdapter(adapterListPointInteret);
        listView.setOnItemClickListener(MenuPointInteret.this::onItemClick);
    }

    public  void sortListByTheme(View view){
        adapterListPointInteret = new ListPointInteretAdapter(getApplicationContext(), listPointInteretsByTheme, currentLocation, MenuActivity.listThemes);
        listView.setAdapter(adapterListPointInteret);
        listView.setOnItemClickListener(MenuPointInteret.this::onItemClick);
    }
}
