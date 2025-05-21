package com.example.lojafazopix;

import static com.example.lojafazopix.DatabaseMethods.IntentAssembly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class telaLogin extends AppCompatActivity {

    private EditText nameField,passField,emailField;
    private Button entrarButton;
    MainActivity mainActivity;

    Button entrar, criar;
    ImageButton sair;
    EditText nomeInput, emailInput, senhaInput;
    String nome, email, senha;
    String UserKey; // para referencia ao key do usuario
    Intent loadMain;

    TextView switchText;
    Boolean switchAction = true; // true cria conta, false entra

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telalogin);

        switchText = findViewById(R.id.switchText);
        entrar = findViewById(R.id.entrarButton);
        sair = findViewById(R.id.close);

        nomeInput = findViewById(R.id.nameField);
        emailInput = findViewById(R.id.emailField);
        senhaInput = findViewById(R.id.passField);

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pega todos os dados, independente do uso
                nome = nomeInput.getText().toString().trim();
                email = emailInput.getText().toString();
                senha = senhaInput.getText().toString();
                if(switchAction) {
                    if (!nome.isEmpty() && !senha.isEmpty()) {
                        // se for logar, nao se importa com o email vazio
                        userLogin(nome, senha);
                    } else {
                        if (nome.isEmpty())
                            nomeInput.setError("Preencha o nome!");
                        if (senha.isEmpty())
                            senhaInput.setError("Preencha a senha!");
                    }
                } else {
                    if (!nome.isEmpty() && !email.isEmpty() && !senha.isEmpty()) {
                        // se for criar conta, verifica o email
                        userCreate(nome, email, senha);
                    } else {
                        if (nome.isEmpty())
                            nomeInput.setError("Preencha o nome!");
                        if (senha.isEmpty())
                            senhaInput.setError("Preencha a senha!");
                        if (email.isEmpty())
                            emailInput.setError("Preencha este campo!");
                    }
                }
            }
        });

        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        switchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAction = !switchAction;
                if (switchAction == true) {
                    switchText.setText("Não tem uma conta? Crie uma aqui!" + switchAction);
                    entrar.setText("Entrar na conta");
                    emailInput.setVisibility(View.GONE);
                } else {
                    switchText.setText("Já tem uma conta? Acesse-a aqui!" + switchAction);
                    entrar.setText("Criar uma conta");
                    emailInput.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void userLogin(String nome, String senha) {
        FirebaseDatabase firedb = FirebaseDatabase.getInstance();
        DatabaseReference userDB = firedb.getReference("users");
        Query query = userDB.orderByChild("username").equalTo(nome);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        if (userSnapshot.child("username").getValue(String.class).equals(nome) && userSnapshot.child("password").getValue(String.class).equals(senha)) {
                            email = userSnapshot.child("email").getValue(String.class);
                            UserKey = userSnapshot.getKey();

                            loadMain = IntentAssembly(telaLogin.this, MainActivity.class, UserKey, nome, email, senha, true);
                            startActivity(loadMain);
                            return;
                        } else Toast.makeText(telaLogin.this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(telaLogin.this, "A senha não corresponde!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(telaLogin.this, "Usuário não existe!", Toast.LENGTH_SHORT).show();
                    Log.e("Usuário não encontrado!", nome+" "+snapshot+" "+senha);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(telaLogin.this, "Erro ao verificar usuário! ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void userCreate(String nome, String email, String senha) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mydb = database.getReference("users");

        Query query = mydb.orderByChild("username").equalTo(nome);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(telaLogin.this, "Nome de usuário em uso!", Toast.LENGTH_SHORT).show();
                    Log.e("Nome de usuário em uso!", nome+" "+snapshot);
                } else {
                    Query emquery = mydb.orderByChild("email").equalTo(email);
                    emquery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Toast.makeText(telaLogin.this, "E-Mail em uso!", Toast.LENGTH_SHORT).show();
                            } else {
                                // preparando criação do novo usuario
                                HashMap<String, Object> userHashmap = new HashMap<>();
                                userHashmap.put("username", nome);
                                userHashmap.put("email", email);
                                userHashmap.put("password", senha);

                                String key = mydb.push().getKey();
                                userHashmap.put("key", key);
                                UserKey = key;


                                mydb.child(key).setValue(userHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(telaLogin.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(telaLogin.this, "Erro ao cadastrar usuário!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(telaLogin.this, "Erro ao verificar e-mail! ", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(telaLogin.this, "Erro ao verificar nome de usuário!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}