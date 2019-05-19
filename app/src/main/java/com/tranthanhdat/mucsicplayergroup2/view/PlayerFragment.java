package com.tranthanhdat.mucsicplayergroup2.view;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chibde.visualizer.CircleBarVisualizer;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.service.PlayerService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerFragment extends Fragment {

    public static final String Play_Music = "play";
    public static final String Pause_Music = "pause";
    public static final String Next_Music = "next";
    public static final String PreBack_Music = "preBack";
    public static final String Reset_Music = "reset";
    public static final String Sufle_Music = "sufle";
    public static final String Repeat_Music = "repeat";

    private CircularImageView image;
    private CircleBarVisualizer circleBarVisualizer;
    private CircleBarVisualizer circleBarVisualizerTmp;
    private FloatingActionButton playBtn;
    private ImageButton ibtnNext;
    private ImageButton ibtnPre;
    private ImageButton iiBtnSufl;
    private ImageButton iBtnRepeat;
    private ImageButton iBtnTimeSleep;
    private TextView tvTimeCurrent;
    private TextView tvTimeDuration;
    private TextView tvSongName;
    private SeekBar seekBar;
    private Bundle savedState = null;

    OnDataPass dataPasser;


    private PlayerService playerService;


    private String songName = "";
    private int idSessionMedia=0;
    private int actualTime;

    private boolean isSetMediaForCir=false;
    private boolean isRepeat=false;
    private boolean isSufle=false;
    private boolean isplaying=false;
    private boolean isBinding=false;

    CountDownTimer timer;

    private String titleSong;

    private int timeDuration;

    private Handler threadHandler = new Handler();

    private MediaPlayer mediaPlayer;
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PlayerService.GUI_UPDATE_ACTION)) {
                isBinding=true;
                if (intent.hasExtra(PlayerService.TOTAL_TIME_VALUE_EXTRA)) {
                    int totalTime = intent.getIntExtra(PlayerService.TOTAL_TIME_VALUE_EXTRA, 0);
                    if (seekBar != null){
                        seekBar.setMax(totalTime);
                    }
                        timeDuration=totalTime;
                        String stringTotalTime = millisecondsToString(totalTime);
                    if (tvTimeDuration != null)
                        tvTimeDuration.setText(stringTotalTime);
                }

                if (intent.hasExtra(PlayerService.TITLE_EXTRA)) {
                    if(tvSongName!=null){
                        String titleTmp = intent.getStringExtra(PlayerService.TITLE_EXTRA);
                        titleSong=titleTmp;
                        tvSongName.setText(titleTmp);

                    }
                }


                if (intent.hasExtra(PlayerService.MEDIAPLAY_SESSIONID)) {
                    if(circleBarVisualizer!=null){
                        if(isSetMediaForCir==false){
                                //circleBarVisualizer
                            idSessionMedia = intent.getIntExtra(PlayerService.MEDIAPLAY_SESSIONID,-1);
                            circleBarVisualizer.setPlayer(idSessionMedia);
                            isSetMediaForCir=true;
                         }
                    }
                }

                if (intent.hasExtra(PlayerService.ACTUAL_TIME_VALUE_EXTRA)) {
                  /*  if (MainActivity.blockGUIUpdate)
                        return;*/

                    actualTime = intent.getIntExtra(PlayerService.ACTUAL_TIME_VALUE_EXTRA, 0);
                    Log.e("ACTUAL_TIME_VALUE_EXTRA",actualTime+"");
                    Log.e("ACTUAL_TIME_VALUE_EXTRA",timeDuration+"");

                    String time = millisecondsToString(actualTime);


                    if(actualTime>=timeDuration){
                        passData(PlayerFragment.Next_Music);
                    }

                    if (seekBar != null) {
                        seekBar.setProgress(actualTime);
                    }
                    if (tvTimeCurrent != null)
                        tvTimeCurrent.setText(time);
                }

            }

            if (intent.getAction().equals(PlayerService.PAUSE_ACTION)){
                isplaying=false;
                playBtn.setImageResource(R.drawable.ic_play);
            }

            if (intent.getAction().equals(PlayerService.PLAY_ACTION)){
                isplaying=true;
                playBtn.setImageResource(R.drawable.ic_pause);
            }

            if (intent.getAction().equals(PlayerService.NEXT_ACTION)){
                isplaying=true;
            }

            if (intent.getAction().equals(PlayerService.PREVIOUS_ACTION)){
                isplaying=true;
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        setComponets(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayerService.GUI_UPDATE_ACTION);
        filter.addAction(PlayerService.NEXT_ACTION);
        filter.addAction(PlayerService.PREVIOUS_ACTION);
        filter.addAction(PlayerService.PLAY_ACTION);
        filter.addAction(PlayerService.PAUSE_ACTION);
        filter.addAction(PlayerService.PAUSE_ACTION);
        filter.addAction(PlayerService.PLAY_ACTION);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    private void setComponets(View view) {

        tvSongName = view.findViewById(R.id.tv_nameSong);
        tvSongName.setText(titleSong);
        //text view
        tvTimeCurrent = view.findViewById(R.id.tv_song_current_duration);
        tvTimeDuration = view.findViewById(R.id.total_duration);
        tvTimeDuration.setText(millisecondsToString(timeDuration));

        //seek bar
        seekBar = view.findViewById(R.id.seek_aong_progessbar);
        seekBar.setMax(timeDuration);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
               /* if(seekBar.getMax()!=0){
                    if(i==seekBar.getMax()){
                        passData(PlayerFragment.Next_Music);
                    }
                }*/
                String currentPositionStr = millisecondsToString(i);
                tvTimeCurrent.setText(currentPositionStr);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                passData(seekBar.getProgress() + "");
            }

        });

        if (circleBarVisualizer != null) {
            circleBarVisualizer.release();
        }
            circleBarVisualizer = view.findViewById(R.id.visualizer);
            circleBarVisualizer.setColor(ContextCompat.getColor(super.getContext(), R.color.colorDarkDrange));
            circleBarVisualizer.setPlayer(idSessionMedia);


        playBtn = view.findViewById(R.id.btn_play);
        playBtn.setImageResource(isplaying?R.drawable.ic_pause:R.drawable.ic_play);
        ibtnNext = view.findViewById(R.id.btn_skip);
        ibtnPre = view.findViewById(R.id.btn_prev);
        iBtnRepeat = view.findViewById(R.id.btn_repeat);
        DrawableCompat.setTint(iBtnRepeat.getDrawable(), ContextCompat.getColor(getContext(), isRepeat?R.color.colorDarkDrange:R.color.colorWhite));
        iiBtnSufl = view.findViewById(R.id.btn_suffle);
        DrawableCompat.setTint(iiBtnSufl.getDrawable(), ContextCompat.getColor(getContext(), isSufle?R.color.colorDarkDrange:R.color.colorWhite));
        iBtnTimeSleep=view.findViewById(R.id.btn_timeSleep);
        //set listener
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isplaying=!isplaying;
                passData(PlayerFragment.Pause_Music);
                playBtn.setImageResource(isplaying?R.drawable.ic_play:R.drawable.ic_pause);
            }
        });
        ibtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passData(PlayerFragment.Next_Music);
            }
        });
        ibtnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passData(PlayerFragment.PreBack_Music);
            }
        });

        iiBtnSufl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSufle=!isSufle;
                passData(PlayerFragment.Sufle_Music);
                DrawableCompat.setTint(iiBtnSufl.getDrawable(), ContextCompat.getColor(getContext(), isSufle?R.color.colorDarkDrange:R.color.colorWhite));
            }
        });

        iBtnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRepeat=!isRepeat;
                passData(PlayerFragment.Repeat_Music);
                DrawableCompat.setTint(iBtnRepeat.getDrawable(), ContextCompat.getColor(getContext(), isRepeat?R.color.colorDarkDrange:R.color.colorWhite));
            }
        });

        iBtnTimeSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mbuider=new AlertDialog.Builder(getContext());
                View mView=getLayoutInflater().inflate(R.layout.time_sleep,null);

                final EditText etIdTimeSleep=mView.findViewById(R.id.tv_id_timesleep);
                Button btnAdd=mView.findViewById(R.id.btn_add_timesleep);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final long timeSleep=Integer.parseInt(etIdTimeSleep.getText().toString())*60*1000;
                        coutDown(timeSleep);

                    }
                });

                mbuider.setView(mView);
                AlertDialog dialog=mbuider.create();
                dialog.show();


            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) super.getContext();
    }

