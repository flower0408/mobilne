package com.example.projekat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projekat.Utils.DatabaseConstants;

public class AutorRepository {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public AutorRepository(Context context) {
        dbHelper = SQLiteHelper.getInstance(context);
    }

    // Insert metoda
    public long insertData(String imeIPrezime, int godiste, String mestoRodjena) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_IME_I_PREZIME, imeIPrezime);
        values.put(DatabaseConstants.COLUMN_GODISTE, godiste);
        values.put(DatabaseConstants.COLUMN_MESTO_RODJENJA, mestoRodjena);

        return database.insert(DatabaseConstants.TABLE_AUTORI, null, values);

    }
    // Get metoda
    public Cursor getData(String[] projection) {
        database = dbHelper.getWritableDatabase();
        return database.query(DatabaseConstants.TABLE_AUTORI, projection, null, null, null, null, null);
    }

    // Update metoda
    public int updateData(int id, String imeIPrezime, int godiste, String mestoRodjena) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_IME_I_PREZIME, imeIPrezime);
        values.put(DatabaseConstants.COLUMN_GODISTE, godiste);
        values.put(DatabaseConstants.COLUMN_MESTO_RODJENJA, mestoRodjena);

        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.update(DatabaseConstants.TABLE_AUTORI, values, whereClause, whereArgs);

    }

    // Delete metoda
    public int deleteData(int id) {
        database = dbHelper.getWritableDatabase();

        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.delete(DatabaseConstants.TABLE_AUTORI, whereClause, whereArgs);

    }
    public Cursor getEntity(int id) {
        database = dbHelper.getWritableDatabase();
        String[] projection = new String[]{DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_IME_I_PREZIME,
                DatabaseConstants.COLUMN_GODISTE,DatabaseConstants.COLUMN_MESTO_RODJENJA};

        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.query(DatabaseConstants.TABLE_AUTORI, projection, whereClause, whereArgs, null, null, null, null);

    }
    public void DBClose(){
        dbHelper.close();
    }

}
