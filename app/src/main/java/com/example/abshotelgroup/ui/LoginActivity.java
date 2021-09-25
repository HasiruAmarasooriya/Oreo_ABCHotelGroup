package com.example.abshotelgroup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.abshotelgroup.R;
import com.example.abshotelgroup.util.DBHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button btnlogin, btnSingUp;
    DBHelper MyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username1);
        password = (EditText) findViewById(R.id.password1);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        btnSingUp = (Button) findViewById(R.id.btnSingUp1);
        MyDB = new DBHelper(this);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (TextUtils.isEmpty(user)) {

                    Toast.makeText(LoginActivity.this, "Please enter your User Name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(pass)) {

                    Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                } else {

                    Boolean Result = MyDB.checkUsernamePassword(user, pass);

                    if (Result == true) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Homepage.class);
                        startActivity(intent);
                    } else {

                        Toast.makeText(LoginActivity.this, "Invalid Credentials.", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });


        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    public void viewFeeds(View view) {
        BottomSheetDialogFragment bottomSheetDialogFragment = new FeedbackViewFragment();
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }
}