package com.miage.toulouse.poi.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.miage.toulouse.poi.Component.MenuPointInteret;
import com.miage.toulouse.poi.Component.MessagesPointInteretActivity;
import com.miage.toulouse.poi.Entity.Coordinates;
import com.miage.toulouse.poi.Entity.PointInteret;
import com.miage.toulouse.poi.Entity.Theme;
import com.miage.toulouse.poi.R;

import java.util.List;

public class ListPointInteretAdapter extends BaseAdapter {
    Context context;
    List<PointInteret> listPointInteret;
    private Location currentLocation;
    private List<Theme> listThemes;
    private static LayoutInflater inflater = null;

    public ListPointInteretAdapter(Context context, List<PointInteret> listPointInteret, Location currentLocation, List<Theme> listThemes) {
        super();
        this.context = context;
        this.listPointInteret = listPointInteret;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.currentLocation = currentLocation;
        this.listThemes = listThemes;
    }

    @Override
    public int getCount() {
        return listPointInteret.size();
    }

    @Override
    public Object getItem(int position) {
        return listPointInteret.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.point_interet_list_item, null);
        PointInteret pointInteret = listPointInteret.get(position);
        TextView header = (TextView) vi.findViewById(R.id.listItemPIHeader);
        TextView description = (TextView) vi.findViewById(R.id.listItemPIDescription);
        TextView distance = (TextView) vi.findViewById(R.id.listItemPIDistance);
        TextView themes = (TextView) vi.findViewById(R.id.listItemPIThemes);
        Button boutonMessages = vi.findViewById(R.id.BoutonMessages);
        header.setText(pointInteret.getNom());
        description.setText(pointInteret.getDescription());
        Coordinates coord = new Coordinates(Float.parseFloat(pointInteret.getLat()), Float.parseFloat(pointInteret.getLon()));
        String d = getDistanceFromCurrentLocation(coord) + " km";
        distance.setText(d);
        String themesUser = getThemesFromListCodes(pointInteret);
        themes.setText(themesUser);

        boutonMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, MessagesPointInteretActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("myPointInteret",pointInteret);
                context.startActivity(intent);
            }
        });
        return vi;
    }

    public float getDistanceFromCurrentLocation(Coordinates destination){
        float[] distances = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), destination.getLat(), destination.getLon(), distances);
        return distances[0]/1000;
    }
    private String getThemesFromListCodes(PointInteret pointInteret){
        StringBuilder themes = new StringBuilder();
        String[] codesThemes = pointInteret.getThemes().split(";");
        for(int i=0;i<codesThemes.length;i++){
            for(Theme theme : listThemes){
                if(theme.getId().equalsIgnoreCase(codesThemes[i])){
                    themes.append(theme.getNom());
                    if(i < (codesThemes.length-1)){
                        themes.append(" - ");
                    }
                }
            }
        }
        return themes.toString();
    }

}
