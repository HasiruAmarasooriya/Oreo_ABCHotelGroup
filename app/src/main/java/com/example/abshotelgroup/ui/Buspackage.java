package com.example.abshotelgroup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.abshotelgroup.R;
import com.example.abshotelgroup.util.AbcHotelConstans;

public class Buspackage extends AppCompatActivity {

    Button btnBook2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buspackage);

        btnBook2 = findViewById(R.id.btnBook2);

        btnBook2.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), VehicleBookingForm.class);
            intent.putExtra(AbcHotelConstans.VEH_CONS, AbcHotelConstans.BUS_BOOK);
            startActivity(intent);
        });

    }
}