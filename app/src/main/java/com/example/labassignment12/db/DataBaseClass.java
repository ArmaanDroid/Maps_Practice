package com.example.labassignment12.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DataBaseClass extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favouriteplaces.db";
    public static final String TABLE_NAME = "table_loc";
    public static final String IDD = "ID";
    public static final String ADDR = "ADDRESS";
    public static final String LATT = "LAT";
    public static final String LNGG = "LNG";

    public DataBaseClass(@Nullable Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT , ADDRESS VARCHAR(100) , LAT VARCHAR(100) , LNG VARCHAR(100))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String address , String lat , String lng , String visted){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ADDR , address );
        contentValues.put(LATT , lat);
        contentValues.put(LNGG , lng);




        long result = db.insert(TABLE_NAME , null , contentValues);

        if(result == -1)
            return false;
        else
            return true;

    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        return res;



    }
    public Integer deleteData(String name){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME , ADDR + "=?" , new String[]{name} );


    }
    public boolean updateData(String address , String lat , String lng  , String newAdress , String visited){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ADDR , newAdress );
        contentValues.put(LATT , lat);
        contentValues.put(LNGG , lng);


        db.update(TABLE_NAME , contentValues , ADDR + "=?" , new String[]{address});
        return true;


    }
}
