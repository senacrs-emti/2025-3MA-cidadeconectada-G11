package com.example.acompanhatche;

import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ApiTestActivity extends AppCompatActivity {

    // BANCO DE DADOS
    private static final String URL_API = "http://192.168.21.224/api/get_data.php"; // <- mudar o ip
    private static final String TAG = "DADOS_API";
    private List<Obra> listaObras = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_api_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fetchDataFromApi();
    }

    private void fetchDataFromApi() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_API,
                null,
                response -> {
                    try {
                        parseJsonArray(response);

                        // exibindo a primeira obra para verificação no Logcat
                        if (!listaObras.isEmpty()) {
                            Obra primeiraObra = listaObras.get(0);
                            Log.e(TAG, "--- PRIMEIRA OBRA DETALHADA COM TIPOS CORRIGIDOS ---");
                            Log.e(TAG, "ID: " + primeiraObra.getObraId());
                            Log.e(TAG, "Nome: " + primeiraObra.getNome());
                            Log.e(TAG, "Investimento: " + primeiraObra.getInvestimento());
                            Log.e(TAG, "Valor Despendido: " + primeiraObra.getValorDespendido());
                            Log.e(TAG, "Status: " + primeiraObra.getStatus());
                            Log.e(TAG, "Lote: " + primeiraObra.getLote());
                            Log.e(TAG, "--- FIM DOS DETALHES ---");
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "Erro ao analisar o JSON!! Estrutura inválida: " + e.getMessage());
                    }
                },
                error -> Log.e(TAG, "Erro na requisição Volley!! Verificar a URL e o Firewall): " + error.toString())
        );

        queue.add(jsonArrayRequest);
    }

    // verificação dos valores monetários
    private Double safeParseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            // removendo caracteres que não são dígitos como ponto ou vírgula
            String cleanValue = value.replaceAll("[^\\d\\.,]", "");

            // tentando tratar a formatação de vírgula como decimal
            // removendo separadores em ponto e substituindo a vírgula decimal por ponto
            if (cleanValue.contains(",")) {
                // removendo separadores em ponto
                int lastCommaIndex = cleanValue.lastIndexOf(',');
                String integerPart = cleanValue.substring(0, lastCommaIndex).replaceAll("\\.", "");
                String decimalPart = cleanValue.substring(lastCommaIndex + 1);
                cleanValue = integerPart + "." + decimalPart;
            } else {
                // se não houver vírgula, assume que os pontos são separadores de milhar e os remove
                cleanValue = cleanValue.replaceAll("\\.", "");
            }

            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Falha ao converter valor '" + value + "' para Double. Usando 0.0. Erro: " + e.getMessage());
            return 0.0;
        }
    }


    private void parseJsonArray(JSONArray jsonArray) throws JSONException {
        listaObras.clear();
        int totalRegistrosLidos = jsonArray.length();

        Log.i(TAG, "Total de registros lidos do JSON: " + totalRegistrosLidos);

        for (int i = 0; i < totalRegistrosLidos; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            // extração de Strings
            String nome = jsonObject.getString("nome");
            String localizacao = jsonObject.getString("localizacao");
            String dataAssinatura = jsonObject.getString("data_assinatura");
            String origemRecurso = jsonObject.getString("origem_recurso");
            String status = jsonObject.getString("status");
            String contratado = jsonObject.getString("contratado");
            String prazoConclusao = jsonObject.getString("prazo_conclusao");
            String obraParalizada = jsonObject.getString("obra_paralizada");
            String razao = jsonObject.getString("razao");
            String investimento = jsonObject.getString("investimento");
            String lote = jsonObject.getString("lote");
            String zona = jsonObject.getString("zona");
            String valorDespendido = jsonObject.getString("valor_despendido");
            String latitude = jsonObject.getString("latitude");
            String longitude = jsonObject.getString("longitude");

            // conversão de Tipos 3 campos
            // obra_id é int, optInt é seguro
            int obraId = jsonObject.optInt("obra_id", -1);



            if (nome != null && !nome.trim().isEmpty()) {
                // cria e preenche um novo objeto Obra com todos os 16 dados
                Obra obra = new Obra(obraId, nome, localizacao, dataAssinatura, origemRecurso, status,
                        contratado, prazoConclusao, obraParalizada, razao,
                        investimento, lote, zona, valorDespendido, latitude, longitude);

                listaObras.add(obra);

                Log.d(TAG, "Obra Válida [" + listaObras.size() + "]: ID: " + obraId +
                        " | Nome: " + nome);
            } else {
                Log.d(TAG, "Registro [" + (i + 1) + "] ignorado por ter nome vazio.");
            }
        }

        Log.e(TAG, "Processamento Concluído. Total de obras válidas armazenadas: " + listaObras.size());
    }
}