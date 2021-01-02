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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.miage.toulouse.poi.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapViewPoint extends AppCompatActivity {
    private MapView mapView;
    private String lat = "48.7588194";
    private String lon = "1.9441072";
    private String description = "";
    private String url = "";
    private Location currentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
