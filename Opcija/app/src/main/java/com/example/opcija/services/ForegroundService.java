package com.example.opcija.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.opcija.R;
import com.example.opcija.activities.MainActivity;

public class ForegroundService extends Service {

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_STOP = "ACTION_STOP";
    private static final String MUSIC_CHANNEL_ID = "Music channel";
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
                case ACTION_PLAY:
                    player.start();
                    Toast.makeText(getApplicationContext(), "You clicked Play button.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PAUSE:
                    Toast.makeText(getApplicationContext(), "You clicked Pause button.", Toast.LENGTH_LONG).show();
                    player.pause();
                    break;
                case ACTION_STOP:
                    Toast.makeText(getApplicationContext(), "You clicked Stop button.", Toast.LENGTH_LONG).show();
                    player.stop();
                    stopForeground(true);
                    Intent ints = new Intent("STOP_MUSIC");
                    getApplicationContext().sendBroadcast(ints);
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
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationID, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MUSIC_CHANNEL_ID);

        // Make notification show big text.
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Music player implemented by foreground service.");
        bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always," +
                "it can be controlled by user via notification.");
        // Set big text style.
        builder.setStyle(bigTextStyle);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true);

        // Add Play button intent in notification.
        Intent playIntent = new Intent(this, ForegroundService.class);
        playIntent.setAction(ACTION_PLAY);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Action playAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", pendingPlayIntent);
        builder.addAction(playAction);

        // Add Pause button intent in notification.
        Intent pauseIntent = new Intent(this, ForegroundService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pendingPrevIntent = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Action prevAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", pendingPrevIntent);
        builder.addAction(prevAction);

        // Add Stop button intent in notification.
        /*Intent stopIntent = new Intent(this, ForegroundService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 2, stopIntent, PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Action stopAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Stop", pendingStopIntent);
        builder.addAction(stopAction);*/

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