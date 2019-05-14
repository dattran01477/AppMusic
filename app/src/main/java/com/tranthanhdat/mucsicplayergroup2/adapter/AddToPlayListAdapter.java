package com.tranthanhdat.mucsicplayergroup2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.database.DatabaseSqlite;
import com.tranthanhdat.mucsicplayergroup2.model.PlayList;
import com.tranthanhdat.mucsicplayergroup2.view.MainActivity;

import java.util.List;

public class AddToPlayListAdapter extends RecyclerView.Adapter<AddToPlayListAdapter.AddToPlaylistViewHolder> {

    private static final String TAG = "AddToPlaylistAdapter";
    private List<PlayList> mPlaylists;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int idSongPick;

    public AddToPlayListAdapter(Context context, List<PlayList> datas, int idSongPick) {
        mContext = context;
        mPlaylists = datas;
        mLayoutInflater = LayoutInflater.from(context);
        this.idSongPick=idSongPick;
    }

    @Override
    public AddToPlayListAdapter.AddToPlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate view from row_item_song.xml
        View itemView = mLayoutInflater.inflate(R.layout.row_item_addtoplaylist, parent, false);
        return new AddToPlayListAdapter.AddToPlaylistViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AddToPlayListAdapter.AddToPlaylistViewHolder holder, int position) {
        //get song in mSong via position
        PlayList playList = mPlaylists.get(position);

        //bind data to viewholder
        /*        holder.tvCode.setText(song.getCode());*/
        holder.tvTitle.setText(playList.getName());
        holder.rltLayout.setTag(playList.getIdPlayList());
    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }

    class AddToPlaylistViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private RelativeLayout rltLayout;
        DatabaseSqlite db=new DatabaseSqlite(mContext);

        public AddToPlaylistViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_name_addtoplaylist);
            rltLayout=(RelativeLayout) itemView.findViewById(R.id.card_row_addtoplaylist);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.addSongPlayList(idSongPick,Integer.parseInt(rltLayout.getTag().toString()));
                    Toast.makeText(mContext,"Added to playlist "+tvTitle.getText(),Toast.LENGTH_SHORT).show();
                    Log.e("addtoplaylist",idSongPick+"");
                }
            });
        }
    }
}
