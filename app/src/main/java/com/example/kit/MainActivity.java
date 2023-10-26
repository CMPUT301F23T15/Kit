package com.example.kit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.kit.database.ItemDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ItemDatabase itemDatabase = new ItemDatabase();

    }
}