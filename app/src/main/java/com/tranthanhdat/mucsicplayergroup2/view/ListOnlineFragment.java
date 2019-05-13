package com.tranthanhdat.mucsicplayergroup2.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.adapter.SongAdapter;
import com.tranthanhdat.mucsicplayergroup2.adapter.SongOnlineAdapter;
import com.tranthanhdat.mucsicplayergroup2.model.Song;

import java.util.ArrayList;


public class ListOnlineFragment extends Fragment {

    private RecyclerView rvSongs;
    private SongOnlineAdapter mSongOnlineAdapter;
    private ArrayList<Song> mSongs;
    private FloatingActionButton fAddBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list_online, container, false);

        /*this.mSongs=(ArrayList<Song>)getArguments().getSerializable("songOnlineList");

        // Inflate the layout for this fragment
        rvSongs = (RecyclerView)view.findViewById(R.id.rv_songs);

        //create song data
        mSongOnlineAdapter = new SongOnlineAdapter(super.getContext(),  mSongs);
        rvSongs.setAdapter(mSongOnlineAdapter);

        //RecyclerView scroll vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(super.getContext(), LinearLayoutManager.VERTICAL, false);
        rvSongs.setLayoutManager(linearLayoutManager);*/


        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
