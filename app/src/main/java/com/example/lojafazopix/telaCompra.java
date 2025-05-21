package com.example.lojafazopix;

import static com.example.lojafazopix.MainActivity.UserKey;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class telaCompra extends AppCompatActivity {
    Intent getIntent;
    String nome, email, senha;
    String key, produto, valor, vendedor;

    TextView nomeView, valorView, vendedorView;
    Button comprar;
    ImageButton voltar, pesquisar, carrinho, perfil;
    ImageView imagemView;
    TextView scrollText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telacompra);

        voltar = findViewById(R.id.voltar);
        pesquisar = findViewById(R.id.search);
        carrinho = findViewById(R.id.cart);
        perfil = findViewById(R.id.user);
        comprar = findViewById(R.id.comprar);

        nomeView = findViewById(R.id.nomeView);
        valorView = findViewById(R.id.precoView);
        vendedorView = findViewById(R.id.vendedorView);
        imagemView = findViewById(R.id.imageView);
        scrollText = findViewById(R.id.scrollText);

        getIntent = getIntent();
        nome = getIntent.getStringExtra("nome");
        email = getIntent.getStringExtra("email");
        senha = getIntent.getStringExtra("senha");

        key = getIntent.getStringExtra("key");
        produto = getIntent.getStringExtra("nomeProduto");
        valor = getIntent.getStringExtra("precoProduto");
        vendedor = getIntent.getStringExtra("vendedorProduto");
        String obs = getIntent.getStringExtra("descricaoProduto");
        String imagem = getIntent.getStringExtra("imagemProduto");

        scrollText.setText(obs);

        nomeView.setText(produto);
        valorView.setText("R$ "+valor);
        vendedorView.setText(vendedor);
        Glide.with(telaCompra.this).load(imagem).into(imagemView);
        Log.e("telacompra", "log "+imagem);


        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getIntent = new Intent(telaCompra.this, MainActivity.class);
                //getIntent.putExtra("nome", nome);
                //getIntent.putExtra("email", email);
                //getIntent.putExtra("senha", senha);
                //getIntent.putExtra("isLogged", MainActivity.isLogged);
                //startActivity(getIntent);
                finish();
            }
        });

        carrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent = new Intent(telaCompra.this, telaCarrinho.class);
                getIntent.putExtra("nome", nome);
                getIntent.putExtra("email", email);
                getIntent.putExtra("senha", senha);
                getIntent.putExtra("isLogged", MainActivity.isLogged);
                startActivity(getIntent);
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent = new Intent(telaCompra.this, telaPerfil.class);
                getIntent.putExtra("nome", nome);
                getIntent.putExtra("email", email);
                getIntent.putExtra("senha", senha);
                getIntent.putExtra("isLogged", MainActivity.isLogged);
                startActivity(getIntent);
            }
        });

        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(telaCompra.this, telaFin.class);
                //Bundle bundle = new Bundle();
                intent.putExtra("UserKey", UserKey);
                intent.putExtra("key", key);
                intent.putExtra("nome", nome);
                //intent.putExtras(bundle);
                // passando poucos parametros, mas o suficiente para a próxima tela identificar
                // o usuario e o produto a ser comprado

                // na tela de carrinho, talvez seja melhor passar um array com as keys,
                // e criar outro adapter para gerenciar uma listagem simples de produtos
                startActivity(intent);
                finish();
            }
        });
    }
}