/*    @Override
    public void onDestroy() {
        super.onDestroy();
        circleBarVisualizer.release();
    }*/

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        Log.d("appmusic", "setmedia" + mediaPlayer);
        this.mediaPlayer = mediaPlayer;

    }
    // Chuyển số lượng milli giây thành một String có ý nghĩa.
    private static String millisecondsToString(int totalTime) {
        //chuyen milisecond to time. ex:1200->0:2:00
        String finalTimeString="";
        String secondString="";

        int hours=(int) (totalTime/(1000*60*60));
        int minutes=(int) (totalTime%(1000*60*60))/(1000*60);
        int seconds=(int) ((totalTime%(1000*60*60))%(1000*60)/1000);

        if(hours>0){
            finalTimeString=hours+":";
        }
        if(seconds<0){
            secondString="0"+seconds;
        }else{
            secondString=""+seconds;
        }

        finalTimeString=finalTimeString+minutes+":"+secondString;


        return finalTimeString;
    }

    /*---pass data sang mainactivity---*/
    public interface OnDataPass {
        public void onDataPass(String type);
    }

    public void passData(String data) {
        dataPasser.onDataPass(data);
    }

    public void setSongName(String songName) {
        this.titleSong = songName;
    }

    public void setTimeDuration(int timeDuration) {
        this.timeDuration = timeDuration;
    }

    public void setVisualizer(){
        circleBarVisualizer.setPlayer(idSessionMedia);
    }

    public void setIsplaying(boolean isplaying) {
        this.isplaying = isplaying;
    }

    public void coutDown(long timeSleep){
        timer=new CountDownTimer(timeSleep,500) {
            @Override
            public void onTick(long millisUntilFinished) {
                Toast.makeText(getContext(),"Đã hẹn giờ thành công",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                sentBoradcastToService(R.id.btnPause);
                Toast.makeText(getContext(),"Đã dừng nhạc",Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    //Truyen thong tin ve cho service thong qua intent broadcast
    private void sentBoradcastToService(@IdRes int id) {
        Intent intent = new Intent(PlayerService.ACTION_NOTIFICATION_BUTTON_CLICK);
        intent.putExtra(PlayerService.EXTRA_BUTTON_CLICKED, id);
        getActivity().sendBroadcast(intent);
    }



}
