package com.example.lojafazopix;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Item {
    String key;
    String nome;
    String descricao;;
    String preco;
    String imagem;
    String vendedor;

    public Item() {}

    public Item(String key, String nome, String descricao, String preco, String vendedor, String imagem) {
        this.key = key;
        this.vendedor = vendedor;
        this.nome = nome;
        this.imagem = imagem;
        this.preco = preco;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) { this.imagem = imagem; }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) { this.vendedor = vendedor; }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }
}
