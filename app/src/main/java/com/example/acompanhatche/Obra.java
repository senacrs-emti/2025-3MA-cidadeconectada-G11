package com.example.acompanhatche;

// Esta classe serve como um contêiner para os dados de uma única obra,
// mapeando exatamente as colunas da tabela 'acompanhatche.obras'.
public class Obra {
    // Tipos de dados atualizados: obraId (int), investimento/valorDespendido (Double)
    private int obraId;
    private String nome;
    private String localizacao;
    private String dataAssinatura;
    private String origemRecurso;
    private String status;
    private String contratado;
    private String prazoConclusao;
    private String obraParalizada;
    private String razao;
    private String investimento;
    private String lote;
    private String zona;
    private String valorDespendido;
    private String latitude;
    private String longitude;

    // Construtor completo
    public Obra(int obraId, String nome, String localizacao, String dataAssinatura, String origemRecurso,
                String status, String contratado, String prazoConclusao, String obraParalizada,
                String razao, String investimento, String lote, String zona, String valorDespendido,
                String latitude, String longitude) {
        this.obraId = obraId;
        this.nome = nome;
        this.localizacao = localizacao;
        this.dataAssinatura = dataAssinatura;
        this.origemRecurso = origemRecurso;
        this.status = status;
        this.contratado = contratado;
        this.prazoConclusao = prazoConclusao;
        this.obraParalizada = obraParalizada;
        this.razao = razao;
        this.investimento = investimento;
        this.lote = lote;
        this.zona = zona;
        this.valorDespendido = valorDespendido;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // --- GETTERS (Métodos de Leitura) ---
    // Os métodos getter refletem os novos tipos

    public int getObraId() { return obraId; }
    public String getNome() { return nome; }
    public String getLocalizacao() { return localizacao; }
    public String getDataAssinatura() { return dataAssinatura; }
    public String getOrigemRecurso() { return origemRecurso; }
    public String getStatus() { return status; }
    public String getContratado() { return contratado; }
    public String getPrazoConclusao() { return prazoConclusao; }
    public String getObraParalizada() { return obraParalizada; }
    public String getRazao() { return razao; }
    public String getInvestimento() { return investimento; } // Retorna Double
    public String getLote() { return lote; }
    public String getZona() { return zona; }
    public String getValorDespendido() { return valorDespendido; } // Retorna Double
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
}