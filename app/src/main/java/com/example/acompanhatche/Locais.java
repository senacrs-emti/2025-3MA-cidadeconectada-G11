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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
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

import com.example.acompanhatche.ApiConfig;

public class Locais extends AppCompatActivity implements GoogleMap.OnMapLoadedCallback, OnMapReadyCallback {

    private static LatLng ultimaPosicaoCamera = null;
    private static float ultimoZoom = 0;
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap meuMapa;
    Location currentLocation;
    private Marker marcadorUsuario;
    private static final String URL_API = ApiConfig.GET_OBRAS;
    private String obraBuscada = null;
    ImageView btnMinhaLocalizacao;
    private List<Obra> listaObras = new ArrayList<>();
    private List<Marker> listaMarkers = new ArrayList<>();
    private AutoCompleteTextView mapSearch;

    ImageView btnPerfil, btnVoltarHome;

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

        if (getIntent().hasExtra("busca_obra")) {
            obraBuscada = getIntent().getStringExtra("busca_obra");
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mapSearch = findViewById(R.id.mapSearch);

        mapSearch.setOnItemClickListener((parent, view, position, id) -> {
            String termo = parent.getItemAtPosition(position).toString();
            buscarObraNoMapa(termo);
        });

        mapSearch.setOnEditorActionListener((v, actionId, event) -> {
            buscarObraNoMapa(mapSearch.getText().toString());
            return true;
        });

        btnPerfil = findViewById(R.id.btnPerfil);

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Locais.this, Perfil.class);
            startActivity(intent);
        });
        btnVoltarHome = findViewById(R.id.btnVoltarHome);

        btnVoltarHome.setOnClickListener(v -> {
            Intent intent = new Intent(Locais.this, MainActivity.class);
            startActivity(intent);
        });

        btnMinhaLocalizacao = findViewById(R.id.btnMinhaLocalizacao);

        btnMinhaLocalizacao.setOnClickListener(v -> {

            if (meuMapa == null || currentLocation == null) {
                Toast.makeText(this, "Localização indisponível", Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng minhaPosicao = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );

            meuMapa.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(minhaPosicao, 16)
            );
        });


    }
    private String removerAcentos(String texto) {
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase();
    }

    private void buscarObraNoMapa(String nomeBuscado) {
        if (meuMapa == null || nomeBuscado.isEmpty()) return;

        String buscaNormalizada = removerAcentos(nomeBuscado);

        for (Marker marker : listaMarkers) {
            if (marker.getTitle() != null) {

                String tituloNormalizado = removerAcentos(marker.getTitle());

                if (tituloNormalizado.contains(buscaNormalizada)) {
                    meuMapa.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16)
                    );
                    marker.showInfoWindow();
                    return;
                }
            }
        }

        Toast.makeText(this, "Obra não encontrada", Toast.LENGTH_SHORT).show();
    }



    private void adicionarObrasNoMapa() {
        if (meuMapa == null) return;

        listaMarkers.clear();


        // marcador do usuário novamente
        if (ultimaPosicaoCamera != null) {

            // Volta exatamente onde o usuário estava antes
            meuMapa.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(ultimaPosicaoCamera, ultimoZoom)
            );

        } else if (currentLocation != null) {

            // Só na PRIMEIRA vez vai para a localização atual
            LatLng poa = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );

            meuMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(poa, 16));

            marcadorUsuario = meuMapa.addMarker(
                    new MarkerOptions()
                            .position(poa)
                            .title("Você está aqui!")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            );

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

                marker.setTag(obra.getObraId());


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

                        if (obraBuscada != null && !obraBuscada.isEmpty()) {
                            buscarObraNoMapa(obraBuscada);
                        }
                        ArrayList<String> sugestoes = new ArrayList<>();

                        for (Obra obra : listaObras) {
                            sugestoes.add(obra.getNome());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_dropdown_item_1line,
                                sugestoes
                        );

                        mapSearch.setAdapter(adapter);



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
            LatLng poa = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );

            marcadorUsuario = meuMapa.addMarker(
                    new MarkerOptions()
                            .position(poa)
                            .title("Você está aqui!")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            );

            meuMapa.moveCamera(CameraUpdateFactory.newLatLngZoom(poa, 16));
        }

        carregarObras();

        meuMapa.setOnCameraIdleListener(() -> {
            ultimaPosicaoCamera = meuMapa.getCameraPosition().target;
            ultimoZoom = meuMapa.getCameraPosition().zoom;
        });


        meuMapa.setOnInfoWindowClickListener(marker -> {

            Object tag = marker.getTag();

            if (tag == null) {
                Toast.makeText(this, "Selecione uma obra", Toast.LENGTH_SHORT).show();
                return;
            }

            int obraId = (int) tag;

            Intent intent = new Intent(Locais.this, ObraDetalhes.class);
            intent.putExtra("obra_id", obraId);
            startActivity(intent);
        });

        meuMapa.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(
                        R.layout.info_window_obra, null
                );

                TextView txtTituloObra = view.findViewById(R.id.txtTituloObra);
                TextView txtDetalhes = view.findViewById(R.id.txtDetalhes);

                txtTituloObra.setText(marker.getTitle());
                txtDetalhes.setText("Toque para ver detalhes");

                return view;
            }
        });




    }


    @Override
    public void onMapLoaded(){

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}