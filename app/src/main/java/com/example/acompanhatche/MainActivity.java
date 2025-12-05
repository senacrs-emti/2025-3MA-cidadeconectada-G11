package com.example.acompanhatche;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String URL_API = ApiConfig.GET_OBRAS;

    RecyclerView recycler;
    List<Obra> listaObras = new ArrayList<>();
    ObraAdapter adapter;

    ImageView btnPerfil;
    Button bt_locais;
    AutoCompleteTextView searchView;

    ArrayAdapter<String> adapterBusca;
    ArrayList<String> sugestoes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recyclerObras);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ObraAdapter(listaObras);
        recycler.setAdapter(adapter);

        btnPerfil = findViewById(R.id.btnPerfil);
        bt_locais = findViewById(R.id.bt_locais);
        searchView = findViewById(R.id.searchView);

        // ===== PERFIL =====
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Perfil.class);
            startActivity(intent);
        });

        // ===== MAPA =====
        bt_locais.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Locais.class);
            startActivity(intent);
        });

        // ===== ADAPTER DE SUGESTÕES =====
        adapterBusca = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                sugestoes
        );
        searchView.setAdapter(adapterBusca);

        // ===== CLIQUE NA SUGESTÃO =====
        searchView.setOnItemClickListener((parent, view, position, id) -> {
            String termo = parent.getItemAtPosition(position).toString();

            Intent intent = new Intent(MainActivity.this, Locais.class);
            intent.putExtra("busca_obra", termo);
            startActivity(intent);
        });

        // ===== ENTER NO TECLADO =====
        searchView.setOnEditorActionListener((v, actionId, event) -> {
            String termo = searchView.getText().toString();

            Intent intent = new Intent(MainActivity.this, Locais.class);
            intent.putExtra("busca_obra", termo);
            startActivity(intent);
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        carregarObrasAleatorias();
    }

    // =====================================================

    private void carregarObrasAleatorias() {

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        listaObras.clear();
                        sugestoes.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            Obra obra = new Obra(
                                    obj.optString("nome"),
                                    obj.optString("status")
                            );

                            listaObras.add(obra);
                            sugestoes.add(obra.getNome());
                        }

                        java.util.Collections.shuffle(listaObras);

                        if (listaObras.size() > 15) {
                            listaObras = listaObras.subList(0, 15);
                        }

                        adapter = new ObraAdapter(listaObras);
                        recycler.setAdapter(adapter);

                        adapterBusca.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }
}
