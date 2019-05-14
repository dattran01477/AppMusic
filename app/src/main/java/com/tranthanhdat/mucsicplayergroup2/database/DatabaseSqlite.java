package com.tranthanhdat.mucsicplayergroup2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.tranthanhdat.mucsicplayergroup2.model.PlayList;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSqlite extends SQLiteOpenHelper {

    private static final String TAG = "SQLite";


    // Phiên bản
    private static final int DATABASE_VERSION = 1;


    // Tên cơ sở dữ liệu.
    private static final String DATABASE_NAME = "Music_Manager";

    // Tên bảng: Note.
    private static final String TABLE_SONG = "SONG";
    // Tên bảng: Note.
    private static final String TABLE_PLAYLIST = "PLAYLIST";
    // Tên bảng: Note.
    private static final String TABLE_PLAYLISTSONG = "SONG_PLAYLIST";

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
        String script1 = "CREATE TABLE IF NOT EXISTS SONG(" +
                "idSong INTEGER PRIMARY KEY," +
                "nameSong VARCHAR," +
                "artist VARCHAR," +
                "filePath)";
        String scripCreatePlaylist="CREATE TABLE IF NOT EXISTS PLAYLIST(" +
                "idPlayList INTEGER PRIMARY KEY," +
                "namePlayList VARCHAR," +
                "imagePlayList)";
        String scripCreatePlaylistSong="CREATE TABLE IF NOT EXISTS SONG_PLAYLIST(" +
                "idSong INTEGER," +
                "idPlayList INTEGER,"+
                "PRIMARY KEY (idSong, idPlayList))";


        // Chạy lệnh tạo bảng.
        db.execSQL(script1);
        db.execSQL(scripCreatePlaylist);
        db.execSQL(scripCreatePlaylistSong);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Hủy (drop) bảng cũ nếu nó đã tồn tại.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTSONG);
        // Và tạo lại.
        onCreate(db);
    }

    public void addSong(Song song) {
        Log.i(TAG, "MyDatabaseHelper.addNote ... " + song.getTitle());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idSong", song.getId());
        values.put("nameSong", song.getTitle());
        values.put("artist",song.getArtist());



        // Trèn một dòng dữ liệu vào bảng.
        db.insert(TABLE_SONG, null, values);


        // Đóng kết nối database.
        db.close();
    }
    public void addPlayList(PlayList playList){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idPlayList", playList.getIdPlayList());
        values.put("namePlayList", playList.getName());

        // Trèn một dòng dữ liệu vào bảng.
        db.insert(TABLE_PLAYLIST, null, values);

        // Đóng kết nối database.
        db.close();
    }

    public void addSongPlayList(int idSong,int idPlayList){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idSong", idSong);
        values.put("idPlayList", idPlayList);

        // Trèn một dòng dữ liệu vào bảng.
        db.insert(TABLE_PLAYLISTSONG, null, values);

        // Đóng kết nối database.
        db.close();
    }

    public void createDefaultSongsIfNeed(List<Song> songs)  {
        int count = this.getSongsCount();
        if(count ==0 ) {
            for (Song song: songs) {
                this.addSong(song);
            }
        }
    }

    public Song getSong(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

     /*   "idSong INTEGER PRIMARY KEY," +
                "nameSong VARCHAR," +
                "artist VARCHAR," +
                "filePath)";*/

        Cursor cursor = db.query(TABLE_SONG, new String[] { "idSong",
                        "nameSong", "artist" }, "idSong" + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Song song = new Song(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return song
        return song;
    }

    public ArrayList<Song> getAllSong() {
        Log.i(TAG, "MyDatabaseHelper.getAllNotes ... " );

        ArrayList<Song> songs = new ArrayList<Song>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SONG;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setId(Integer.parseInt(cursor.getString(0)));
                song.setTitle(cursor.getString(1));
                song.setArtist(cursor.getString(2));

                // Thêm vào danh sách.
                songs.add(song);
            } while (cursor.moveToNext());
        }

        // return note list
        return songs;
    }
    public ArrayList<PlayList> getAllPlaylist() {
        Log.i(TAG, "MyDatabaseHelper.getAllNotes ... " );

        ArrayList<PlayList> playLists = new ArrayList<PlayList>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PLAYLIST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // Duyệt trên con trỏ, và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                PlayList playList = new PlayList();
                playList.setIdPlayList(Integer.parseInt(cursor.getString(0)));
                playList.setName(cursor.getString(1));
                playList.setNumberSong(getCountSongForPlaylist(playList.getIdPlayList()));

                // Thêm vào danh sách.
                playLists.add(playList);
            } while (cursor.moveToNext());
        }

        // return note list
        return playLists;
    }

    private int getCountSongForPlaylist(int idPlayList) {
        int count=0;

        String countQuery = "SELECT" +
                " count(*)" +
                " FROM " +
                TABLE_PLAYLISTSONG +
                " WHERE" +
                " idPlayList="+idPlayList;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor.moveToFirst()) {
            do {
                count=(Integer.parseInt(cursor.getString(0)));
            } while (cursor.moveToNext());
        }

        return count;
    }

    private ArrayList<PlayList> getSongForPlayList(int idPlayList){
        ArrayList<PlayList> playLists=new ArrayList<>();

        return playLists;

    }

    public int getSongsCount() {
        Log.i(TAG, "MyDatabaseHelper.getNotesCount ... " );

        String countQuery = "SELECT  * FROM " + TABLE_SONG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }
}

