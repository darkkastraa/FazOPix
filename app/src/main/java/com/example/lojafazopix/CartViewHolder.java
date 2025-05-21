package com.example.lojafazopix;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.lojafazopix.MainActivity.getUserKey;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CartViewHolder extends RecyclerView.ViewHolder {
    private CartAdapter adapter;
    String key, descricao;
    ImageView imageView;
    TextView nome, vendedor, preco;
    ImageButton excluir;
    Button comprar;

    public CartViewHolder(@NonNull View itemView, CartAdapter adapter) {
        super(itemView);
        try {
            this.adapter = adapter;

            imageView = itemView.findViewById(R.id.cartImage);
            excluir = itemView.findViewById(R.id.cartDelete);
            nome = itemView.findViewById(R.id.cartName);
            vendedor = itemView.findViewById(R.id.cartVendor);
            preco = itemView.findViewById(R.id.cartPrice);
            comprar = itemView.findViewById(R.id.cartComprar);
            excluir.setOnClickListener(v -> {
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("users").child(getUserKey()).child("cart").child(key);
                cartRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //telaCarrinho a= new telaCarrinho();
                        //a.reload.requestLayout();
                        Toast.makeText(itemView.getContext(), "Produto removido do carrinho", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        } catch (Exception e) {
            Log.e("ERRO", "Erro: "+e.getMessage());
        }
    }
}
