package com.example.mohamed.legapp2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.example.mohamed.legapp2.Beacon;

import java.util.ArrayList;


public class LocalisationActivity extends Activity {

    private ArrayList<Beacon> beacons;
    private ArrayList<Localisation> localisations;
    String text1 = " ";
    String text2 = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localisation);

        localisations = new ArrayList<Localisation>();
        Localisation localisation1 = new Localisation("Salle de réunion","10000","1");
        Localisation localisation2 = new Localisation("Bureau Directeur","10000","2");
        Localisation localisation3 = new Localisation("Bureau Trésorier","10000","3");
        localisations.add(localisation1);
        localisations.add(localisation2);
        localisations.add(localisation3);

        beacons = new ArrayList<Beacon>();
        Intent intent = this.getIntent();
        beacons = intent.getParcelableArrayListExtra("key");

        TextView textView1 = (TextView) findViewById(R.id.textView1);
        TextView textView2 = (TextView) findViewById(R.id.textView2);

        int l = beacons.size();
        int m = localisations.size();
        //le RSSI decroit avec la distance
        for (int i=0;i<l;i++){
            text1 = text1+" Balise "+(i+1)+" : ("+beacons.get(i).major +" , "+ beacons.get(i).minor+" , "+beacons.get(i).rssi+")" ;
            for (int j=0;j<m;j++){
                if(beacons.get(i).rssi > -70 && beacons.get(i).minor.equals(localisations.get(j).minor) && beacons.get(i).major.equals(localisations.get(j).major)) {
                    text2 = text2+localisations.get(j).nom +" ";
                }
            }
        }

        textView1.setText(text1);
        textView2.setText(text2);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_localisation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
