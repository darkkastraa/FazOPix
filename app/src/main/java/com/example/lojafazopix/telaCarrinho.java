package com.example.lojafazopix;

import static com.example.lojafazopix.MainActivity.getUserKey;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class telaCarrinho extends AppCompatActivity {
    String nome, email, senha;
    String nomeProd, vendorProd, imageProd, descProd;
    String precoProd;
    String UserKey;
    String key, vendor;
    Boolean isLogged;

    FirebaseDatabase firedb = FirebaseDatabase.getInstance();
    DatabaseReference user;
    DatabaseReference cart;
    DatabaseReference prods;

    RecyclerView recyclerView;
    CartAdapter adapter;
    List<Item> items = new ArrayList<>();
    View layoutManager;
    Button comprar;
    public RelativeLayout reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telacarrinho);

        Intent intent = getIntent();
        UserKey = intent.getStringExtra("key");
        nome = intent.getStringExtra("nome");
        email = intent.getStringExtra("email");
        senha = intent.getStringExtra("senha");
        isLogged = intent.getBooleanExtra("isLogged", false);

        ImageButton voltar = findViewById(R.id.voltar);
        ImageButton pesquisar = findViewById(R.id.search);
        ImageButton carrinho = findViewById(R.id.cart);
        ImageButton perfil = findViewById(R.id.user);

        layoutManager = findViewById(R.id.emptyCart);
        recyclerView = findViewById(R.id.cartRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(this, items);
        recyclerView.setAdapter(adapter);
        comprar = findViewById(R.id.buttonCompra);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            } /* fecha a tela atual */
        });

        carrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(telaCarrinho.this, telaCarrinho.class);
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
                Intent intent = new Intent(telaCarrinho.this, telaPerfil.class);
                intent.putExtra("nome", nome);
                intent.putExtra("email", email);
                intent.putExtra("senha", senha);
                intent.putExtra("isLogged", MainActivity.isLogged);
                intent.putExtra("key", UserKey);
                startActivity(intent);
            }
        });

        comprar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(telaCarrinho.this, telaFin.class);
               intent.putExtra("UserKey", UserKey);
               startActivity(intent);
               finish();
           }
        });

        user = firedb.getReference().child("users");
        prods = firedb.getReference().child("products");
        cart = user.child(UserKey).child("cart");

        cart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    items.clear();
                    layoutManager.setVisibility(View.GONE);
                    for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                        key = cartSnapshot.getKey();
                        Query query = prods.orderByChild("key").equalTo(key);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot productSnapshot : snapshot.getChildren()) { // Iterar pelos nós filhos
                                        nomeProd = productSnapshot.child("product").getValue(String.class);
                                        precoProd = productSnapshot.child("price").getValue(String.class);
                                        vendorProd = productSnapshot.child("sellerID").getValue(String.class);
                                        imageProd = productSnapshot.child("image").getValue(String.class);
                                        descProd = productSnapshot.child("description").getValue(String.class);

                                        if (!(nomeProd == null || nomeProd.isEmpty()) && !(precoProd == null) && !(vendorProd == null || vendorProd.isEmpty()) && !(imageProd == null || imageProd.isEmpty()) && !(descProd == null || descProd.isEmpty())) {
                                            items.add(new Item(key, nomeProd, descProd, precoProd, vendorProd, imageProd));
                                            adapter.notifyItemInserted(items.size() - 1);
                                        } else {
                                            Toast.makeText(telaCarrinho.this, "Erro ao carregar produtos!", Toast.LENGTH_SHORT).show();
                                            layoutManager.setVisibility(View.VISIBLE);
                                        }
                                        Log.e("Log snapshot", "key: " + key + "\nsnapshot: " + snapshot);
                                    }
                                } else {
                                    layoutManager.setVisibility(View.VISIBLE);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                layoutManager.setVisibility(View.VISIBLE);
                                Log.e("erro", "database "+error.getMessage());
                            }
                        });
                    }
                } else {
                    layoutManager.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                layoutManager.setVisibility(View.VISIBLE);
                Toast.makeText(telaCarrinho.this, "Erro ao carregar produtos!", Toast.LENGTH_SHORT).show();
                Log.e("erro", "database "+error.getMessage());
            }
        });
    }
}
