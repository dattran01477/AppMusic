package com.tranthanhdat.mucsicplayergroup2.model;

import java.util.ArrayList;
import java.util.List;

public class PlayList {
    private List<Song> songs;
    private String name;

    public PlayList() {
        songs = new ArrayList<>();
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
