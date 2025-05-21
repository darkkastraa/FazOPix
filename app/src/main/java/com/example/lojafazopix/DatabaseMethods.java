package com.example.lojafazopix;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;

public class DatabaseMethods {
    public static Intent IntentAssembly(Context context, Class<?> targetActivity, String key, String name, String email, String password, Boolean isLogged) {
        Intent intent = new Intent(context, targetActivity);
        intent.putExtra("key", key);
        intent.putExtra("nome", name);
        intent.putExtra("email", email);
        intent.putExtra("senha", password);
        intent.putExtra("isLogged", isLogged);
        return intent;
    }
    static boolean DatabaseInsert(FirebaseDatabase database,  String param1, String param2, String param3, String param4) {


        return true;
    }

}
