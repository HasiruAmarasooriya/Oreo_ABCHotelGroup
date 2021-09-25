package com.example.abshotelgroup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.abshotelgroup.R;

public class Homepage extends AppCompatActivity {

    Button btnRoomPackage, btnPackage2, btnOurdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        btnRoomPackage = findViewById(R.id.btnRoomPackage);
        btnPackage2 = findViewById(R.id.btnPackage2);
        btnOurdetails = findViewById(R.id.btnOurdetails);

        btnRoomPackage.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        btnPackage2.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), VehiclePackages.class);
            startActivity(intent);
        });

        btnOurdetails.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Feedback.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_room) {
            Intent createCustomerIntent = new Intent(Homepage.this, RoomBookingActivity.class);
            startActivity(createCustomerIntent);
        } else if (item.getItemId() == R.id.action_vehicle) {
            Intent createCustomerIntent = new Intent(Homepage.this, VehBookingViewActivity.class);
            startActivity(createCustomerIntent);
        }
        return super.onOptionsItemSelected(item);
    }
} 