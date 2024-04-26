package com.example.projekat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.content.Intent;
import android.widget.TextView;

import com.example.projekat.Utils.FragmentTransition;
import com.example.projekat.Utils.InitDB;
import com.example.projekat.activities.LoginActivity;
import com.example.projekat.activities.MyActivity;
import com.example.projekat.activities.RegistracijaActivity;
import com.example.projekat.fragments.KorisniciFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // Requesting permission to INTERNET and RECORD AUDIO
    private boolean isPermissions = true;
    private String [] permissions = {
            android.Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
    };
    private static final int REQUEST_PERMISSIONS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        InitDB.initDB(this);

        Button registerButton = findViewById(R.id.register);
        Button loginButton = findViewById(R.id.login);
        Button homeButton = findViewById(R.id.home);
        Button intentionButton = findViewById(R.id.intention);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pozivamo RegistracijaActivity
                Intent intent = new Intent(MainActivity.this, RegistracijaActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pozivamo LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pozivamo MyActivity
                Intent intent = new Intent(MainActivity.this, MyActivity.class);
                startActivity(intent);
            }
        });


        intentionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                startActivity(intent);*/

                /* //Probati da posaljem mail
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:natasakotaranin1@gmail.com"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"natasakotaranin@gmail.com"}); // Postavljanje adrese pošiljaoca
                intent.putExtra(Intent.EXTRA_SUBJECT, "Naslov e-pošte");
                intent.putExtra(Intent.EXTRA_TEXT, "Tekst e-pošte");
                startActivity(intent);*/


                openGallery();


            }
        });

        onRequestPermission();

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Handle the selected image here, for example, you can get the URI of the selected image using data.getData()
            // For example:
            // Uri selectedImageUri = data.getData();
            // Then you can use this URI to do further operations like displaying the image.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_PERMISSIONS:
                for(int i = 0; i < permissions.length; i++) {
                    Log.i("ShopApp", "permission " + permissions[i] + " " + grantResults[i]);
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                        isPermissions = false;
                    }
                }
                break;
        }

        if (!isPermissions) {
            Log.e("ShopApp", "Error: no permission");
            finishAndRemoveTask();
        }

    }

    private void onRequestPermission(){
        Log.i("ShopApp", "onRequestPermission");
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }
}