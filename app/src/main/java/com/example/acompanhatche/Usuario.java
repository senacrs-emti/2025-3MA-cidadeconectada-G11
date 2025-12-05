package com.example.acompanhatche;

public class Usuario {
    private String nome;
    private String email;
    private String senha;

    // o construtor usado para enviar dados ao servidor
    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
}
