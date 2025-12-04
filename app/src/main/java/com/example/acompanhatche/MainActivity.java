package com.example.acompanhatche;

import android.os.Bundle;

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
    private static final String URL_API = "http://192.168.21.224/api/get_data.php";

    RecyclerView recycler;
    List<Obra> listaObras = new ArrayList<>();
    ObraAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        // encontra o RecyclerView no layout
        recycler = findViewById(R.id.recyclerObras);

        // configura o LayoutManager e o Adapter
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ObraAdapter(listaObras);
        recycler.setAdapter(adapter);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        carregarObrasAleatorias();

    }
    private void carregarObrasAleatorias() {

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
                                    obj.optString("nome"),
                                    obj.optString("status")
                            );

                            listaObras.add(obra);
                        }

                        // 🔀 embaralha a lista
                        java.util.Collections.shuffle(listaObras);

                        if (listaObras.size() > 5) {
                            listaObras = listaObras.subList(0, 5);
                        }

                        adapter = new ObraAdapter(listaObras);
                        recycler.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }
        );

        queue.add(request);
    }


}


