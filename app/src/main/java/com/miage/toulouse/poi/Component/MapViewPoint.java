package com.miage.toulouse.poi.Component;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.miage.toulouse.poi.Entity.PointInteret;
import com.miage.toulouse.poi.Entity.Utilisateur;
import com.miage.toulouse.poi.R;
import com.miage.toulouse.poi.Services.APIService;
import com.miage.toulouse.poi.Services.GestionAPI;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapViewPoint extends AppCompatActivity {
    private MapView mapView;
    APIService apiService;
    GestionAPI gestionAPI = new GestionAPI();
    private String lat = "48.7588194";
    private String lon = "1.9441072";
    private String description = "";
    private String url = "";
    private String idPointInteret = "";
    private Location currentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService= gestionAPI.initApiService();
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.map_view);
        mapView = findViewById(R.id.mapview);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.zoomTo(19, Long.parseLong("0"));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lat = extras.getString("lat");
            lon = extras.getString("lon");
            description = extras.getString("description");
            url = extras.getString("url");
            idPointInteret = extras.getString("idPointInteret");
            currentLocation = (Location) extras.get("currentLocation");
        }
        // Current Location
        GeoPoint currentLoc = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.person, null);
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
        Drawable dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, (int) (48.0f * getResources().getDisplayMetrics().density), (int) (48.0f * getResources().getDisplayMetrics().density), true));
        Marker location = new Marker(mapView);
        location.setPosition(currentLoc);
        location.setTitle("Vous êtes ici");
        location.setIcon(dr);
        mapView.getOverlays().add(location);

        // Point Interet
        GeoPoint pointInteret = new GeoPoint(Float.parseFloat(lat), Float.parseFloat(lon));
        mapController.setCenter(pointInteret);
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(pointInteret);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle(description);
        startMarker.setSnippet("URL : "+url);
        mapView.getOverlays().add(startMarker);

    }

    public void goToMenuPointInteret(View view){
        Intent intent = new Intent(this, MenuPointInteret.class);
        startActivity(intent);
        finish();
    }

    public void updatePointsVisites(View view){
        final Call<Void> user = apiService.updatePointsVisites(MenuActivity.currentUser.getIdentifiant(), idPointInteret);
        user.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    updateCurrentUser();
                    Toast.makeText(MapViewPoint.this, "Point Interet ajouté dans la liste", Toast.LENGTH_SHORT).show();
                    //MenuPointInteret.listPointInteretsVisites.add()
                }
                else{
                    Toast.makeText(MapViewPoint.this, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Erreur get User : "+t);
                Toast.makeText(MapViewPoint.this, "Erreur get User : "+t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateCurrentUser(){
        final Call<Utilisateur> user = apiService.getUserById(MenuActivity.currentUser.getIdentifiant());
        user.enqueue(new Callback<Utilisateur>() {
            @Override
            public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
                if(response.isSuccessful()){
                    MenuActivity.currentUser = response.body();
                    String stringToPrint = "User is "+MenuActivity.currentUser.getNom()+" "+MenuActivity.currentUser.getPrenom()+" "+MenuActivity.currentUser.getPointsVisites();
                    Log.d("MenuActivity", stringToPrint);
                }
                else{
                    Toast.makeText(MapViewPoint.this, "Erreur API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Utilisateur> call, Throwable t) {
                System.out.println("Erreur get User : "+t);
                Toast.makeText(MapViewPoint.this, "Erreur get User : "+t, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
