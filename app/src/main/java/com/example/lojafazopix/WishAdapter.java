package com.example.lojafazopix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WishAdapter extends RecyclerView.Adapter<WishViewHolder>{
    Context context;
    List<Item> wishItems;

    public WishAdapter(Context context, List<Item> wishItems) {
        this.context = context;
        this.wishItems = wishItems;
    }

    @NonNull
    @Override
    public WishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WishViewHolder(LayoutInflater.from(context).inflate(R.layout.wishlist_item_view, parent, false), context);
    }

    @Override
    public void onBindViewHolder(@NonNull WishViewHolder holder, int position) {
        holder.key = wishItems.get(position).getKey();
        holder.nomeView.setText(wishItems.get(position).getNome());
        holder.precoView.setText(String.valueOf(wishItems.get(position).getPreco()));
        Glide.with(context).load(wishItems.get(position).getImagem()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return wishItems.size();
    }

}
