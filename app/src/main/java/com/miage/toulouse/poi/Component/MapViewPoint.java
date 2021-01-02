package com.miage.toulouse.poi.Component;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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
            //The key argument here must match that used in the other activity
        }
        GeoPoint startPoint = new GeoPoint(Float.parseFloat(lat), Float.parseFloat(lon));
        mapController.setCenter(startPoint);
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //startMarker.setIcon();
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
