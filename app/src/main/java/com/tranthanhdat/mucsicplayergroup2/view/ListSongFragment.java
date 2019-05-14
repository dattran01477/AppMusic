package com.tranthanhdat.mucsicplayergroup2.view;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.adapter.AddToPlayListAdapter;
import com.tranthanhdat.mucsicplayergroup2.adapter.PlayListAdapter;
import com.tranthanhdat.mucsicplayergroup2.adapter.SongAdapter;
import com.tranthanhdat.mucsicplayergroup2.database.DatabaseSqlite;
import com.tranthanhdat.mucsicplayergroup2.model.PlayList;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

public class ListSongFragment extends Fragment {

    private RecyclerView rvSongs;
    private SongAdapter mSongAdapter;
    private ArrayList<Song> mSongs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_list_song, container, false);

        DatabaseSqlite db=new DatabaseSqlite(getContext());

        this.mSongs=db.getAllSong();

        // Inflate the layout for this fragment
        rvSongs = (RecyclerView)view.findViewById(R.id.rv_songs);

        //create song data
        mSongAdapter = new SongAdapter(super.getContext(),  mSongs,this);


        rvSongs.setAdapter(mSongAdapter);

        //RecyclerView scroll vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(super.getContext(), LinearLayoutManager.VERTICAL, false);
        rvSongs.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void showDialogPlaylist(int idSongPick){

       RecyclerView rvPlaylists;
       AddToPlayListAdapter mAddToPlaylistAdapter;
       ArrayList<PlayList> mPlaylists;
       DatabaseSqlite db = new DatabaseSqlite(getContext());


        AlertDialog.Builder mbuider=new AlertDialog.Builder(getContext());
        View mView=getLayoutInflater().inflate(R.layout.fragment_playlist,null);
        rvPlaylists = mView.findViewById(R.id.rv_playlists);


        mbuider.setView(mView);
        AlertDialog dialog=mbuider.create();

        mPlaylists=db.getAllPlaylist();
        //create song data
        mAddToPlaylistAdapter = new AddToPlayListAdapter(super.getContext(),  mPlaylists,idSongPick);
        rvPlaylists.setAdapter(mAddToPlaylistAdapter);
        //RecyclerView scroll vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(super.getContext(), LinearLayoutManager.VERTICAL, false);
        rvPlaylists.setLayoutManager(linearLayoutManager);

        dialog.show();
    }

}
