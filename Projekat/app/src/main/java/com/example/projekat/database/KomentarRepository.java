package com.example.projekat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projekat.Utils.DatabaseConstants;

public class KomentarRepository {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public KomentarRepository(Context context) {
        dbHelper = SQLiteHelper.getInstance(context);
    }

    // Insert metoda
    public long insertData(String imeIPrezime, String komentar, int knjigaID) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_IME_I_PREZIME, imeIPrezime);
        values.put(DatabaseConstants.COLUMN_KOMENTAR, komentar);
        values.put(DatabaseConstants.COLUMN_KNJIGA, knjigaID);

        return database.insert(DatabaseConstants.TABLE_KOMENTARI, null, values);

    }
    // Get metoda
    public Cursor getData(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        database = dbHelper.getWritableDatabase();
        return database.query(DatabaseConstants.TABLE_KOMENTARI, projection, selection, selectionArgs, null, null, null);

    }

    // Update metoda
    public int updateData(int id, String imeIPrezime, int komentar, int knjigaID) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_IME_I_PREZIME, imeIPrezime);
        values.put(DatabaseConstants.COLUMN_KOMENTAR, komentar);
        values.put(DatabaseConstants.COLUMN_KNJIGA, knjigaID);

        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.update(DatabaseConstants.TABLE_KOMENTARI, values, whereClause, whereArgs);

    }

    // Delete metoda
    public int deleteData(int id) {
        database = dbHelper.getWritableDatabase();

        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.delete(DatabaseConstants.TABLE_KOMENTARI, whereClause, whereArgs);

    }

    public Cursor getEntity(int id) {
        database = dbHelper.getWritableDatabase();
        String[] projection = new String[]{DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_IME_I_PREZIME,
                DatabaseConstants.COLUMN_KOMENTAR,DatabaseConstants.COLUMN_KNJIGA};

        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.query(DatabaseConstants.TABLE_KOMENTARI, projection, whereClause, whereArgs, null, null, null, null);

    }

    public void DBClose(){
        dbHelper.close();
    }
}
