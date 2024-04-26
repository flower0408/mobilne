package com.example.projekat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projekat.Utils.DatabaseConstants;

public class KnjigeRepository {

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public KnjigeRepository(Context context) {
        dbHelper = SQLiteHelper.getInstance(context);
    }

    // Insert metoda
    public long insertData(String naslov, int brStranica, String povez, String zanr, int autorID) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_NASLOV, naslov);
        values.put(DatabaseConstants.COLUMN_BRSTRANICA, brStranica);
        values.put(DatabaseConstants.COLUMN_POVEZ, povez);
        values.put(DatabaseConstants.COLUMN_ZANR, zanr);
        values.put(DatabaseConstants.COLUMN_AUTOR, autorID);

        return database.insert(DatabaseConstants.TABLE_KNJIGE, null, values);
    }
    // Get metoda
    public Cursor getData(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        database = dbHelper.getWritableDatabase();

        return database.query(DatabaseConstants.TABLE_KNJIGE, projection, selection, selectionArgs, null, null, null);

    }

    // Update metoda
    public int updateData(int id, String naslov, int brStranica, String povez, String zanr, int autorID) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_NASLOV, naslov);
        values.put(DatabaseConstants.COLUMN_BRSTRANICA, brStranica);
        values.put(DatabaseConstants.COLUMN_POVEZ, povez);
        values.put(DatabaseConstants.COLUMN_ZANR, zanr);
        values.put(DatabaseConstants.COLUMN_AUTOR, autorID);

        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.update(DatabaseConstants.TABLE_KNJIGE, values, whereClause, whereArgs);

    }

    // Delete metoda
    public int deleteData(int id) {
        database = dbHelper.getWritableDatabase();

        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.delete(DatabaseConstants.TABLE_KNJIGE, whereClause, whereArgs);

    }
    public Cursor getEntity(int id) {
        database = dbHelper.getWritableDatabase();
        String[] projection = new String[]{DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_NASLOV, DatabaseConstants.COLUMN_BRSTRANICA,
                DatabaseConstants.COLUMN_POVEZ, DatabaseConstants.COLUMN_ZANR, DatabaseConstants.COLUMN_AUTOR};

        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.query(DatabaseConstants.TABLE_KNJIGE, projection, whereClause, whereArgs, null, null, null, null);

    }

    public void DBCLose(){
        dbHelper.close();
    }
}
