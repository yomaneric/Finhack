package com.example.finhack;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int PERMISSIONS_REQUEST_CODE_ACCESS_WIFI_STATE = 1;
    List<Integer> pre_location = new ArrayList<>();
    List<Integer> meeting_location = new ArrayList<>();
    ImageView imageView;
    int pre_cur_rssi = 0;
    int meeting_cur_rssi = 0;
    int error_term = -5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE);
        TextView NN = findViewById(R.id.NN);
        NN.setVisibility(View.INVISIBLE);
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
        // If request is cancelled, the result arrays are empty.
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
            return;
        }
    }

    public void Start(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        TextView wifiNames = findViewById(R.id.wifi_name);
        TextView rssi = findViewById(R.id.rssi);
        boolean pre_checked = false;
        boolean meeting_checked = false;
        wifiNames.setMovementMethod(new ScrollingMovementMethod());
        rssi.setMovementMethod(new ScrollingMovementMethod());
        //Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //startActivity(myIntent);
        // Level of a Scan Result
        wifiManager.startScan();
        List<ScanResult> wifiList = wifiManager.getScanResults();
        rssi.setText("RSSI\n");
        wifiNames.setText("WIFI\n");
        /*for (ScanResult scanResult : wifiList){
            rssi.setText(rssi.getText() + Integer.toString(scanResult.level) + "\n");
            wifiNames.setText(wifiNames.getText() + scanResult.SSID + "(" + scanResult.BSSID + ")" + "\n");
        }*/
        for (ScanResult scanResult : wifiList) {
            if ((scanResult.BSSID.equals("ac:a3:1e:c5:4d:a0") || scanResult.BSSID.equals("a8:bd:27:ca:3d:70")) && pre_checked == false) {
                pre_checked = true;
                rssi.setText(rssi.getText() + Integer.toString(scanResult.level-error_term) + "\n");
                pre_cur_rssi = scanResult.level-error_term;
                wifiNames.setText(wifiNames.getText() + scanResult.SSID +"\n");
            }
            else if ((scanResult.BSSID.equals("ac:a3:1e:c5:4d:a4") || scanResult.BSSID.equals("ac:a3:1e:c5:4d:b4")) && meeting_checked == false) {
                meeting_checked = true;
                rssi.setText(rssi.getText() + Integer.toString(scanResult.level-error_term) + "\n");
                meeting_cur_rssi = scanResult.level-error_term;
                wifiNames.setText(wifiNames.getText() + scanResult.SSID + "\n");
            }
            if (meeting_checked == true && pre_checked == true)
                break;
        }
    }

    public void FindNear(){
        TextView NearText = findViewById(R.id.NN);
        NearText.setVisibility(View.VISIBLE);
        Button startButton = findViewById(R.id.startButton);
        startButton.setVisibility(View.INVISIBLE);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setVisibility(View.VISIBLE);
        NearText.setText("The Nearest Neighbour is Zone " + Integer.toString(NearestNeighbor()) +
                ".\nFulfilled Merchants: Ray-ban");
    }

    public void rssiFingerPrint(){

        pre_location.add(-55);
        pre_location.add(-53);
        pre_location.add(-66);
        pre_location.add(-48);
        pre_location.add(-53);
        pre_location.add(-60);
        pre_location.add(-42);
        pre_location.add(-54);
        pre_location.add(-55);

        meeting_location.add(-38);
        meeting_location.add(-44);
        meeting_location.add(-46);
        meeting_location.add(-37);
        meeting_location.add(-43);
        meeting_location.add(-45);
        meeting_location.add(-31);
        meeting_location.add(-38);
        meeting_location.add(-41);
    }

    public int NearestNeighbor(){

        List<Integer> sumOfDiff = new ArrayList<>();
        TextView ResultView = findViewById(R.id.Result);

        for (int i = 0; i < 9; i++) {
            int meeting_diff = Math.abs(meeting_cur_rssi - meeting_location.get(i));
            int pre_diff = Math.abs(pre_cur_rssi - pre_location.get(i));
            sumOfDiff.add(meeting_diff+pre_diff);
            ResultView.setText(ResultView.getText() + "Zone" + Integer.toString(i+1) + ": " +Integer.toString(meeting_diff+pre_diff)+ getString(R.string.tab) + getString(R.string.tab) + getString(R.string.tab));
            if ((i+1) % 3 == 0)
                ResultView.setText(ResultView.getText() + "\n");
        }

        return sumOfDiff.indexOf(Collections.min(sumOfDiff)) + 1;
    }

}
