package com.tranthanhdat.mucsicplayergroup2.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.view.ListSongFragment;

import java.net.URI;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder>  {
    private static final String TAG = "SongAdapter";
    private List<Song> mSongs;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ListSongFragment listSongFragment;
    private int idSongPick;

    public SongAdapter(Context context, List<Song> datas, ListSongFragment playListFragment) {
        mContext = context;
        mSongs = datas;
        mLayoutInflater = LayoutInflater.from(context);
        this.listSongFragment=playListFragment;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate view from row_item_song.xml
        View itemView = mLayoutInflater.inflate(R.layout.row_item_card, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        //get song in mSong via position
        Song song = mSongs.get(position);

        //bind data to viewholder
/*        holder.tvCode.setText(song.getCode());*/
     Glide.with(listSongFragment).load(Uri.parse(song.getmImageUrl())).placeholder(R.drawable.image_album).error(R.drawable.image_album).centerCrop().into(holder.tvImage);

       /* holder.tvImage.setImageResource(R.drawable.image_album);*/
    /*    holder.tvImage.setImageURI(null);
        holder.tvImage.setImageURI(song.getmImageUrl());*/
        holder.tvTitle.setText(song.getTitle());
        holder.rltLayout.setTag(position);
        holder.tvLyric.setText(song.getId()+"");
        holder.tvArtist.setText(song.getArtist());

    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private ImageView tvImage;
        private TextView tvTitle;
        private TextView tvLyric;
        private TextView tvArtist;
        private RelativeLayout rltLayout;

        public SongViewHolder(View itemView) {
            super(itemView);
            tvImage = (ImageView) itemView.findViewById(R.id.image_song);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvLyric = (TextView) itemView.findViewById(R.id.tv_lyric);
            tvArtist = (TextView) itemView.findViewById(R.id.tv_artist);
            rltLayout=(RelativeLayout) itemView.findViewById(R.id.card_row);
            itemView.setOnCreateContextMenuListener(this);
            /*itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return true;
                }
            });*/
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem addPlayList = contextMenu.add(contextMenu.NONE, 1, 1, "Add to play list");
            MenuItem Delete = contextMenu.add(contextMenu.NONE, 2, 2, "Delete");
            MenuItem Edit = contextMenu.add(contextMenu.NONE, 3, 3, "Edit");
            addPlayList.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
            Edit.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        listSongFragment.showDialogPlaylist(Integer.parseInt(tvLyric.getText().toString()));
                        Log.e("menuItem",tvLyric.getText()+"");

                    case 2:
                        //Do stuff
                        break;
                    case 3:
                        listSongFragment.showDialogEditPlaylist(Integer.parseInt(tvLyric.getText().toString()));
                        break;
                }
                return true;
            }
        };
    }

}
