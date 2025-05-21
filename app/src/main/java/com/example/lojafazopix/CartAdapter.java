package com.example.lojafazopix;

import static com.example.lojafazopix.MainActivity.getUserKey;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    Context context;
    List<Item> items;

    public CartAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.carrinho_item_view, parent, false);
        return new CartViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        try {
            holder.key = items.get(position).getKey();
            holder.descricao = items.get(position).getDescricao();
            holder.nome.setText(items.get(position).getNome());
            holder.vendedor.setText(items.get(position).getVendedor());
            holder.preco.setText(String.valueOf(items.get(position).getPreco()));
            Glide.with(context).load(items.get(position).getImagem()).into(holder.imageView);
        } catch (Exception e) {
            Log.e("ERRO", "Erro: "+e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
