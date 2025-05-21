package com.example.lojafazopix;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class thanks extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thanks);

        imageView = findViewById(R.id.imageView);
        Glide.with(this).load(R.drawable.crushed).into(imageView);
    }

}
