package com.tranthanhdat.mucsicplayergroup2.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chibde.visualizer.CircleBarVisualizer;
import com.jgabrielfreitas.core.BlurImageView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.database.DatabaseSqlite;
import com.tranthanhdat.mucsicplayergroup2.database.InternalStorage;
import com.tranthanhdat.mucsicplayergroup2.model.PlayList;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.model.TrackSongOnline;
import com.tranthanhdat.mucsicplayergroup2.service.PlayerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlayerFragment.OnDataPass {

    public final  static String UPDATE_UI_PLAYLIST="UPDATE_UI_PLAYLIST";
    public final  static String UPDATE_UI_LISTSONGONLINE="UPDATE_UI_LISTSONGONLINE";
    public final  static String UPDATE_UI_LISTSONG="UPDATE_UI_LISTSONG";
    public final static String UPDATE_LISTSONGS="UPDATE_LISTSONGS";
    public final static String PLAYMUSIC_FORPLAYLIST="PLAYMUSIC_FORPLAYLIST";
    final public static Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");

    //Strorge
    private InternalStorage internalStorage;
    //View
    private BottomNavigationView nMainNav;
    private FrameLayout nMainFrame;
    private BlurImageView blurImageView;
    //Fragment
    private PlayerFragment playerSongFragment;
    private ListSongFragment listSongFragment;
    private ListOnlineFragment listOnlineFragment;
    private PlayListFragment playListFragment;

    private PlayerService playerService;
    private DatabaseSqlite db;
    private Intent playerIntent;
    private boolean musicbound = false;

    private static final int STORAGE_REQUEST_CODE = 400;
    public static final int MY_REQUEST_CODE = 100;
    //object
    ArrayList<Song> songList;
    ArrayList<Song> songUrlList;

    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MainActivity.UPDATE_LISTSONGS)) {
                int idPlayList= (int) intent.getIntExtra("idPlayList",0);
                if(idPlayList==0){
                    songList=db.getAllSong();
                }
                else {
                    songList=db.getSongForPlayList(idPlayList);
                }
                playerService.setPlayList(songList);
            }
            if(intent.getAction().equals(MainActivity.PLAYMUSIC_FORPLAYLIST)){
                int songPosn=intent.getIntExtra("songPosn",1);
                playerService.setSong(songPosn);
                playerSongFragment.setSongName(playerService.getSongNameCurrent());
            }
            if(intent.getAction().equals(PlayerService.PLAYONLINE_ACTION)){
                String url=intent.getStringExtra("url");
                playerService.setUrl(url);
            }

            if (intent.getAction().equals(PlayerService.PAUSE_ACTION)||
                    intent.getAction().equals(PlayerService.PLAY_ACTION)||
                    intent.getAction().equals(PlayerService.NEXT_ACTION)||
                    intent.getAction().equals(PlayerService.PREVIOUS_ACTION)){
                showNotification();
            }
        }
    };

    //Connect voi service
    private ServiceConnection mucsicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerService.MusicBinder binder = (PlayerService.MusicBinder) iBinder;

            /*playerService=PlayerService.getInstance();*/
            //get Service
            playerService = binder.getService();
            //pass list
            playerService.setPlayList(songList);
        /*    registerReceiver(receiver, new IntentFilter(
                    PlayerService.ACTION_NOTIFICATION_BUTTON_CLICK));*/
            musicbound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicbound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        playerService.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //request permission
        if (!checkStoragePermisstion()) {
            //storge permisstion not allowed, request it
            requestStoragePermission();
        }

        //get database
         db=new DatabaseSqlite(this);

        //scan playlist inAndroid and get for songList, neu bang 0 thi se quet trong bo nho va sau do luu vao database
        if(db.getSongsCount()==0){
            List<Song> songs=scanSongInStroge();
            db.createDefaultSongsIfNeed(songs);
        }

        this.songList = db.getAllSong();
        setComponents();

        //setfragment dau tien khi ung dung mo len là listsongfragment
        setFragment(listSongFragment);
        nMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        playerSongFragment.setTimeDuration((int) playerService.getTimeDuration());
                        playerSongFragment.setIsplaying(playerService.isPlaying());
                        setFragment(playerSongFragment);
                        return true;
                    case R.id.nav_song:
                        playerService.setPlayList(db.getAllSong());
                        setFragment(listSongFragment);
                        return true;
                    case R.id.nav_playlist:
                        setFragment(playListFragment);
                        return true;
                    case R.id.nav_album:
                        setFragment(listOnlineFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        createNotificationChannel();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (playerIntent == null) {
            playerIntent = new Intent(this, PlayerService.class);
            getApplicationContext().bindService(playerIntent, mucsicServiceConnection, Context.BIND_AUTO_CREATE);
            startService(playerIntent);
        }


    }
    //Khoi tao cac bien. component can thiet cho chuong trinh
    private void setComponents() {
        Bundle bundle = new Bundle();
        // set Fragmentclass Arguments

        bundle.putSerializable("songList", songList);

        nMainFrame = findViewById(R.id.main_frame);
        nMainNav = findViewById(R.id.main_nav);

        playListFragment=new PlayListFragment();
        playListFragment.onAttach(this);

        playerSongFragment = new PlayerFragment();
        playerSongFragment.onAttach(this);


        listSongFragment = new ListSongFragment();
        listSongFragment.setmSongs(songList);
        listSongFragment.onAttach(this);

        listOnlineFragment = new ListOnlineFragment();

        //get componet to activity .xml
        blurImageView = findViewById(R.id.background_blur);

        //blurImage
        blurImageView.setBlur(5);

        //set intent filter
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayerService.GUI_UPDATE_ACTION);
        filter.addAction(MainActivity .PLAYMUSIC_FORPLAYLIST);
        filter.addAction(MainActivity.UPDATE_LISTSONGS);
        filter.addAction(PlayerService.NEXT_ACTION);
        filter.addAction(PlayerService.PREVIOUS_ACTION);
        filter.addAction(PlayerService.PLAY_ACTION);
        filter.addAction(PlayerService.PAUSE_ACTION);
        filter.addAction(PlayerService.PLAYONLINE_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Nhan du lieu tra ve tu onlinesongfragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == MainActivity.RESULT_OK && requestCode == MY_REQUEST_CODE) {
            TrackSongOnline song=new TrackSongOnline();
            song.setUrl(data.getStringExtra("Url"));
            song.setNameSong(data.getStringExtra("Name"));

            db.addTrackSong(song);
            Toast.makeText(MainActivity.this,"Added Song online",Toast.LENGTH_SHORT).show();
            Intent updateUiPlaylist=new Intent(MainActivity.UPDATE_UI_LISTSONGONLINE);
            sendBroadcast(updateUiPlaylist);
        }
    }
    //Set fragment vao main_frame cua main activity
    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
    //Yeu cau cap quyen
    private void requestStoragePermission() {
        String[] storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }
    //Kiem tra nguoi dung da cap quyen hay ch
    private boolean checkStoragePermisstion() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }
    //sư kiện songpick được gọi của songFragment
    public void songPicked(View view) {
        nMainNav.setSelectedItemId(R.id.nav_home);
        String a = view.getTag().toString();
        int b = Integer.parseInt(a);
        playerService.setSong(b);
        /*SystemClock.sleep(200);*/
        setFragment(playerSongFragment);
        showNotification();
    }
    //scan nhac trong bo nho dien thoai.
    private ArrayList<Song> scanSongInStroge() {
        Song songTmp;
        ArrayList<Song> Songtmps = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            // query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            // no media on the device
        } else {

            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int ArtisColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int ComposerColumn = cursor.getColumnIndex(MediaStore.Audio.Media.COMPOSER);
            int displayName=cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int album_column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            do {
                if(cursor.getString(displayName).endsWith(".mp3")){
                    songTmp = new Song();
                    Uri uri1 = ContentUris.withAppendedId(MainActivity.sArtworkUri,
                            cursor.getLong(album_column_index));
                    songTmp.setmImageUrl(uri1.toString());
                    songTmp.setTitle(cursor.getString(titleColumn));
                    songTmp.setId(cursor.getLong(idColumn));
                    songTmp.setArtist(cursor.getString(ArtisColumn));
                    Songtmps.add(songTmp);
                }
            } while (cursor.moveToNext());
        }

        return Songtmps;
    }

    //Nhan data tu player fragment
    @Override
    public void onDataPass(String type) {
        switch (type) {
            case PlayerFragment.Next_Music:
                sentBoradcastToService(R.id.btnNext);
                break;
            case PlayerFragment.Pause_Music:
                sentBoradcastToService(R.id.btnPause);
                break;
            case PlayerFragment.PreBack_Music:
                sentBoradcastToService(R.id.btnPrevious);
                break;
            case PlayerFragment.Sufle_Music:
                playerService.sufle();
                break;
            case PlayerFragment.Repeat_Music:
                playerService.repeat();
                break;
            default:
                playerService.seekTo(Integer.parseInt(type));
                break;
        }
    }
    //Truyen thong tin ve cho service thong qua intent broadcast
    private void sentBoradcastToService(@IdRes int id) {
        Intent intent = new Intent(PlayerService.ACTION_NOTIFICATION_BUTTON_CLICK);
        intent.putExtra(PlayerService.EXTRA_BUTTON_CLICKED, id);
        sendBroadcast(intent);
    }
    //sự kiện add playlist duoc goi tu playlist fragment
    public void addOnlineClick(View view) {
        Intent intent = new Intent(this,Pop.class);
        startActivityForResult(intent, MainActivity.MY_REQUEST_CODE);
    }

    public void updateUiPlayerFragment(){
        //nghỉ 100s cho bên service cập nhập kịp
        SystemClock.sleep(100);
        MediaPlayer player = playerService.getPlayer();
        setFragment(playerSongFragment);
        playerSongFragment.setMediaPlayer(player);
        playerSongFragment.setSongName(playerService.getSongNameCurrent());
    }

    //when onclick button scan music, it will jump inio here
    public void scanMusic(View view) {
    }

    //sự kiện add playlist duoc goi tu playlist fragment
    public void addPlayList(View view) {
        /*Intent intent = new Intent(this,AddPlayListActivity.class);
        startActivity(intent);*/
        AlertDialog.Builder mbuider=new AlertDialog.Builder(MainActivity.this);
        View mView=getLayoutInflater().inflate(R.layout.activity_add_play_list,null);

        final EditText etNamePlayList=mView.findViewById(R.id.tv_name_addplaylist);
        Button btnAdd=mView.findViewById(R.id.btn_add_addplaylist);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayList playListTmp=new PlayList();
                playListTmp.setName(etNamePlayList.getText().toString());

                try{
                    db.addPlayList(playListTmp);
                    Toast.makeText(MainActivity.this,"Added playlist",Toast.LENGTH_SHORT).show();
                    Intent updateUiPlaylist=new Intent(MainActivity.UPDATE_UI_PLAYLIST);
                    sendBroadcast(updateUiPlaylist);
                }
                catch (SQLException e){
                    Toast.makeText(MainActivity.this,"Add playlist, Failed!",Toast.LENGTH_SHORT).show();
                }

            }
        });
        mbuider.setView(mView);
        AlertDialog dialog=mbuider.create();
        dialog.show();

    }

    //Intent dung de truyen du lieu cho broadcast
    private PendingIntent onButtonNotificationClick(@IdRes int id) {
        Intent intent = new Intent(playerService.ACTION_NOTIFICATION_BUTTON_CLICK);
        intent.putExtra(playerService.EXTRA_BUTTON_CLICKED, id);
        return PendingIntent.getBroadcast(this, id, intent, 0);
    }
    //hien thi notification khi an choi nhac
    private void showNotification() {

        RemoteViews notificationLayout =
                new RemoteViews(this.getPackageName(), R.layout.musicnotification);
        //set attribute
        notificationLayout.setTextViewText(R.id.textSongName,playerService.getSongNameCurrent());

        notificationLayout.setImageViewResource(R.id.btnPause,playerService.isPlaying()?R.drawable.ic_pause:R.drawable.ic_play);

        notificationLayout.setImageViewUri(R.id.image_album_notificaiton,Uri.parse(playerService.getImageUrlSong()));

        notificationLayout.setOnClickPendingIntent(R.id.btnPrevious,
                onButtonNotificationClick(R.id.btnPrevious));
        notificationLayout.setOnClickPendingIntent(R.id.btnNext,
                onButtonNotificationClick(R.id.btnNext));
        notificationLayout.setOnClickPendingIntent(R.id.btnPause,
                onButtonNotificationClick(R.id.btnPause));

        Notification
                notification = new NotificationCompat.Builder(this, playerService.CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(notificationLayout)
                .build();
        NotificationManager notificationManager =
                (android.app.NotificationManager) getSystemService(playerService.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(PlayerService.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }




}
