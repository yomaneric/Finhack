package com.example.finhack;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import javax.xml.transform.Result;
import java.security.PKCS12Attribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MainActivity extends AppCompatActivity {

    int PERMISSIONS_REQUEST_CODE_ACCESS_WIFI_STATE = 1;
    List<Integer> pre_location = new ArrayList<>();
    List<Integer> guest_location = new ArrayList<>();
    ImageView imageView;
    int pre_cur_rssi = 0;
    int guest_cur_rssi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE);
        rssiFingerPrint();

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED))
        {
            //Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    PERMISSIONS_REQUEST_CODE_ACCESS_WIFI_STATE);
        }

        Button startButton = findViewById(R.id.startButton);
        Button findButton = findViewById(R.id.FindButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start();
            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindNear();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void Start(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        TextView wifiNames = findViewById(R.id.wifi_name);
        TextView rssi = findViewById(R.id.rssi);
        boolean pre_checked = false;
        boolean guest_checked = false;
        wifiNames.setMovementMethod(new ScrollingMovementMethod());
        rssi.setMovementMethod(new ScrollingMovementMethod());
        //Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //startActivity(myIntent);
        // Level of a Scan Result
        wifiManager.startScan();
        List<ScanResult> wifiList = wifiManager.getScanResults();
        rssi.setText("RSSI\n");
        wifiNames.setText("WIFI\n");
        for (ScanResult scanResult : wifiList) {
            if (scanResult.BSSID.equals("a8:bd:27:ca:3d:70") && pre_checked == false) {
                pre_checked = true;
                rssi.setText(rssi.getText() + Integer.toString(scanResult.level) + "\n");
                pre_cur_rssi = scanResult.level;
                wifiNames.setText(wifiNames.getText() + scanResult.SSID +"\n");
            }
            else if (scanResult.BSSID.equals("a8:bd:27:ca:3d:63") && guest_checked == false) {
                guest_checked = true;
                rssi.setText(rssi.getText() + Integer.toString(scanResult.level) + "\n");
                guest_cur_rssi = scanResult.level;
                wifiNames.setText(wifiNames.getText() + scanResult.SSID + "\n");
            }
            if (guest_checked == true && pre_checked == true)
                break;
        }
    }

    public void FindNear(){
        TextView NearText = findViewById(R.id.NN);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setVisibility(View.VISIBLE);
        NearText.setText(Integer.toString(NearestNeighbor()));
    }

    public void rssiFingerPrint(){

        pre_location.add(-47);
        pre_location.add(-50);
        pre_location.add(-51);
        pre_location.add(-51);
        pre_location.add(-57);
        pre_location.add(-43);
        pre_location.add(-51);
        pre_location.add(-54);
        pre_location.add(-48);

        guest_location.add(-37);
        guest_location.add(-44);
        guest_location.add(-46);
        guest_location.add(-36);
        guest_location.add(-43);
        guest_location.add(-45);
        guest_location.add(-31);
        guest_location.add(-38);
        guest_location.add(-41);
    }

    public int NearestNeighbor(){

        List<Integer> sumOfDiff = new ArrayList<>();
        Button findButton = findViewById(R.id.FindButton);
        TextView ResultView = findViewById(R.id.Result);

        for (int i = 0; i < 9; i++) {
            int guest_diff = Math.abs(guest_cur_rssi - guest_location.get(i));
            int pre_diff = Math.abs(pre_cur_rssi - pre_location.get(i));
            sumOfDiff.add(guest_diff+pre_diff);
            ResultView.setText(ResultView.getText() + "Zone" + Integer.toString(i+1) + ": " +Integer.toString(guest_diff+pre_diff)+ getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab));
            if ((i+1) % 3 == 0)
                ResultView.setText(ResultView.getText() + "\n");
        }

        return sumOfDiff.indexOf(Collections.min(sumOfDiff));
    }

}
