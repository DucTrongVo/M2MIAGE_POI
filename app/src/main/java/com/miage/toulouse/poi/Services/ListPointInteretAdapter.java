package com.miage.toulouse.poi.Services;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

        header.setText(pointInteret.getNom());
        description.setText(pointInteret.getDescription());
        Coordinates coord = new Coordinates(Float.parseFloat(pointInteret.getLat()), Float.parseFloat(pointInteret.getLon()));
        String d = getDistanceFromCurrentLocation(coord) + " km";
        distance.setText(d);
        String themesUser = getThemesFromListCodes(pointInteret);
        themes.setText(themesUser);
        return vi;
    }

    public float getDistanceFromCurrentLocation(Coordinates destination){
        float[] distances = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), destination.getLat(), destination.getLon(), distances);
        return distances[0]/1000;
    }
    private String getThemesFromListCodes(PointInteret pointInteret){
        StringBuilder themes = new StringBuilder();
        for(String codeTheme : pointInteret.getThemes().split(";")){
            for(int i=0;i<listThemes.size();i++){
                if(listThemes.get(i).getId().equalsIgnoreCase(codeTheme)){
                    themes.append(listThemes.get(i).getNom());
                    if(i < (listThemes.size()-2)){
                        themes.append(" - ");
                    }
                }
            }
        }
        return themes.toString();
    }

}
