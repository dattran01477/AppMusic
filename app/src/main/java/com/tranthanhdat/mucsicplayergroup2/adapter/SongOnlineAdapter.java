package com.tranthanhdat.mucsicplayergroup2.adapter;

import android.content.Context;
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

import java.util.List;

public class SongOnlineAdapter extends RecyclerView.Adapter<SongOnlineAdapter.SongViewHolder>{

    private static final String TAG = "SongAdapter";
    private List<Song> mSongsOnline;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public SongOnlineAdapter(Context context, List<Song> datas) {
        mContext = context;
        mSongsOnline = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = mLayoutInflater.inflate(R.layout.row_item_songonline, parent, false);
        return new SongOnlineAdapter.SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int position) {
        //get song in mSong via position
        Song song = mSongsOnline.get(position);

        //bind data to viewholder
        /*        holder.tvCode.setText(song.getCode());*/
        songViewHolder.tvTitle.setText(song.getTitle());
        songViewHolder.rltLayout.setTag(position);
        songViewHolder.tvArtist.setText(song.getArtist());
    }

    @Override
    public int getItemCount() {
        return 0;
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
        }
    }
}
