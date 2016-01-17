package com.example.hidesecret;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/8/8.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "jc_hide";
    private static final int DB_VERSION = 1;
    private static final String TABLE_INFO = "table_info";


    private static SQLiteDatabase mDb;
    private static DatabaseHelper mHelper;

    public static SQLiteDatabase getInstance(Context context){
        if(mDb == null){
            mDb = getHelper(context).getWritableDatabase();
        }
        return mDb;
    }

    private static DatabaseHelper getHelper(Context context){
        if(mHelper == null){
            mHelper = new DatabaseHelper(context);
        }
        return mHelper;
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "
                + TABLE_INFO
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " realname varchar(100), "
                + "secretname varchar(100))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if (newVersion>oldVersion){
            onCreate(db);
        }

    }
}
