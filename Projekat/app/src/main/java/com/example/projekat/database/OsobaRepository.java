package com.example.projekat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projekat.Utils.DatabaseConstants;

public class OsobaRepository {

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public OsobaRepository(Context context) {
        dbHelper = SQLiteHelper.getInstance(context);
    }

    // Insert metoda
    public long insertData(String email, String lozinka, String ime, String prezime, String telefon) {
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_EMAIL, email);
        values.put(DatabaseConstants.COLUMN_LOZINKA, lozinka);
        values.put(DatabaseConstants.COLUMN_IME, ime);
        values.put(DatabaseConstants.COLUMN_PREZIME, prezime);
        values.put(DatabaseConstants.COLUMN_TELEFON, telefon);

        return database.insert(DatabaseConstants.TABLE_OSOBA, null, values);

    }
    // Get metoda
    public Cursor getData(String[] projection) {
        database = dbHelper.getWritableDatabase();
        return database.query(DatabaseConstants.TABLE_OSOBA, projection, null, null, null, null, null);
    }

    // Update metoda
    public int updateData(int id, String email, String lozinka, String ime, String prezime, String telefon) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseConstants.COLUMN_EMAIL, email);
        values.put(DatabaseConstants.COLUMN_LOZINKA, lozinka);
        values.put(DatabaseConstants.COLUMN_IME, ime);
        values.put(DatabaseConstants.COLUMN_PREZIME, prezime);
        values.put(DatabaseConstants.COLUMN_TELEFON, telefon);

        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.update(DatabaseConstants.TABLE_OSOBA, values, whereClause, whereArgs);

    }

    // Get metoda za dohvatanje podataka korisnika na osnovu e-maila
    public Cursor getEntityByEmail(String email) {
        database = dbHelper.getWritableDatabase();
        String[] projection = new String[]{
                DatabaseConstants.COLUMN_ID,
                DatabaseConstants.COLUMN_EMAIL,
                DatabaseConstants.COLUMN_LOZINKA,
                DatabaseConstants.COLUMN_IME,
                DatabaseConstants.COLUMN_PREZIME,
                DatabaseConstants.COLUMN_TELEFON
        };
        String whereClause = DatabaseConstants.COLUMN_EMAIL + " = ?";
        String[] whereArgs = { email };
        return database.query(DatabaseConstants.TABLE_OSOBA, projection, whereClause, whereArgs, null, null, null, null);
    }


    // Delete metoda
    public int deleteData(int id) {
        database = dbHelper.getWritableDatabase();

        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.delete(DatabaseConstants.TABLE_OSOBA, whereClause, whereArgs);

    }
    public Cursor getEntity(int id) {
        database = dbHelper.getWritableDatabase();
        String[] projection = new String[]{DatabaseConstants.COLUMN_ID, DatabaseConstants.COLUMN_EMAIL,
                DatabaseConstants.COLUMN_LOZINKA,DatabaseConstants.COLUMN_IME, DatabaseConstants.COLUMN_PREZIME, DatabaseConstants.COLUMN_TELEFON};
        String whereClause = DatabaseConstants.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.query(DatabaseConstants.TABLE_OSOBA, projection, whereClause, whereArgs, null, null, null, null);

    }
    public void DBClose(){
        dbHelper.close();
    }
}
