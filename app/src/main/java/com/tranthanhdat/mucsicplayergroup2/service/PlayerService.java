package com.tranthanhdat.mucsicplayergroup2.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.app.Notification;
import android.widget.RemoteViews;

import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.view.PlayerWidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;

public class PlayerService  extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public static final  String CHANNEL_ID="Mucsic";
    public static  final String ACTION_NOTIFICATION_BUTTON_CLICK="btnClick";
    public static  final String ACTION_UPDATE_UI="update";
    public static  final String EXTRA_BUTTON_CLICKED="extraBtnClick";
    protected int smallNotificationIconId = R.mipmap.ic_launcher;
    private final  IBinder musicBind=new MusicBinder();

    private static final String ACTION_SET_PLAYLIST = "mediaplayer.patryk.mediaplayerpatryk.action.SET_PLAYLIST";
    private static final String ACTION_PLAY = "mediaplayer.patryk.mediaplayerpatryk.action.ACTION_PLAY";
    private static final String ACTION_PAUSE = "mediaplayer.patryk.mediaplayerpatryk.action.ACTION_PAUSE";
    private static final String ACTION_NEXT = "mediaplayer.patryk.mediaplayerpatryk.action.ACTION_NEXT";
    private static final String ACTION_PREVIOUS = "mediaplayer.patryk.mediaplayerpatryk.action.ACTION_PREVIOUS";
    private static final String ACTION_SEND_INFO = "mediaplayer.patryk.mediaplayerpatryk.action.ACTION_SEND_INFO";
    private static final String ACTION_SEEK_TO = "mediaplayer.patryk.mediaplayerpatryk.action.ACTION_SEEK_TO";

    public final static String LOADING_ACTION = "LOADING_ACTION";
    public final static String LOADED_ACTION = "LOADED_ACTION";
    public final static String GUI_UPDATE_ACTION = "GUI_UPDATE_ACTION";
    public final static String COMPLETE_ACTION = "COMPLETE_ACTION";
    public final static String PLAY_ACTION = "PLAY_ACTION";
    public final static String PAUSE_ACTION = "PAUSE_ACTION";
    public final static String NEXT_ACTION = "NEXT_ACTION";
    public final static String PREVIOUS_ACTION = "PREVIOUS_ACTION";
    public final static String DELETE_ACTION = "DELETE_ACTION";
    public final static String MINUS_TIME_ACTION = "MINUS_TIME_ACTION";
    public final static String PLUS_TIME_ACTION = "PLUS_TIME_ACTION";
    public final static String PLAYONLINE_ACTION = "PLAYONLINE_ACTION";

    public final static String TOTAL_TIME_VALUE_EXTRA = "TOTAL_TIME_VALUE_EXTRA";
    public final static String ACTUAL_TIME_VALUE_EXTRA = "ACTUAL_TIME_VALUE_EXTRA";
    public final static String COVER_URL_EXTRA = "COVER_URL_EXTRA";
    public final static String TITLE_EXTRA = "TITLE_EXTRA";
    public final static String ARTIST_EXTRA = "ARTIST_EXTRA";
    public final static String URL_EXTRA = "URL_EXTRA";
    public final static String SONG_NUM_EXTRA = "SONG_NUM_EXTRA";
    public final static String PLAYLIST_NAME_NUM_EXTRA = "PLAYLIST_NAME_NUM_EXTRA";
    public final static String MEDIAPLAY_SESSIONID = "MEDIAPLAY_SESSIONID";

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    private int songPosnPre;
    private String urlMusic;
    private Thread updateThread;

    // varible check
    private boolean isPlaying=false;
    private boolean isPlayingOnline=false;
    private boolean isSufle=false;
    private boolean isRepeat=false;
    private boolean isUpdatingThread;
    private boolean isPrepared;



    private static final int NOTIFY_ID=1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        isPlaying=true;
        mediaPlayer.start();
        sendInfoBroadcast();
        startUiUpdateThread();

        Intent intentPause=new Intent(PLAY_ACTION);
        sendBroadcast(intentPause);
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction() != null ? intent.getAction() : "";

        try {
            if (action.equals(PlayerWidget.ACTION_PLAY_PAUSE))
            {
                if (isPlaying)
                    pauseMusic();
                else{
                    playMusic();
                }
            }
            else if (action.equals(PlayerWidget.ACTION_STOP))
            {
                stopMusic();
            }
            else if (action.equals(PlayerWidget.ACTION_NEXT))
            {
                nextTo();
                playMusic();
            }
            else if (action.equals(PlayerWidget.ACTION_PREVIOUS))
            {
                preBack();
                playMusic();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, START_STICKY, 1);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        songPosn=0;
        player=new MediaPlayer();
        //init music media player
        initMusicPlayer();
    }

    //truyen thong tin bang broadcast
    private void sendInfoBroadcast() {
        if (player == null || songs.get(songPosn) == null)
            return;
        if(isPlayingOnline){

        }
        else {
            Intent updateIntent = new Intent();
            updateIntent.setAction(GUI_UPDATE_ACTION);
            updateIntent.putExtra(ARTIST_EXTRA, songs.get(songPosn).getArtist());
            updateIntent.putExtra(TITLE_EXTRA, songs.get(songPosn).getTitle());
            updateIntent.putExtra(ACTUAL_TIME_VALUE_EXTRA, player.getCurrentPosition());
            updateIntent.putExtra(TOTAL_TIME_VALUE_EXTRA, player.getDuration());
            updateIntent.putExtra(SONG_NUM_EXTRA, songPosn);
            updateIntent.putExtra(MEDIAPLAY_SESSIONID, player.getAudioSessionId());
            sendBroadcast(updateIntent);


        }
    }

    //Khoi tao mediaplayer
    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }

    //set list nhac
    public void setPlayList(ArrayList<Song> theSongs){
        this.songs=theSongs;
        //dang ki broadcastReceiver de nhan du lieu tu broadcast
        registerReceiver(receiver, new IntentFilter(
                PlayerService.ACTION_NOTIFICATION_BUTTON_CLICK));


    }
    //Binder
    public class MusicBinder extends Binder{
        public PlayerService getService(){
            return PlayerService.this;
        }
    }

    public void playMusic(){
        player.reset();
            if(isPlayingOnline){//kiem tra xem co phai phat online hay khong, neu co thi dung STREAM_MUSIC
                isPlayingOnline=false;
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(this, Uri.parse(this.urlMusic));
                } catch (IOException e) {

                }
            }
            else {
                //getsong
                Song playSong=songs.get(songPosn);
                Log.e("MusicService",songPosn+"");
                //get id
                long currSong=playSong.getId();
                //setUri
                Uri trackUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,currSong);

                try {
                    player.setDataSource(getApplicationContext(),trackUri);
                } catch (Exception e) {
                    Log.e("Music Service","error setting data source",e);
                }
            }
            player.prepareAsync();
    }


    public void pauseMusic(){
        player.pause();
        isPlaying=false;
    }

    public void contrinuteMusic(){
        player.start();
        isPlaying=true;
        startUiUpdateThread();
    }

    public void stopMusic(){

    }

    public void seekTo(int value){
        player.seekTo(value);
    }

    public void nextTo(){
        if(isRepeat){
            this.songPosn=this.songPosn;
        }
        else if(isSufle){
            Random rand = new Random();
            //save vi tri bai hat truoc do
            songPosnPre=this.songPosn;
            // sinh ngau nhien bai hat bang ham random
            this.songPosn = rand.nextInt(songs.size());
        }
        else {
            int tmp=this.songPosn+1;
            if(tmp>songs.size()-1){
                this.songPosn=0;
            }
            else {
                this.songPosn=tmp;
            }
        }
    }

    public void preBack(){
        if(isSufle){
            this.songPosn=this.songPosnPre;
        }
        else{
            int tmp=this.songPosn-1;
            if(tmp<0){
                this.songPosn=songs.size()-1;
            }
            else {
                this.songPosn=tmp;
            }
        }
    }

    public void sufle(){
        isSufle=!isSufle;
    }

    public void repeat(){
        isRepeat=!isRepeat;
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    //Set bai hat de bat dau choi
    public void setSong(int songIndex){
        songPosn=songIndex;
        playMusic();
    }

    public int getSongPosn(){
        return songPosn;
    }

    public MediaPlayer getPlayer(){
        return player;
    }

    //set url de de choi online nhac
    public void setUrl(String url){
        isPlayingOnline=true;
        this.urlMusic=url;
        playMusic();
    }

    public String getSongNameCurrent(){
        return songs.get(songPosn).getTitle();
    }

    public long getTimeDuration(){
        return player.getDuration();
    }

    public String getImageUrlSong(){return songs.get(songPosn).getmImageUrl();}


    //xu ly khi nhac duoc du lieu tu broadcast
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra(EXTRA_BUTTON_CLICKED, -1);
            switch (id) {
                case R.id.btnPrevious:
                    preBack();
                    playMusic();
                    Intent intentPrevious=new Intent(PREVIOUS_ACTION);
                    sendBroadcast(intentPrevious);
                    break;
                case R.id.btnNext:
                    nextTo();
                    playMusic();
                    Intent intentNext=new Intent(NEXT_ACTION);
                    sendBroadcast(intentNext);
                    break;
                case R.id.btnPause:
                    if (isPlaying) {
                        pauseMusic();
                        Intent intentPause=new Intent(PAUSE_ACTION);
                        sendBroadcast(intentPause);
                    } else {
                        contrinuteMusic();
                        Intent intentPause=new Intent(PLAY_ACTION);
                        sendBroadcast(intentPause);
                    }
                    break;
            }
        }
    };

    //update info thoi gian hien tai cua bai nhac
    private void startUiUpdateThread() {
        isUpdatingThread = true;

        if (updateThread == null) {
            updateThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Intent guiUpdateIntent = new Intent();
                    guiUpdateIntent.setAction(GUI_UPDATE_ACTION);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    while (isPlaying) {
                            Log.e("Duration",player.getCurrentPosition()+"");
                            guiUpdateIntent.putExtra(ACTUAL_TIME_VALUE_EXTRA, player.getCurrentPosition());
                            sendBroadcast(guiUpdateIntent);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                    }
                    updateThread = null;
                }
            });

            updateThread.start();
        }
    }

    private void setImageRourceNotifiCation(){
        RemoteViews notificationLayout =
                new RemoteViews(getPackageName(), R.layout.musicnotification);
        if(isPlaying){
            notificationLayout.setImageViewResource(R.id.btnPause,R.drawable.ic_play);
        }else {
            notificationLayout.setImageViewResource(R.id.btnPause,R.drawable.ic_pause);
        }
    }
}
