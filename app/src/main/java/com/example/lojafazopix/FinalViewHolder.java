package com.example.lojafazopix;

import static android.text.TextUtils.indexOf;

import static com.example.lojafazopix.telaFin.itemTotal;
import static com.example.lojafazopix.telaFin.priceTotal;
import static java.lang.Float.parseFloat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FinalViewHolder extends RecyclerView.ViewHolder{
    TextView nomeView;
    TextView precoView;
    TextView qtdView;
    TextView addQtd;
    TextView rmvQtd;

    telaFin telafin;
    Float precoFin;
    public FinalViewHolder(@NonNull View itemView) {
        super(itemView);
        telafin = new telaFin();
        nomeView = itemView.findViewById(R.id.product_name);
        precoView = itemView.findViewById(R.id.product_price);
        qtdView = itemView.findViewById(R.id.product_quantity);
        qtdView.setText("1");
        addQtd = itemView.findViewById(R.id.product_add);
        rmvQtd = itemView.findViewById(R.id.product_remove);
        addQtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qtd = Integer.parseInt(qtdView.getText().toString());
                int quantidade = Integer.parseInt(itemTotal.getText().toString());

                Float preco = precoFin;
                Float valor = Float.parseFloat(priceTotal.getText().toString().replace("R$ ", ""));
                if (qtd < 10) {
                    qtd++;
                    qtdView.setText(String.valueOf(qtd));
                    itemTotal.setText(String.valueOf(quantidade + 1));

                    precoView.setText(String.valueOf(preco * qtd));
                    //priceTotal.setText("R$ " + valor + preco);
                    priceTotal.setText("R$ "+(valor + preco));
                } else {
                    Toast.makeText(itemView.getContext(), "Quantidade máxima atingida", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rmvQtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qtd = Integer.parseInt(qtdView.getText().toString());
                int quantidade = Integer.parseInt(itemTotal.getText().toString());

                Float preco = precoFin;
                Float valor = Float.parseFloat(priceTotal.getText().toString().replace("R$ ", ""));
                if (qtd > 1) {
                    qtd--;
                    qtdView.setText(String.valueOf(qtd));
                    itemTotal.setText(String.valueOf(quantidade-1));

                    precoView.setText(String.valueOf(preco * qtd));
                    priceTotal.setText("R$ "+(valor - preco));
                } else {
                    Toast.makeText(itemView.getContext(), "Quantidade mínima atingida", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*qtdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Integer quant = Integer.parseInt(qtdView.getText().toString());
                preco = precoFin * quant;
                precoView.setText(String.valueOf(preco));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
    }
}
