package com.example.projekat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
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
import com.example.projekat.database.OsobaRepository;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private OsobaRepository repository;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("pref_file", Context.MODE_PRIVATE);
        repository = new OsobaRepository(LoginActivity.this);

        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEditText = findViewById(R.id.username);
                String email = emailEditText.getText().toString().trim();

                cursor = repository.getEntityByEmail(email);

                if (cursor != null && cursor.getCount() > 0) {
                    // Email exists, proceed with login
                    SharedPreferences.Editor sp_editor = sharedPreferences.edit();
                    if (sharedPreferences.contains("pref_email")) {
                        String emailPref = sharedPreferences.getString("pref_email", "default");
                        sp_editor.putString("pref_email", emailPref + "\r\n" + email);
                    } else {
                        sp_editor.putString("pref_email", email);
                    }
                    sp_editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MyActivity.class);
                    startActivity(intent);
                } else {
                    // Email does not exist, show a toast
                    Toast.makeText(LoginActivity.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the cursor and database connection
        if (cursor != null) {
            cursor.close();
        }
        repository.DBClose();
    }

}