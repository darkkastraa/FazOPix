package com.example.lojafazopix;

import static android.app.PendingIntent.getActivity;
import static android.text.TextUtils.indexOf;
import static com.example.lojafazopix.DatabaseMethods.IntentAssembly;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.lojafazopix.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static boolean isLogged = false; // sera usada para verificar se o usuario esta logado.

    DatabaseReference dbRefer = FirebaseDatabase.getInstance().getReference("products");

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    Intent loadCart, loadLogin, loadProfile, loadSearch, loadCompra;

    List<Item> items = new ArrayList<>();
    static String val; // para receber a string do valor, a ser convertida para double
    String produto, obs; // recebe as strings do nome do produto, e observações
    Float valor; // valor numérico, após passar pela conversão

    private MyAdapter adapter;
    EditText nomeInput, valorInput, obsInput; // campos do popup 'dialog'
    String vendedor; // pega do nome de usuario para exibir no anuncio do produto;
    Button cancelarButton, confirmarButton; // botoes do popup 'dialog'

    String nome, email, senha;
    static String UserKey;

    EditText searchInput;
    ImageView imageView;
    Button imageSelect;
    Uri selectedImageUri;
    Bitmap ImageBitmap;
    String ImageUrl; // recebe o URL da imagem se for bem-sucedido ao enviar pro Firebase

    ImageButton searchImg, cartImg, userImg;
    FloatingActionButton addProd;

    public Dialog dialog;
    public static String key;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar); // setta a barra superior como a definida no activity_main
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout); // componente do menu, activity_main
        navigationView = findViewById(R.id.nav_view); //
        navigationView.setNavigationItemSelectedListener(this);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle); // adiciona um listener no Toggle do dL (abre e fecha)
        drawerToggle.syncState();

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.home); // se sIS for nulo, esta na activity_main
        }

        MenuItem item = navigationView.getMenu().findItem(R.id.idUser);

        Intent intent = getIntent();
        UserKey = intent.getStringExtra("key");
        item.setTitle(UserKey);
        nome = intent.getStringExtra("nome");
        vendedor = nome;
        email = intent.getStringExtra("email");
        senha = intent.getStringExtra("senha");
        isLogged = intent.getBooleanExtra("isLogged", false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        searchInput = findViewById(R.id.searchInput);
        addProd = findViewById(R.id.addProd);
        searchImg = findViewById(R.id.search);
        cartImg = findViewById(R.id.cart);
        userImg = findViewById(R.id.user);
        adapter = new MyAdapter(this, items);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadproducts(""); // pra puxar os produtos inicialmente
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pesquisa = searchInput.getText().toString().trim();
                if (!pesquisa.isEmpty()) {
                    loadproducts(pesquisa);
                } else {
                    Toast.makeText(MainActivity.this, "Digite um termo para pesquisar!", Toast.LENGTH_SHORT).show();
                    loadproducts("");
                }
            }
        });

        cartImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogged) {
                    loadCart = IntentAssembly(MainActivity.this, telaCarrinho.class, UserKey, nome, email, senha, isLogged);
                    startActivity(loadCart);
                } else {
                    loadLogin = new Intent(MainActivity.this, telaLogin.class);
                    startActivity(loadLogin);
                }
            }
        });

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogged) {
                    Intent intent = IntentAssembly(MainActivity.this, telaPerfil.class, UserKey, nome, email, senha, isLogged);
                    /*Intent intent = new Intent(MainActivity.this, telaPerfil.class);
                    intent.putExtra("key", UserKey);
                    intent.putExtra("nome", nome);
                    intent.putExtra("email", email);
                    intent.putExtra("senha", senha);
                    intent.putExtra("isLogged", isLogged);*/
                    startActivity(intent);
                } else {
                    loadLogin = new Intent(MainActivity.this, telaLogin.class);
                    startActivity(loadLogin);
                }
            }
        });

        addProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLogged) {
                    showCustomDialog();
                } else {
                    loadLogin = new Intent(MainActivity.this, telaLogin.class);
                    startActivity(loadLogin);
                }
            }
        });
    }

    private void showCustomDialog() {
        // Criando um Dialog personalizado
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.adicionar_produto);  // Layout XML do Dialog
        dialog.setCancelable(true);  // Permite que o Dialog seja fechado ao clicar fora dele

        // elementos do dialog
        nomeInput = dialog.findViewById(R.id.nomePopup);
        valorInput = dialog.findViewById(R.id.valorPopup);
        obsInput = dialog.findViewById(R.id.obsPopup);
        cancelarButton = dialog.findViewById(R.id.cancelarPopup);
        confirmarButton = dialog.findViewById(R.id.adicionarPopup);

        imageView = dialog.findViewById(R.id.imagemPopup);
        imageSelect = dialog.findViewById(R.id.selecionarImagem);

        confirmarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pegando o texto inserido no EditText
                produto = nomeInput.getText().toString();
                obs = obsInput.getText().toString();
                val = valorInput.getText().toString();
                if (!val.isEmpty()) {
                    /*int valPos = indexOf(val, ','); // verifica se tem virgula
                    if ( valPos >= 0) {
                        String bfComma = val.substring(0, valPos);
                        String afComma = val.substring(valPos + 1);
                        String concat = bfComma + "." + afComma;
                        valor = Float.parseFloat(concat);
                    } else {
                        valor = Float.parseFloat(val);
                    }*/
                    valor = Float.parseFloat(val.replace(",", "."));
                }
                if (!produto.isEmpty() || !val.isEmpty()) {
                    if (selectedImageUri != null) {
                        // chama o insert na database e retorna key (se funcionar)
                        productSession(produto, obs, valor, ImageBitmap);
                    } else {
                        Toast.makeText(MainActivity.this, "É necessário selecionar uma imagem", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "É necessário preencher todos os campos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra("isLogged", isLogged);
                pickImageLauncher.launch(intent);
            }
        });
        // Exibindo o Dialog
        dialog.show();
    }

    ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    // Recupera o URI da imagem
                    Intent data = result.getData();
                    if (data != null) {
                        selectedImageUri = data.getData();
                        try {
                            ImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            // Exibe a imagem no ImageView
                            imageView.setImageBitmap(ImageBitmap);
                        } catch (Exception e) {
                            Log.e(e.getClass().getName(), e.getMessage());
                        }
                    } else
                        Toast.makeText(MainActivity.this, "Erro ao obter URI", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "Erro ao selecionar Imagem", Toast.LENGTH_SHORT).show();
                }
            }
        });

    private void productSession(String nomeProduto, String obsProduto, float valorProduto, Bitmap ImageBitmap) {
        UploadImage();
        if (ImageUrl != null) {
            HashMap<String, Object> userHashmap = new HashMap<>();
            userHashmap.put("product", nomeProduto);
            userHashmap.put("description", obsProduto);
            userHashmap.put("price", valorProduto);
            userHashmap.put("sellerID", UserKey);
            userHashmap.put("image", ImageUrl);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mydb = database.getReference("products");

            key = mydb.push().getKey();
            userHashmap.put("key", key);

            mydb.child(key).setValue(userHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                        nomeInput.getText().clear();
                        valorInput.getText().clear();
                        obsInput.getText().clear();
                        imageView.setImageResource(0);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "Erro ao cadastrar produto!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Erro ao enviar imagem!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigationView.setCheckedItem(item.getItemId());
        /*if( item.getItemId() == R.id.home) {
            Toast.makeText(MainActivity.this, "Página Inicial", Toast.LENGTH_SHORT).show();
        }
        else if( item.getItemId() == R.id.login) {
            loadLogin = new Intent(MainActivity.this, telaLogin.class);
            startActivity(loadLogin);
        }
        else if( item.getItemId() == R.id.cart) {
            Toast.makeText(this, "Carrinho", Toast.LENGTH_SHORT).show();
        }
        else if( item.getItemId() == R.id.favorite) {
            Toast.makeText(this, "Favoritos", Toast.LENGTH_SHORT).show();
        }
        else if( item.getItemId() == R.id.addProduct) {
            Toast.makeText(this, "Adicionar Produto", Toast.LENGTH_SHORT).show();
        }
        else if( item.getItemId() == R.id.myProducts) {
            Toast.makeText(this, "Meus Produtos", Toast.LENGTH_SHORT).show();
        }
        else if( item.getItemId() == R.id.mySales) {
            Toast.makeText(this, "Minhas Promoções", Toast.LENGTH_SHORT).show();
        }
        else if( item.getItemId() == R.id.settings) {
            Toast.makeText(this, "Configurações", Toast.LENGTH_SHORT).show();
        }
        else if( item.getItemId() == R.id.logout) {
            Toast.makeText(this, "Sair", Toast.LENGTH_SHORT).show();
        }
        else {

        }
        drawerLayout.closeDrawer(GravityCompat.START);

         */
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void UploadImage() {
        if (selectedImageUri != null) {
            /*final StorageReference myRef = storageRef.child("images/" + selectedImageUri.getLastPathSegment());
            myRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                ImageUrl = uri.toString();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Erro ao receber URL da imagem!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Erro ao enviar imagem!", Toast.LENGTH_SHORT).show();
                }
            });
        }*/ // upload pro Storage descartado (precisa de conta Cloud Billing)

            // em seguida configurei um envio realizado atraves da api do imgur (velho amigo)
            try {
                File file = new File(getCacheDir(), "image.jpg");
                FileOutputStream fos = new FileOutputStream(file);
                ImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                RequestBody title = RequestBody.create(MediaType.parse("text/plain"), nome);
                RequestBody description = RequestBody.create(MediaType.parse("text/plain"), obs);

                ImgurAPI imgurAPI = ImgurClient.getImgurAPI();
                Call<ImgurResponse> call = imgurAPI.uploadImage(body, title, description);
                call.enqueue(new Callback<ImgurResponse>() {
                    @Override
                    public void onResponse(Call<ImgurResponse> call, Response<ImgurResponse> response) {
                        if (response.isSuccessful()) {
                            ImgurResponse imgurResponse = response.body();
                            if (imgurResponse != null) {
                                ImageUrl = imgurResponse.getData().getLink();
                                // Faça algo com a URL da imagem
                            } else {
                                Toast.makeText(MainActivity.this, "Corpo da resposta vazio!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ImgurResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Erro ao enviar imagem!", Toast.LENGTH_SHORT).show();
                        Log.e(MainActivity.class.getName(), "Erro: " + t.getMessage());
                    }
                });
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Erro ao preparar imagem!", Toast.LENGTH_SHORT).show();
                Log.e(MainActivity.class.getName(), "Erro: " + e.getMessage());
            }
        }
    }
    public static String getProductKey() {
        return key;
    }
    public static String getUserKey() {
        return UserKey;
    }

    private void loadproducts(String nome) {
        int sz = items.size();
        items.clear();
        adapter.notifyItemRangeRemoved(0, sz);
        if (!nome.isEmpty()) {
            Query query = dbRefer.orderByChild("product").equalTo(nome);
            //adapter.notifyDataSetChanged();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        items.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String dbNome = ds.child("product").getValue(String.class);
                            String dbObs = ds.child("description").getValue(String.class);
                            String dbValor = ds.child("price").getValue(String.class);
                            String dbVendedor = ds.child("sellerID").getValue(String.class);
                            String dbImagem = ds.child("image").getValue(String.class);
                            String dbKey = ds.child("key").getValue(String.class);

                            items.add(new Item(dbKey, dbNome, dbObs, dbValor, dbVendedor, dbImagem));
                            adapter.notifyItemInserted(items.size() - 1);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Erro ao carregar dados!", Toast.LENGTH_SHORT).show();   // Handle error
                }
            });
        } else {
            dbRefer.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        items.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String dbNome = ds.child("product").getValue(String.class);
                            String dbObs = ds.child("description").getValue(String.class);
                            String dbValor = ds.child("price").getValue(String.class);
                            String dbVendedor = ds.child("sellerID").getValue(String.class);
                            String dbImagem = ds.child("image").getValue(String.class);
                            String dbKey = ds.child("key").getValue(String.class);

                            items.add(new Item(dbKey, dbNome, dbObs, dbValor, dbVendedor, dbImagem));
                            adapter.notifyItemInserted(items.size() - 1);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Erro ao carregar dados!", Toast.LENGTH_SHORT).show();   // Handle error
                    Log.e("MainActivity", databaseError.getMessage());
                    Log.e("MainActivity",  databaseError.toString());
                }
            });
        }
    }
}