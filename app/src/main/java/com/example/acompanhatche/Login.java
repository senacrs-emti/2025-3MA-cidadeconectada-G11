package com.example.acompanhatche;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private TextView text_tela_cadastro;
    private EditText edit_email, edit_senha;
    private Button bt_entrar;
    private ProgressBar progressbar;
    String[] mensagens = {"Preencha todos os campos!", "Login efetuado com sucesso!"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        IniciarComponentes();

        text_tela_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Login.this, Cadastro.class);
                startActivity(intent);
            }
        });

        bt_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();

                if (email.isEmpty() || senha.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.rgb(240, 22, 47));
                    snackbar.show();
                } else {
                    AutenticarUsuario(v);
                }
            }
        });
    }

    private void AutenticarUsuario(View v) {
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressbar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Main();
                        }
                    }, 3000);
                } else {
                    String erro;

                    try {
                        throw task.getException();
                    } catch (Exception e){
                        erro = "Erro ao fazer login";
                    }
                    Snackbar snackbar = Snackbar.make(v,erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.rgb(240, 22, 47));
                    snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null) {
            Main();
        }
    }

    private void Main() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void IniciarComponentes(){
        text_tela_cadastro = findViewById(R.id.text_tela_cadastro);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        bt_entrar = findViewById(R.id.bt_entrar);
        progressbar = findViewById(R.id.progressbar);
    }

}