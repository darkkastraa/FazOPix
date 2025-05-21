package com.example.lojafazopix;

import static android.text.TextUtils.indexOf;

import static com.example.lojafazopix.telaFin.itemTotal;
import static com.example.lojafazopix.telaFin.priceTotal;
import static com.google.gson.JsonParser.parseString;
import static java.lang.Float.parseFloat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class FinalAdapter extends RecyclerView.Adapter<FinalViewHolder> {
    Context context;
    List<Item> items;
    private OnLoadedListener listener;
    int i = 0;
    Float iv = 0f;

    public void setOnLoadedListener(OnLoadedListener listener) {
        this.listener = listener;
    }

    public FinalAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;

    }

    @NonNull
    @Override
    public FinalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FinalViewHolder(LayoutInflater.from(context).inflate(R.layout.finalization_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FinalViewHolder holder, int position) {
        holder.nomeView.setText(items.get(position).getNome());
        String valS = String.valueOf(items.get(position).getPreco());

        Log.e("log adapter", "preco "+valS);
        valS = valS.replace(",", ".");
        /*int valPos = indexOf(val, ','); // verifica se tem virgula
        if (valPos >= 0) {
            String bfComma = val.substring(0, valPos);
            String afComma = val.substring(valPos + 1);
            val = bfComma + "." + afComma;
        }*/
        holder.precoView.setText(valS);
        iv += parseFloat(valS);
        i++;
        itemTotal.setText(String.valueOf(i));
        priceTotal.setText(String.valueOf(iv));
        holder.precoFin = parseFloat(valS);
        /*allp += parseFloat(valS);
        float val = Float.parseFloat(items.get(position).getPreco());
        int quantidade = Integer.parseInt(holder.qtdView.getText().toString());

        // Atualiza o HashMap com os dados do item
        if (resumo.containsKey(items.get(position).getNome())) {
            // Item já existe no resumo, atualiza a quantidade
            Resumo itemResumo = resumo.get(items.get(position).getNome());
            itemResumo.qtd += quantidade;
        } else {
            // Item novo no resumo,adiciona com valor unitário e quantidade
            resumo.put(items.get(position).getNome(), new Resumo(val, quantidade));
        }*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /*public HashMap<String, Resumo> getResumo() {
        return resumo;
    }*/
    public int getItem() {
        return i;
    }
    public float getValues() {
        return iv;
    }
}
