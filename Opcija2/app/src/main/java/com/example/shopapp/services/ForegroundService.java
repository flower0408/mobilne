package com.example.shopapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.shopapp.R;
import com.example.shopapp.activities.HomeActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class ForegroundService extends Service {

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP = "ACTION_STOP";
    private NotificationManager notificationManager;
    private NotificationChannel channel;
    private MediaPlayer player;

    private int notificationID = 50;

    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = getSystemService(NotificationManager.class);
        if (intent != null)
        {
            String action = intent.getAction();
            Log.i("SERVICE STARTED", "YES");
            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP:
                    Toast.makeText(getApplicationContext(), "You clicked Stop button.", Toast.LENGTH_LONG).show();
                    player.stop();
                    stopForeground(true);
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    /* Used to build and start foreground service. */
    private void startForegroundService()
    {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.setLooping(true);
        player.start();

        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationID, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, HomeActivity.MUSIC_CHANNEL_ID);

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Music player implemented by foreground service.");
        bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always," +
                "it can be controlled by user via notification.");
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setFullScreenIntent(pendingIntent, true);

        // Add Stop button intent in notification.
        Intent stopIntent = new Intent(this, ForegroundService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 2, stopIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Action stopAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Stop", pendingStopIntent);
        builder.addAction(stopAction);

        // Start foreground service.
        startForeground(notificationID, builder.build());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Stop foreground service.", Toast.LENGTH_LONG).show();

        // Stop foreground service and remove the notification.
        player.stop();
        stopForeground(true);
        stopSelf();
    }
}