package com.example.acompanhatche;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ObraDetalhes extends AppCompatActivity {

    private Button btnVoltarMapa;


    private TextView txtNome, txtStatus, txtLocalizacao,
            txtOrigemRecurso, txtContratado,
            txtDataAssinatura, txtPrazo,
            txtInvestimento,  txtLote, txtZona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obra_detalhes);

        txtNome = findViewById(R.id.txtNome);
        txtStatus = findViewById(R.id.txtStatus);
        txtLocalizacao = findViewById(R.id.txtLocalizacao);
        txtOrigemRecurso = findViewById(R.id.txtOrigemRecurso);
        txtContratado = findViewById(R.id.txtContratado);
        txtDataAssinatura = findViewById(R.id.txtDataAssinatura);
        txtPrazo = findViewById(R.id.txtPrazo);
        txtInvestimento = findViewById(R.id.txtInvestimento);
        txtLote = findViewById(R.id.txtLote);
        txtZona = findViewById(R.id.txtZona);

        btnVoltarMapa = findViewById(R.id.btnVoltarMapa);
        btnVoltarMapa.setOnClickListener(v -> finish());




        int obraId = getIntent().getIntExtra("obra_id", -1);

        if (obraId == -1) {
            Toast.makeText(this, "Obra inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carregarDetalhes(obraId);
    }


    private void carregarDetalhes(int id) {

        String url = ApiConfig.GET_OBRA_POR_ID + id;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {

                        if (response.length() == 0) {
                            Toast.makeText(this, "Nenhuma obra encontrada", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        JSONObject responseObj = response.getJSONObject(0);

                        txtNome.setText(responseObj.optString("nome"));
                        txtStatus.setText(responseObj.optString("status"));
                        txtLocalizacao.setText(responseObj.optString("localizacao"));

                        txtOrigemRecurso.setText(
                                "Origem do recurso: " +
                                        responseObj.optString("origem_recurso")
                        );

                        txtContratado.setText(
                                "Contratado: " +
                                        responseObj.optString("contratado")
                        );

                        txtDataAssinatura.setText(
                                "Data da assinatura: " +
                                        responseObj.optString("data_assinatura")
                        );

                        txtPrazo.setText(
                                "Prazo de conclusão: " +
                                        responseObj.optString("prazo_conclusao")
                        );

                        txtInvestimento.setText(
                                "Investimento: " +
                                        responseObj.optString("investimento")
                        );

                        txtLote.setText(responseObj.optString("lote")); // Ex: LOTE 4
                        txtZona.setText(responseObj.optString("zona")); // Ex: ZONA OESTE



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(
                        this,
                        "Erro ao carregar os dados",
                        Toast.LENGTH_SHORT
                ).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

}
