package com.example.shopapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.example.shopapp.activities.HomeActivity;
import com.example.shopapp.clients.ClientUtils;
import com.example.shopapp.model.Product;
import com.example.shopapp.tools.CheckConnectionTools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncService extends Service {

    public static String RESULT_CODE = "RESULT_CODE";

    ExecutorService executor = Executors.newSingleThreadExecutor(); //kreira samo jedan thread
    Handler handler = new Handler(Looper.getMainLooper()); //handler koji upravlja glavim thread-om od aplikacije (applications main thread)

    /*
     * Metoda koja se poziva prilikom izvrsavanja zadatka servisa
     * Koristeci Intent mozemo prilikom startovanja servisa proslediti
     * odredjene parametre.
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("REZ", "SyncService onStartCommand");
        int status = CheckConnectionTools.getConnectivityStatus(getApplicationContext());
        if(status == CheckConnectionTools.TYPE_WIFI || status == CheckConnectionTools.TYPE_MOBILE){
            // Alternativa za SyncTask
            executor.execute(() -> {
                //Background work here
                Log.i("REZ", "Background work here");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.post(() -> {
                    //UI Thread work here
                    Log.i("REZ", "UI Thread work here");
                    Intent ints = new Intent(HomeActivity.SYNC_DATA);
                    int intsStatus = CheckConnectionTools.getConnectivityStatus(getApplicationContext());
                    ints.putExtra(RESULT_CODE, intsStatus);
                    getApplicationContext().sendBroadcast(ints);
                });
            });
        }
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
