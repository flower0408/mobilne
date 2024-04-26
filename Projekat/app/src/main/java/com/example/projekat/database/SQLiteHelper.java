package com.example.projekat.database;

import static com.example.projekat.Utils.DatabaseConstants.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
    //Dajemo ime bazi
    private static final String DATABASE_NAME = "projekat.db";
    //i pocetnu verziju baze. Obicno krece od 1
    private static final int DATABASE_VERSION = 1;
    private static SQLiteHelper sqLiteHelper;

    private static final String DB_CREATE_KNJIGE = "create table "
            + TABLE_KNJIGE + "("
            + COLUMN_ID  + " integer primary key autoincrement , "
            + COLUMN_NASLOV + " text, "
            + COLUMN_BRSTRANICA + " integer, "
            + COLUMN_POVEZ + " text, "
            + COLUMN_ZANR + " text check (" + COLUMN_ZANR + " in ('ROMAN', 'FANTASTIKA', 'AKCIONI', 'BIOGRAFIJA', 'FILOZOFIJA')), "
            + COLUMN_AUTOR + " integer references AUTORI(_id)"
            + ")";

    private static final String DB_CREATE_AUTORI = "create table "
            + TABLE_AUTORI + "("
            + COLUMN_ID  + " integer primary key autoincrement , "
            + COLUMN_IME_I_PREZIME + " text, "
            + COLUMN_GODISTE + " integer, "
            + COLUMN_MESTO_RODJENJA + " text"
            + ")";

    private static final String DB_CREATE_KOMENTARI = "create table "
            + TABLE_KOMENTARI + "("
            + COLUMN_ID  + " integer primary key autoincrement , "
            + COLUMN_IME_I_PREZIME + " text, "
            + COLUMN_KOMENTAR + " text, "
            + COLUMN_KNJIGA + " integer references KNJIGE(_id)"
            + ")";

    private static final String DB_CREATE_OSOBA = "create table "
            + TABLE_OSOBA + "("
            + COLUMN_ID  + " integer primary key autoincrement , "
            + COLUMN_EMAIL + " text, "
            + COLUMN_LOZINKA + " text, "
            + COLUMN_IME + " text, "
            + COLUMN_PREZIME + " text, "
            + COLUMN_TELEFON + " text "
            + ")";

    //Potrebno je dodati konstruktor zbog pravilne inicijalizacije
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static SQLiteHelper getInstance(Context context) {

        if (sqLiteHelper == null) {
            synchronized (SQLiteHelper.class){ //thread safe singleton
                if (sqLiteHelper == null)
                    sqLiteHelper = new SQLiteHelper(context);
            }
//            Log.i("REZ_DB", "ON CREATE SQLITE HELPER");

        }

        return sqLiteHelper;
    }
    //Prilikom kreiranja baze potrebno je da pozovemo odgovarajuce metode biblioteke
    //prilikom kreiranja moramo pozvati db.execSQL za svaku tabelu koju imamo
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("REZ_DB", "ON CREATE SQLITE HELPER");
        db.execSQL(DB_CREATE_KNJIGE);
        Log.i("REZ_DB", "ON CREATE SQLITE HELPER");

        db.execSQL(DB_CREATE_AUTORI);
        Log.i("REZ_DB", "ON CREATE SQLITE HELPER");

        db.execSQL(DB_CREATE_KOMENTARI);

        Log.i("REZ_DB", "ON CREATE SQLITE HELPER");

        db.execSQL(DB_CREATE_OSOBA);
    }

    //kada zelimo da izmenimo tabele, moramo pozvati drop table za sve tabele koje imamo
    //  moramo voditi računa o podacima, pa ćemo onda raditi ovde migracije po potrebi
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("REZ_DB", "ON UPGRADE SQLITE HELPER");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KNJIGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTORI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KOMENTARI);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OSOBA);
        onCreate(db);
    }
}
