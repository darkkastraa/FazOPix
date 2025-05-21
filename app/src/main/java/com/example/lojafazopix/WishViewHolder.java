package com.example.lojafazopix;

import static com.example.lojafazopix.MainActivity.UserKey;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WishViewHolder extends RecyclerView.ViewHolder {
    Context context;
    TextView nomeView, precoView;
    ImageButton addCart;
    String key;
    ImageView imageView;
    Button compraBtn;

    public WishViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;

        imageView = itemView.findViewById(R.id.Card_image);
        nomeView = itemView.findViewById(R.id.nomeView);
        precoView = itemView.findViewById(R.id.precoView);

        compraBtn = itemView.findViewById(R.id.Card_button);
        addCart = itemView.findViewById(R.id.Card_add);

        compraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para comprar o produto
                Intent intent = new Intent(context, telaCompra.class);
                intent.putExtra("UserKey", UserKey);
                intent.putExtra("key", key);
                context.startActivity(intent);
            }
        });

        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para adicionar ao carrinho
            }
        });
    }
}
