package com.tranthanhdat.mucsicplayergroup2.view;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.jgabrielfreitas.core.BlurImageView;
import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.database.DatabaseSqlite;
import com.tranthanhdat.mucsicplayergroup2.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongFoPlayListActivity extends AppCompatActivity {

    private FrameLayout nMainFrame;
    private BlurImageView blurImageView;
    //Fragment
    private PlayerFragment playerSongFragment;
    private ListSongForPlaylistFragment listSongFragment;
    private DatabaseSqlite db;
    private ArrayList<Song> mSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_fo_play_list);
        // Intent truyền sang.
        Intent intent = this.getIntent();
        mSongs= (ArrayList<Song>) intent.getSerializableExtra("PlayList");
        setComponents();
        setFragment(listSongFragment);
    }

    //Set fragment vao main_frame cua main activity
    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.song_for_playlist_frame, fragment);
        fragmentTransaction.commit();
    }

    private void setComponents() {
        Bundle bundle = new Bundle();
// set Fragmentclass Arguments
       /* bundle.putSerializable("songList", songList);*/

        nMainFrame = findViewById(R.id.main_frame);

        listSongFragment = new ListSongForPlaylistFragment();
        listSongFragment.setmSongs(mSongs);
        listSongFragment.onAttach(this);

        //get componet to activity .xml
        blurImageView = findViewById(R.id.song_for_playlist_background_blur);
        //blurImage
        blurImageView.setBlur(5);
    }
}
