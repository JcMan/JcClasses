package com.example.hidesecret;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Administrator on 2015/8/12.
 */
public class SecretDao {

    private static final String TABLE_INFO = "table_info";
    private Context mContext;
    public SecretDao(Context context){
        mContext = context;
    }

    public boolean addInfo(String realName,String secretName){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        ContentValues values = createValues(realName,secretName);
        long id = db.insert(TABLE_INFO, null, values);
        return id > 0;
    }

    private ContentValues createValues(String realName, String secretName){
        ContentValues values = new ContentValues();
        values.put("realname",realName);
        values.put("secretname",secretName);
        return values;
    }

    public boolean deleteInfo(){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        return db.delete(TABLE_INFO," 1=1 ",null) > 0;
    }

    public List<Secret> getInfos(){
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select * from "+TABLE_INFO;
        return parseCursor(db.rawQuery(sql,null));
    }

    private List<Secret> parseCursor(Cursor cursor){
        List<Secret> _List =  new ArrayList<Secret>();
        while (cursor.moveToNext()){
            String realname = cursor.getString(cursor.getColumnIndex(Secret.REALNAME));
            String secretname = cursor.getString(cursor.getColumnIndex(Secret.SECRETNAME));
            Secret entry = new Secret(realname,secretname);
            _List.add(entry);
        }
        return _List;
    }

    public boolean hasData() {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select count(*) from " + TABLE_INFO;
        Cursor cursor = db.rawQuery(sql, null);
        boolean has = false;
        if(cursor.moveToFirst()){
            int count = cursor.getInt(0);
            if(count > 0) {
                has = true;
            }
        }
        cursor.close();
        return has;
    }

    public int getDataCount() {
        SQLiteDatabase db = DatabaseHelper.getInstance(mContext);
        String sql = "select count(*) from " + TABLE_INFO;
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if(cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }


}
