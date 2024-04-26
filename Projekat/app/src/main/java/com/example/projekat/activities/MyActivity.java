package com.example.projekat.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.projekat.MainActivity;
import com.example.projekat.R;
import com.example.projekat.Utils.FragmentTransition;
import com.example.projekat.fragments.AutorFragment;
import com.example.projekat.fragments.KnjigeFragment;
import com.example.projekat.fragments.KomentariFragment;
import com.example.projekat.fragments.KorisniciFragment;
import com.example.projekat.fragments.ProfilFragment;
import com.example.projekat.fragments.SettingsFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class MyActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private String permission = Manifest.permission.CAMERA;
    private String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private ActivityResultLauncher<String> mPermissionResult;
    private ActivityResultLauncher<String> locationPermissionLauncher;

    private SharedPreferences sharedPreferences;
    private boolean allowSync;
    private String synctime;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mPermissionResult = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        openCamera();
                         //FragmentTransition.to(KorisniciFragment.newInstance("S","s"), this, false, R.id.fragmentContainer);
                    } else {
                        TextView textView = findViewById(R.id.prikaz);
                        textView.setText("“Nije dozvoljena kamera!”");
                    }
                });

        // Initialize location permission launcher
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, proceed with location-related functionality
                        openLocation();
                    } else {
                        // Permission denied, show a message
                        Toast.makeText(MyActivity.this, "Location permission denied", Toast.LENGTH_SHORT).show();
                    }
                });

        Button traziDozvolu = findViewById(R.id.prikaziDozvolu);
        traziDozvolu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int getPermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
                if (getPermission != PackageManager.PERMISSION_GRANTED) {
                    mPermissionResult.launch(permission);
                } else {
                    FragmentTransition.to(KorisniciFragment.newInstance("S","s"), MyActivity.this, false, R.id.fragmentContainer);
                }
            }
        });

        Button traziDozvolu2 = findViewById(R.id.prikaziDozvolu2);
        traziDozvolu2.setOnClickListener(view -> checkLocationPermission());


        Button obojiIIspisi = findViewById(R.id.prikaziBoju);
        obojiIIspisi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ConstraintLayout)findViewById(R.id.aktivnost_kontejner)).setBackgroundColor(Color.RED);
                TextView textView = findViewById(R.id.prikaz);
                String ulogovani = getSharedPreferences("pref_file", MODE_PRIVATE).getString("pref_email", "default");
                String[] niz = ulogovani.split("\r\n");
                textView.setText(niz[niz.length-1]);
            }
        });

        consultPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void consultPreferences(){

        /* PRE
         * getDefaultSharedPreferences():
         * koristi podrazumevano ime preference-file-a.
         * Podrzazumevani fajl je setovan na nivou aplikacije tako da
         * sve aktivnosti u istom context-u mogu da mu pristupe jednostavnije
         * getSharedPreferences(name,mode):
         * trazi da se specificira ime preference file-a requires
         * i mod u kome se radi (e.g. private, world_readable, etc.)
         * sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
         * */
        sharedPreferences = getSharedPreferences("pref_file", Context.MODE_PRIVATE);
        /*
         * Proverava da li postoji kljuc pref_sync_list unutar sharedPreferences fajla
         * */
        if(sharedPreferences.contains("pref_sync_list")){
            Log.i("REZ_SP", "Postoji pref_sync_list kljuc");
            /*
             * Koristeci parove kljuc-vrednost iz shared preferences mozemo da dobijemo
             * odnosno da zapisemo nekakve vrednosti. Te vrednosti mogu da budu iskljucivo
             * prosti tipovi u Javi.
             * Kao prvi parametar prosledjujemo kljuc, a kao drugi podrazumevanu vrednost,
             * ako nesto pod tim kljucem se ne nalazi u storage-u, da dobijemo podrazumevanu
             * vrednost nazad, i to nam je signal da nista nije sacuvano pod tim kljucem.
             * */
            synctime = sharedPreferences.getString("pref_sync_list", "1");
            Log.i("REZ_SP", "SYNC TIME = " + synctime.toString());

        }

        if(sharedPreferences.contains("pref_sync")){
            Log.i("REZ_SP", "Postoji pref_sync kljuc");
            allowSync = sharedPreferences.getBoolean("pref_sync", false);
        }

        if(sharedPreferences.contains("pref_name")){
            Log.i("REZ_SP", "Postoji pref_name kljuc");
            String name = sharedPreferences.getString("pref_name", "default");
            Log.i("REZ_SP", "PREF NAME = " + name);
        }else{
            Log.i("REZ_SP", "Ne postoji pref_name kljuc");
        }
    }


    @Override
    protected void onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        Log.i("PREFERENCE LISETENER", "CHANGED");
        assert key != null;
        switch (key) {

            case "pref_name":
                setAppName();
                break;
        }
    }

    private void setAppName(){
        SharedPreferences.Editor sp_editor = sharedPreferences.edit();
        Log.i("REZ_SP", "Set pref_name kljuc");
        if(sharedPreferences.contains("pref_name")){
            String s = sharedPreferences.getString("pref_name", "ShopApp");
            sp_editor.putString("pref_name", s);
            sp_editor.commit();
            getSupportActionBar().setTitle(s);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            // Handle the case where the device doesn't have a camera app installed or the camera is not available
            // For example, display a message to the user
            Toast.makeText(this, "Camera is not available", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            // Check if the 'data' Intent has extras and if the 'data' extras contain the image data
            Bundle extras = data.getExtras();
            if (extras != null && extras.containsKey("data")) {
                // Handle the captured image here
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                // Do something with the imageBitmap
                saveImageToExternalStorage(imageBitmap);
            } else {
                // Handle the case where the 'data' extras do not contain the image data
                // For example, display a message to the user or handle it accordingly
                Toast.makeText(this, "Failed to retrieve image data", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where the image capture activity did not return a successful result
            // For example, display a message to the user or handle it accordingly
            Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveImageToExternalStorage(Bitmap imageBitmap) {
        // Create a file name for the image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        // Get the directory for storing images
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null) {
            try {
                // Create the image file
                File imageFile = new File(storageDir, imageFileName);

                // Save the Bitmap to the file
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                // Inform the user that the image has been saved
                Toast.makeText(this, "Image saved to: " + imageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to access external storage", Toast.LENGTH_SHORT).show();
        }
    }
    private void checkLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, locationPermission);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            locationPermissionLauncher.launch(locationPermission);
        } else {
            // Permission already granted, proceed with location-related functionality
            openLocation();
        }
    }

    private void openLocation() {
        //Toast.makeText(this, "Opening location...", Toast.LENGTH_SHORT).show();
        /* // Open Maps App: You can open a map app with a specific location using an intent.
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=latitude,longitude(label)");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);*/
        // Open Google Maps Website: Similar to opening the Maps app, but opens the Google Maps website in a browser.
        Uri locationUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=latitude,longitude");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);
        startActivity(mapIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ((Objects.requireNonNull(item.getTitle())).toString()) {
            case "Knjige":
                FragmentTransition.to(KnjigeFragment.newInstance(), MyActivity.this, true, R.id.fragmentContainer);
                return true;

            case "Profil":
                FragmentTransition.to(ProfilFragment.newInstance(), MyActivity.this, true, R.id.fragmentContainer);
                return true;

            case "Logout":
                Intent intent = new Intent(MyActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Završava trenutnu aktivnost (MyActivity) kako se ne bi mogla ponovo vratiti pritiskom na dugme "Back"
                return true;

            case "Autori":
                FragmentTransition.to(AutorFragment.newInstance(), MyActivity.this, true, R.id.fragmentContainer);

                return true;

            case "Settings":
                FragmentTransition.to(SettingsFragment.newInstance(), MyActivity.this, true, R.id.fragmentContainer);

                return true;
            case "Komentari":
                FragmentTransition.to(KomentariFragment.newInstance(), MyActivity.this, true, R.id.fragmentContainer);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}