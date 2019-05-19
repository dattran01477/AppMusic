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
import com.tranthanhdat.mucsicplayergroup2.adapter.SongForPlayListAdapter;
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
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainActivity.UPDATE_UI_LISTSONG)){
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

        View view=inflater.inflate(R.layout.fragment_list_song, container, false);
        // Inflate the layout for this fragment
        rvSongs = (RecyclerView)view.findViewById(R.id.rv_songs);
       loadRv();
        return view;
    }

    public void loadRv(){
        DatabaseSqlite db=new DatabaseSqlite(getContext());

        mSongs=db.getAllSong();
        //create song data
        mSongAdapter = new SongAdapter(super.getContext(),  mSongs,this);

        rvSongs.setAdapter(mSongAdapter);

        //RecyclerView scroll vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(super.getContext(), LinearLayoutManager.VERTICAL, false);
        rvSongs.setLayoutManager(linearLayoutManager);
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
        View mView=getLayoutInflater().inflate(R.layout.menu_context_playlist,null);
        rvPlaylists = mView.findViewById(R.id.rv_playlists__menucontext_playlist);

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

    public void showDialogEditPlaylist(int idSongPick){

        RecyclerView rvPlaylists;
        AddToPlayListAdapter mAddToPlaylistAdapter;
        Song mSong=new Song();
        final EditText nameEtUpdate;
        final DatabaseSqlite db = new DatabaseSqlite(getContext());
        Button btnUpdateName;


        mSong=db.getSong(idSongPick);
        AlertDialog.Builder mbuider=new AlertDialog.Builder(getContext());
        View mView=getLayoutInflater().inflate(R.layout.menu_context_editsong,null);
        nameEtUpdate=mView.findViewById(R.id.et_name_songedit_menucontext);
        btnUpdateName=mView.findViewById(R.id.btn_update_songedit_menucontext);

        nameEtUpdate.setText(mSong.getTitle().toString());

        final Song finalMSong = mSong;
        btnUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Song songTmp= finalMSong;
                songTmp.setTitle(nameEtUpdate.getText().toString());
                try {
                    db.updateSong(songTmp);
                    Intent updateUiPlaylist=new Intent(MainActivity.UPDATE_UI_LISTSONG);
                    getActivity().sendBroadcast(updateUiPlaylist);
                    Toast.makeText(getContext(),"Successful!",Toast.LENGTH_SHORT).show();
                }catch (SQLException e){
                    Toast.makeText(getContext(),"Failure!",Toast.LENGTH_SHORT).show();
                }

            }
        });
        mbuider.setView(mView);
        AlertDialog dialog=mbuider.create();



        dialog.show();
    }

    public void setmSongs(ArrayList<Song> mSongs) {
        this.mSongs = mSongs;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.UPDATE_UI_LISTSONG);
        getActivity().registerReceiver(receiver, filter);
    }
}
