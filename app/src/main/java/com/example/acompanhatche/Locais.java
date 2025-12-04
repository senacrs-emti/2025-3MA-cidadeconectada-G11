package com.example.acompanhatche;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
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

import java.util.ArrayList;
import java.util.List;

public class Locais extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback {

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap meuMapa;
    Location currentLocation;
    private static final String URL_API = "http://192.168.21.224/api/get_data.php";
    private List<Obra> listaObras = new ArrayList<>();
    private List<Marker> listaMarkers = new ArrayList<>();
    private SearchView mapSearch;

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

        SearchView searchView = findViewById(R.id.mapSearch);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarObraNoMapa(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }
    private void buscarObraNoMapa(String nomeBuscado) {
        if (meuMapa == null || nomeBuscado.isEmpty()) return;

        for (Marker marker : listaMarkers) {
            if (marker.getTitle() != null &&
                    marker.getTitle().toLowerCase().contains(nomeBuscado.toLowerCase())) {

                meuMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        marker.getPosition(), 16
                ));
                marker.showInfoWindow();
                return;
            }
        }

        Toast.makeText(this, "Obra não encontrada", Toast.LENGTH_SHORT).show();
    }


    private void adicionarObrasNoMapa() {
        if (meuMapa == null) return;

        meuMapa.clear();
        listaMarkers.clear();

        // marcador do usuário novamente
        if (currentLocation != null) {
            LatLng poa = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );

            meuMapa.addMarker(new MarkerOptions()
                    .position(poa)
                    .title("Você está aqui!")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        }

        for (Obra obra : listaObras) {

            double lat = obra.getLatitudeDouble();
            double lng = obra.getLongitudeDouble();

            if (lat != 0 && lng != 0) {
                LatLng posicao = new LatLng(lat, lng);

                Marker marker = meuMapa.addMarker(
                        new MarkerOptions()
                                .position(posicao)
                                .title(obra.getNome())
                                .snippet(obra.getStatus())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                );

                listaMarkers.add(marker);
            }
        }
    }


    private void carregarObras() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        listaObras.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            Obra obra = new Obra(
                                    obj.optInt("obra_id"),
                                    obj.optString("nome"),
                                    obj.optString("localizacao"),
                                    obj.optString("data_assinatura"),
                                    obj.optString("origem_recurso"),
                                    obj.optString("status"),
                                    obj.optString("contratado"),
                                    obj.optString("prazo_conclusao"),
                                    obj.optString("obra_paralizada"),
                                    obj.optString("razao"),
                                    obj.optString("investimento"),
                                    obj.optString("lote"),
                                    obj.optString("zona"),
                                    obj.optString("valor_despendido"),
                                    obj.optString("latitude"),
                                    obj.optString("longitude")
                            );

                            listaObras.add(obra);
                        }

                        adicionarObrasNoMapa();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Erro ao carregar obras", Toast.LENGTH_SHORT).show()
        );


        queue.add(request);

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

        if (currentLocation != null) {
            LatLng poa = new LatLng(currentLocation.getLatitude(), (currentLocation.getLongitude()));
            meuMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(poa, 16));
            MarkerOptions options = new MarkerOptions().position(poa).title("Você está aqui!");
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            meuMapa.addMarker(options);

        }
        carregarObras();

    }


    @Override
    public void onMapLoaded(){

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}