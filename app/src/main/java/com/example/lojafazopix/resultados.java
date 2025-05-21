package com.example.lojafazopix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class resultados extends AppCompatActivity {
    String nome, email, senha;
    Intent getIntent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultados);
        getIntent = getIntent();
        nome = getIntent.getStringExtra("nome");
        email = getIntent.getStringExtra("email");
        senha = getIntent.getStringExtra("senha");
        // MainActivity.isLogged;

        ImageButton voltar = findViewById(R.id.voltar);
        ImageButton pesquisar = findViewById(R.id.search);
        ImageButton carrinho = findViewById(R.id.cart);
        ImageButton perfil = findViewById(R.id.user);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        carrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(resultados.this, telaCarrinho.class);
                intent.putExtra("nome", nome);
                intent.putExtra("email", email);
                intent.putExtra("senha", senha);
                intent.putExtra("isLogged", MainActivity.isLogged);
                startActivity(intent);
            }
        });
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(resultados.this, telaPerfil.class);
                intent.putExtra("nome", nome);
                intent.putExtra("email", email);
                intent.putExtra("senha", senha);
                intent.putExtra("isLogged", MainActivity.isLogged);
                startActivity(intent);
            }
        });
    }
}
