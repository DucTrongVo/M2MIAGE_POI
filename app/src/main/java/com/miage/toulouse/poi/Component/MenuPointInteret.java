package com.miage.toulouse.poi.Component;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.miage.toulouse.poi.Entity.Coordinates;
import com.miage.toulouse.poi.Entity.PointInteret;
import com.miage.toulouse.poi.Entity.Theme;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;
import com.miage.toulouse.poi.Services.GestionAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuPointInteret extends AppCompatActivity implements LocationListener{
    private APIService apiService;
    private GestionAPI gestionAPI;

    private List<PointInteret> listPointInterets;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;

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

        checkPermission();
    }

    private void afficheListePointInteret(){
        final Call<List<PointInteret>> listPIs = apiService.getAllPointInteret();
        listPIs.enqueue(new Callback<List<PointInteret>>() {
            @Override
            public void onResponse(Call<List<PointInteret>> call, Response<List<PointInteret>> response) {
                if(response.isSuccessful()){
                    listPointInterets  = response.body();
                    List<String> listContent = initiateList(listPointInterets);
                    arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, listContent);
                    listView.setAdapter(arrayAdapter);
                    initiateList(listPointInterets);
                }
            }

            @Override
            public void onFailure(Call<List<PointInteret>> call, Throwable t) {
                Toast.makeText(MenuPointInteret.this, "Erreur Fetch Piont Interet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private List<String> initiateList(List<PointInteret> listPointInterets){
        List<String> listContent = new ArrayList<>();
        for(PointInteret p : listPointInterets){
            String content = p.getNom() + " - Description : "+p.getDescription()+" - Themes : "+getThemesFromListCodes(p);
            listContent.add(content);
        }
        for(int j=0;j<listPointInterets.size()/2;j++){
            for(int i=0;i<listPointInterets.size()-1;i++){
                Coordinates coord1 = new Coordinates(Float.parseFloat(listPointInterets.get(i).getLat()), Float.parseFloat(listPointInterets.get(i).getLon()));
                String content1 = listPointInterets.get(i).getNom() + " - Description : "+listPointInterets.get(i).getDescription()
                        + " - Distance : "+getDistanceFromCurrentLocation(coord1)+" - Themes : "+getThemesFromListCodes(listPointInterets.get(i));
                Coordinates coord2 = new Coordinates(Float.parseFloat(listPointInterets.get(i+1).getLat()), Float.parseFloat(listPointInterets.get(i+1).getLon()));
                String content2 = listPointInterets.get(i+1).getNom() + " - Description : "+listPointInterets.get(i+1).getDescription()
                        + " - Distance : "+getDistanceFromCurrentLocation(coord2)+" - Themes : "+getThemesFromListCodes(listPointInterets.get(i+1));
                if(getDistanceFromCurrentLocation(coord1) > getDistanceFromCurrentLocation(coord2)){
                    listContent.set(i,content2);
                    listContent.set(i+1,content1);
                    PointInteret temp = listPointInterets.get(i);
                    listPointInterets.set(i,listPointInterets.get(i+1));
                    listPointInterets.set(i+1,temp);
                }
            }
        }

        return listContent;
    }

    private String getThemesFromListCodes(PointInteret pointInteret){
        StringBuilder themes = new StringBuilder();
        for(String codeTheme : pointInteret.getThemes().split(";")){
            for(Theme theme : MenuActivity.listThemes){
                if(theme.getId().equalsIgnoreCase(codeTheme)){
                    themes.append(theme.getNom()).append(", ");
                }
            }
        }

        return themes.toString();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 8) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
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

    private Location getLastKnowLocation(){
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission")
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
}
