package com.example.projekat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projekat.R;
import com.example.projekat.database.AutorRepository;
import com.example.projekat.database.OsobaRepository;

public class RegistracijaActivity extends AppCompatActivity {

    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextName;
    EditText editTextLastname;
    EditText editTextNumber;
    Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registracija);

        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        editTextName = findViewById(R.id.name);
        editTextLastname = findViewById(R.id.lastname);
        editTextNumber = findViewById(R.id.number);
        buttonRegister = findViewById(R.id.register);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String name = editTextName.getText().toString();
                String lastname = editTextLastname.getText().toString();
                String number = editTextNumber.getText().toString();


                OsobaRepository osobaRepository = new OsobaRepository(RegistracijaActivity.this);
                osobaRepository.insertData(username, password, name, lastname, number);
                osobaRepository.DBClose();

                Toast.makeText(RegistracijaActivity.this, "Uspešna registracija", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistracijaActivity.this, LoginActivity.class);
                startActivity(intent);
                 /* // U slucaju da treba da se postavi vreme tranzicije
                 new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(RegistracijaActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }, 2000);*/
            }
        });

    }


}