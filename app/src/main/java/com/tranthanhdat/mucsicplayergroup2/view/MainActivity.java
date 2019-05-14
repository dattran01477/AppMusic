package com.tranthanhdat.mucsicplayergroup2.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chibde.visualizer.CircleBarVisualizer;
import com.jgabrielfreitas.core.BlurImageView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tranthanhdat.mucsicplayergroup2.R;
import com.tranthanhdat.mucsicplayergroup2.database.DatabaseSqlite;
import com.tranthanhdat.mucsicplayergroup2.database.InternalStorage;
import com.tranthanhdat.mucsicplayergroup2.model.PlayList;
import com.tranthanhdat.mucsicplayergroup2.model.Song;
import com.tranthanhdat.mucsicplayergroup2.service.PlayerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlayerFragment.OnDataPass {

    public final  static String UPDATE_UI_PLAYLIST="UPDATE_UI_PLAYLIST";

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
                        MediaPlayer player = playerService.getPlayer();
                        setFragment(playerSongFragment);
                       /* playerSongFragment.setMediaPlayer(player);*/
                        return true;
                    case R.id.nav_song:
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
        listSongFragment.onAttach(this);

        listOnlineFragment = new ListOnlineFragment();

        //get componet to activity .xml
        blurImageView = findViewById(R.id.background_blur);

        //blurImage
        blurImageView.setBlur(5);
 /*       // set custom color to the line.
        circleBarVisualizer.setColor(ContextCompat.getColor( this, R.color.colorDarkDrange));*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //Nhan du lieu tra ve tu onlinesongfragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == MainActivity.RESULT_OK && requestCode == MY_REQUEST_CODE) {
            String url = data.getStringExtra("Url");
            nMainNav.setSelectedItemId(R.id.nav_home);
            playerService.setUrl(url);
            playerService.playMusic();
            MediaPlayer player = playerService.getPlayer();
            setFragment(playerSongFragment);
            playerSongFragment.setMediaPlayer(player);
        } else {

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
        SystemClock.sleep(200);
        setFragment(playerSongFragment);
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
            do {
                if(cursor.getString(displayName).endsWith(".mp3")){
                    songTmp = new Song();
                    songTmp.setTitle(cursor.getString(titleColumn));
                    songTmp.setId(cursor.getLong(idColumn));
                    songTmp.setArtist(cursor.getString(titleColumn));
                    Songtmps.add(songTmp);
                }
            } while (cursor.moveToNext());
        }

        return Songtmps;
    }

  /*  private ArrayList<Song> getSongInStrorge() {
        ArrayList<Song> songTmps = null;


        try {
            songTmps = (ArrayList<Song>) InternalStorage.readObject(this, "lsSong");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (songTmps == null) {
            songTmps = scanSongInStroge();
            //write lsSong into inStorage
            try {
                InternalStorage.writeObject(this, "lsSong", songTmps);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return songTmps;
    }*/
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

        final EditText etIdPlayList=mView.findViewById(R.id.tv_id_addplaylist);
        final EditText etNamePlayList=mView.findViewById(R.id.tv_name_addplaylist);
        Button btnAdd=mView.findViewById(R.id.btn_add_addplaylist);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayList playListTmp=new PlayList();
                playListTmp.setIdPlayList(Integer.parseInt(etIdPlayList.getText().toString()));
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
}
