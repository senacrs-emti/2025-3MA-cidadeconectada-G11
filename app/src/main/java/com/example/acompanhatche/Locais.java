package com.example.acompanhatche;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.ReturnThis;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class Locais extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback {

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap meuMapa;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    private final ActivityResultLauncher<String> locationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getLastLocation();
        } else {
            Toast.makeText(this, "Permissão de localização negada!", Toast.LENGTH_SHORT).show();
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_locais);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                SupportMapFragment mapFragment =
                        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

                if (mapFragment != null) {
                    mapFragment.getMapAsync(Locais.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        meuMapa = googleMap;
        if (currentLocation != null){
            LatLng poa = new LatLng(currentLocation.getLatitude(), (currentLocation.getLongitude()));
            meuMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(poa, 16));
            MarkerOptions options = new MarkerOptions().position(poa).title("Você está aqui!");
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            meuMapa.addMarker(options);
        }

    }
    /**
     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     super.onRequestPermissionsResult(requestCode, permissions, grantResults);
     if (requestCode == FINE_PERMISSION_CODE) {
     if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
     getLastLocation();
     } else {
     Toast.makeText(this, "Permissão de acesso à localização negada! Por favor, ative a permissão para acessar.", Toast.LENGTH_SHORT).show();
     }
     }
     }*/



    @Override
    public void onMapLoaded(){

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}