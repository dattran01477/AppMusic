package com.tranthanhdat.mucsicplayergroup2.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.adapter.PlayListAdapter;
import com.tranthanhdat.mucsicplayergroup2.database.DatabaseSqlite;
import com.tranthanhdat.mucsicplayergroup2.model.PlayList;
import com.tranthanhdat.mucsicplayergroup2.model.Song;

import java.util.ArrayList;

public class PlayListFragment extends Fragment {

    private RecyclerView rvPlaylists;
    private PlayListAdapter mPlaylistAdapter;
    private ArrayList<PlayList> mPlaylists;
    private ArrayList<Song> mSongs;
    private DatabaseSqlite db;

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainActivity.UPDATE_UI_PLAYLIST)){
                loadRv();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_playlist, container, false);

        db=new DatabaseSqlite(getContext());

        // Inflate the layout for this fragment
        rvPlaylists = (RecyclerView)view.findViewById(R.id.rv_playlists);

        loadRv();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_hold, menu);
    }

    private void loadRv(){
        this.mPlaylists=db.getAllPlaylist();
        //create song data
        mPlaylistAdapter = new PlayListAdapter(super.getContext(),  mPlaylists,this);
        rvPlaylists.setAdapter(mPlaylistAdapter);

        //RecyclerView scroll vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(super.getContext(), LinearLayoutManager.VERTICAL, false);
        rvPlaylists.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.UPDATE_UI_PLAYLIST);
        getActivity().registerReceiver(receiver, filter);
    }

    public void showSongForPlayList(int idPlayList){
        Intent intent = new Intent(getContext(),SongFoPlayListActivity.class);
        Intent intentUpdateSongList=new Intent(MainActivity.UPDATE_LISTSONGS);
        mSongs=db.getSongForPlayList(idPlayList);
        intent.putExtra("PlayList",mSongs);
        intentUpdateSongList.putExtra("idPlayList", idPlayList);
        getActivity().startActivity(intent);
        getActivity().sendBroadcast(intentUpdateSongList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
    }
}
