package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="User.db";
    public static final String TABLE_NAME="User_table";
    public static final String Col_1="ID";
    public static final String Col_2="Name";
    public static final String Col_3="Email";
    public static final String Col_4="Phone";

    public Database(Context context) {
        super(context, DATABASE_NAME,null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("create table " + TABLE_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT,Email TEXT,Phone INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String name, String email, String phone){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(Col_2,name);
        contentValues.put(Col_3,email);
        contentValues.put(Col_4,phone);
        db.insert(TABLE_NAME,null,contentValues);
            return true;

    }
    public Cursor getData(String email){
        email='"'+email+'"';
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res= db.rawQuery("select * from "+TABLE_NAME+" where Email="+email,null);
        return res;
    }
}
