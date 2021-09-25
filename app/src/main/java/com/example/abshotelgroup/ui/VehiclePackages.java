package com.example.abshotelgroup.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.abshotelgroup.util.DBHelper;
import com.example.abshotelgroup.R;

public class VehiclePackages extends AppCompatActivity {

    Button car,Van,Bus,Ourdetails;
    DBHelper MyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_packages);

        car = (Button) findViewById(R.id.car);
        Van = (Button) findViewById(R.id.Van);
        Bus = (Button) findViewById(R.id.Bus);
        Ourdetails = (Button) findViewById(R.id.Ourdetails);


        MyDB = new DBHelper(this);

        car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CarPackage.class);
                startActivity(intent);
            }
        });

        Van.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), vanPackage.class);
                startActivity(intent);
            }
        });

        Bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Buspackage.class);
                startActivity(intent);
            }
        });

        Ourdetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Aboutus.class);
                startActivity(intent);
            }
        });
    }
}