package com.example.matant.gpsportclient.Utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matant.gpsportclient.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by nirb on 11/20/2015.
 */
public class MarkerInfoWindowAdapter  implements GoogleMap.InfoWindowAdapter
{
    private MapMarker marker;
    private LayoutInflater inflater;
    public MarkerInfoWindowAdapter(MapMarker marker,LayoutInflater inflater)
    {
        this.marker = marker;
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        View v  = inflater.inflate(R.layout.infowindow_layout, null);

        ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

        TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);

        //markerIcon.setImageResource(manageMarkerIcon(marker.getmIcon()));

       // markerLabel.setText(marker.getmLabel());

        return v;
    }
}