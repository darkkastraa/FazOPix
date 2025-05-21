package com.example.lojafazopix;

import static androidx.core.content.ContextCompat.startActivity;

import static com.example.lojafazopix.MainActivity.getUserKey;
import static com.example.lojafazopix.MainActivity.isLogged;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.ClientInfoStatus;
import java.util.HashMap;

public class MyViewHolder extends RecyclerView.ViewHolder {

    private final MainActivity mainActivity;
    Context context;
    ImageView imageView;
    Button compraBtn;
    ImageButton addCarrinho, addFavorito;
    String key;
    TextView nomeView, precoView, vendedorView;
    String descricao;
    String vendedor;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference().child("users");

    String imageViewString;
    String UserKey;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        context = itemView.getContext();
        mainActivity = new MainActivity();
        imageView = itemView.findViewById(R.id.imageview);
        nomeView = itemView.findViewById(R.id.nome);
        precoView = itemView.findViewById(R.id.preco);
        vendedorView = itemView.findViewById(R.id.vendedor);
        compraBtn = itemView.findViewById(R.id.buttonComprar);
        addCarrinho = itemView.findViewById(R.id.buttonCarrinho);
        addFavorito = itemView.findViewById(R.id.buttonFavorito);

        compraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.isLogged) {
                    Toast.makeText(context, "comprado", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, telaCompra.class);
                    intent.putExtra("nome", mainActivity.nome);
                    intent.putExtra("email", mainActivity.email);
                    intent.putExtra("senha", mainActivity.senha);
                    intent.putExtra("isLogged", MainActivity.isLogged);

                    intent.putExtra("key", key);
                    intent.putExtra("nomeProduto", nomeView.getText().toString());
                    intent.putExtra("precoProduto", precoView.getText().toString());
                    intent.putExtra("vendedorProduto", vendedorView.getText().toString());
                    intent.putExtra("descricaoProduto", descricao);
                    intent.putExtra("imagemProduto", imageViewString);

                    context.startActivity(intent);
                }
                else {
                    Intent loadLogin = new Intent(context, telaLogin.class);
                    context.startActivity(loadLogin);
                }
            }
        });
        addCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogged) {
                    UserKey = getUserKey();

                    //DEBUG PURPOSES ONLY\
                    /*
                    if (UserKey != null)
                        Log.e("UserKey", "UserKey: " + UserKey);
                    if (key != null)
                        Log.e("Key", "Key: " + key);
                    */

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("product", nomeView.getText().toString());
                    map.put("description", descricao);
                    map.put("price", precoView.getText().toString());
                    map.put("sellerID", vendedor);

                    userRef.child(UserKey).child("cart").child(key).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Produto adicionado ao carrinho", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mainActivity, "Entre para salvar produtos", Toast.LENGTH_SHORT).show();
                    Intent loadLogin = new Intent(context, telaLogin.class);
                    context.startActivity(loadLogin);
                }
            }
        });
        addFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogged) {
                    UserKey = getUserKey();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("product", nomeView.getText().toString());
                    map.put("description", descricao);
                    map.put("price", precoView.getText().toString());
                    map.put("sellerID", vendedorView.getText().toString());

                    userRef.child(UserKey).child("wishlist").child(key).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Produto adicionado aos favoritos", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mainActivity, "Entre para salvar produtos", Toast.LENGTH_SHORT).show();
                    Intent loadLogin = new Intent(context, telaLogin.class);
                    context.startActivity(loadLogin);
                }
            }
        });
    }

}
