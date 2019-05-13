package com.tranthanhdat.mucsicplayergroup2.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseSqlite extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";


    // Phiên bản
    private static final int DATABASE_VERSION = 1;


    // Tên cơ sở dữ liệu.
    private static final String DATABASE_NAME = "Music_Manager";

    public DatabaseSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Truy van khong tra ket qua:Create,Insert,Updadte,Delete
    public void QueryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    //Truy van co tra ket qua:Select
    public Cursor GetData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Script tạo bảng.
        String script1 = "CREATE TABLE IF NOT EXISTS Song(" +
                "idSong INTEGER PRIMARY KEY," +
                "nameSong VARCHAR," +
                "idSinger INTEGER," +
                "idPlayList INTEGER," +
                "idAlbum INTEGER," +
                "composer VARCHAR," +
                "bitrate VARCHAR," +
                "imageSong VARCHAR," +
                "filePath)";
        String scripCreatePlaylist="CREATE TABLE IF NOT EXISTS PlayList(" +
                "idPlayList INTEGER PRIMARY KEY," +
                "namePlayList VARCHAR," +
                "imagePlayList)";
        // Chạy lệnh tạo bảng.
        db.execSQL(script1);
        db.execSQL(scripCreatePlaylist);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

