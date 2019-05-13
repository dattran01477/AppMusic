package com.tranthanhdat.mucsicplayergroup2.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.adapter.SongAdapter;
import com.tranthanhdat.mucsicplayergroup2.model.Song;

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

        this.mSongs=(ArrayList<Song>)getArguments().getSerializable("songList");

        // Inflate the layout for this fragment
        rvSongs = (RecyclerView)view.findViewById(R.id.rv_songs);

        //create song data
        mSongAdapter = new SongAdapter(super.getContext(),  mSongs);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_itemplaylist, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

}
