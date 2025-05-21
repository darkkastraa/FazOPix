package com.example.lojafazopix;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class telaFin extends AppCompatActivity {
    Intent getIntent;
    String UserKey, key;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference().child("users");
    DatabaseReference dbProducts = FirebaseDatabase.getInstance().getReference().child("products");
    DatabaseReference dbCart = dbUsers.child("key").child("cart");

    RecyclerView finRecycler;
    FinalAdapter adapter;
    List<Item> items = new ArrayList<>();

    static TextView itemTotal, saleTotal, priceTotal;
    TextView clientName, clientCpf, clientCep;

    int qtdResumo = 0;
    Float precoResumo = 0f;

    String credentials;
    TextView dialogText;
    EditText dialogInput;
    Button dialogButton, cancelButton, confirmButton;

    Spinner spinnerFin;
    String[] paymentMethod = {"PIX", "Débito", "Crédito", "Boleto", "Transf. Bancária", "Carteira Digital"};

    EditText cardNumber, cardCvv;

    String nomeProd, vendorProd, imageProd, descProd;
    String precoProd;
    String total;
    Float totalSum = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telafin);

        getIntent = getIntent();
        UserKey = getIntent.getStringExtra("UserKey");
        key = getIntent.getStringExtra("key");

        finRecycler = findViewById(R.id.finRecycler);
        adapter = new FinalAdapter(this, items);
        finRecycler.setLayoutManager(new LinearLayoutManager(this));
        finRecycler.setAdapter(adapter);

        if (key != null) {
            loadFromProduct(key);
        } else {
            loadFromCart();
        }

        itemTotal = findViewById(R.id.itemTotal);
        saleTotal = findViewById(R.id.saleTotal);
        priceTotal = findViewById(R.id.priceTotal);

        clientName = findViewById(R.id.clientName);
        clientCpf = findViewById(R.id.clientCpf);
        clientCep = findViewById(R.id.clientCep);

        spinnerFin = findViewById(R.id.spinnerFin);
        spinnerFin.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, paymentMethod));

        cardNumber = findViewById(R.id.cardNumber);
        cardCvv = findViewById(R.id.cardCvv);
        cancelButton = findViewById(R.id.cancelarCompra);
        confirmButton = findViewById(R.id.confirmarCompra);

        //loadFromCart(); <- caso chegue pela tela do carrinho
        //loadFromProduct(); <- caso chegue pela tela do produto

        clientName.setText(getIntent.getStringExtra("name"));
        /*HashMap<String, Resumo> resumoItens = adapter.getResumo();

        for (Map.Entry<String, Resumo> entry : resumoItens.entrySet()) {
            String nome = entry.getKey();
            Resumo itemResumo = entry.getValue();
            precoResumo += itemResumo.val;
            qtdResumo += itemResumo.qtd;
        }*/

        //priceTotal.setText("R$ " + precoResumo); // faz a exibição do valor total (itens + quantidades)
        //itemTotal.setText(String.valueOf(qtdResumo));
        //Log.e("logs", "qtdResumo: "+qtdResumo +"\npreco total: "+precoResumo);
        clientCpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCredentials("CPF");
                clientCpf.setTextColor(getResources().getColor(R.color.black));
            }
        });
        clientCep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCredentials("CEP");
                clientCep.setTextColor(getResources().getColor(R.color.black));
            }
        });
        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Não é necessário implementar este método para esta funcionalidade
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Não é necessário implementar este método para esta funcionalidade
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove o TextWatcher para evitar loop infinito
                cardNumber.removeTextChangedListener(this);

                // Remove os pontos existentes do texto
                String card = s.toString().replaceAll("\\.", "");

                // Formata o texto com pontos a cada 4 dígitos
                StringBuilder formattedCard = new StringBuilder();
                for (int i = 0; i < card.length(); i++){
                    formattedCard.append(card.charAt(i));
                    if ((i + 1) % 4 == 0 && i != card.length() - 1) {
                        formattedCard.append(".");
                    }
                }

                // Define o texto formatado no EditText
                cardNumber.setText(formattedCard.toString());

                // Define o cursor no final do texto
                cardNumber.setSelection(cardNumber.getText().length());

                // Adiciona o TextWatcher novamente
                cardNumber.addTextChangedListener(this);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clientCpf.getText().toString().equals("Definir CPF...") || !clientCep.getText().toString().equals("Definir CEP...")) {
                    if (cardNumber.getText().toString().length() == 19 && cardCvv.getText().toString().length() == 3) {
                        Intent intent = new Intent(telaFin.this, thanks.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(telaFin.this, "Preencha as informações do cartão!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(telaFin.this, "Preencha as informações pessoais!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getCredentials(String tipo) {
        Dialog dialog = new Dialog(telaFin.this);
        dialog.setContentView(R.layout.dialog_credentials);
        dialog.setCancelable(true);

        dialogText = dialog.findViewById(R.id.textView);
        dialogInput = dialog.findViewById(R.id.editText);
        dialogButton = dialog.findViewById(R.id.button);
        dialogText.setText("Informe seu "+tipo+":");

        dialogInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
                // Não é necessário implementar este método para esta funcionalidade
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Não é necessário implementar este método para esta funcionalidade
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove o TextWatcher para evitar loop infinito
                dialogInput.removeTextChangedListener(this);

                // Remove os pontos existentes do texto
                String cpf = s.toString().replaceAll("\\.", "");

                // Formata o texto com pontos acada 3 dígitos
                if (Objects.equals(tipo, "CPF")) {
                    if (cpf.length() > 3 && cpf.length() <= 6) {
                        cpf = cpf.substring(0, 3) + "." + cpf.substring(3);
                    } else if (cpf.length() > 6 && cpf.length() <= 9) {
                        cpf = cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6);
                    } else if (cpf.length() > 9) {
                        cpf = cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
                    }
                } else if (Objects.equals(tipo, "CEP")) {
                    InputFilter[] filters = new InputFilter[1];
                    filters[0] = new InputFilter.LengthFilter(9);
                    dialogInput.setFilters(filters);
                    if (cpf.length() == 5) {
                        cpf = cpf.substring(0, 5) + "-" + cpf.substring(5);
                    }
                }

                // Define o texto formatado no EditText
                dialogInput.setText(cpf);

                // Define o cursor no final do texto
                dialogInput.setSelection(dialogInput.getText().length());

                // Adiciona o TextWatcher novamente
                dialogInput.addTextChangedListener(this);
            }
        });

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogInput.getText().toString().isEmpty()) {
                    Toast.makeText(telaFin.this, "Preencha o campo!", Toast.LENGTH_SHORT).show();
                } else {
                    credentials = dialogInput.getText().toString();
                    if(Objects.equals(tipo, "CPF")) {
                        if (dialogInput.getText().toString().length() == 15) {
                            clientCpf.setText(credentials);
                        } else {
                            Toast.makeText(telaFin.this, "CPF inválido!", Toast.LENGTH_SHORT).show();
                        }
                    } else if (Objects.equals(tipo, "CEP")) {
                        if (dialogInput.getText().length()==9) {
                            clientCep.setText(credentials);
                        } else {
                            Toast.makeText(telaFin.this, "CEP inválido!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                }

            }
        });
        dialog.show();
    }
    private void loadFromCart() {
        Query userFinder = dbUsers.orderByChild("key").equalTo(UserKey);
        userFinder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        dbCart = userSnapshot.child("cart").getRef();
                        dbCart.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                                        key = cartSnapshot.getKey(); // pega a key do produto
                                        Query prodFinder = dbProducts.orderByChild("key").equalTo(key);
                                        prodFinder.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                                        nomeProd = productSnapshot.child("product").getValue(String.class);
                                                        precoProd = productSnapshot.child("price").getValue(String.class);
                                                        totalSum += Float.parseFloat(precoProd);
                                                        vendorProd = productSnapshot.child("sellerID").getValue(String.class);
                                                        imageProd = productSnapshot.child("image").getValue(String.class);
                                                        descProd = productSnapshot.child("description").getValue(String.class);
                                                        items.add(new Item(productSnapshot.getKey(), nomeProd, descProd, precoProd, vendorProd, imageProd));
                                                        adapter.notifyItemInserted(items.size() - 1);
                                                        //Log.e("telaFin", "nomeProd: "+nomeProd+"\nPreco: "+precoProd+"\nVendedor: "+vendorProd+"\nImagem: "+imageProd);
                                                    }
                                                } else {
                                                    Toast.makeText(telaFin.this, "Produto não encontrado", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(telaFin.this, "Erro ao localizar produto", Toast.LENGTH_SHORT).show();
                                                Log.e("telaFin", "Erro ao localizar produto: "+error.getMessage());
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(telaFin.this, "Carrinho vazio", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(telaFin.this, "Erro ao localizar carrinho", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(telaFin.this, "Erro ao encontrar usuário", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(telaFin.this, "Erro ao conectar ao servidor", Toast.LENGTH_SHORT).show();
                Log.e("telaFin", "Erro ao conectar ao servidor: "+error.getMessage());
            }
        });
    }
    private void loadFromProduct(String keyProd) {
        Query prodFinder = dbProducts.orderByChild("key").equalTo(keyProd);
        prodFinder.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        nomeProd = productSnapshot.child("product").getValue(String.class);
                        precoProd = productSnapshot.child("price").getValue(String.class);
                        totalSum += Float.parseFloat(precoProd);
                        vendorProd = productSnapshot.child("sellerID").getValue(String.class);
                        imageProd = productSnapshot.child("image").getValue(String.class);
                        descProd = productSnapshot.child("description").getValue(String.class);
                        items.add(new Item(productSnapshot.getKey(), nomeProd, descProd, precoProd, vendorProd, imageProd));
                        adapter.notifyItemInserted(items.size() - 1);
                        //Log.e("telaFin", "nomeProd: "+nomeProd+"\nPreco: "+precoProd+"\nVendedor: "+vendorProd+"\nImagem: "+imageProd);
                    }
                } else {
                    Toast.makeText(telaFin.this, "Produto não encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(telaFin.this, "Erro ao localizar produto", Toast.LENGTH_SHORT).show();
                Log.e("telaFin", "Erro ao localizar produto: "+error.getMessage());
            }
        });
    }
}
