package com.gmail.nithish.weatherforecast;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.MapFragment;
import com.hamweather.aeris.communication.AerisEngine;

public class MapActivity extends AppCompatActivity   {
    private String locationData;

    public String getLocationData()
    {
        return locationData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent it = getIntent();
        if(it!=null)
        {
            locationData = it.getStringExtra("LOCATION");
            Log.d("LOCATION DETAILS", locationData);
        }
        else
        {
            Log.d("Error Intent DETAILS","itent received is null");
        }
        AerisEngine.initWithKeys(this.getString(R.string.aeris_client_id), this.getString(R.string.aeris_client_secret), this);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment mapfg = new mapFragment();
        fragmentTransaction.add(R.id.mapFrameLayout, mapfg);
        fragmentTransaction.commit();
        Log.d("MAPACT", "Start");
    }
}
