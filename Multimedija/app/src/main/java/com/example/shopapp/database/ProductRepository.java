package com.example.shopapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class ProductRepository {

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;

    public ProductRepository(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Insert metoda
    public long insertData(String title, String description, String image) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, title);
        values.put(SQLiteHelper.COLUMN_DESCRIPTION, description);
        values.put(SQLiteHelper.COLUMN_IMAGE, image);

        return database.insert(SQLiteHelper.TABLE_PRODUCTS, null, values);
    }

    // Get metoda
    public Cursor getData(Long id, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        projection = new String[]{SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_TITLE, SQLiteHelper.COLUMN_DESCRIPTION, SQLiteHelper.COLUMN_IMAGE};

        //nacin 1 - preko queryBuilder-a
//        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
//        queryBuilder.appendWhere(SQLiteHelper.COLUMN_ID + "="  + id);
//        queryBuilder.setTables(SQLiteHelper.TABLE_PRODUCTS);
//
//        Cursor cursor = queryBuilder.query(
//                database,
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                sortOrder
//        );
//        return cursor;
        // nacin 2 - preko query metode
        return database.query(SQLiteHelper.TABLE_PRODUCTS, projection, null, null, null, null, null);
    }

    // Update metoda
    public int updateData(long id, String title, String description, String image) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_TITLE, title);
        values.put(SQLiteHelper.COLUMN_DESCRIPTION, description);
        values.put(SQLiteHelper.COLUMN_IMAGE, image);

        String whereClause = SQLiteHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.update(SQLiteHelper.TABLE_PRODUCTS, values, whereClause, whereArgs);
    }

    // Delete metoda
    public int deleteData(long id) {
        String whereClause = SQLiteHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.delete(SQLiteHelper.TABLE_PRODUCTS, whereClause, whereArgs);
    }
}
