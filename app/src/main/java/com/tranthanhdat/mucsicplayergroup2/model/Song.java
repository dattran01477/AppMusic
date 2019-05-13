package com.tranthanhdat.mucsicplayergroup2.model;

public class Song {

    private String mTitle;
    private String mLyric;
    private String mArtist;
    private String mUrl;
    private String mImageUrl;
    private int duration;
    private long id;


    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }




    public Song() {

    }

    public Song(String code, String title, String lyric, String artist) {

        this.mTitle = title;
        this.mLyric = lyric;
        this.mArtist = artist;
    }



    public String getTitle() {
        return mTitle;
    }

    public String getLyric() {
        return mLyric;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setLyric(String lyric) {
        this.mLyric = lyric;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
