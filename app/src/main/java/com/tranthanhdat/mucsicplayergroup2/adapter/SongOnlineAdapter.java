package com.tranthanhdat.mucsicplayergroup2.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.model.TrackSongOnline;
import com.tranthanhdat.mucsicplayergroup2.service.PlayerService;
import com.tranthanhdat.mucsicplayergroup2.view.ListOnlineFragment;

import java.util.List;

public class SongOnlineAdapter extends RecyclerView.Adapter<SongOnlineAdapter.SongViewHolder>{

    private static final String TAG = "SongOnlineAdapter";
    private List<TrackSongOnline> mSongsOnline;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ListOnlineFragment mlistOnlineFragment;

    public SongOnlineAdapter(Context context, List<TrackSongOnline> datas,ListOnlineFragment listOnlineFragment) {
        mContext = context;
        mSongsOnline = datas;
        mLayoutInflater = LayoutInflater.from(context);
        this.mlistOnlineFragment=listOnlineFragment;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = mLayoutInflater.inflate(R.layout.row_item_songonline, parent, false);
        return new SongOnlineAdapter.SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int position) {
        //get song in mSong via position
        TrackSongOnline song = mSongsOnline.get(position);

        //bind data to viewholder
        /*        holder.tvCode.setText(song.getCode());*/
        songViewHolder.tvTitle.setText(song.getNameSong());
        songViewHolder.tvUrl.setText(song.getUrl());
        songViewHolder.rltLayout.setTag(position);
        songViewHolder.tvArtist.setText(song.getId()+"");
    }

    @Override
    public int getItemCount() {
        return this.mSongsOnline.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvUrl;
        private TextView tvArtist;
        private RelativeLayout rltLayout;

        public SongViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title_song_online);
            tvUrl = (TextView) itemView.findViewById(R.id.tv_url_song_online);
            tvArtist = (TextView) itemView.findViewById(R.id.tv_artist_song_online);
            rltLayout=(RelativeLayout) itemView.findViewById(R.id.card_row_songonline);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mlistOnlineFragment.playOnline(tvUrl.getText().toString());
                }
            });
        }

    }
}
