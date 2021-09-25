package com.example.abshotelgroup.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.abshotelgroup.R;
import com.example.abshotelgroup.util.DBHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText username, email, password, confirmPassword;
    Button btnRegister, btnSignIn;
    DBHelper mMyDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmpassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnSignIn = (Button) findViewById(R.id.btnSingIn);


        mMyDb = new DBHelper(this);


        btnRegister.setOnClickListener(view -> {
            String user = username.getText().toString();
            String emai = email.getText().toString();
            String pass = password.getText().toString();
            String confirmpass = confirmPassword.getText().toString();


            if (TextUtils.isEmpty(user)) {
                Toast.makeText(RegisterActivity.this, "Please Enter Your Name.", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(emai)) {
                Toast.makeText(RegisterActivity.this, "Please Enter Your Email.", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(pass)) {
                Toast.makeText(RegisterActivity.this, "Please Enter Your Password.", Toast.LENGTH_SHORT).show();
            }else if(pass.length()<5){
                Toast.makeText(RegisterActivity.this, "The Password Should At least Contain Five Characters !", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(confirmpass)) {
                Toast.makeText(RegisterActivity.this, "Please Enter Your Confirm Password.", Toast.LENGTH_SHORT).show();
            } else {
                if (pass.equals(confirmpass)) {
                    Boolean usercheckResult = mMyDb.checkUsername(user);

                    if (usercheckResult == false) {
                        Boolean regRisult = mMyDb.insertData(user, emai, pass);
                        if (regRisult == true) {

                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(RegisterActivity.this, "User already Exists.\n Please Sing In.", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(RegisterActivity.this, "Password not matching", Toast.LENGTH_SHORT).show();
                }

            }
        });


        btnSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMyDb.close();
    }
}