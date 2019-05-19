package com.tranthanhdat.mucsicplayergroup2.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.view.ListSongForPlaylistFragment;
import com.tranthanhdat.mucsicplayergroup2.view.ListSongFragment;

import java.util.List;

public class SongForPlayListAdapter extends RecyclerView.Adapter<SongForPlayListAdapter.SongViewHolder>  {
    private static final String TAG = "SongAdapter";
    private List<Song> mSongs;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ListSongForPlaylistFragment listSongFragment;
    private int idSongPick;

    public SongForPlayListAdapter(Context context, List<Song> datas, ListSongForPlaylistFragment playListFragment) {
        mContext = context;
        mSongs = datas;
        mLayoutInflater = LayoutInflater.from(context);
        this.listSongFragment=playListFragment;
    }

    @Override
    public SongForPlayListAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate view from row_item_song.xml
        View itemView = mLayoutInflater.inflate(R.layout.row_item_song_for_playlist, parent, false);
        return new SongForPlayListAdapter.SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongForPlayListAdapter.SongViewHolder holder, int position) {
        //get song in mSong via position
        Song song = mSongs.get(position);

        //bind data to viewholder
        /*        holder.tvCode.setText(song.getCode());*/
        Glide.with(listSongFragment).load(Uri.parse(song.getmImageUrl())).placeholder(R.drawable.image_album).error(R.drawable.image_album).centerCrop().into(holder.tvImage);
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

    class SongViewHolder extends RecyclerView.ViewHolder{
        private ImageView tvImage;
        private TextView tvTitle;
        private TextView tvLyric;
        private TextView tvArtist;
        private RelativeLayout rltLayout;

        public SongViewHolder(View itemView) {
            super(itemView);
            tvImage = (ImageView) itemView.findViewById(R.id.image_song_songforplaylist);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title_songforplaylist);
            tvLyric = (TextView) itemView.findViewById(R.id.tv_lyric_songforplaylist);
            tvArtist = (TextView) itemView.findViewById(R.id.tv_artist_songforplaylist);
            rltLayout=(RelativeLayout) itemView.findViewById(R.id.card_row_songforplaylist);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listSongFragment.playMusic(Integer.parseInt(rltLayout.getTag().toString()));
                }
            });
        }

    }

}
