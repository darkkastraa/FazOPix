package com.example.lojafazopix;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    Context context;
    List<Item> items;


    public MyAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.key = items.get(position).getKey();
        holder.descricao = items.get(position).getDescricao();
        holder.nomeView.setText(items.get(position).getNome());
        holder.precoView.setText(String.valueOf(items.get(position).getPreco()));
        holder.vendedor = items.get(position).getVendedor();
        Glide.with(context).load(items.get(position).getImagem()).into(holder.imageView);
        holder.imageViewString = items.get(position).getImagem();

        DatabaseReference dbRefer = FirebaseDatabase.getInstance().getReference("users");
        dbRefer.orderByChild("sellerID").equalTo(items.get(position).getVendedor());
        dbRefer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String vendedorNome = userSnapshot.child("username").getValue(String.class);
                        holder.vendedorView.setText(vendedorNome);
                        return;
                    }
                } else {
                    holder.vendedorView.setText("John Doe");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Lidar com erro, por exemplo:
                Log.e("MyAdapter", "Erro ao obter nome do vendedor: " + error.getMessage());
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return items.size();
    }
}
