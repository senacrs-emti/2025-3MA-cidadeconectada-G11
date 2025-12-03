package com.example.acompanhatche;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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

        // exemplo: dados de teste (apague quando integrar com API)
        listaObras.add(new Obra("Ponte da Azenha", "Em andamento"));
        listaObras.add(new Obra("Viaduto João Pessoa", "Concluído"));
        listaObras.add(new Obra("Avenida Pe. Cacique", "Paralisado"));
        adapter.notifyDataSetChanged();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
