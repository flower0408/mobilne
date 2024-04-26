package com.example.projekat.Utils;

import android.app.Activity;
import android.util.Log;

import com.example.projekat.database.AutorRepository;

public class InitDB {
    public static void initDB(Activity activity) {
        AutorRepository autorRepository = new AutorRepository(activity.getApplicationContext());
        Log.i("REZ_DB", "ENTRY INSERT TO DATABASE");
        {
            String[] projection = { DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_IME_I_PREZIME,
                    DatabaseConstants.COLUMN_GODISTE, DatabaseConstants.COLUMN_MESTO_RODJENJA};
            if (autorRepository.getData(projection).getCount() == 0) {
                autorRepository.insertData("Mihail Bulgakov", 1891, "Kijev");
                autorRepository.insertData("Fjodor Dostojevski", 1821, "Moskva");
            }
        }
    }
}
