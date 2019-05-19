package com.tranthanhdat.mucsicplayergroup2.adapter;

import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.model.PlayList;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.view.PlayListFragment;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlaylistViewHolder> {

    private static final String TAG = "PlaylistAdapter";
    private List<PlayList> mPlaylists;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private PlayListFragment playListFragment;

    public PlayListAdapter(Context context, List<PlayList> datas, PlayListFragment playListFragment) {
        mContext = context;
        mPlaylists = datas;
        mLayoutInflater = LayoutInflater.from(context);
        this.playListFragment=playListFragment;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate view from row_item_song.xml
        View itemView = mLayoutInflater.inflate(R.layout.row_item_playlistsong, parent, false);
        return new PlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlayListAdapter.PlaylistViewHolder holder, int position) {
        //get song in mSong via position
        PlayList playList = mPlaylists.get(position);

        //bind data to viewholder
        /*        holder.tvCode.setText(song.getCode());*/
        holder.tvImage.setImageResource(R.drawable.playlistimge);
        holder.tvTitle.setText(playList.getName());
        holder.rltLayout.setTag(playList.getIdPlayList());
        holder.tvNumberSong.setText(playList.getNumberSong()+" "+"Bài");
    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private ImageView tvImage;
        private TextView tvTitle;
        private TextView tvNumberSong;
        private RelativeLayout rltLayout;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            tvImage = (ImageView) itemView.findViewById(R.id.image_playlist);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_name_playlist);
            tvNumberSong = (TextView) itemView.findViewById(R.id.tv_numbersong_playlist);
            rltLayout=(RelativeLayout) itemView.findViewById(R.id.card_row_playlist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playListFragment.showSongForPlayList(Integer.parseInt(rltLayout.getTag().toString()));
                }
            });
        }
    }
}
