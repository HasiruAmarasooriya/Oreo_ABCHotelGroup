package com.example.abshotelgroup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.abshotelgroup.R;

public class MainActivity extends AppCompatActivity {

    Button btnPackage, btnPackage2, btnOurdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPackage = findViewById(R.id.btnPackage);
        btnPackage2 = findViewById(R.id.btnPackage2);
        btnOurdetails = findViewById(R.id.btnOurdetails);

        btnPackage.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(intent);
        });

        btnPackage2.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
            startActivity(intent);
        });

        btnOurdetails.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Aboutus.class);
            startActivity(intent);
        });
    }
}