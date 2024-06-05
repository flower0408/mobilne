package com.example.shopapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.shopapp.R;
import com.example.shopapp.services.ForegroundService;
import com.example.shopapp.services.SyncService;

import java.util.Objects;


public class HomeActivity extends AppCompatActivity {
    public static String CHECK_COLOR = "CHECK_COLOR";
    public static final String MUSIC_CHANNEL_ID = "Music channel";
    public static final String COLOR_CHANNEL_ID = "Color channel";
    BroadcastReceiver broadcastReceiver;
    private static final int NOTIFICATION_ID = 1;
    Button oboji;
    Button reprodukuj;
    Button dodaj;
    private String[] permissions = {Manifest.permission.POST_NOTIFICATIONS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        oboji = findViewById(R.id.oboji);
        oboji.setOnClickListener(view -> setManager());

        setUpReceiver();
        createNotificationChannel(MUSIC_CHANNEL_ID, "Kanal za muziku", "Opis 1");
        createNotificationChannel(COLOR_CHANNEL_ID, "Kanal za boju", "Opis 2");

        reprodukuj = findViewById(R.id.reprodukuj);
        reprodukuj.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ForegroundService.class);
                intent.setAction(ForegroundService.ACTION_START_FOREGROUND_SERVICE);
                startForegroundService(intent);
        });
        dodaj = findViewById(R.id.dodaj);
        dodaj.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, NewProductActivity.class);
            startActivity(intent);
        });
    }

    private void setUpReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), "CHECK_COLOR")) {
                    int buttonColor = ((Button) findViewById(R.id.oboji)).getCurrentTextColor();
                    if (buttonColor == Color.BLACK) {
                        oboji.setTextColor(Color.GREEN);
                    } else if (buttonColor == Color.GREEN) {
                        oboji.setTextColor(Color.BLACK);
                    }
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    // notificationId is a unique int for each notification that you must define

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, COLOR_CHANNEL_ID);
                    mBuilder.setSmallIcon(R.drawable.ic_action_about);
                    mBuilder.setContentTitle("Obavestenje o boji");
                    mBuilder.setContentText("Boja je promenjena!");

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity)context, permissions, 101);
                    }
                    else {
                        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(CHECK_COLOR);
        registerReceiver(broadcastReceiver, filter, Context.RECEIVER_EXPORTED);

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

        Intent alarmIntent = new Intent(this, SyncService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 60000;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

}