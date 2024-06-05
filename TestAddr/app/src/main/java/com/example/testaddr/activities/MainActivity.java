package com.example.testaddr.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.testaddr.R;
import com.example.testaddr.services.ForegroundService;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static String CHECK_COLOR = "CHECK_COLOR";
    public static final String MUSIC_CHANNEL_ID = "Music channel";
    public static final String COLOR_CHANNEL_ID = "Color channel";
    BroadcastReceiver broadcastReceiver;
    private static final int NOTIFICATION_ID = 1;
    Button proveri;
    Button dodaj;
    TextView statusTextView;

    private SwitchMaterial switchMusic;
    private String[] permissions = {Manifest.permission.POST_NOTIFICATIONS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        proveri = findViewById(R.id.proveri);
        proveri.setOnClickListener(view -> setManager());
        statusTextView = findViewById(R.id.statusTextView);

        setUpReceiver();
        createNotificationChannel(MUSIC_CHANNEL_ID, "Kanal za muziku", "Opis 1");
        createNotificationChannel(COLOR_CHANNEL_ID, "Kanal za boju", "Opis 2");

        switchMusic = findViewById(R.id.switch1);
        switchMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startForegroundService();
                } else {
                    stopForegroundService();
                }
            }
        });
        dodaj = findViewById(R.id.dodaj);
        dodaj.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewProductActivity.class);
            startActivity(intent);
        });

    }
    private void startForegroundService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.setAction(ForegroundService.ACTION_START_FOREGROUND_SERVICE);
        startService(serviceIntent);
    }

    private void stopForegroundService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.setAction(ForegroundService.ACTION_STOP);
        startService(serviceIntent);
    }
    private void setUpReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), CHECK_COLOR)) {
                    Button obojiButton = findViewById(R.id.proveri);
                    obojiButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.pink_300));
                    statusTextView.setText("Switch je iskljucen");
                    //getWindow().getDecorView().setBackgroundColor(Color.parseColor("#ffff00")); // Boja "Zuta"
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(CHECK_COLOR);
        registerReceiver(broadcastReceiver, filter, Context.RECEIVER_EXPORTED);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Ovaj metod će se pozvati kada se aktivnost ponovo pokrene zbog klika na notifikaciju
        // Ovde možete implementirati sve akcije koje želite da se izvrše nakon otvaranja aktivnosti ponovo
        Log.i("MainActivity", "Aktivnost se ponovo pokrenula zbog klika na notifikaciju.");
    }


    private void createNotificationChannel(String id, String name, String description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void setManager() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean switchStatus = switchMusic.isChecked();
                if (switchStatus) {
                    sendNotification("Switch je uključen.");
                    Button obojiButton = findViewById(R.id.proveri);
                    obojiButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.blue_300));
                    statusTextView.setText("Switch je uključen");
                    //getWindow().getDecorView().setBackgroundColor(Color.parseColor("#FFFFFF"));
                } else {
                    Intent intent = new Intent(CHECK_COLOR);
                    sendBroadcast(intent);
                }
                handler.postDelayed(this, 60000); // Ponovo pokreni proveru nakon 1 minuta
            }
        };
        handler.post(runnable); // Pokreni proveru odmah na klik proveri
    }
    private void sendNotification(String message) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, MUSIC_CHANNEL_ID);
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setContentTitle("Obaveštenje");
        mBuilder.setContentText(message);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 101);
        } else {
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }
    }
