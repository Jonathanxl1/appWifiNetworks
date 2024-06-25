package com.fortyfourwebs.wifinetworks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private Button scanButton;
    private ListView listViewRedes;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        scanButton = findViewById(R.id.scanButton);
        listViewRedes = findViewById(R.id.listViewRedes);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewRedes.setAdapter(adapter);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }

                scanWifi();

            }
        });

        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void scanWifi(){
        Toast.makeText(this,"Escaneando redes Wifi",Toast.LENGTH_LONG).show();
        boolean success  = wifiManager.startScan();

        if(!success){
            Toast.makeText(this, "Error al inicio el scaneo", Toast.LENGTH_SHORT).show();
        }

    }

    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(wifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                List<ScanResult> results = wifiManager.getScanResults();
                List<String> redes = new ArrayList<>();
                for (ScanResult result : results) {
                    redes.add("SSid " + result.SSID);
                }
                adapter.clear();
                adapter.addAll(redes);

            } else {
                Toast.makeText(context, "Error al obtener resultados del scaneo", Toast.LENGTH_SHORT).show();
            }

        }

    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiScanReceiver);
    }
}