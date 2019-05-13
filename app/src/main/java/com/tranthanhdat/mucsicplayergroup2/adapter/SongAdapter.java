package com.tranthanhdat.mucsicplayergroup2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private static final String TAG = "SongAdapter";
    private List<Song> mSongs;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public SongAdapter(Context context, List<Song> datas) {
        mContext = context;
        mSongs = datas;
        mLayoutInflater = LayoutInflater.from(context);
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

    class SongViewHolder extends RecyclerView.ViewHolder {
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
        }
    }
}
