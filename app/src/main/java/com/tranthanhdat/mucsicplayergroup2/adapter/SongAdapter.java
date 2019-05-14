package com.tranthanhdat.mucsicplayergroup2.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.view.ListSongFragment;
import com.tranthanhdat.mucsicplayergroup2.view.MainActivity;
import com.tranthanhdat.mucsicplayergroup2.view.PlayListFragment;

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
        holder.tvImage.setImageResource(R.drawable.image_album);
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
            addPlayList.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
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
                }
                return true;
            }
        };
    }

}
