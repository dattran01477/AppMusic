package com.tranthanhdat.mucsicplayergroup2.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.adapter.SongAdapter;
import com.tranthanhdat.mucsicplayergroup2.adapter.SongForPlayListAdapter;
import com.tranthanhdat.mucsicplayergroup2.adapter.SongOnlineAdapter;
import com.tranthanhdat.mucsicplayergroup2.database.DatabaseSqlite;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.model.TrackSongOnline;
import com.tranthanhdat.mucsicplayergroup2.service.PlayerService;

import java.util.ArrayList;


public class ListOnlineFragment extends Fragment {

    private RecyclerView rvSongs;
    private SongOnlineAdapter mSongOnlineAdapter;
    private ArrayList<TrackSongOnline> mSongs;
    private FloatingActionButton fAddBtn;
    private DatabaseSqlite db;

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainActivity.UPDATE_UI_LISTSONGONLINE)){
                loadRv();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list_online, container, false);

        db=new DatabaseSqlite(getContext());

        // Inflate the layout for this fragment
        rvSongs = (RecyclerView)view.findViewById(R.id.rv_songs_online);


        loadRv();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.UPDATE_UI_LISTSONGONLINE);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void setmSongs(ArrayList<TrackSongOnline> mSongs) {
        this.mSongs = mSongs;
    }

    private void loadRv(){
        this.mSongs=db.getAllTrackSong();
        //create song data
        mSongOnlineAdapter = new SongOnlineAdapter(super.getContext(),  mSongs,this);

        rvSongs.setAdapter(mSongOnlineAdapter);

        //RecyclerView scroll vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(super.getContext(), LinearLayoutManager.VERTICAL, false);
        rvSongs.setLayoutManager(linearLayoutManager);
    }

    public void playOnline(String url){
        Intent playOnline=new Intent(PlayerService.PLAYONLINE_ACTION);
        playOnline.putExtra("url",url);
        getContext().sendBroadcast(playOnline);
    }


}
