package com.example.lojafazopix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.Objects;

public class telaPerfil extends AppCompatActivity {
    String nome, email, senha;
    String UserKey;
    String Produto_nome, Produto_img;
    String Produto_preco;
    String Produto_key;

    Intent getIntent;
    Button sair, atualizar;
    ImageButton voltar, pesquisar, carrinho, perfil;
    ImageButton passToggle;
    EditText nomeInput, emailInput, senhaInput;
    TextView uid;

    List<Item> wishItems = new ArrayList<>();
    WishAdapter wishAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telaperfil);

        getIntent = getIntent();
        UserKey = getIntent.getStringExtra("key");
        nome = getIntent.getStringExtra("nome");
        email = getIntent.getStringExtra("email");
        senha = getIntent.getStringExtra("senha");

        uid = findViewById(R.id.uid);
        uid.setText("UID: "+ UserKey);

        // pega a ref. da database pra saber onde procurar
        DatabaseReference dbUpdate = FirebaseDatabase.getInstance().getReference("users");
        Query userWishlist = dbUpdate.orderByChild("key").equalTo(UserKey);
        DatabaseReference dbProducts = FirebaseDatabase.getInstance().getReference("products");

        voltar = findViewById(R.id.voltar);
        pesquisar = findViewById(R.id.search);
        carrinho = findViewById(R.id.cart);
        perfil = findViewById(R.id.user);
        passToggle = findViewById(R.id.passwordToggle);

        atualizar = findViewById(R.id.atualizarButton);
        sair = findViewById(R.id.sairButton);
        nomeInput = findViewById(R.id.nomeField);
        emailInput = findViewById(R.id.emailField);
        senhaInput = findViewById(R.id.passField);
        nomeInput.setText(nome);
        emailInput.setText(email);
        senhaInput.setText(senha);

        recyclerView = findViewById(R.id.wlRecycler);
        wishAdapter = new WishAdapter(this, wishItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(wishAdapter);

        // query e subquery para encontrar usuario e depois extarir sua wishlist
        userWishlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    wishItems.clear();
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        userSnapshot.child("wishlist").getChildren().forEach(productSnapshot -> {
                            Produto_key = productSnapshot.getKey();
                            Query prod_list = dbProducts.orderByChild("key").equalTo(Produto_key);
                            prod_list.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot prodSnapshot : snapshot.getChildren()) {
                                            Produto_nome = Objects.requireNonNull(prodSnapshot.child("product").getValue()).toString();
                                            Produto_preco = Objects.requireNonNull(prodSnapshot.child("price").getValue()).toString();
                                            Produto_img = Objects.requireNonNull(prodSnapshot.child("image").getValue()).toString();
                                            String Produto_vendedor = Objects.requireNonNull(prodSnapshot.child("sellerID").getValue()).toString();
                                            String Produto_desc = Objects.requireNonNull(prodSnapshot.child("description").getValue()).toString();
                                            Log.e("log snap", "Snap: "+prodSnapshot);
                                            wishItems.add(new Item(Produto_key, Produto_nome, Produto_desc, Produto_preco, Produto_vendedor, Produto_img));

                                            wishAdapter.notifyItemInserted(wishItems.size() - 1);
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(telaPerfil.this, "Ocorreu um erro.", Toast.LENGTH_SHORT).show();
                Log.e("Query userWishlist", "Erro: "+error.getMessage());
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(telaPerfil.this, MainActivity.class);
                //intent.putExtra("nome", nome);
                //intent.putExtra("email", email);
                //intent.putExtra("senha", senha);
                //intent.putExtra("isLogged", MainActivity.isLogged);
                //startActivity(intent);
                finish();
            }
        });

        carrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(telaPerfil.this, telaCarrinho.class);
                intent.putExtra("nome", nome);
                intent.putExtra("email", email);
                intent.putExtra("senha", senha);
                intent.putExtra("isLogged", MainActivity.isLogged);
                intent.putExtra("key", UserKey);
                startActivity(intent);
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(telaPerfil.this, telaPerfil.class);
                intent.putExtra("nome", nome);
                intent.putExtra("email", email);
                intent.putExtra("senha", senha);
                intent.putExtra("isLogged", MainActivity.isLogged);
                startActivity(intent);
            }
        });

        atualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // codigo pra UPDATE no banco
                nome = nomeInput.getText().toString();
                email = emailInput.getText().toString();
                senha = senhaInput.getText().toString();

                // na key dos users encontra o userkey
                Query query = dbUpdate.orderByChild("key").equalTo(UserKey);
                Toast.makeText(telaPerfil.this, "Atualizando "+UserKey, Toast.LENGTH_SHORT).show();
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Obtém a referência do nó do usuário
                            // com base na referencia obtida pela userkey
                            DatabaseReference userRef = snapshot.getChildren().iterator().next().getRef();


                            // e atualiza os dados com base nos editTexts (dados coletados la em cima)p
                            userRef.child("username").setValue(nome);
                            userRef.child("email").setValue(email);
                            userRef.child("password").setValue(senha);
                        } else{
                            Toast.makeText(telaPerfil.this, "Erro ao atualizar!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(telaPerfil.this, "Erro ao encontrar usuário! ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(telaPerfil.this, MainActivity.class);
                intent.putExtra("nome", "");
                intent.putExtra("email", "");
                intent.putExtra("senha", "");
                intent.putExtra("isLogged", false);
                startActivity(intent);
            }
        });

        passToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senhaInput.getInputType() == 129) {
                    senhaInput.setInputType(1);
                    passToggle.setImageResource(R.drawable.baseline_visibility_24);
                } else {
                    senhaInput.setInputType(129);
                    passToggle.setImageResource(R.drawable.baseline_visibility_off_24);
                }
            }
        });
    }
}

