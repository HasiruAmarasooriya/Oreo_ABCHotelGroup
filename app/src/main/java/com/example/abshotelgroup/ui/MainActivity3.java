package com.example.abshotelgroup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.abshotelgroup.R;
import com.example.abshotelgroup.util.AbcHotelConstans;


public class MainActivity3 extends AppCompatActivity {

    Button btnBook2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        btnBook2 = findViewById(R.id.btnBook2);

        btnBook2.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity4.class);
            intent.putExtra(AbcHotelConstans.BOOKING_CONS, AbcHotelConstans.HALF_BOARD_BOOK);
            startActivity(intent);
        });
    }
}