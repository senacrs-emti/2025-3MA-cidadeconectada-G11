package com.example.acompanhatche;

import java.io.Serializable;

public class Obra implements Serializable {

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

    // -------------------------------
    // CONSTRUTOR COMPLETO (USADO PELA API)
    // -------------------------------
    public Obra(int obraId, String nome, String localizacao, String dataAssinatura,
                String origemRecurso, String status, String contratado,
                String prazoConclusao, String obraParalizada, String razao,
                String investimento, String lote, String zona,
                String valorDespendido, String latitude, String longitude) {

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


    // -------------------------------
    // CONSTRUTOR SIMPLES (USADO NO MAIN ACTIVITY PARA TESTE)
    // -------------------------------
    public Obra(String nome, String status) {
        this.obraId = -1;
        this.nome = nome;
        this.status = status;

        this.localizacao = "";
        this.dataAssinatura = "";
        this.origemRecurso = "";
        this.contratado = "";
        this.prazoConclusao = "";
        this.obraParalizada = "";
        this.razao = "";
        this.investimento = "";
        this.lote = "";
        this.zona = "";
        this.valorDespendido = "";
        this.latitude = "";
        this.longitude = "";
    }

    // -------------------------------
    // GETTERS
    // -------------------------------
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
    public String getInvestimento() { return investimento; }
    public String getLote() { return lote; }
    public String getZona() { return zona; }
    public String getValorDespendido() { return valorDespendido; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }

    public double getLatitudeDouble() {
        try {
            return Double.parseDouble(latitude);
        } catch (Exception e) {
            return 0;
        }
    }

    public double getLongitudeDouble() {
        try {
            return Double.parseDouble(longitude);
        } catch (Exception e) {
            return 0;
        }
    }

}


